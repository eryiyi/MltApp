package com.Lbins.Mlt.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.adapter.ItemRecordAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseFragment;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.dao.DBHelper;
import com.Lbins.Mlt.dao.RecordMsg;
import com.Lbins.Mlt.data.RecordData;
import com.Lbins.Mlt.library.internal.PullToRefreshBase;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
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
public class RecordTwoFragment extends BaseFragment implements OnClickContentItemListener, View.OnClickListener {
    private View view;
    private Resources res;
    private PullToRefreshListView lstv;
    private ItemRecordAdapter adapter;
    private List<RecordMsg> lists = new ArrayList<RecordMsg>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private ImageView no_data;

    private RecordMsg recordMsgTmp;
//
//    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
//    LinearLayout mRadioGroup_content;
//    LinearLayout ll_more_columns;
//    RelativeLayout rl_column;
//    private ArrayList<NewsClassify> newsClassify = new ArrayList<NewsClassify>();
//    private int columnSelectIndex = 0;
//    public ImageView shade_left;
//    public ImageView shade_right;
//    private int mScreenWidth = 0;
//    private int mItemWidth = 0;
//
//    //关注的区域
//    GuanzhuAreaObj guanzhuAreaObj;
//    private String[] areaNames = new String[10];
//    private String[] areaIds = new String[10];
//    private String tmpId ="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.record_one_fragment, null);
        res = getActivity().getResources();
        initView();
//        mScreenWidth = BaseTools.getWindowsWidth(getActivity());
//        mItemWidth = mScreenWidth / 5;
//        guanzhuAreaObj = RecordGzActivity.guanzhuAreaObj;
//        if(guanzhuAreaObj != null){
//            areaNames = guanzhuAreaObj.getArea_name().split(",");
//            areaIds = guanzhuAreaObj.getAreaid().split(",");
//            newsClassify.clear();
//            for(int i=0;i<areaNames.length;i++){
//                newsClassify.add(new NewsClassify(areaIds[i], areaNames[i]));
//            }
//        }
//        initTabColumn();
//        if(areaIds != null && areaIds.length >0){
//            tmpId = areaIds[0];
//        }
        initData();
        return view;
    }

    void initView() {
//        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) view.findViewById(R.id.mColumnHorizontalScrollView);
//        mRadioGroup_content = (LinearLayout) view.findViewById(R.id.mRadioGroup_content);
        no_data = (ImageView) view.findViewById(R.id.no_data);
        lstv = (PullToRefreshListView) view.findViewById(R.id.lstv);

        adapter = new ItemRecordAdapter(lists, getActivity());
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setAdapter(adapter);
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
        no_data.setOnClickListener(this);

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


//    private void initTabColumn() {
//        mRadioGroup_content.removeAllViews();
//        int count = newsClassify.size();
//        mColumnHorizontalScrollView.setParam(getActivity(), mScreenWidth, mRadioGroup_content, shade_left, shade_right,
//                ll_more_columns, rl_column);
//        for (int i = 0; i < count; i++) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.leftMargin = 5;
//            params.rightMargin = 5;
//            TextView columnTextView = new TextView(getActivity());
//            columnTextView.setTextAppearance(getActivity() , R.style.top_category_scroll_view_item_text);
//            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
//            columnTextView.setGravity(Gravity.CENTER);
//            columnTextView.setPadding(5, 5, 5, 5);
//            columnTextView.setId(i);
//            columnTextView.setText(newsClassify.get(i).getName());
//            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
//            if (columnSelectIndex == i) {
//                columnTextView.setSelected(true);
//            }
//            columnTextView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
//                        View localView = mRadioGroup_content.getChildAt(i);
//                        if (localView != v)
//                            localView.setSelected(false);
//                        else {
//                            localView.setSelected(true);
//                            String name = areaNames[i];
//                            tmpId = areaIds[i];
//                            lists.clear();
//                            initData();
//                        }
//                    }
////                    Toast.makeText(getApplicationContext(), newsClassify.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            mRadioGroup_content.addView(columnTextView, i, params);
//        }
//    }
//
//    private void selectTab(int tab_postion) {
//        columnSelectIndex = tab_postion;
//        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
//            View checkView = mRadioGroup_content.getChildAt(tab_postion);
//            int k = checkView.getMeasuredWidth();
//            int l = checkView.getLeft();
//            int i2 = l + k / 2 - mScreenWidth / 2;
//            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
//        }
//        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
//            View checkView = mRadioGroup_content.getChildAt(j);
//            boolean ischeck;
//            if (j == tab_postion) {
//                ischeck = true;
//            } else {
//                ischeck = false;
//            }
//            checkView.setSelected(ischeck);
//        }
//    }

//    private void initFragment() {
//        int count = newsClassify.size();
//        for (int i = 0; i < count; i++) {
//            Bundle data = new Bundle();
//            data.putString(Constants.NEWS_TYPEID_UUID, newsClassify.get(i).getId());
//            data.putString(Constants.NEWS_TYPEID_NAME, newsClassify.get(i).getName());
//            NewsFragment newfragment = new NewsFragment();
//            newfragment.setArguments(data);
//            fragments.add(newfragment);
//        }
//
//
//    }


//    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
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
//        public void onPageSelected(int position) {
//            selectTab(position);
//        }
//    };
//
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return true;
//    }


    RecordMsg recordVO;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        String str = (String) object;
        if ("111".equals(str)) {
            switch (flag) {
                case 1:
                    //分享
                    recordVO = lists.get(position);
                    lists.get(position).setIs_read("1");
                    adapter.notifyDataSetChanged();

                    recordVO.setIs_read("1");
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                    //
                    share(recordVO);
                    break;
                case 2:
                case 4: {
                    //头像
                    recordVO = lists.get(position);
                    lists.get(position).setIs_read("1");
                    adapter.notifyDataSetChanged();
                    Intent mineV = new Intent(getActivity(), ProfileActivity.class);
                    mineV.putExtra("id", recordVO.getMm_emp_id());
                    startActivity(mineV);

                    recordVO.setIs_read("1");
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                }
                break;
                case 3:
                    //电话
                    lists.get(position).setIs_read("1");
                    adapter.notifyDataSetChanged();

                    recordVO = lists.get(position);
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
                    recordVO = lists.get(position);
                    intent.putExtra("info", recordVO);
                    startActivity(intent);

                    lists.get(position).setIs_read("1");
                    adapter.notifyDataSetChanged();

                    recordVO.setIs_read("1");
                    DBHelper.getInstance(getActivity()).updateRecord(recordVO);
                    break;
                case 6:
                    //收藏图标
                    lists.get(position).setIs_read("1");
                    adapter.notifyDataSetChanged();
                    if ("1".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) {
                        recordVO = lists.get(position);
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
                    lists.get(position).setIs_read("1");
                    adapter.notifyDataSetChanged();
                    recordVO = lists.get(position);
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
                                } else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                                if (lists.size() == 0) {
                                    no_data.setVisibility(View.VISIBLE);
                                    lstv.setVisibility(View.GONE);
                                } else {
                                    no_data.setVisibility(View.GONE);
                                    lstv.setVisibility(View.VISIBLE);
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

                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("access_token", ""), String.class))) {
                    params.put("accessToken", getGson().fromJson(getSp().getString("access_token", ""), String.class));
                } else {
                    params.put("accessToken", "");
                }
//                if(!StringUtil.isNullOrEmpty(keyword.getText().toString())){
//                    params.put("keyword", keyword.getText().toString());
//                }

                params.put("is_guanzhu", "1");
                params.put("countryid", RecordGzActivity.idPostion);

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
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


}
