package com.Lbins.Mlt.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.ItemTelAdapter;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.KefuTelData;
import com.Lbins.Mlt.library.internal.PullToRefreshListView;
import com.Lbins.Mlt.module.KefuTel;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.SelectTelPopWindow;
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
 * Created by zhanghailong on 2016/3/8.
 * 查询客服电话
 */
public class SelectTelActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private ImageView cursor1;
    private ImageView cursor2;
    private TextView textView1, textView2;
    private List<View> views;
    private int offset = 0;
    private int currIndex = 0;
    private int bmpW;
    private View view1, view2;

    private List<KefuTel> lists = new ArrayList<KefuTel>();
    private List<KefuTel> listsAll = new ArrayList<KefuTel>();
    private PullToRefreshListView gridView;
    private PullToRefreshListView gridView2;
    private ItemTelAdapter adapter;
    private ItemTelAdapter adapterVideo;

    private TextView back;
    private ImageView no_data1;
    private ImageView no_data2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_tel_activity);
        initView();
        InitImageView();
        InitTextView();
        InitViewPager();
        initData();
        initDataAll();
    }

    public void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_TEL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    KefuTelData data = getGson().fromJson(s, KefuTelData.class);
                                    lists.clear();
                                    lists.addAll(data.getData());
                                    if (lists.size() > 0) {
                                        no_data1.setVisibility(View.GONE);
                                        gridView.setVisibility(View.VISIBLE);
                                    } else {
                                        no_data1.setVisibility(View.VISIBLE);
                                        gridView.setVisibility(View.GONE);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SelectTelActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectTelActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SelectTelActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class))) {
                    params.put("mm_emp_countryId", getGson().fromJson(getSp().getString("mm_emp_countryId", ""), String.class));
                }
                params.put("mm_tel_type", "0");//地区的
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

    public void initDataAll() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_TEL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    KefuTelData data = getGson().fromJson(s, KefuTelData.class);
                                    listsAll.clear();
                                    listsAll.addAll(data.getData());
                                    if (listsAll.size() > 0) {
                                        no_data2.setVisibility(View.GONE);
                                        gridView2.setVisibility(View.VISIBLE);
                                    } else {
                                        no_data2.setVisibility(View.VISIBLE);
                                        gridView2.setVisibility(View.GONE);
                                    }
                                    adapterVideo.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SelectTelActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectTelActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SelectTelActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_tel_type", "1");//1是全国的
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

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        back = (TextView) this.findViewById(R.id.back);
    }


    private void InitViewPager() {
        viewPager = (ViewPager) findViewById(R.id.vPager);
        views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.four_shop_lay1, null);
        view2 = inflater.inflate(R.layout.four_shop_lay2, null);

        views.add(view1);
        views.add(view2);

        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        gridView = (PullToRefreshListView) view1.findViewById(R.id.lstv);
        gridView2 = (PullToRefreshListView) view2.findViewById(R.id.lstv);
        no_data1 = (ImageView) view1.findViewById(R.id.no_data);
        no_data2 = (ImageView) view2.findViewById(R.id.no_data);

        adapter = new ItemTelAdapter(lists, SelectTelActivity.this);
        adapterVideo = new ItemTelAdapter(listsAll, SelectTelActivity.this);

        gridView.setAdapter(adapter);
        gridView2.setAdapter(adapterVideo);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (lists.size() > (i - 1)) {
                    KefuTel kefuTel = lists.get(i - 1);
                    if (kefuTel != null) {
                        String tel = kefuTel.getProvinceName() + kefuTel.getCityName() + kefuTel.getAreaName();
                        if(StringUtil.isNullOrEmpty(tel)){
                            tel = "全国客服";
                        }
                        showTel("", kefuTel.getMm_tel(), tel ,"");
                    }
                }
            }
        });
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listsAll.size() > (i - 1)) {
                    KefuTel kefuTel = listsAll.get(i - 1);
                    if (kefuTel != null) {
                        showTel("", kefuTel.getMm_tel(),  "全国客服" ,"");
                    }
                }
            }
        });

    }

    private SelectTelPopWindow telphonePop;
    private String tmpTel = "";
    private void showTel(String cover, String tel, String nickname,String company) {
        tmpTel = tel;
        telphonePop = new SelectTelPopWindow(SelectTelActivity.this, itemsOnClick, nickname, company, cover);
        telphonePop.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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


    private void InitTextView() {
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);

        textView1.setOnClickListener(new MyOnClickListener(0));
        textView2.setOnClickListener(new MyOnClickListener(1));
    }


    private void InitImageView() {
        cursor1 = (ImageView) findViewById(R.id.cursor1);
        cursor2 = (ImageView) findViewById(R.id.cursor2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
//        int one = offset * 1 + bmpW;
//        int two = one * 1;

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int arg0) {
//            Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
//            currIndex = arg0;
//            animation.setFillAfter(true);
//            animation.setDuration(300);
//            imageView.startAnimation(animation);
            if (arg0 == 0) {
                cursor1.setImageDrawable(getResources().getDrawable(R.drawable.line_bg));
                cursor2.setImageDrawable(getResources().getDrawable(R.drawable.line_bg_white));
            }
            if (arg0 == 1) {
                cursor1.setImageDrawable(getResources().getDrawable(R.drawable.line_bg_white));
                cursor2.setImageDrawable(getResources().getDrawable(R.drawable.line_bg));
            }
        }

    }

}
