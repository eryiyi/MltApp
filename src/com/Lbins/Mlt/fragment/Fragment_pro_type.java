package com.Lbins.Mlt.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.Lbins.Mlt.MainActivity;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.Pro_type_adapter;
import com.Lbins.Mlt.base.ActivityTack;
import com.Lbins.Mlt.base.BaseFragment;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.CountrysData;
import com.Lbins.Mlt.module.CityObj;
import com.Lbins.Mlt.module.CountryObj;
import com.Lbins.Mlt.module.ProvinceObj;
import com.Lbins.Mlt.ui.LoginActivity;
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

public class Fragment_pro_type extends BaseFragment {
    private ImageView hint_img;
    private GridView listView;
    private Pro_type_adapter adapter;
    private List<CountryObj> toolsList = new ArrayList<CountryObj>();
    private CityObj cityObj;
    private ProvinceObj provinceObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pro_type, null);

        provinceObj = (ProvinceObj) getArguments().getSerializable("provinceObj");
        cityObj = (CityObj) getArguments().getSerializable("cityObj");

        hint_img = (ImageView) view.findViewById(R.id.hint_img);
        listView = (GridView) view.findViewById(R.id.listView);

//		((TextView)view.findViewById(R.id.toptype)).setText(cityObj.getCity());
        adapter = new Pro_type_adapter(getActivity(), toolsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                CountryObj countryObj = toolsList.get(arg2);
                if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class)))) {
                    Intent loginV = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginV);
                } else {
                    if ("1".equals(getGson().fromJson(getSp().getString("is_see_all", ""), String.class))) {
                        goTo(countryObj.getAreaID(), countryObj.getArea());
                    } else {
                        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_level_num", ""), String.class))) {
                            switch (Integer.parseInt(getGson().fromJson(getSp().getString("mm_level_num", ""), String.class))) {
                                case 0:
                                    //县区
                                    if (countryObj.getAreaID().equals(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
                                        //如果是当前用户登陆的县区 可以查看该信息
                                        goTo(countryObj.getAreaID(), countryObj.getArea());
                                    } else {
                                        Toast.makeText(getActivity(), R.string.select_area_error, Toast.LENGTH_SHORT).show();
                                        ActivityTack.getInstanse().popUntilActivity(MainActivity.class);
                                    }
                                    break;
                                case 1:
                                    //是市级vip
                                    if (cityObj.getCityID().equals(getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class))) {
                                        //如果是当前用户登陆的县区 可以查看该信息
                                        goTo(countryObj.getAreaID(), countryObj.getArea());
                                    } else {
                                        Toast.makeText(getActivity(), R.string.select_area_error, Toast.LENGTH_SHORT).show();
                                        ActivityTack.getInstanse().popUntilActivity(MainActivity.class);
                                    }
                                    break;
                                case 2:
                                    //是省级vip
                                    if (provinceObj.getProvinceID().equals(getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class))) {
                                        //如果是当前用户登陆的县区 可以查看该信息
                                        goTo(countryObj.getAreaID(), countryObj.getArea());
                                    } else {
                                        Toast.makeText(getActivity(), R.string.select_area_error, Toast.LENGTH_SHORT).show();
                                        ActivityTack.getInstanse().popUntilActivity(MainActivity.class);
                                    }
                                    break;
                                case 3:
                                case 4:
                                    goTo(countryObj.getAreaID(), countryObj.getArea());
                                    break;
                            }
                        }
                    }
                }
            }
        });
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        getBigType();
        return view;
    }

    void goTo(String countryId, String countryName) {
        Intent intent1 = new Intent("select_country");
        intent1.putExtra("countryId", countryId);
        intent1.putExtra("countryName", countryName);
        getActivity().sendBroadcast(intent1);
        ActivityTack.getInstanse().popUntilActivity(MainActivity.class);
//		getActivity().finish();
    }


    public void getBigType() {
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
                                    toolsList.clear();
                                    toolsList.addAll(data.getData());
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
