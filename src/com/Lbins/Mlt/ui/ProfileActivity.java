package com.Lbins.Mlt.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.AnimateFirstDisplayListener;
import com.Lbins.Mlt.adapter.ItemRecordAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.adapter.ViewPagerAdapter;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.DBHelper;
import com.Lbins.Mlt.dao.RecordMsg;
import com.Lbins.Mlt.data.EmpAdObjData;
import com.Lbins.Mlt.data.EmpData;
import com.Lbins.Mlt.data.RecordData;
import com.Lbins.Mlt.module.Emp;
import com.Lbins.Mlt.module.EmpAdObj;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.ContentListView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/19.
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener, ContentListView.OnRefreshListener,
        ContentListView.OnLoadListener, OnClickContentItemListener {
    private ContentListView lstv;
    private int pageIndex = 1;
    private ItemRecordAdapter adapter;
    private List<RecordMsg> lists = new ArrayList<RecordMsg>();
    private String type = "";

    //header
    LinearLayout headLiner;
    private ImageView head;//头像
    private ImageView adPic;
    private TextView content;//公司简介
    private TextView nickname;//姓名
    private TextView address;//公司地址

    private TextView back;
    private Button companyUrl;
    private Button updateBtn;//更改个人信息
    private String id;
    private Emp emp;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    //导航
    private ViewPager viewpager;
    private ViewPagerAdapter adapterAd;
    private LinearLayout viewGroup;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<EmpAdObj> listsAd = new ArrayList<EmpAdObj>();

    /**
     * 加载数据监听实现
     */
    @Override
    public void onLoad() {
        pageIndex++;
        loadData(ContentListView.LOAD);
    }

    /**
     * 刷新数据监听实现
     */
    @Override
    public void onRefresh() {
        pageIndex = 1;
        loadData(ContentListView.REFRESH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        id = getIntent().getExtras().getString("id");

        lstv = (ContentListView) this.findViewById(R.id.lstv);
        headLiner = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.profile_header, null);
        this.findViewById(R.id.nav).setOnClickListener(this);
        head = (ImageView) headLiner.findViewById(R.id.head);
        head.setOnClickListener(this);
        content = (TextView) headLiner.findViewById(R.id.content);
        adPic = (ImageView) headLiner.findViewById(R.id.adPic);
        adPic.setOnClickListener(this);
        companyUrl = (Button) headLiner.findViewById(R.id.companyUrl);
        updateBtn = (Button) headLiner.findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kefuV = new Intent(ProfileActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
            }
        });
        nickname = (TextView) headLiner.findViewById(R.id.nickname);
        address = (TextView) headLiner.findViewById(R.id.address);
        headLiner.findViewById(R.id.qiugou).setOnClickListener(this);
        headLiner.findViewById(R.id.gongying).setOnClickListener(this);
        back = (TextView) this.findViewById(R.id.back);
        back.setOnClickListener(this);
        adapter = new ItemRecordAdapter(lists, ProfileActivity.this);
        adapter.setOnClickContentItemListener(this);
        lstv.setAdapter(adapter);
        lstv.addHeaderView(headLiner);
        lstv.setOnRefreshListener(this);
        lstv.setOnLoadListener(this);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 1 && lists.size() > i - 2) {
                    lists.get(i - 2).setIs_read("1");
                    adapter.notifyDataSetChanged();
                    recordVO = lists.get(i - 2);
                    recordVO.setIs_read("1");
                    DBHelper.getInstance(ProfileActivity.this).updateRecord(recordVO);
                }

            }
        });

        getProfile();
        loadData(ContentListView.REFRESH);

        getAd();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.qiugou:
                type = "0";
                pageIndex = 1;
                loadData(ContentListView.REFRESH);
                break;
            case R.id.gongying:
                type = "1";
                pageIndex = 1;
                loadData(ContentListView.REFRESH);
                break;
            case R.id.head:
                //点击了头像，放大
                final String[] picUrls = {emp.getMm_emp_cover()};
                Intent intent = new Intent(this, GalleryUrlActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.putExtra(Constants.IMAGE_URLS, picUrls);
                intent.putExtra(Constants.IMAGE_POSITION, 0);
                startActivity(intent);
                break;
            case R.id.nav:
                //导航
            {
                if (!StringUtil.isNullOrEmpty(emp.getLat_company()) && !StringUtil.isNullOrEmpty(emp.getLng_company())) {
                    //开始导航
                    if (!StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)) {
                        Intent naviV = new Intent(ProfileActivity.this, GPSNaviActivity.class);
                        naviV.putExtra("lat_end", emp.getLat_company());
                        naviV.putExtra("lng_end", emp.getLng_company());
                        startActivity(naviV);
                    } else {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.please_open_gps), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.no_location_lat_lng), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.adPic:
                if (emp != null && !StringUtil.isNullOrEmpty(emp.getAd_pic()) && !emp.getAd_pic().endsWith("ad_mp.jpg")) {
                    //说明存在广告图，点击进入分享页面
                    Intent mpV = new Intent(ProfileActivity.this, ShareMingpianActivity.class);
                    mpV.putExtra("mm_emp_id", emp.getMm_emp_id());
                    mpV.putExtra("mm_emp_ad_pic", emp.getAd_pic());
                    startActivity(mpV);
                } else {
                    showMsg(ProfileActivity.this, "暂无名片，请联系客服");
                    Intent kefuV = new Intent(ProfileActivity.this, SelectTelActivity.class);
                    startActivity(kefuV);
                }
                break;
        }
    }

    void getProfile() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MEMBER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    EmpData data = getGson().fromJson(s, EmpData.class);
                                    emp = data.getData();
                                    initData();
                                } else {
                                    showMsg(ProfileActivity.this, getResources().getString(R.string.get_data_error));
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
                        showMsg(ProfileActivity.this, getResources().getString(R.string.get_data_error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
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

    private void loadData(final int currentid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_RECORDS_BYID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        lstv.onRefreshComplete();
                        lstv.onLoadComplete();
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    RecordData data = getGson().fromJson(s, RecordData.class);
                                    if (ContentListView.REFRESH == currentid) {
                                        lists.clear();
                                        lists.addAll(data.getData());
                                        lstv.setResultSize(data.getData().size());
                                        adapter.notifyDataSetChanged();
                                    }
                                    if (ContentListView.LOAD == currentid) {
                                        lists.addAll(data.getData());
                                        lstv.setResultSize(data.getData().size());
                                        adapter.notifyDataSetChanged();
                                    }

                                    if (data != null && data.getData() != null) {
                                        for (RecordMsg recordMsg : data.getData()) {
                                            RecordMsg recordMsgLocal = DBHelper.getInstance(ProfileActivity.this).getRecord(recordMsg.getMm_msg_id());
                                            if (recordMsgLocal != null) {
                                                //已经存在了 不需要插入了
                                            } else {
                                                DBHelper.getInstance(ProfileActivity.this).saveRecord(recordMsg);
                                            }
                                        }
                                    }

                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(ProfileActivity.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(ProfileActivity.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else {
                                    Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        lstv.onRefreshComplete();
                        lstv.onLoadComplete();
                        Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("index", String.valueOf(pageIndex));
                params.put("size", "10");
                params.put("mm_emp_id", id);
                params.put("mm_msg_type", type);
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
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

    void initData() {
        imageLoader.displayImage(emp.getMm_emp_cover(), head, UniversityApplication.txOptions, animateFirstListener);
        imageLoader.displayImage(emp.getAd_pic(), adPic, UniversityApplication.adOptions, animateFirstListener);

        if (!StringUtil.isNullOrEmpty(emp.getMm_emp_company())) {
            address.setText(emp.getMm_emp_company());
        } else {
            address.setText(getResources().getString(R.string.no_www));
        }
        content.setText(emp.getMm_emp_company_detail());
        nickname.setText(emp.getMm_emp_nickname());
        back.setText(emp.getMm_emp_nickname());
        companyUrl.setText(emp.getMm_emp_company() == null ? "" : emp.getMm_emp_company() + getResources().getString(R.string.wangzhan));
        if (emp.getMm_emp_id().equals(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))) {
            //是当前登陆者自己 显示
            updateBtn.setVisibility(View.VISIBLE);
        } else {
            //隐藏
            updateBtn.setVisibility(View.GONE);
        }
    }

    RecordMsg recordVO;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag) {
            case 1:
                //分享
                lists.get(position).setIs_read("1");
                adapter.notifyDataSetChanged();
                recordVO = lists.get(position);
                recordVO.setIs_read("1");
                DBHelper.getInstance(ProfileActivity.this).updateRecord(recordVO);
                share(recordVO);
                break;
            case 2:
            case 4: {
//                头像
                recordVO = lists.get(position);
                lists.get(position).setIs_read("1");
                adapter.notifyDataSetChanged();

                recordVO.setIs_read("1");
                DBHelper.getInstance(ProfileActivity.this).updateRecord(recordVO);
            }
            break;
            case 3:
                //电话
                lists.get(position).setIs_read("1");
                adapter.notifyDataSetChanged();

                recordVO = lists.get(position);
                if (recordVO != null && !StringUtil.isNullOrEmpty(recordVO.getMm_emp_mobile())) {
                    showTel(recordVO.getMm_emp_mobile(), recordVO.getMm_emp_nickname());
                } else {
                    //
                    Toast.makeText(ProfileActivity.this, R.string.no_tel, Toast.LENGTH_SHORT).show();
                }
                recordVO.setIs_read("1");
                DBHelper.getInstance(ProfileActivity.this).updateRecord(recordVO);
                break;
            case 5:
            case 8:
                //图片
                Intent intent = new Intent(ProfileActivity.this, DetailRecordActivity.class);
                recordVO = lists.get(position);
                intent.putExtra("info", recordVO);
                startActivity(intent);

                lists.get(position).setIs_read("1");
                adapter.notifyDataSetChanged();

                recordVO.setIs_read("1");
                DBHelper.getInstance(ProfileActivity.this).updateRecord(recordVO);
                break;
            case 6:
                //收藏图标
                lists.get(position).setIs_read("1");
                adapter.notifyDataSetChanged();
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    recordVO = lists.get(position);
                    progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    saveFavour(recordVO.getMm_msg_id());

                    recordVO.setIs_read("1");
                    DBHelper.getInstance(ProfileActivity.this).updateRecord(recordVO);
                } else {
                    //未登录
                    showLogin();
                }

                break;
            case 7:
                //导航
            {
                lists.get(position).setIs_read("1");
                adapter.notifyDataSetChanged();
                recordVO = lists.get(position);
                if (!StringUtil.isNullOrEmpty(recordVO.getMm_msg_title())) {
                    String[] arrs = recordVO.getMm_msg_title().split(",");
                    if (arrs != null && arrs.length > 0) {
                        //开始导航
                        if (!StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)) {
                            Intent naviV = new Intent(ProfileActivity.this, GPSNaviActivity.class);
                            naviV.putExtra("lat_end", arrs[0]);
                            naviV.putExtra("lng_end", arrs[1]);
                            startActivity(naviV);
                        } else {
                            Toast.makeText(ProfileActivity.this, getResources().getString(R.string.please_open_gps), Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(ProfileActivity.this, getResources().getString(R.string.no_location_lat_lng), Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    void saveFavour(final String mm_msg_id) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    Toast.makeText(ProfileActivity.this, R.string.favour_success, Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(ProfileActivity.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(ProfileActivity.this, LoginActivity.class);
                                    startActivity(loginV);
                                } else if (Integer.parseInt(code) == 2) {
                                    Toast.makeText(ProfileActivity.this, R.string.favour_error_one, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, R.string.no_favour, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, R.string.no_favour, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_id", mm_msg_id);
                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class));
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
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


    // 登陆注册选择窗口
    private void showLogin() {
        final Dialog picAddDialog = new Dialog(ProfileActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(ProfileActivity.this, R.layout.login_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(getResources().getString(R.string.please_reg_or_login));
        //登陆
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        //注册
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(ProfileActivity.this, RegistActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        TextView kefuzhongxin = (TextView) picAddInflate.findViewById(R.id.kefuzhongxin);
        kefuzhongxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kefuV = new Intent(ProfileActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }


    // 拨打电话窗口
    private void showTel(final String tel, String name) {
        final Dialog picAddDialog = new Dialog(ProfileActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(ProfileActivity.this, R.layout.tel_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(tel + " " + name);
        btn_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

    private RecordMsg recordMsgTmp;

    void share(RecordMsg recordVO) {
        //
        recordMsgTmp = recordVO;

        new ShareAction(ProfileActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(ProfileActivity.this, R.drawable.logo);
            String title = recordMsgTmp.getMm_msg_content();
            String content = recordMsgTmp.getMm_emp_nickname() + recordMsgTmp.getMm_emp_company();
            new ShareAction(ProfileActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl(InternetURL.VIEW_RECORD_BYID_URL + "?id=" + recordMsgTmp.getMm_msg_id())
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ProfileActivity.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ProfileActivity.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ProfileActivity.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(ProfileActivity.this).onActivityResult(requestCode, resultCode, data);
    }

    private void initViewPager() {
        adapterAd = new ViewPagerAdapter(ProfileActivity.this);
        adapterAd.change(listsAd);
        adapterAd.setOnClickContentItemListener(this);
        viewpager = (ViewPager) headLiner.findViewById(R.id.viewpager);
        viewpager.setAdapter(adapterAd);
        viewpager.setOnPageChangeListener(myOnPageChangeListener);
        initDot();
        runnable = new Runnable() {
            @Override
            public void run() {
                int next = viewpager.getCurrentItem() + 1;
                if (next >= adapterAd.getCount()) {
                    next = 0;
                }
                viewHandler.sendEmptyMessage(next);
            }
        };
        viewHandler.postDelayed(runnable, autoChangeTime);
    }


    // 初始化dot视图
    private void initDot() {
        viewGroup = (LinearLayout) headLiner.findViewById(R.id.viewGroup);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapterAd.getCount()];
        for (int i = 0; i < adapterAd.getCount(); i++) {
            dot = new ImageView(ProfileActivity.this);
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
        if (position < 0 || position > adapterAd.getCount()) {
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
                                    listsAd.clear();
                                    if (data != null && data.getData().size() > 0) {
                                        listsAd.addAll(data.getData());
                                    }
                                    if (lists.size() == 0) {
                                        listsAd.add(new EmpAdObj("http://xhmt.sdhmmm.cn:7777/upload/20160313/1457875390482.jpg", "http://xhmt.sdhmmm.cn:7777/html/download.html"));
                                    }
                                    initViewPager();
                                } else {
                                    Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_id", id);
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

    public void companyIndex(View view) {
        //
        if (StringUtil.isNullOrEmpty(emp.getMm_emp_company_url())) {
            showMsg(ProfileActivity.this, getResources().getString(R.string.no_www));
        } else {
            Intent webV = new Intent(ProfileActivity.this, WebViewActivity.class);
            webV.putExtra("strurl", emp.getMm_emp_company_url());
            startActivity(webV);
        }
    }

}
