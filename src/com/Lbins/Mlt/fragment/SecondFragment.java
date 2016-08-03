package com.Lbins.Mlt.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.AdViewPagerAdapter;
import com.Lbins.Mlt.adapter.ItemRecordAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseFragment;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.DBHelper;
import com.Lbins.Mlt.dao.RecordMsg;
import com.Lbins.Mlt.data.AdObjData;
import com.Lbins.Mlt.data.RecordData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
import com.Lbins.Mlt.module.AdObj;
import com.Lbins.Mlt.ui.*;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.SelectTelPopWindow;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
 * Created by Administrator on 2016/1/22.
 */
public class SecondFragment extends BaseFragment implements OnClickContentItemListener, View.OnClickListener {
    private View view;
    private Resources res;
    private PullToRefreshListView lstv;
    private ItemRecordAdapter adapter;
    private List<RecordMsg> lists = new ArrayList<RecordMsg>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private ImageView no_data;
//    private EditText keyword;

    String countryId; //选择的县区
    private TextView mLocation;
    private String is_guanzhu = "0";//0不是查询关注区域 1是查询关注的区域

    //导航
    private ViewPager viewpager;
    private AdViewPagerAdapter adapterAd;
    private LinearLayout viewGroup;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<AdObj> listsAd = new ArrayList<AdObj>();

    private LinearLayout headLiner;
    private RelativeLayout search_liner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.two_fragment, null);
        res = getActivity().getResources();
        initView();
        initData();
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("areaName", ""), String.class))) {
            mLocation.setText(getGson().fromJson(getSp().getString("areaName", ""), String.class) + "-" + getResources().getString(R.string.dianjiseeother));
        } else if (!StringUtil.isNullOrEmpty(UniversityApplication.area)) {
            mLocation.setText(UniversityApplication.area + "-" + getResources().getString(R.string.dianjiseeother));
        }
        getAd();
        return view;
    }

    void initView() {
        headLiner = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.ad_header, null);
        search_liner = (RelativeLayout) headLiner.findViewById(R.id.search_liner);
        search_liner.setVisibility(View.GONE);
        mLocation = (TextView) view.findViewById(R.id.mLocation);
        mLocation.setOnClickListener(this);
        view.findViewById(R.id.btn_add).setOnClickListener(this);
        view.findViewById(R.id.btn_search).setOnClickListener(this);

        no_data = (ImageView) view.findViewById(R.id.no_data);
        lstv = (PullToRefreshListView) view.findViewById(R.id.lstv);
        ListView listView = lstv.getRefreshableView();

        listView.addHeaderView(headLiner);
        adapter = new ItemRecordAdapter(lists, getActivity());
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
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

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    initData();
                } else {
                    lstv.onRefreshComplete();
                    //未登录
                    showLogin();
                }
            }
        });
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lists.size() > position - 1) {
                    lists.get(position - 1).setIs_read("1");
                    adapter.notifyDataSetChanged();
                    recordVO = lists.get(position - 1);
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                }
            }
        });
        adapter.setOnClickContentItemListener(this);

