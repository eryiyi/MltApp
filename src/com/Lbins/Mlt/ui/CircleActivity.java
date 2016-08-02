package com.Lbins.Mlt.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.AdViewPagerAdapter;
import com.Lbins.Mlt.adapter.AnimateFirstDisplayListener;
import com.Lbins.Mlt.adapter.ItemTopAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.RecordMsg;
import com.Lbins.Mlt.data.AdObjData;
import com.Lbins.Mlt.data.PaihangObjData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
import com.Lbins.Mlt.module.AdObj;
import com.Lbins.Mlt.module.PaihangObj;
import com.Lbins.Mlt.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/7/3.
 */
public class CircleActivity extends BaseActivity implements OnClickContentItemListener,View.OnClickListener {
    private PullToRefreshListView lstv;
    private ItemTopAdapter adapter;
    private List<PaihangObj> lists = new ArrayList<PaihangObj>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private ImageView no_data;

    private LinearLayout headLiner;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    //导航
//    private ViewPager viewpager;
//    private AdViewPagerAdapter adapterAd;
//    private LinearLayout viewGroup;
//    private ImageView dot, dots[];
//    private Runnable runnable;
//    private int autoChangeTime = 5000;
//    private List<AdObj> listsAd = new ArrayList<AdObj>();

    private EditText keyword;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity);
        registerBoradcastReceiver();
        initView();
        initData();
