package com.Lbins.Mlt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.ItemCountryAdapter;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.CountrysData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
import com.Lbins.Mlt.module.CityObj;
import com.Lbins.Mlt.module.CountryObj;
import com.Lbins.Mlt.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/19.
 */
public class SelectCountryActivity extends BaseActivity implements View.OnClickListener {
    private PullToRefreshListView lstv;
    private ItemCountryAdapter adapter;
    private List<CountryObj> lists = new ArrayList<CountryObj>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private CityObj cityObj;
//    private ProvinceObj provinceObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_province);
        cityObj = (CityObj) getIntent().getExtras().get("cityObj");
//        provinceObj = (ProvinceObj) getIntent().getExtras().get("provinceObj");
        initView();
        initData();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new ItemCountryAdapter(lists, SelectCountryActivity.this);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setAdapter(adapter);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SelectCountryActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    initData();
                } else {
                    lstv.onRefreshComplete();
                    //未登录
                    Intent loginV = new Intent(SelectCountryActivity.this, LoginActivity.class);
                    startActivity(loginV);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SelectCountryActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    initData();
                } else {
                    lstv.onRefreshComplete();
                    //未登录
                    Intent loginV = new Intent(SelectCountryActivity.this, LoginActivity.class);
                    startActivity(loginV);
                }
            }
        });

        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryObj countryObj = lists.get(position - 1);
                //这里需要判断
                if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class)))) {
                    //未登录
                    Intent loginV = new Intent(SelectCountryActivity.this, LoginActivity.class);
                    startActivity(loginV);
                } else {
                    if ("1".equals(getGson().fromJson(getSp().getString("is_see_all", ""), String.class))) {
                        //可以查看所有信息
                        goTo(countryObj.getAreaID(), countryObj.getArea());
                    } else {
                        switch (Integer.parseInt(getGson().fromJson(getSp().getString("mm_level_num", ""), String.class))) {
                            case 0:
                                //县区
                                if (countryObj.getAreaID().equals(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
                                    //如果是当前用户登陆的县区 可以查看该信息
                                    goTo(countryObj.getAreaID(), countryObj.getArea());
                                } else {
                                    showMsg(SelectCountryActivity.this, getResources().getString(R.string.select_area_error));
                                    finish();
                                }
                                break;
                            case 1:
                                //是市级vip
                                if (cityObj.getCityID().equals(getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class))) {
                                    //如果是当前用户登陆的县区 可以查看该信息
                                    goTo(countryObj.getAreaID(), countryObj.getArea());
                                } else {
                                    showMsg(SelectCountryActivity.this, getResources().getString(R.string.select_area_error));
                                    finish();
                                }
                                break;
                            case 2:
                                //是省级vip
//                                if(provinceObj.getProvinceID().equals(getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class))){
                                //如果是当前用户登陆的县区 可以查看该信息
                                goTo(countryObj.getAreaID(), countryObj.getArea());
//                                }else {
//                                    showMsg(SelectCountryActivity.this, getResources().getString(R.string.select_area_error));
//                                    finish();
//                                }
                                break;
                            case 3:
                            case 4:
                                goTo(countryObj.getAreaID(), countryObj.getArea());
                                break;
                        }
                    }

                }
            }
        });
    }

    void goTo(String countryId, String countryName) {
        //调用广播，刷新主页
        Intent intent1 = new Intent("select_country");
        intent1.putExtra("countryId", countryId);
        intent1.putExtra("countryName", countryName);
        sendBroadcast(intent1);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_COUNTRY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    CountrysData data = getGson().fromJson(s, CountrysData.class);
                                    lists.clear();
                                    lists.addAll(data.getData());
                                    lstv.onRefreshComplete();
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectCountryActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(SelectCountryActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("father", cityObj.getCityID());
                params.put("is_use", "1");
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

}
