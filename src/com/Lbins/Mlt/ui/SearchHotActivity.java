package com.Lbins.Mlt.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.ItemSearchHotAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.DBHelper;
import com.Lbins.Mlt.data.HotWordObjData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
import com.Lbins.Mlt.module.HotWordObj;
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
 * Created by zhl on 2016/8/2.
 */
public class SearchHotActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private EditText keyword;
    private List<Map<String, String>> mylist = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> splitList = new ArrayList<Map<String, String>>();
    PullToRefreshListView lstv;
    ItemSearchHotAdapter adapter;
    List<HotWordObj> listHotWord = new ArrayList<HotWordObj>();
    List<HotWordObj> hotWordObjs = new ArrayList<HotWordObj>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_hot_activity);

        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.btn_search).setOnClickListener(this);
        keyword = (EditText) this.findViewById(R.id.keyword);
        keyword.addTextChangedListener(watcher);
        hotWordObjs = DBHelper.getInstance(SearchHotActivity.this).getWords();
        lstv = (PullToRefreshListView) findViewById(R.id.lstv);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        // 配置适配器
        adapter = new ItemSearchHotAdapter(this, mylist, splitList); // 布局里的控件id
        adapter.setOnClickContentItemListener(this);
        lstv.setAdapter(adapter);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                lstv.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                lstv.onRefreshComplete();
            }
        });

        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                lists.get(position - 1).setIs_read("1");
//                adapter.notifyDataSetChanged();
//                recordVO = lists.get(position - 1);
//                DBHelper.getInstance(SearchRecordActivity.this).updateRecord(recordVO);
            }
        });
        progressDialog = new ProgressDialog(SearchHotActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        setData(); //设置数据
    }

    private void setData() {
        // 组织数据源
        Map<String, String> mp = new HashMap<String, String>();
        mp.put("itemTitle", "历史搜索");
        mylist.add(mp);
        splitList.add(mp);
        for (int i = 0; i < hotWordObjs.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("itemTitle", hotWordObjs.get(i).getMm_hot_word_title());
            mylist.add(map);
        }
        initData();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //todo
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.btn_search:
            {
                //搜索
                if(!StringUtil.isNullOrEmpty(keyword.getText().toString())){
                    HotWordObj hotWordObj = new HotWordObj();
                    hotWordObj.setMm_hot_word_title(keyword.getText().toString());
                    DBHelper.getInstance(SearchHotActivity.this).saveWord(hotWordObj);

                    Intent searchIntent = new Intent(SearchHotActivity.this, SearchRecordActivity.class);
                    searchIntent.putExtra("content" , keyword.getText().toString());
                    startActivity(searchIntent);
                    finish();
                }
            }
                break;
        }
    }

    void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_HOT_WORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    HotWordObjData data = getGson().fromJson(s, HotWordObjData.class);
                                    listHotWord = data.getData();
                                    Map<String, String> mp = new HashMap<String, String>();
                                    mp.put("itemTitle", "热门搜索");
                                    mylist.add(mp);
                                    splitList.add(mp);
                                    for (int i = 0; i < listHotWord.size(); i++) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("itemTitle", listHotWord.get(i).getMm_hot_word_title());
                                        mylist.add(map);
                                    }
                                    lstv.onRefreshComplete();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SearchHotActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
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
        switch (flag){
            case 1:
                String str = (String) object;
                if(!StringUtil.isNullOrEmpty(str) && !"历史搜索".equals(str)&& !"热门搜索".equals(str)){
                    keyword.setText(str);
                }
                Intent searchIntent = new Intent(SearchHotActivity.this, SearchRecordActivity.class);
                searchIntent.putExtra("content" , keyword.getText().toString());
                startActivity(searchIntent);
                finish();
                break;
        }
    }
}