//        getAd();
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
            IS_REFRESH = true;
            pageIndex = 1;
            if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                initData();
            } else {
                lstv.onRefreshComplete();
                //未登录
                showLogin();
            }
        }
    };

    void initView() {
        title = (TextView) this.findViewById(R.id.title);
        title.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        headLiner = (LinearLayout) LayoutInflater.from(CircleActivity.this).inflate(R.layout.ad_header, null);
        TextView btn_top = (TextView) headLiner.findViewById(R.id.btn_top);
        btn_top.setVisibility(View.VISIBLE);
        btn_top.setOnClickListener(this);
        no_data = (ImageView) this.findViewById(R.id.no_data);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        ListView listView = lstv.getRefreshableView();

        keyword = (EditText) headLiner.findViewById(R.id.keyword);
        keyword.addTextChangedListener(watcher);

        listView.addHeaderView(headLiner);
        adapter = new ItemTopAdapter(lists, CircleActivity.this);

        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lists.size() > position - 2) {
                    recordVO = lists.get(position - 2);
                    Intent mineV = new Intent(CircleActivity.this, ProfileActivity.class);
                    mineV.putExtra("id", recordVO.getMm_emp_id());
                    startActivity(mineV);
                }
            }
        });
        adapter.setOnClickContentItemListener(this);
        no_data.setOnClickListener(this);
    }

    PaihangObj recordVO;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        String str = (String) object;
        if ("000".equals(str)) {
            switch (flag) {
                case 0:
//                    AdObj adObj = listsAd.get(position);
//                    Intent webV = new Intent(CircleActivity.this, WebViewActivity.class);
//                    webV.putExtra("strurl", adObj.getMm_ad_url() == null ? "" : adObj.getMm_ad_url());
//                    startActivity(webV);
                    break;
            }
        }
        if ("111".equals(str)) {
            switch (flag) {
                case 2:
                case 4: {
                    //头像
                    recordVO = lists.get(position);
                    Intent mineV = new Intent(CircleActivity.this, ProfileActivity.class);
                    mineV.putExtra("id", recordVO.getMm_emp_id());
                    startActivity(mineV);
                }
                break;
                case 3:
                    //电话
                    recordVO = lists.get(position);
                    if (recordVO != null && !StringUtil.isNullOrEmpty(recordVO.getMm_emp_mobile())) {
                        showTel(recordVO.getMm_emp_mobile(), recordVO.getMm_emp_nickname());
                    } else {
                        //
                        Toast.makeText(CircleActivity.this, R.string.no_tel, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 5:
                    //导航
                {
                    recordVO = lists.get(position);
                    if (!StringUtil.isNullOrEmpty(recordVO.getLat()) && !StringUtil.isNullOrEmpty(recordVO.getLng())) {
                        //开始导航
                        if (!StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)) {
                            Intent naviV = new Intent(CircleActivity.this, GPSNaviActivity.class);
                            naviV.putExtra("lat_end", recordVO.getLat());
                            naviV.putExtra("lng_end", recordVO.getLng());
                            startActivity(naviV);
                        } else {
                            //如果定位为null说明没开导航 需要重新打开GPS 定位
                            Toast.makeText(CircleActivity.this, getResources().getString(R.string.please_open_gps), Toast.LENGTH_SHORT).show();
                            //打开GPS
                            Intent gpsIntent = new Intent();
                            gpsIntent.setClassName("com.android.settings",
                                    "com.android.settings.widget.SettingsAppWidgetProvider");
                            gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
                            gpsIntent.setData(Uri.parse("custom:3"));
                            try {
                                PendingIntent.getBroadcast(CircleActivity.this, 0, gpsIntent, 0).send();
                            }
                            catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(CircleActivity.this, getResources().getString(R.string.no_location_lat_lng), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }

    }

    private RecordMsg recordMsgTmp;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 拨打电话窗口
    private void showTel(final String tel, String name) {
        final Dialog picAddDialog = new Dialog(CircleActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(CircleActivity.this, R.layout.tel_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(tel + " " + name);
        //提交
        btn_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String contreport = jubao_cont.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                startActivity(intent);
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

    void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_PAIHANG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    PaihangObjData data = getGson().fromJson(s, PaihangObjData.class);
                                    if (IS_REFRESH) {
                                        lists.clear();
                                    }
                                    lists.addAll(data.getData());
                                    lstv.onRefreshComplete();
                                    adapter.notifyDataSetChanged();
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(CircleActivity.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(CircleActivity.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else {
                                    Toast.makeText(CircleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (lists.size() > 0) {
                                no_data.setVisibility(View.GONE);
                            } else {
                                no_data.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(CircleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("index", String.valueOf(pageIndex));
                params.put("is_del", "0");
                params.put("size", "10");
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
                }
                if (!StringUtil.isNullOrEmpty(keyword.getText().toString())) {
                    params.put("keyword", keyword.getText().toString());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.no_data:
                IS_REFRESH = true;
                pageIndex = 1;
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    initData();
                } else {
                    lstv.onRefreshComplete();
                    //未登录
                    showLogin();
                }
                break;
            case R.id.btn_top:
                Intent intent = new Intent(CircleActivity.this, SelectTelActivity.class);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.title:
            {
                //返回顶部
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }
                break;
        }
    }
    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("change_color_size")) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("change_color_size");//
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    // 登陆注册选择窗口
    private void showLogin() {
        final Dialog picAddDialog = new Dialog(CircleActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(CircleActivity.this, R.layout.login_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(getResources().getString(R.string.please_reg_or_login));
        //登陆
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(CircleActivity.this, LoginActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        //注册
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(CircleActivity.this, RegistActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        TextView kefuzhongxin = (TextView) picAddInflate.findViewById(R.id.kefuzhongxin);
        kefuzhongxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kefuV = new Intent(CircleActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

//    private void initViewPager() {
//        adapterAd = new AdViewPagerAdapter(CircleActivity.this);
//        adapterAd.change(listsAd);
//        adapterAd.setOnClickContentItemListener(this);
//        viewpager = (ViewPager) headLiner.findViewById(R.id.viewpager);
//        viewpager.setAdapter(adapterAd);
//        viewpager.setOnPageChangeListener(myOnPageChangeListener);
//        initDot();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                int next = viewpager.getCurrentItem() + 1;
//                if (next >= adapterAd.getCount()) {
//                    next = 0;
//                }
//                viewHandler.sendEmptyMessage(next);
//            }
//        };
//        viewHandler.postDelayed(runnable, autoChangeTime);
//    }
//
//
//    // 初始化dot视图
//    private void initDot() {
//        viewGroup = (LinearLayout) headLiner.findViewById(R.id.viewGroup);
//
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                20, 20);
//        layoutParams.setMargins(4, 3, 4, 3);
//
//        dots = new ImageView[adapterAd.getCount()];
//        for (int i = 0; i < adapterAd.getCount(); i++) {
//            dot = new ImageView(CircleActivity.this);
//            dot.setLayoutParams(layoutParams);
//            dots[i] = dot;
//            dots[i].setTag(i);
//            dots[i].setOnClickListener(onClick);
//
//            if (i == 0) {
//                dots[i].setBackgroundResource(R.drawable.dotc);
//            } else {
//                dots[i].setBackgroundResource(R.drawable.dotn);
//            }
//
//            viewGroup.addView(dots[i]);
//        }
//    }
//
//    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//
//        @Override
//        public void onPageSelected(int arg0) {
//            setCurDot(arg0);
//            viewHandler.removeCallbacks(runnable);
//            viewHandler.postDelayed(runnable, autoChangeTime);
//        }
//
//    };
//    // 实现dot点击响应功能,通过点击事件更换页面
//    View.OnClickListener onClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            int position = (Integer) v.getTag();
//            setCurView(position);
//        }
//
//    };
//
//    /**
//     * 设置当前的引导页
//     */
//    private void setCurView(int position) {
//        if (position < 0 || position > adapterAd.getCount()) {
//            return;
//        }
//        viewpager.setCurrentItem(position);
////        if (!StringUtil.isNullOrEmpty(lists.get(position).getNewsTitle())){
////            titleSlide = lists.get(position).getNewsTitle();
////            if(titleSlide.length() > 13){
////                titleSlide = titleSlide.substring(0,12);
////                article_title.setText(titleSlide);//当前新闻标题显示
////            }else{
////                article_title.setText(titleSlide);//当前新闻标题显示
////            }
////        }
//
//    }
//
//    /**
//     * 选中当前引导小点
//     */
//    private void setCurDot(int position) {
//        for (int i = 0; i < dots.length; i++) {
//            if (position == i) {
//                dots[i].setBackgroundResource(R.drawable.dotc);
//            } else {
//                dots[i].setBackgroundResource(R.drawable.dotn);
//            }
//        }
//    }
//
//    /**
//     * 每隔固定时间切换广告栏图片
//     */
//    @SuppressLint("HandlerLeak")
//    private final Handler viewHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            setCurView(msg.what);
//        }
//
//    };
//
//    private void getAd() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_AD_LOGIN_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code = jo.getString("code");
//                                if (Integer.parseInt(code) == 200) {
//                                    AdObjData data = getGson().fromJson(s, AdObjData.class);
//                                    listsAd.clear();
//                                    if (data != null && data.getData().size() > 0) {
//                                        listsAd.addAll(data.getData());
//                                    }
//                                    initViewPager();
//                                } else {
//                                    Toast.makeText( CircleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        } else {
//                            Toast.makeText( CircleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Toast.makeText(CircleActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("mm_ad_type", "1");
//                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class))) {
//                    params.put("mm_emp_provinceId", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
//                }
//                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class))) {
//                    params.put("mm_emp_cityId", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
//                }
//                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
//                    params.put("mm_emp_countryId", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
//                }
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

}
