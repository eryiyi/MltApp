package com.Lbins.Mlt.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.ItemRecordAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.DBHelper;
import com.Lbins.Mlt.dao.RecordMsg;
import com.Lbins.Mlt.data.RecordData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
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
 * Created by zhl on 2016/8/2.
 */
public class SearchRecordActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener{
    private PullToRefreshListView lstv;
    private ItemRecordAdapter adapter;
    private List<RecordMsg> lists = new ArrayList<RecordMsg>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private ImageView no_data;
    private EditText keyword;

    String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        registerBoradcastReceiver();
        content = getIntent().getExtras().getString("content");
        initView();
        initData();
    }
    void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        no_data = (ImageView) this.findViewById(R.id.no_data);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new ItemRecordAdapter(lists, SearchRecordActivity.this);
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
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    progressDialog = new ProgressDialog(SearchRecordActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    content = keyword.getText().toString();
                    initData();
                } else {
                    lstv.onRefreshComplete();
                    //未登录
                    showLogin();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    progressDialog = new ProgressDialog(SearchRecordActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    content = keyword.getText().toString();
                    initData();
                } else {
                    lstv.onRefreshComplete();
                    //未登录
                    showLogin();
                }
            }
        });

        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lists.size() > position -1) {
                    lists.get(position - 1).setIs_read("1");
                    adapter.notifyDataSetChanged();
                    recordVO = lists.get(position - 1);
                    DBHelper.getInstance(SearchRecordActivity.this).updateRecord(recordVO);
                }
            }
        });
        adapter.setOnClickContentItemListener(this);
        keyword = (EditText) this.findViewById(R.id.keyword);
        keyword.addTextChangedListener(watcher);
        no_data.setOnClickListener(this);
        if(!StringUtil.isNullOrEmpty(content)){
            keyword.setText(content);
        }

    }

    // 登陆注册选择窗口
    private void showLogin() {
        final Dialog picAddDialog = new Dialog(SearchRecordActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(SearchRecordActivity.this, R.layout.login_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(getResources().getString(R.string.please_reg_or_login));
        //登陆
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(SearchRecordActivity.this, LoginActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        //注册
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(SearchRecordActivity.this, RegistActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        TextView kefuzhongxin = (TextView) picAddInflate.findViewById(R.id.kefuzhongxin);
        kefuzhongxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kefuV = new Intent(SearchRecordActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
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
                content = keyword.getText().toString();
                progressDialog = new ProgressDialog(SearchRecordActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                content = keyword.getText().toString();
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
//                    AdObj adObj = listsAd.get(position);
//                    Intent webV = new Intent(getActivity(), WebViewActivity.class);
//                    webV.putExtra("strurl", adObj.getMm_ad_url() == null ? "" : adObj.getMm_ad_url());
//                    startActivity(webV);
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
                    DBHelper.getInstance(SearchRecordActivity.this).updateRecord(recordVO);
                    share(recordVO);
                    break;
                case 2:
                case 4: {
                    //头像
                    adapter.notifyDataSetChanged();
                    Intent mineV = new Intent(SearchRecordActivity.this, ProfileActivity.class);
                    mineV.putExtra("id", recordVO.getMm_emp_id());
                    startActivity(mineV);
                }
                break;
                case 3:
                    //电话
                    if (recordVO != null && !StringUtil.isNullOrEmpty(recordVO.getMm_emp_mobile())) {
                        showTel(recordVO.getMm_emp_cover(), recordVO.getMm_emp_mobile(), recordVO.getMm_emp_nickname(), recordVO.getMm_emp_company());
                    } else {
                        Toast.makeText(SearchRecordActivity.this, R.string.no_tel, Toast.LENGTH_SHORT).show();
                    }
                    recordVO.setIs_read("1");
                    DBHelper.getInstance(SearchRecordActivity.this).updateRecord(recordVO);
                    break;
                case 5:
                case 8:
                    //图片
                    Intent intent = new Intent(SearchRecordActivity.this, DetailRecordActivity.class);
                    intent.putExtra("info", recordVO);
                    startActivity(intent);
                    recordVO.setIs_read("1");
                    DBHelper.getInstance(SearchRecordActivity.this).updateRecord(recordVO);
                    break;
                case 6:
                    //收藏图标
                    if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                        progressDialog = new ProgressDialog(SearchRecordActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        saveFavour(recordVO.getMm_msg_id());

                        recordVO.setIs_read("1");
                        DBHelper.getInstance(SearchRecordActivity.this).updateRecord(recordVO);
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
                                Intent naviV = new Intent(SearchRecordActivity.this, GPSNaviActivity.class);
                                naviV.putExtra("lat_end", arrs[0]);
                                naviV.putExtra("lng_end", arrs[1]);
                                startActivity(naviV);
                            } else {
                                Toast.makeText(SearchRecordActivity.this, getResources().getString(R.string.please_open_gps), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(SearchRecordActivity.this, getResources().getString(R.string.no_location_lat_lng), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }

    }

    private RecordMsg recordMsgTmp;

    void share(RecordMsg recordVO) {
        recordMsgTmp = recordVO;
        new ShareAction(SearchRecordActivity.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(SearchRecordActivity.this, R.drawable.logo);
            String title = recordMsgTmp.getMm_msg_content();
            String content = recordMsgTmp.getMm_emp_nickname() + recordMsgTmp.getMm_emp_company();
            new ShareAction(SearchRecordActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.VIEW_RECORD_BYID_URL + "?id=" + recordMsgTmp.getMm_msg_id()))
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(SearchRecordActivity.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(SearchRecordActivity.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(SearchRecordActivity.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(SearchRecordActivity.this).onActivityResult(requestCode, resultCode, data);
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
                                            RecordMsg recordMsgLocal = DBHelper.getInstance(SearchRecordActivity.this).getRecord(recordMsg.getMm_msg_id());
                                            if (recordMsgLocal != null) {
                                                //已经存在了 不需要插入了
                                            } else {
                                                DBHelper.getInstance(SearchRecordActivity.this).saveRecord(recordMsg);
                                            }

                                        }
                                    }
                                    lstv.onRefreshComplete();
                                    adapter.notifyDataSetChanged();
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(SearchRecordActivity.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(SearchRecordActivity.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else {
                                    Toast.makeText(SearchRecordActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (lists.size() == 0) {
                                no_data.setVisibility(View.VISIBLE);
                                lstv.setVisibility(View.GONE);
                            } else {
                                no_data.setVisibility(View.GONE);
                                lstv.setVisibility(View.VISIBLE);
                            }
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
//                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("index", String.valueOf(pageIndex));
                params.put("size", "10");
                params.put("mm_msg_type", "0");
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

                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
                    params.put("countryid", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
                } else {
                    params.put("countryid", "");
                }

                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
                }
                if (!StringUtil.isNullOrEmpty(content)) {
                    params.put("keyword", content);
                }
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
        switch (view.getId()){
            case R.id.no_data:
                IS_REFRESH = true;
                pageIndex = 1;
                if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                    progressDialog = new ProgressDialog(SearchRecordActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    content = keyword.getText().toString();
                    initData();
                } else {
                    lstv.onRefreshComplete();
                    //未登录
                    showLogin();
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_INDEX_SUCCESS_QIUGOU)) {
                RecordMsg record1 = (RecordMsg) intent.getExtras().get("addRecord");
                lists.add(0, record1);
                adapter.notifyDataSetChanged();
                lstv.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            }
            if (action.equals("change_color_size")) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_INDEX_SUCCESS_QIUGOU);//添加成功，刷新首页
//        myIntentFilter.addAction("select_country");//选择县区
        myIntentFilter.addAction("change_color_size");//
//        myIntentFilter.addAction("change_guanzhu_area");//查询关注的区域
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
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
                                    Toast.makeText(SearchRecordActivity.this, R.string.favour_success, Toast.LENGTH_SHORT).show();
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(SearchRecordActivity.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(SearchRecordActivity.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else if (Integer.parseInt(code) == 2) {
                                    Toast.makeText(SearchRecordActivity.this, R.string.favour_error_one, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SearchRecordActivity.this, R.string.no_favour, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SearchRecordActivity.this, R.string.no_favour, Toast.LENGTH_SHORT).show();
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

    private SelectTelPopWindow telphonePop;
    private String tmpTel = "";
    private void showTel(String cover, String tel, String nickname,String company) {
        tmpTel = tel;
        telphonePop = new SelectTelPopWindow(SearchRecordActivity.this, itemsOnClick, nickname, company, cover);
        telphonePop.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