//        keyword = (EditText) view.findViewById(R.id.keyword);
//        keyword.addTextChangedListener(watcher);
        no_data.setOnClickListener(this);
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
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

    RecordMsg recordVO;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        String str = (String) object;
        if ("000".equals(str)) {
            switch (flag) {
                case 0:
                    AdObj adObj = listsAd.get(position);
                    if(!StringUtil.isNullOrEmpty(adObj.getMm_ad_url())){
                        Intent webV = new Intent(getActivity(), WebViewActivity.class);
                        webV.putExtra("strurl", adObj.getMm_ad_url());
                        startActivity(webV);
                    }
                    break;
            }
        }
        if ("111".equals(str)) {
            lists.get(position).setIs_read("1");
            adapter.notifyDataSetChanged();
            recordVO = lists.get(position);
            switch (flag) {
                case 1:
                    //分享
                    recordVO.setIs_read("1");
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                    share(recordVO);
                    break;
                case 2:
                case 4: {
                    //头像
                    Intent mineV = new Intent(getActivity(), ProfileActivity.class);
                    mineV.putExtra("id", recordVO.getMm_emp_id());
                    startActivity(mineV);
                    recordVO.setIs_read("1");
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                }
                break;
                case 3:
                    //电话
                    if (recordVO != null && !StringUtil.isNullOrEmpty(recordVO.getMm_emp_mobile())) {
                        showTel(recordVO.getMm_emp_cover(),recordVO.getMm_emp_mobile(), recordVO.getMm_emp_nickname(), recordVO.getMm_emp_company());
                    } else {
                        Toast.makeText(getActivity(), R.string.no_tel, Toast.LENGTH_SHORT).show();
                    }
                    recordVO.setIs_read("1");
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                    break;
                case 5:
                case 8:
                    //图片
                    Intent intent = new Intent(getActivity(), DetailRecordActivity.class);
                    intent.putExtra("info", recordVO);
                    startActivity(intent);
                    recordVO.setIs_read("1");
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                    break;
                case 6:
                    //收藏图标
                    if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        saveFavour(recordVO.getMm_msg_id());
                        recordVO.setIs_read("1");
                        DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                    } else {
                        //未登录
                        showLogin();
                    }
                    break;
                case 7:
                    //导航
                {
                    if (!StringUtil.isNullOrEmpty(recordVO.getMm_msg_title())) {
                        String[] arrs = recordVO.getMm_msg_title().split(",");
                        if (arrs != null && arrs.length > 0) {
                            //开始导航
                            if (!StringUtil.isNullOrEmpty(UniversityApplication.lat) && !StringUtil.isNullOrEmpty(UniversityApplication.lng)) {
                                Intent naviV = new Intent(getActivity(), GPSNaviActivity.class);
                                naviV.putExtra("lat_end", arrs[0]);
                                naviV.putExtra("lng_end", arrs[1]);
                                startActivity(naviV);
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.please_open_gps), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_location_lat_lng), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }


    private RecordMsg recordMsgTmp;

    void share(RecordMsg recordVO) {
        //
        recordMsgTmp = recordVO;

        new ShareAction(getActivity()).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(getActivity(), R.drawable.logo);
            String title = recordMsgTmp.getMm_msg_content();
            String content = recordMsgTmp.getMm_emp_nickname() + recordMsgTmp.getMm_emp_company();
            new ShareAction(getActivity()).setPlatform(share_media).setCallback(umShareListener)
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
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_RECORD_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    RecordData data = getGson().fromJson(s, RecordData.class);
                                    if (IS_REFRESH) {
                                        lists.clear();
                                    }
                                    lists.addAll(data.getData());
                                    if (data != null && data.getData() != null) {
                                        for (RecordMsg recordMsg : data.getData()) {
                                            RecordMsg recordMsgLocal = DBHelper.getInstance(getActivity()).getRecord(recordMsg.getMm_msg_id());
                                            if (recordMsgLocal != null) {
                                                //已经存在了 不需要插入了
                                            } else {
                                                DBHelper.getInstance(getActivity()).saveRecord(recordMsg);
                                            }
                                        }
                                    }
                                    lstv.onRefreshComplete();
                                    adapter.notifyDataSetChanged();
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(getActivity(), R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(loginV);
                                    getActivity().finish();
                                } else if (Integer.parseInt(code) == 2) {
                                    Toast.makeText(getActivity(), R.string.favour_error_one, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (lists.size() == 0) {
                                no_data.setVisibility(View.GONE);
                                lstv.setVisibility(View.VISIBLE);
                            } else {
                                no_data.setVisibility(View.GONE);
                                lstv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

//                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("index", String.valueOf(pageIndex));
                params.put("size", "10");
                params.put("mm_msg_type", "1");
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class))) {
                    params.put("provinceid", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
                } else {
                    params.put("provinceid", "");
                }
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class))) {
                    params.put("cityid", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
                } else {
                    params.put("cityid", "");
                }

                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
                }
//                if (!StringUtil.isNullOrEmpty(keyword.getText().toString())) {
//                    params.put("keyword", keyword.getText().toString());
//                }
                //当前登陆者的等级vip 0  -- 4
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_level_num", ""), String.class))) {
                    params.put("mm_level_num", getGson().fromJson(getSp().getString("mm_level_num", ""), String.class));
                } else {
                    params.put("mm_level_num", "");
                }
                //权限-- 查看全部信息
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_see_all", ""), String.class))) {
                    params.put("is_see_all", getGson().fromJson(getSp().getString("is_see_all", ""), String.class));
                } else {
                    params.put("is_see_all", "");
                }

                //是否是选择的县区
                if (!StringUtil.isNullOrEmpty(countryId)) {
                    params.put("is_select_countryId", countryId);
                } else {
                    params.put("is_select_countryId", "");
                }

                if ("1".equals(is_guanzhu)) {
                    params.put("is_guanzhu", "1");
                    params.put("countryid", getGson().fromJson(getSp().getString("gz_areaId", ""), String.class));
                } else {
                    params.put("is_guanzhu", "0");
                    //默认情况下
                    if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
                        params.put("countryid", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
                    } else {
                        params.put("countryid", "");
                    }
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
            case R.id.mLocation:
                //
                if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class)))) {
                    //未登录
                    showLogin();
                } else {
                    Intent selectV = new Intent(getActivity(), SelectProvinceActivity.class);
                    startActivity(selectV);
                }
                break;
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
            case R.id.btn_add: {
                //发布信息
                if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class)))) {
                    //未登录
                    showLogin();
                } else {
                    if ("0".equals(getGson().fromJson(getSp().getString("is_upate_profile", ""), String.class))) {
                        showUpdateProfile();
                    } else {
                        Intent addV = new Intent(getActivity(), AddRecordActivity.class);
                        startActivity(addV);
                    }
                }
            }
            break;
            case R.id.btn_search:
            {
                //搜索
                Intent searchV = new Intent(getActivity(), SearchHotActivity.class);
                startActivity(searchV);
            }
            break;
        }
    }

    // 补充资料窗口
    private void showUpdateProfile() {
        final Dialog picAddDialog = new Dialog(getActivity(), R.style.dialog);
        View picAddInflate = View.inflate(getActivity(), R.layout.update_profile_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        //确定
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateV = new Intent(getActivity(), UpdateProfiletActivity.class);
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


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_INDEX_SUCCESS_GONGYING)) {
                RecordMsg record1 = (RecordMsg) intent.getExtras().get("addRecord");
                lists.add(0, record1);
                adapter.notifyDataSetChanged();
                lstv.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            }
            if (action.equals("select_country")) {
                is_guanzhu = "0";
                countryId = intent.getExtras().getString("countryId");
                String countryName = intent.getExtras().getString("countryName");
                mLocation.setText(countryName + "-" + getResources().getString(R.string.dianjiseeother));
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
            if (action.equals("change_color_size")) {
                adapter.notifyDataSetChanged();
            }
            if (action.equals("change_guanzhu_area")) {
                //查询关注的区域
                is_guanzhu = "1";
                initData();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();

        myIntentFilter.addAction(Constants.SEND_INDEX_SUCCESS_GONGYING);//添加说说和添加视频成功，刷新首页
        myIntentFilter.addAction("select_country");//选择县区
        myIntentFilter.addAction("change_color_size");//
        myIntentFilter.addAction("change_guanzhu_area");//查询关注的区域
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
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
                                    Toast.makeText(getActivity(), R.string.favour_success, Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(getActivity(), R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(loginV);
                                    getActivity().finish();
                                } else if (Integer.parseInt(code) == 2) {
                                    Toast.makeText(getActivity(), R.string.favour_error_one, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.no_favour, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.no_favour, Toast.LENGTH_SHORT).show();
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
        final Dialog picAddDialog = new Dialog(getActivity(), R.style.dialog);
        View picAddInflate = View.inflate(getActivity(), R.layout.login_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(getResources().getString(R.string.please_reg_or_login));
        //登陆
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        //注册
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(getActivity(), RegistActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        TextView kefuzhongxin = (TextView) picAddInflate.findViewById(R.id.kefuzhongxin);
        kefuzhongxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kefuV = new Intent(getActivity(), SelectTelActivity.class);
                startActivity(kefuV);
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    private void initViewPager() {
        adapterAd = new AdViewPagerAdapter(getActivity());
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
                InternetURL.GET_AD_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    AdObjData data = getGson().fromJson(s, AdObjData.class);
                                    listsAd.clear();
                                    if (data != null && data.getData().size() > 0) {
                                        listsAd.addAll(data.getData());
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
                params.put("mm_ad_type", "2");
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class))) {
                    params.put("mm_emp_provinceId", getGson().fromJson(getSp().getString("mm_emp_provinceId", ""), String.class));
                }
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class))) {
                    params.put("mm_emp_cityId", getGson().fromJson(getSp().getString("mm_emp_cityId", ""), String.class));
                }
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
                    params.put("mm_emp_countryId", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
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

    private SelectTelPopWindow telphonePop;
    private String tmpTel = "";
    private void showTel(String cover, String tel, String nickname,String company) {
        tmpTel = tel;
        telphonePop = new SelectTelPopWindow(getActivity(), itemsOnClick, nickname, company, cover);
        telphonePop.showAtLocation(getActivity().findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            telphonePop.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure: {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tmpTel));
                    startActivity(intent);
                }
                break;
                case R.id.btn_cancel: {}
                break;
                default:
                    break;
            }
        }
    };

}