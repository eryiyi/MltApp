package com.Lbins.Mlt;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.GuanzhuAreaObjData;
import com.Lbins.Mlt.data.MsgAdData;
import com.Lbins.Mlt.fragment.FindFragment;
import com.Lbins.Mlt.fragment.FirstFragment;
import com.Lbins.Mlt.fragment.FourFragment;
import com.Lbins.Mlt.fragment.SecondFragment;
import com.Lbins.Mlt.module.GuanzhuAreaObj;
import com.Lbins.Mlt.module.MsgAd;
import com.Lbins.Mlt.ui.*;
import com.Lbins.Mlt.util.HttpUtils;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.MainPopMenu;
import com.Lbins.Mlt.widget.MarqueeButton;
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

public class MainActivity extends BaseActivity implements View.OnClickListener, MainPopMenu.OnItemClickListener {
    /**
     * Called when the activity is first created.
     */
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private FirstFragment oneFragment;
    private SecondFragment twoFragment;
    private FindFragment threeFragment;
    private FourFragment fourFragment;

    private ImageView foot_one;
    private ImageView foot_two;
    private ImageView foot_three;
    private ImageView foot_four;

    private long waitTime = 2000;
    private long touchTime = 0;

    //设置底部图标
    Resources res;

    private int index;
    public MainPopMenu mainPopMenu;
    private MarqueeButton btSecond;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        UmengUpdateAgent.update(this);
        //获得上一次登陆时间
        //保存这次登陆时间
        save("denglu_time", System.currentTimeMillis() + "");
        res = getResources();
        fm = getSupportFragmentManager();

        initView();

        switchFragment(R.id.foot_one);

