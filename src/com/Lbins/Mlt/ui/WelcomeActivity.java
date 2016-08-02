package com.Lbins.Mlt.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.Lbins.Mlt.MainActivity;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.baidu.Utils;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.EmpData;
import com.Lbins.Mlt.module.Emp;
import com.Lbins.Mlt.util.HttpUtils;
import com.Lbins.Mlt.util.StringUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/18.
 */
public class WelcomeActivity extends BaseActivity implements View.OnClickListener, Runnable, AMapLocationListener {
    boolean isMobileNet, isWifiNet;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //定位
    private AMapLocationClient mlocationClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
//        UmengUpdateAgent.update(this);

        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();

        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
            isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(this, R.string.net_work_error, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 启动一个线程
        new Thread(WelcomeActivity.this).start();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void run() {
        try {
            // 3秒后跳转到登录界面
            Thread.sleep(1500);
//            SharedPreferences.Editor editor = getSp().edit();
//            boolean isFirstRun = getSp().getBoolean("isFirstRun", true);
//            if (isFirstRun) {
//                editor.putBoolean("isFirstRun", false);
//                editor.commit();
//                Intent loadIntent = new Intent(WelcomeActivity.this, AboutActivity.class);
//                startActivity(loadIntent);
//                finish();
//            } else {
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class)) && !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("password", ""), String.class))) {
                    loginData();
                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                }
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void loginData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.LOGIN__URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    EmpData data = getGson().fromJson(s, EmpData.class);
                                    saveAccount(data.getData());
                                } else if (Integer.parseInt(code) == 1) {
                                    save("isLogin", "0");//1已经登录了  0未登录
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                } else if (Integer.parseInt(code) == 2) {
                                    save("isLogin", "0");//1已经登录了  0未登录
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                } else if (Integer.parseInt(code) == 3) {
                                    save("isLogin", "0");//1已经登录了  0未登录
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                } else if (Integer.parseInt(code) == 4) {
                                    save("isLogin", "0");//1已经登录了  0未登录
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                } else if (Integer.parseInt(code) == 7) {
                                    showMsg(WelcomeActivity.this, getResources().getString(R.string.login_error_seven));
                                } else {
                                    save("isLogin", "0");//1已经登录了  0未登录
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                        save("isLogin", "0");//1已经登录了  0未登录
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class));
                params.put("password", getGson().fromJson(getSp().getString("password", ""), String.class));
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("userId", ""), String.class))) {
                    //说明存在userId
                    params.put("userId", getGson().fromJson(getSp().getString("userId", ""), String.class));
                } else {
                    params.put("userId", "");
                }
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

    public void saveAccount(Emp emp) {
        //登录成功，绑定百度云推送
        if (StringUtil.isNullOrEmpty(emp.getUserId())) {
            //进行绑定
            PushManager.startWork(getApplicationContext(),
                    PushConstants.LOGIN_TYPE_API_KEY,
                    Utils.getMetaValue(WelcomeActivity.this, "api_key"));
        } else {
            //如果已经绑定，就保存
            save("userId", emp.getUserId());
        }

        // 登陆成功，保存用户名密码
        save("mm_emp_id", emp.getMm_emp_id());
        save("mm_emp_mobile", emp.getMm_emp_mobile());
        save("mm_emp_nickname", emp.getMm_emp_nickname());
        save("mm_emp_password", emp.getMm_emp_password());
        save("mm_emp_cover", emp.getMm_emp_cover());
        save("mm_emp_type", emp.getMm_emp_type());
        save("mm_emp_company", emp.getMm_emp_company());
        save("mm_emp_company_type", emp.getMm_emp_company_type());
        save("mm_emp_company_address", emp.getMm_emp_company_address());
        save("mm_emp_company_detail", emp.getMm_emp_company_detail());
        save("mm_emp_provinceId", emp.getMm_emp_provinceId());
        save("mm_emp_cityId", emp.getMm_emp_cityId());
        save("mm_emp_countryId", emp.getMm_emp_countryId());
        save("mm_emp_regtime", emp.getMm_emp_regtime());
        save("mm_emp_endtime", emp.getMm_emp_endtime());
        save("mm_level_id", emp.getMm_level_id());
        save("mm_emp_beizhu", emp.getMm_emp_beizhu());
        save("mm_emp_msg_num", emp.getMm_emp_msg_num());
        save("is_login", emp.getIs_login());
        save("is_fabugongying", emp.getIs_fabugongying());
        save("is_fabuqiugou", emp.getIs_fabuqiugou());
        save("is_fabuqiugou_see", emp.getIs_fabuqiugou_see());
        save("is_fabugongying_see", emp.getIs_fabugongying_see());
        save("is_see_all", emp.getIs_see_all());
        save("is_use", emp.getIs_use());
        save("is_pic", emp.getIs_pic());
        save("is_video", emp.getIs_video());
        save("is_chengxin", emp.getIs_chengxin());
        save("is_miaomu", emp.getIs_miaomu());
        save("lat", emp.getLat());
        save("lng", emp.getLng());
        save("ischeck", emp.getIscheck());
        save("access_token", emp.getAccess_token());
        save("provinceName", emp.getProvinceName());
        save("levelName", emp.getLevelName());
        save("cityName", emp.getCityName());
        save("areaName", emp.getAreaName());
        save("access_token", emp.getAccess_token());
        save("mm_level_num", emp.getMm_level_num());

        save("isLogin", "1");//1已经登录了  0未登录
        save("is_upate_profile", emp.getIs_upate_profile());//1是否补充资料 0否 1是

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

//    // 根据控件的选择，重新设置定位参数
//    private void initOption() {
//        // 设置是否需要显示地址信息
//        locationOption.setNeedAddress(true);
//        /**
//         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
//         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
//         */
//        locationOption.setGpsFirst(true);
////        String strInterval = etInterval.getText().toString();
////        if (!TextUtils.isEmpty(strInterval)) {
////            // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
//        locationOption.setInterval(Long.valueOf("1000"));
////        }
//
//    }

//    Handler mHandler = new Handler() {
//        public void dispatchMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case Utils.MSG_LOCATION_FINISH:
//                    AMapLocation loc = (AMapLocation) msg.obj;
//                    String result = Utils.getLocationStr(loc);
//                    if ("true".equals(result)) {
//                        //定位成功
//                        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))) {
//                            sendLocation();
//                        }
//                    } else if ("false".equals(result)) {
//
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        ;
//    };
//
//    // 定位监听
//    @Override
//    public void onLocationChanged(AMapLocation loc) {
//        if (null != loc) {
//            Message msg = mHandler.obtainMessage();
//            msg.obj = loc;
//            msg.what = Utils.MSG_LOCATION_FINISH;
//            mHandler.sendMessage(msg);
//        }
//    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                UniversityApplication.lat = String.valueOf(amapLocation.getLatitude());
                UniversityApplication.lng = String.valueOf(amapLocation.getLongitude());
                UniversityApplication.address = amapLocation.getAddress();
                if(!StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng) && !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
                    sendLocation();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }


    void sendLocation() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SEND_LOCATION_BYID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {

                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", (UniversityApplication.lat == null ? "" : UniversityApplication.lat));
                params.put("lng", (UniversityApplication.lng == null ? "" : UniversityApplication.lng));
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
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
    protected void onDestroy() {
        super.onDestroy();
//        if (null != mlocationClient) {
//            /**
//             * 如果AMapLocationClient是在当前Activity实例化的，
//             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//             */
//            mlocationClient.onDestroy();
//            mlocationClient = null;
//        }
    }


}
