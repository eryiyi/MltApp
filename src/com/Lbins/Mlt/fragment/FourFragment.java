package com.Lbins.Mlt.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.AnimateFirstDisplayListener;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.adapter.ViewPagerAdapter;
import com.Lbins.Mlt.base.BaseFragment;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.EmpAdObjData;
import com.Lbins.Mlt.data.FavourCountData;
import com.Lbins.Mlt.data.NetwwwObjData;
import com.Lbins.Mlt.module.EmpAdObj;
import com.Lbins.Mlt.module.NetwwwObj;
import com.Lbins.Mlt.ui.*;
import com.Lbins.Mlt.util.CompressPhotoUtil;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.SelectPhoPopWindow;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/22.
 */
public class FourFragment extends BaseFragment implements View.OnClickListener, OnClickContentItemListener {
    private View view;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    Resources res;
    //导航
    private ViewPager viewpager;
    private ViewPagerAdapter adapter;
    private LinearLayout viewGroup;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<EmpAdObj> lists = new ArrayList<EmpAdObj>();

    private ImageView head;
    private TextView nickname;
    private TextView vipType;
    private TextView regTime;
    private TextView regAddress;

    private LinearLayout login_one;

    private TextView count_favour;
    private TextView btn_chengxin_xiehui;
    private ImageView img_xiehui;
    private ImageView img_chengxin;