        //控制字体 颜色和大小
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
            UniversityApplication.fontSize = getGson().fromJson(getSp().getString("font_size", ""), String.class);
        }
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
            UniversityApplication.fontColor = getGson().fromJson(getSp().getString("font_color", ""), String.class);
        }

        mainPopMenu = new MainPopMenu(this);
        mainPopMenu.setOnItemClickListener(this);
        getDataMsgAd();
    }

    boolean isMobileNet, isWifiNet;

    @Override
    public void onClick(View v) {
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
        if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) && (v.getId() == R.id.foot_four)) {
            //未登录
            showLogin();
        } else {
            switchFragment(v.getId());
        }

    }

    // 登陆注册选择窗口
    private void showLogin() {
        final Dialog picAddDialog = new Dialog(MainActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(MainActivity.this, R.layout.login_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(getResources().getString(R.string.please_reg_or_login));
        //登陆
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        //注册
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(MainActivity.this, RegistActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        TextView kefuzhongxin = (TextView) picAddInflate.findViewById(R.id.kefuzhongxin);
        kefuzhongxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kefuV = new Intent(MainActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    private void initView() {
        foot_one = (ImageView) this.findViewById(R.id.foot_one);
        foot_two = (ImageView) this.findViewById(R.id.foot_two);
        foot_three = (ImageView) this.findViewById(R.id.foot_three);
        foot_four = (ImageView) this.findViewById(R.id.foot_four);
        foot_one.setOnClickListener(this);
        foot_two.setOnClickListener(this);
        foot_three.setOnClickListener(this);
        foot_four.setOnClickListener(this);
        btSecond = (MarqueeButton) this.findViewById(R.id.btSecond);
    }


    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_one:
                if (oneFragment == null) {
                    oneFragment = new FirstFragment();
                    fragmentTransaction.add(R.id.content_frame, oneFragment);
                } else {
                    fragmentTransaction.show(oneFragment);
                }
                foot_one.setImageResource(R.drawable.tree_toolbar_wanted_p);
                foot_two.setImageResource(R.drawable.tree_toolbar_sell);
                foot_three.setImageResource(R.drawable.tree_toolbar_star);
                foot_four.setImageResource(R.drawable.tree_toolbar_user);

                break;
            case R.id.foot_two:
                if (twoFragment == null) {
                    twoFragment = new SecondFragment();
                    fragmentTransaction.add(R.id.content_frame, twoFragment);
                } else {
                    fragmentTransaction.show(twoFragment);
                }

                foot_one.setImageResource(R.drawable.tree_toolbar_wanted);
                foot_two.setImageResource(R.drawable.tree_toolbar_sell_p);
                foot_three.setImageResource(R.drawable.tree_toolbar_star);
                foot_four.setImageResource(R.drawable.tree_toolbar_user);
                break;
            case R.id.foot_three:
                if (threeFragment == null) {
                    threeFragment = new FindFragment();
                    fragmentTransaction.add(R.id.content_frame, threeFragment);
                } else {
                    fragmentTransaction.show(threeFragment);
                }
                foot_one.setImageResource(R.drawable.tree_toolbar_wanted);
                foot_two.setImageResource(R.drawable.tree_toolbar_sell);
                foot_three.setImageResource(R.drawable.tree_toolbar_star_p);
                foot_four.setImageResource(R.drawable.tree_toolbar_user);
                break;
            case R.id.foot_four:
                if (fourFragment == null) {
                    fourFragment = new FourFragment();
                    fragmentTransaction.add(R.id.content_frame, fourFragment);
                } else {
                    fragmentTransaction.show(fourFragment);
                }
                foot_one.setImageResource(R.drawable.tree_toolbar_wanted);
                foot_two.setImageResource(R.drawable.tree_toolbar_sell);
                foot_three.setImageResource(R.drawable.tree_toolbar_star);
                foot_four.setImageResource(R.drawable.tree_toolbar_user_p);
                break;

        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (oneFragment != null) {
            ft.hide(oneFragment);
        }
        if (twoFragment != null) {
            ft.hide(twoFragment);
        }
        if (threeFragment != null) {
            ft.hide(threeFragment);
        }
        if (fourFragment != null) {
            ft.hide(fourFragment);
        }
    }


    //弹出顶部主菜单
    public void onTopMenuPopupButtonClick(View view) {
        mainPopMenu.showAsDropDown(view);
    }

    @Override
    public void onItemClick(int index) {
        switch (index) {
            case 0:
                //发布信息
                if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class)))) {
                    //未登录
                    showLogin();
                } else {
                    if ("0".equals(getGson().fromJson(getSp().getString("is_upate_profile", ""), String.class))) {
                        showUpdateProfile();
                    } else {
                        Intent addV = new Intent(MainActivity.this, AddRecordActivity.class);
                        startActivity(addV);
                    }
                }
                break;
            case 1:
                //一键关注区域
                if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class)))) {
                    //未登录
                    showLogin();
                } else {
                    getGuanzhuArea();
                }

                break;
        }
    }

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
                                            GuanzhuAreaObj guanzhuAreaObj = listgz.get(0);
                                            if (guanzhuAreaObj != null) {
                                                if ("0".equals(guanzhuAreaObj.getIscheck())) {
                                                    showMsg(MainActivity.this, getResources().getString(R.string.also_area_please_wait));
                                                } else if ("1".equals(guanzhuAreaObj.getIscheck())) {
                                                    Intent intent = new Intent(MainActivity.this, RecordGzActivity.class);
                                                    intent.putExtra("guanzhuAreaObj", guanzhuAreaObj);
                                                    startActivity(intent);
                                                } else if ("2".equals(guanzhuAreaObj.getIscheck())) {
                                                    showMsg(MainActivity.this, getResources().getString(R.string.also_area_please_wait1));
                                                } else {
                                                    showMsg(MainActivity.this, getResources().getString(R.string.also_area_please_wait2));
                                                    Intent guanzhuV = new Intent(MainActivity.this, SetGuanzhuActivity.class);
                                                    startActivity(guanzhuV);
                                                }
                                            }
                                        } else {
                                            showMsg(MainActivity.this, getResources().getString(R.string.also_area_please_wait2));
                                            Intent guanzhuV = new Intent(MainActivity.this, SetGuanzhuActivity.class);
                                            startActivity(guanzhuV);
                                        }
                                    } else {
                                        showMsg(MainActivity.this, getResources().getString(R.string.also_area_please_wait2));
                                        Intent guanzhuV = new Intent(MainActivity.this, SetGuanzhuActivity.class);
                                        startActivity(guanzhuV);
                                    }
                                } else {
                                    showMsg(MainActivity.this, getResources().getString(R.string.also_area_please_wait2));
                                    Intent guanzhuV = new Intent(MainActivity.this, SetGuanzhuActivity.class);
                                    startActivity(guanzhuV);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    // 补充资料窗口
    private void showUpdateProfile() {
        final Dialog picAddDialog = new Dialog(MainActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(MainActivity.this, R.layout.update_profile_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        //确定
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateV = new Intent(MainActivity.this, UpdateProfiletActivity.class);
                startActivity(updateV);
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

    //--------附近商家用，附近服务用--------
//    List<NearbyDistanceObj> nearbyDistanceObjs = new ArrayList<NearbyDistanceObj>();
//
//    void getDistance() {
//        //获得附近距离--设置好的
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_NEARBY_DISTANCE_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code1 = jo.getString("code");
//                                if (Integer.parseInt(code1) == 200) {
//                                    NearbyDistanceObjData data = getGson().fromJson(s, NearbyDistanceObjData.class);
//                                    nearbyDistanceObjs = data.getData();
//                                    if (nearbyDistanceObjs != null && nearbyDistanceObjs.size() > 0) {
//                                        NearbyDistanceObj nearbyDistanceObj = nearbyDistanceObjs.get(0);
//                                        //保存设置好的距离
//                                        save("mm_distance", nearbyDistanceObj.getMm_distance());
//                                        save("mm_nearby_distance_id", nearbyDistanceObj.getMm_nearby_distance_id());
//                                    }
//
//                                } else {
//
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
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

    public static List<MsgAd> listAd = new ArrayList<MsgAd>();
    void getDataMsgAd(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.MANAGER_MSG_AD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MsgAdData data = getGson().fromJson(s, MsgAdData.class);
                            if (Integer.parseInt(data.getCode()) == 200) {
                                listAd  = data.getData();
                                StringBuffer strb = new StringBuffer();
                                for(MsgAd msgAd: MainActivity.listAd){
                                    strb = strb.append(msgAd.getMsg_ad_title());
                                }
                                btSecond.setText(strb.toString());
                            } else {
                                Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
