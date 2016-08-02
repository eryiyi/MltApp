package com.Lbins.Mlt.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.ItemNearbyAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.EmpsData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
import com.Lbins.Mlt.module.Emp;
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
 * Created by Administrator on 2016/2/23.
 */
public class NearbyActivity extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private PullToRefreshListView lstv;
    ItemNearbyAdapter adapter;
    List<Emp> lists = new ArrayList<Emp>();
    private ImageView no_data;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_activity);
        initView();
        if (!StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)) {
            getData();
        } else {
            if (lists.size() > 0) {
                no_data.setVisibility(View.GONE);
                lstv.setVisibility(View.VISIBLE);
            } else {
                no_data.setVisibility(View.VISIBLE);
                lstv.setVisibility(View.GONE);
            }
        }
    }

    void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_NEARBY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    EmpsData data = getGson().fromJson(s, EmpsData.class);
                                    if (data != null && data.getData().size() > 0) {
                                        //计算距离
//                                        List<Emp> listsAll = new ArrayList<Emp>();
//                                        listsAll.addAll(data.getData());
//                                        for (int i = 0; i < listsAll.size(); i++) {
//                                            Emp fuwuObj = listsAll.get(i);
//                                            if (fuwuObj != null && !StringUtil.isNullOrEmpty(fuwuObj.getLat()) && !StringUtil.isNullOrEmpty(fuwuObj.getLng())) {
//                                                LatLng latLng = new LatLng(Double.valueOf(UniversityApplication.lat), Double.valueOf(UniversityApplication.lng));
//                                                LatLng latLng1 = new LatLng(Double.valueOf(fuwuObj.getLat()), Double.valueOf(fuwuObj.getLng()));
//                                                String distance = StringUtil.getDistance(latLng, latLng1);
//                                                listsAll.get(i).setDistance(distance + "km");
//                                                fuwuObj.setDistance(distance + "km");
//                                                if (Double.valueOf(distance) < 100) {
//                                                    //100KM以内的
//                                                    lists.add(fuwuObj);
//                                                }
//                                            }
//                                        }
                                        if (IS_REFRESH) {
                                            lists.clear();
                                        }
                                        lists.addAll(data.getData());
                                        adapter.notifyDataSetChanged();
                                        if (lists.size() > 0) {
                                            no_data.setVisibility(View.GONE);
                                            lstv.setVisibility(View.VISIBLE);
                                        } else {
                                            no_data.setVisibility(View.VISIBLE);
                                            lstv.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    showMsg(NearbyActivity.this, getResources().getString(R.string.get_data_error));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        lstv.onRefreshComplete();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (lists.size() > 0) {
                            no_data.setVisibility(View.GONE);
                            lstv.setVisibility(View.VISIBLE);
                        } else {
                            no_data.setVisibility(View.VISIBLE);
                            lstv.setVisibility(View.GONE);
                        }
                        showMsg(NearbyActivity.this, getResources().getString(R.string.get_data_error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if(!StringUtil.isNullOrEmpty(UniversityApplication.lat)){
                    params.put("lat", (UniversityApplication.lat == null ? "" : UniversityApplication.lat));
                }
                if(!StringUtil.isNullOrEmpty(UniversityApplication.lng)){
                   params.put("lng", (UniversityApplication.lng == null ? "" : UniversityApplication.lng));
                }
                params.put("index", String.valueOf(pageIndex));
                params.put("size", "10");
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

    void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new ItemNearbyAdapter(lists, NearbyActivity.this);
        no_data = (ImageView) this.findViewById(R.id.no_data);
        adapter.setOnClickContentItemListener(this);

        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setAdapter(adapter);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                getData();
            }
        });

        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent profileV = new Intent(NearbyActivity.this, ProfileActivity.class);
                Emp emp = lists.get(position - 1);
                profileV.putExtra("id", emp.getMm_emp_id());
                startActivity(profileV);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag) {
            case 1:
            {
                Emp emp = lists.get(position);
                if (!StringUtil.isNullOrEmpty(emp.getLat_company()) && !StringUtil.isNullOrEmpty(emp.getLng_company())) {
                    //开始导航
                    if (!StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)) {
                        Intent naviV = new Intent(NearbyActivity.this, GPSNaviActivity.class);
                        naviV.putExtra("lat_end", emp.getLat_company());
                        naviV.putExtra("lng_end", emp.getLng_company());
                        startActivity(naviV);
                    } else {
                        Toast.makeText(NearbyActivity.this, getResources().getString(R.string.please_open_gps), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(NearbyActivity.this, getResources().getString(R.string.no_location_lat_lng), Toast.LENGTH_SHORT).show();
                }
            }
                break;
            case 2:
            {
                Emp emp = lists.get(position);
                if (emp != null) {
                    showTel(emp.getMm_emp_mobile());
                }
            }
                break;
        }
    }
    // 拨打电话窗口
    private void showTel(String tel) {
        final Dialog picAddDialog = new Dialog(NearbyActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(NearbyActivity.this, R.layout.tel_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(tel);
        //提交
        btn_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                if (!StringUtil.isNullOrEmpty(contreport)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + jubao_cont.getText().toString()));
                    startActivity(intent);
                }
                picAddDialog.dismiss();
            }
        });

        //取消
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

}
