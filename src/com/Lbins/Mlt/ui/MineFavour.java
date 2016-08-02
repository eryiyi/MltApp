package com.Lbins.Mlt.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.adapter.ItemFavourAdapter;
import com.Lbins.Mlt.adapter.OnClickContentItemListener;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.FavourData;
import com.Lbins.Mlt.data.RecordDataSingle;
import com.Lbins.Mlt.module.Favour;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.SelectDelPop;
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
 * Created by zhanghailong on 2016/3/3.
 */
public class MineFavour extends BaseActivity implements View.OnClickListener, OnClickContentItemListener {
    private TextView back;
    private ImageView no_data;
    private ListView lstv;
    private List<Favour> lists = new ArrayList<Favour>();
    private ItemFavourAdapter adapter;

    private SelectDelPop selectPhoPop;

    private int positionTmp;
    private Favour favourTmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_favour_activity);

        initView();
        getData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    void initView() {
        back = (TextView) this.findViewById(R.id.back);
        back.setOnClickListener(this);
        no_data = (ImageView) this.findViewById(R.id.no_data);
        lstv = (ListView) this.findViewById(R.id.lstv);
        adapter = new ItemFavourAdapter(lists, MineFavour.this);
        lstv.setAdapter(adapter);
        adapter.setOnClickContentItemListener(this);
        lstv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                favourTmp = lists.get(i);
                positionTmp = i;
                showSelectImageDialog();
                return false;
            }
        });

    }

    // 选择相册，相机
    private void showSelectImageDialog() {
        selectPhoPop = new SelectDelPop(MineFavour.this, itemsOnClickPic);
        //显示窗口
        selectPhoPop.showAtLocation(MineFavour.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClickPic = new View.OnClickListener() {
        public void onClick(View v) {
            selectPhoPop.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    //删除
                    DeleteFavour();
                }
                break;
                default:
                    break;
            }
        }

    };

    void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.LIST_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    FavourData data = getGson().fromJson(s, FavourData.class);
                                    lists.clear();
                                    lists.addAll(data.getData());
                                    adapter.notifyDataSetChanged();

                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(MineFavour.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(MineFavour.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else {
                                    showMsg(MineFavour.this, getResources().getString(R.string.get_data_error));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (lists.size() > 0) {
                            no_data.setVisibility(View.GONE);
                            lstv.setVisibility(View.VISIBLE);
                        } else {
                            no_data.setVisibility(View.VISIBLE);
                            lstv.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        showMsg(MineFavour.this, getResources().getString(R.string.get_data_error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    Favour recordVO;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag) {
            case 1:
                //分享
                recordVO = lists.get(position);
                share(recordVO);
                break;
            case 2:
            case 4: {
                //头像

                recordVO = lists.get(position);
                Intent mineV = new Intent(MineFavour.this, ProfileActivity.class);
                mineV.putExtra("id", recordVO.getMm_emp_id());
                startActivity(mineV);
            }
            break;
            case 3:
                //电话
                recordVO = lists.get(position);
                if (recordVO != null && !StringUtil.isNullOrEmpty(recordVO.getMm_emp_mobile())) {
                    showTel(recordVO.getMm_emp_cover(), recordVO.getMm_emp_mobile(), recordVO.getMm_emp_nickname(), recordVO.getMm_emp_company());
                } else {
                    //
                    Toast.makeText(MineFavour.this, R.string.no_tel, Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                //图片
                recordVO = lists.get(position);
                getRecord(recordVO.getMm_msg_id());
//
                break;

        }
    }


    private Favour recordMsgTmp;

    void share(Favour recordVO) {
        //
        recordMsgTmp = recordVO;

        new ShareAction(MineFavour.this).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(MineFavour.this, R.drawable.logo);
            String title = recordMsgTmp.getMm_msg_content();
            String content = recordMsgTmp.getMm_emp_nickname() + recordMsgTmp.getMm_emp_company();
            new ShareAction(MineFavour.this).setPlatform(share_media).setCallback(umShareListener)
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
            Toast.makeText(MineFavour.this, platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MineFavour.this, platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MineFavour.this, platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(MineFavour.this).onActivityResult(requestCode, resultCode, data);
    }


    private SelectTelPopWindow telphonePop;
    private String tmpTel = "";
    private void showTel(String cover, String tel, String nickname,String company) {
        tmpTel = tel;
        telphonePop = new SelectTelPopWindow(MineFavour.this, itemsOnClick, nickname, company, cover);
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


    void getRecord(final String mm_msg_id) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_RECORD_BY_ID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    RecordDataSingle data = getGson().fromJson(s, RecordDataSingle.class);
                                    Intent intent = new Intent(MineFavour.this, DetailRecordActivity.class);
                                    intent.putExtra("info", data.getData());
                                    startActivity(intent);

                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(MineFavour.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(MineFavour.this, LoginActivity.class);
                                    startActivity(loginV);
                                } else {
                                    Toast.makeText(MineFavour.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                params.put("id", mm_msg_id);
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


    void DeleteFavour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    lists.remove(positionTmp);
                                    adapter.notifyDataSetChanged();
                                } else if (Integer.parseInt(code) == 9) {
                                    Toast.makeText(MineFavour.this, R.string.login_out, Toast.LENGTH_SHORT).show();
                                    save("password", "");
                                    Intent loginV = new Intent(MineFavour.this, LoginActivity.class);
                                    startActivity(loginV);
                                    finish();
                                } else {
                                    showMsg(MineFavour.this, getResources().getString(R.string.get_data_error));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        if (lists.size() > 0) {
                            no_data.setVisibility(View.GONE);
                            lstv.setVisibility(View.VISIBLE);
                        } else {
                            no_data.setVisibility(View.VISIBLE);
                            lstv.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        showMsg(MineFavour.this, getResources().getString(R.string.get_data_error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_record_favour_id", (favourTmp.getMm_record_favour_id() == null ? "" : favourTmp.getMm_record_favour_id()));
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


}