    private String txpic = "";
    private SelectPhoPopWindow deleteWindow;
    AsyncHttpClient client = new AsyncHttpClient();
    private String pics = "";
    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.four_fragment, null);
        res = getActivity().getResources();
        initView();
        getAd();
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getFavourCount();
    }

    private void initData() {
        imageLoader.displayImage(getGson().fromJson(getSp().getString("mm_emp_cover", ""), String.class), head, UniversityApplication.txOptions, animateFirstListener);

        String strName = getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class);
        if ("0".equals(getGson().fromJson(getSp().getString("mm_emp_type", ""), String.class))) {
            strName += " " + getResources().getString(R.string.miaomujingying);
        }
        if ("1".equals(getGson().fromJson(getSp().getString("mm_emp_type", ""), String.class))) {
            strName += " " + getResources().getString(R.string.miaomuemp);
        }
        nickname.setText(strName);

        String vipTypeStr = "会员等级:";
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("levelName", ""), String.class))) {
            vipTypeStr += getGson().fromJson(getSp().getString("levelName", ""), String.class);
        }
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_endtime", ""), String.class))) {
            vipTypeStr += "到期日期:" + getGson().fromJson(getSp().getString("mm_emp_endtime", ""), String.class);
        }
        vipType.setText(vipTypeStr);
        regTime.setText("注册日期:" + getGson().fromJson(getSp().getString("mm_emp_regtime", ""), String.class));
        regAddress.setText(getGson().fromJson(getSp().getString("provinceName", ""), String.class) + getGson().fromJson(getSp().getString("cityName", ""), String.class) + getGson().fromJson(getSp().getString("areaName", ""), String.class));
        //判断是否苗木协会和苗木会员
        if ("1".equals(getGson().fromJson(getSp().getString("is_chengxin", ""), String.class))) {//is_miaomu
            img_chengxin.setVisibility(View.VISIBLE);
        } else {
            img_chengxin.setVisibility(View.GONE);
        }
        if ("1".equals(getGson().fromJson(getSp().getString("is_miaomu", ""), String.class))) {
            img_xiehui.setVisibility(View.VISIBLE);
        } else {
            img_xiehui.setVisibility(View.GONE);
        }
        if ("0".equals(getGson().fromJson(getSp().getString("is_miaomu", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("is_chengxin", ""), String.class))) {
            btn_chengxin_xiehui.setVisibility(View.VISIBLE);
        } else {
            btn_chengxin_xiehui.setVisibility(View.GONE);
        }
    }

    void initView() {
        head = (ImageView) view.findViewById(R.id.head);
        nickname = (TextView) view.findViewById(R.id.nickname);
        vipType = (TextView) view.findViewById(R.id.vipType);
        regTime = (TextView) view.findViewById(R.id.regTime);
        regAddress = (TextView) view.findViewById(R.id.regAddress);
        btn_chengxin_xiehui = (TextView) view.findViewById(R.id.btn_chengxin_xiehui);//申请苗木会员加入苗木协会
        btn_chengxin_xiehui.setOnClickListener(this);
        img_chengxin = (ImageView) view.findViewById(R.id.img_chengxin);
        img_xiehui = (ImageView) view.findViewById(R.id.img_xiehui);


        view.findViewById(R.id.relate_set).setOnClickListener(this);
        view.findViewById(R.id.relate_shop).setOnClickListener(this);
        view.findViewById(R.id.relate_bank).setOnClickListener(this);
        view.findViewById(R.id.relate_work).setOnClickListener(this);
        view.findViewById(R.id.relate_wuliu).setOnClickListener(this);
        view.findViewById(R.id.relate_jiajie).setOnClickListener(this);
        view.findViewById(R.id.relate_msg).setOnClickListener(this);
        view.findViewById(R.id.realte_diaoche).setOnClickListener(this);
        view.findViewById(R.id.relate_about).setOnClickListener(this);
        view.findViewById(R.id.realte_ziliao).setOnClickListener(this);
        view.findViewById(R.id.relate_updatepwr).setOnClickListener(this);
        view.findViewById(R.id.relate_suggest).setOnClickListener(this);
        view.findViewById(R.id.relate_vip).setOnClickListener(this);
        view.findViewById(R.id.relate_nearby).setOnClickListener(this);
        view.findViewById(R.id.relate_favour).setOnClickListener(this);
        view.findViewById(R.id.relate_kefu).setOnClickListener(this);
        view.findViewById(R.id.relate_more_area).setOnClickListener(this);
        view.findViewById(R.id.relate_weixinkefu).setOnClickListener(this);
        view.findViewById(R.id.relate_zhaoshang).setOnClickListener(this);
        view.findViewById(R.id.relate_notice).setOnClickListener(this);
        view.findViewById(R.id.relate_erweima).setOnClickListener(this);
        view.findViewById(R.id.relate_map).setOnClickListener(this);
        view.findViewById(R.id.realte_xiecheng).setOnClickListener(this);
        view.findViewById(R.id.addLocation).setOnClickListener(this);
        head.setOnClickListener(this);
        login_one = (LinearLayout) view.findViewById(R.id.login_one);
        login_one.setVisibility(View.VISIBLE);
        count_favour = (TextView) view.findViewById(R.id.count_favour);
        count_favour.setVisibility(View.GONE);
    }

    private void initViewPager() {
        adapter = new ViewPagerAdapter(getActivity());
        adapter.change(lists);
        adapter.setOnClickContentItemListener(this);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(myOnPageChangeListener);
        initDot();
        runnable = new Runnable() {
            @Override
            public void run() {
                int next = viewpager.getCurrentItem() + 1;
                if (next >= adapter.getCount()) {
                    next = 0;
                }
                viewHandler.sendEmptyMessage(next);
            }
        };
        viewHandler.postDelayed(runnable, autoChangeTime);
    }


    // 初始化dot视图
    private void initDot() {
        viewGroup = (LinearLayout) view.findViewById(R.id.viewGroup);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapter.getCount()];
        for (int i = 0; i < adapter.getCount(); i++) {

            dot = new ImageView(getActivity());
            dot.setLayoutParams(layoutParams);
            dots[i] = dot;
            dots[i].setTag(i);
            dots[i].setOnClickListener(onClick);

            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }

            viewGroup.addView(dots[i]);
        }
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot(arg0);
            viewHandler.removeCallbacks(runnable);
            viewHandler.postDelayed(runnable, autoChangeTime);
        }

    };
    // 实现dot点击响应功能,通过点击事件更换页面
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            setCurView(position);
        }

    };

    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position > adapter.getCount()) {
            return;
        }
        viewpager.setCurrentItem(position);
