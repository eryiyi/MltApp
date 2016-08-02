package com.Lbins.Mlt.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.ItemQQAdapter;
import com.Lbins.Mlt.adapter.ItemWeixinAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.WeixinObjData;
import com.Lbins.Mlt.module.WeixinObj;
import com.Lbins.Mlt.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghailong on 2016/3/20.
 */
public class WeixinKefuActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private ListView lstv;
    private ListView lstvQ;
    private ItemWeixinAdapter adapter;
    private ItemQQAdapter adapterQ;
    List<WeixinObj> lists = new ArrayList<WeixinObj>();
    List<WeixinObj> listsW = new ArrayList<WeixinObj>();
    List<WeixinObj> listsQ = new ArrayList<WeixinObj>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weixin_kefu_activity);


        this.findViewById(R.id.back).setOnClickListener(this);
        lstv = (ListView) this.findViewById(R.id.lstv);
        lstvQ = (ListView) this.findViewById(R.id.lstvQ);
        adapter = new ItemWeixinAdapter(listsW, WeixinKefuActivity.this);
        adapterQ = new ItemQQAdapter(listsQ, WeixinKefuActivity.this);
        adapterQ.setOnClickContentItemListener(this);
        lstv.setAdapter(adapter);
        lstvQ.setAdapter(adapterQ);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
            }
        });
        lstvQ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
//                if(listsQ != null && listsQ.size() >i){
//                    WeixinObj weixinObj = listsQ.get(i);
//                    if(weixinObj != null){
//                        String url="mqqwpa://im/chat?chat_type=wpa&uin=" + weixinObj.getMm_weixin();
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                    }
//
//                }

            }
        });
        getTel();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    void getTel() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_WEIXINS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    WeixinObjData data = getGson().fromJson(s, WeixinObjData.class);
                                    lists.clear();
                                    lists.addAll(data.getData());
                                    if (lists != null && lists.size() > 0) {
                                        for (WeixinObj weixinObj : lists) {
                                            if ("0".equals(weixinObj.getMm_weixin_type())) {
                                                //微信
                                                listsW.add(weixinObj);
                                            }
                                            if ("1".equals(weixinObj.getMm_weixin_type())) {
                                                //微信
                                                listsQ.add(weixinObj);
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    adapterQ.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag) {
            case 1:
                if (listsQ != null && listsQ.size() > position) {
                    WeixinObj weixinObj = listsQ.get(position);
                    if (weixinObj != null) {
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + weixinObj.getMm_weixin();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }

                }
                break;
        }
    }
}
