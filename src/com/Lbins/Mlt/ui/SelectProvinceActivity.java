package com.Lbins.Mlt.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.ItemCountryAdapter;
import com.Lbins.Mlt.adapter.ItemGuanzhuAdapter;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.CountrysData;
import com.Lbins.Mlt.data.GuanzhuAreaObjData;
import com.Lbins.Mlt.module.CountryObj;
import com.Lbins.Mlt.module.GuanzhuAreaObj;
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
public class SelectProvinceActivity extends BaseActivity implements View.OnClickListener {
    private GridView lstv;
    private ItemCountryAdapter adapter;
    private List<CountryObj> lists = new ArrayList<CountryObj>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private GridView lstvGz;
    private TextView no_data_text;
    private String[] areaNames = new String[10];
    private String[] areaIds = new String[10];
    private List<String> areaNamesList = new ArrayList<String>();

    private ItemGuanzhuAdapter adapterGz;
    GuanzhuAreaObj guanzhuAreaObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_province);
        initView();
        getHotCity();
        getGuanzhuArea();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        lstvGz = (GridView) this.findViewById(R.id.lstvGz);
        no_data_text = (TextView) this.findViewById(R.id.no_data_text);
        adapterGz = new ItemGuanzhuAdapter(areaNamesList, SelectProvinceActivity.this);
        lstvGz.setAdapter(adapterGz);
        lstvGz.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lstvGz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (areaIds != null && areaIds.length > i) {
                    String idPostion = areaIds[i];//县区的id
                    String name = areaNames[i];//县区的名字
                    Intent intent = new Intent(SelectProvinceActivity.this, RecordGzActivity.class);
                    intent.putExtra("idPostion", idPostion);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });

        lstv = (GridView) this.findViewById(R.id.lstv);
        adapter = new ItemCountryAdapter(lists, SelectProvinceActivity.this);
        lstv.setAdapter(adapter);
        lstv.setSelector(new ColorDrawable(Color.TRANSPARENT));

        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryObj countryObj = lists.get(position);
                Intent intent = new Intent(SelectProvinceActivity.this, RecordGzActivity.class);
                intent.putExtra("idPostion", countryObj.getAreaID());
                intent.putExtra("name", countryObj.getArea());
                startActivity(intent);

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
//    public void initData(){
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_COUNTRY_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code1 =  jo.getString("code");
//                                if(Integer.parseInt(code1) == 200){
//                                    CountrysData data = getGson().fromJson(s, CountrysData.class);
//                                    lists.clear();
//                                    lists.addAll(data.getData());
//                                    adapter.notifyDataSetChanged();
//                                }
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//                        }else {
//                            Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                        Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("is_use", "1");
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        getRequestQueue().add(request);
//    }


    //查询关注区域
    public void getGuanzhuArea() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_GUANZHU_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    GuanzhuAreaObjData data = getGson().fromJson(s, GuanzhuAreaObjData.class);
                                    if (data.getData() != null && data.getData().size() > 0) {
                                        //说明已经申请了
                                        List<GuanzhuAreaObj> listgz = data.getData();
                                        if (listgz != null && listgz.size() > 0) {
                                            guanzhuAreaObj = listgz.get(0);
                                            if (guanzhuAreaObj != null) {
                                                if ("0".equals(guanzhuAreaObj.getIscheck())) {
//                                                    showMsg(SelectProvinceActivity.this, "您已经申请了关注区域！请等待管理员审核");
                                                    no_data_text.setText(getResources().getString(R.string.also_area_please_wait));
                                                    no_data_text.setClickable(false);
                                                    no_data_text.setVisibility(View.VISIBLE);
                                                } else if ("1".equals(guanzhuAreaObj.getIscheck())) {
//                                                    Intent intent = new Intent(SelectProvinceActivity.this, RecordGzActivity.class);
//                                                    intent.putExtra("guanzhuAreaObj", guanzhuAreaObj);
//                                                    startActivity(intent);
                                                    areaNames = guanzhuAreaObj.getArea_name().split(",");
                                                    areaIds = guanzhuAreaObj.getAreaid().split(",");
                                                    for (String str : areaNames) {
                                                        areaNamesList.add(str);
                                                    }
                                                    adapterGz.notifyDataSetChanged();

                                                } else if ("2".equals(guanzhuAreaObj.getIscheck())) {
//                                                    showMsg(SelectProvinceActivity.this, "您申请的关注区域未通过审核，请联系客服！");
                                                    no_data_text.setText(getResources().getString(R.string.also_area_please_wait1));
                                                    no_data_text.setClickable(false);
                                                    no_data_text.setVisibility(View.VISIBLE);
                                                } else {
//                                                    showMsg(SelectProvinceActivity.this, "您尚未申请关注区域，请设置关注区域！");
                                                    no_data_text.setText(getResources().getString(R.string.also_area_please_wait2));
                                                    no_data_text.setVisibility(View.VISIBLE);
                                                    no_data_text.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent guanzhuV = new Intent(SelectProvinceActivity.this, SetGuanzhuActivity.class);
                                                            startActivity(guanzhuV);
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            no_data_text.setText(getResources().getString(R.string.also_area_please_wait2));
                                            no_data_text.setVisibility(View.VISIBLE);
                                            no_data_text.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent guanzhuV = new Intent(SelectProvinceActivity.this, SetGuanzhuActivity.class);
                                                    startActivity(guanzhuV);
                                                }
                                            });
                                        }
                                    } else {
                                        no_data_text.setText(getResources().getString(R.string.also_area_please_wait2));
                                        no_data_text.setVisibility(View.VISIBLE);
                                        no_data_text.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent guanzhuV = new Intent(SelectProvinceActivity.this, SetGuanzhuActivity.class);
                                                startActivity(guanzhuV);
                                            }
                                        });
                                    }
                                } else {
                                    no_data_text.setText(getResources().getString(R.string.also_area_please_wait2));
                                    no_data_text.setVisibility(View.VISIBLE);
                                    no_data_text.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent guanzhuV = new Intent(SelectProvinceActivity.this, SetGuanzhuActivity.class);
                                            startActivity(guanzhuV);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("index", "1");
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


    public void getHotCity() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_HOT_CITY_URL,
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
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SelectProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
}