//        if (!StringUtil.isNullOrEmpty(lists.get(position).getNewsTitle())){
//            titleSlide = lists.get(position).getNewsTitle();
//            if(titleSlide.length() > 13){
//                titleSlide = titleSlide.substring(0,12);
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }else{
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }
//        }

    }

    /**
     * 选中当前引导小点
     */
    private void setCurDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (position == i) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }
        }
    }

    /**
     * 每隔固定时间切换广告栏图片
     */
    @SuppressLint("HandlerLeak")
    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurView(msg.what);
        }

    };

    EmpAdObj slidePic;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        slidePic = lists.get(position);
        switch (flag) {
            case 0:
//                Intent webView = new Intent(getActivity(), WebViewActivity.class);
//                webView.putExtra("strurl", slidePic.getMm_emp_ad_url());
//                startActivity(webView);
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relate_set:
                Intent setV = new Intent(getActivity(), SettingActivity.class);
                startActivity(setV);
                break;
            case R.id.relate_shop:
                //商店
            {
                Intent shopV = new Intent(getActivity(), FourFuwuActivity.class);
                shopV.putExtra("mm_fuwu_type", "0");
                startActivity(shopV);
            }
            break;
            case R.id.relate_bank:
                //银行
            {
                getNetobj("2");
            }
            break;
            case R.id.relate_work:
                //装车工人
            {
                Intent shopV = new Intent(getActivity(), FourFuwuActivity.class);
                shopV.putExtra("mm_fuwu_type", "1");
                startActivity(shopV);
            }
            break;
            case R.id.relate_wuliu:
                //物流中心
            {
                Intent shopV = new Intent(getActivity(), FourFuwuActivity.class);
                shopV.putExtra("mm_fuwu_type", "2");
                startActivity(shopV);
            }
            break;
            case R.id.relate_jiajie:
                //嫁接
            {
                Intent shopV = new Intent(getActivity(), FourFuwuActivity.class);
                shopV.putExtra("mm_fuwu_type", "3");
                startActivity(shopV);
            }
            break;
            case R.id.relate_msg:
                //短信
            {
                getNetobj("1");
            }
            break;
            case R.id.realte_diaoche:
                //调车
            {
                Intent shopV = new Intent(getActivity(), FourFuwuActivity.class);
                shopV.putExtra("mm_fuwu_type", "4");
                startActivity(shopV);
            }
            break;
            case R.id.relate_about:
                //关于我们
            {
                Intent aboutV = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(aboutV);
            }
            break;
            case R.id.realte_ziliao:
                //用户资料
            {
                Intent profileV = new Intent(getActivity(), ProfileActivity.class);
                profileV.putExtra("id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                startActivity(profileV);
            }
            break;
            case R.id.relate_updatepwr:
                //修改密码
            {
                Intent updateP = new Intent(getActivity(), FindPwrActivity.class);
                startActivity(updateP);
            }
            break;
            case R.id.relate_suggest:
                //意见反馈
            {
                Intent suggestV = new Intent(getActivity(), AddSuggestActivity.class);
                startActivity(suggestV);
            }
            break;
            case R.id.relate_vip: {
                //购买VIP
//                Intent vipV = new Intent(getActivity(), VipActivity.class);
//                startActivity(vipV);
                Toast.makeText(getActivity(), R.string.no_open, Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.relate_nearby: {
                //附近商家
                Intent nearbyV = new Intent(getActivity(), NearbyActivity.class);
                startActivity(nearbyV);
            }
            break;
            case R.id.head:
                //修改头像
            {
//                if ("0".equals(getGson().fromJson(getSp().getString("is_cover", ""), String.class))) {
                    //如果是0 默认允许
                    ShowPickDialog();
//                } else {
//                    Toast.makeText(getActivity(), "修改头像，请联系客服开通权限！", Toast.LENGTH_SHORT).show();
//                }
            }
            break;
            case R.id.relate_favour: {
                //我的收藏
                Intent mineFavourV = new Intent(getActivity(), MineFavour.class);
                startActivity(mineFavourV);
            }
            break;
            case R.id.relate_kefu:
            case R.id.btn_chengxin_xiehui: {
                //客服中心
                Intent kefuV = new Intent(getActivity(), SelectTelActivity.class);
                startActivity(kefuV);
            }
            break;
            case R.id.relate_more_area: {
                //设置关注区域
                Intent guanzhuV = new Intent(getActivity(), SetGuanzhuActivity.class);
                startActivity(guanzhuV);
            }
            break;
            case R.id.relate_weixinkefu:
                Intent weixinV = new Intent(getActivity(), WeixinKefuActivity.class);
                startActivity(weixinV);
                break;
            case R.id.relate_zhaoshang:
                Toast.makeText(getActivity(), R.string.no_open, Toast.LENGTH_SHORT).show();
                break;
            case R.id.relate_notice: {
                //公告
                Intent noticeV = new Intent(getActivity(), NoticeActivity.class);
                startActivity(noticeV);
            }
            break;
            case R.id.relate_erweima:
                //
                Intent intentErweima = new Intent(getActivity(), ErweimaActivity.class);
                startActivity(intentErweima);
                break;
            case R.id.relate_map: {
                //地图
//                Intent mapV = new Intent(getActivity(), WebViewActivity.class);
//                mapV.putExtra("strurl", "http://map.baidu.com/mobile/webapp/index/index");
//                startActivity(mapV);

                final Uri uri = Uri.parse("http://map.baidu.com/mobile/webapp/index/index");
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
            break;
            case R.id.realte_xiecheng: {
                //汽车火车飞机时刻表
//                Intent xiechengV = new Intent(getActivity(), WebViewActivity.class);
//                xiechengV.putExtra("strurl", "http://m.ctrip.com/html5");
//                startActivity(xiechengV);

                final Uri uri = Uri.parse("http://m.ctrip.com/html5");
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
            break;
            case R.id.addLocation: {
                Intent addLocationV = new Intent(getActivity(), AddCompanyLocationActivity.class);
                startActivity(addLocationV);
            }
            break;
        }
    }

    private void getAd() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_AD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    EmpAdObjData data = getGson().fromJson(s, EmpAdObjData.class);
                                    lists.clear();
                                    if (data != null && data.getData().size() > 0) {
                                        lists.addAll(data.getData());
                                    }
                                    if (lists.size() == 0) {
                                        lists.add(new EmpAdObj(InternetURL.INTERNAL + "/upload/20160313/1457875390482.jpg", InternetURL.INTERNAL + "/html/download.html"));
                                    }
                                    initViewPager();
                                } else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("sure_quite")) {
                login_one.setVisibility(View.GONE);
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("sure_quite");
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    void getFavourCount() {
        //
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_FAVOUR_COUNT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    FavourCountData data = getGson().fromJson(s, FavourCountData.class);
                                    if (data != null && data.getData() != null) {
                                        count_favour.setVisibility(View.VISIBLE);
                                        String count = data.getData() == "" ? "0" : data.getData();
                                        int countInt = Integer.parseInt(count);
                                        if (countInt > 99) {
                                            count_favour.setText("99+");
                                        } else {
                                            count_favour.setText(count);
                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    // 选择相册，相机
    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(getActivity(), itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(getActivity().findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//        deleteWindow.setBackgroundDrawable(new ColorDrawable(0x55000000));

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "ppCover.jpg")));
                    startActivityForResult(camera, 2);
                }
                break;
                case R.id.mapstorage: {
                    Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
                    mapstorage.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(mapstorage, 1);
                }
                break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/ppCover.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            if (photo != null) {
                pics = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                head.setImageBitmap(photo);
                //保存头像
                uploadCover();
            }
        }
    }

    //上传到七牛云存贮
    void uploadCover() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("space", InternetURL.QINIU_SPACE);
        RequestParams params = new RequestParams(map);
        client.get(InternetURL.UPLOAD_TOKEN, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String token = response.getString("data");
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(StringUtil.getBytes(pics), StringUtil.getUUID(), token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    publishAll(key);
                                }
                            }, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    //更新数据库用户头像

    //更新
    private void publishAll(final String uploadpic) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_COVER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    save("mm_emp_cover", InternetURL.QINIU_URL + uploadpic);
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
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                params.put("mm_emp_cover", uploadpic);
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



    private void getNetobj(final String type) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_NET_BY_TYPE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {

                                    NetwwwObjData data = getGson().fromJson(s, NetwwwObjData.class);
                                    List<NetwwwObj> list = data.getData();
                                    if(list != null && list.size() > 0){
                                        NetwwwObj netwwwObj = list.get(0);
                                        if(netwwwObj != null && "1".equals(type)){
                                            //短信平台
                                            final Uri uri = Uri.parse(netwwwObj.getMm_net_url());
                                            final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(it);
                                        }
                                        if(netwwwObj != null && "2".equals(type)){
                                            //商业银行
                                            final Uri uri = Uri.parse(netwwwObj.getMm_net_url());
                                            final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(it);
                                        }
                                    }

                                } else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_net_type", type);
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
