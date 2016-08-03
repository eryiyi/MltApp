package com.Lbins.Mlt.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.NetwwwObjData;
import com.Lbins.Mlt.module.NetwwwObj;
import com.Lbins.Mlt.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/3.
 */
public class OtherActivity extends BaseActivity implements View.OnClickListener {
    private TextView txt_reg;
    private TextView txt_level;
    private TextView txt_endtime;
    private TextView txt_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity);

        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.liner_one).setOnClickListener(this);
        this.findViewById(R.id.liner_two).setOnClickListener(this);
        this.findViewById(R.id.liner_three).setOnClickListener(this);
        this.findViewById(R.id.liner_four).setOnClickListener(this);
        this.findViewById(R.id.liner_five).setOnClickListener(this);
        this.findViewById(R.id.liner_six).setOnClickListener(this);
        this.findViewById(R.id.liner_seven).setOnClickListener(this);
        this.findViewById(R.id.liner_eight).setOnClickListener(this);
        this.findViewById(R.id.liner_nine).setOnClickListener(this);
        this.findViewById(R.id.liner_eleven).setOnClickListener(this);
        this.findViewById(R.id.liner_twelven).setOnClickListener(this);
        this.findViewById(R.id.liner_aboutus).setOnClickListener(this);

        txt_reg = (TextView) this.findViewById(R.id.txt_reg);
        txt_level = (TextView) this.findViewById(R.id.txt_level);
        txt_endtime = (TextView) this.findViewById(R.id.txt_endtime);
        txt_address = (TextView) this.findViewById(R.id.txt_address);

        String vipTypeStr = "会员等级:";
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("levelName", ""), String.class))) {
            vipTypeStr += getGson().fromJson(getSp().getString("levelName", ""), String.class);
        }
        String endStr =  "到期日期:" ;
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_endtime", ""), String.class))) {
            endStr += getGson().fromJson(getSp().getString("mm_emp_endtime", ""), String.class);
        }

        txt_reg.setText("注册日期:" + getGson().fromJson(getSp().getString("mm_emp_regtime", ""), String.class));
        txt_address.setText("注册地区:" +getGson().fromJson(getSp().getString("provinceName", ""), String.class) + getGson().fromJson(getSp().getString("cityName", ""), String.class) + getGson().fromJson(getSp().getString("areaName", ""), String.class));
        txt_level.setText(vipTypeStr);
        txt_endtime.setText(endStr);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.back:
                finish();
                break;
            case R.id.liner_one:
            {
                //实名认证
                Intent kefuV = new Intent(OtherActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
            }
                break;
            case R.id.liner_two:
            {
                //关注区域
                Intent guanzhuV = new Intent(OtherActivity.this, SetGuanzhuActivity.class);
                startActivity(guanzhuV);
            }
            break;
            case R.id.liner_three:
            {
                //升级vip
//                Intent vipV = new Intent(OtherActivity.this, VipActivity.class);
//                startActivity(vipV);
                Toast.makeText(OtherActivity.this, R.string.no_open, Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.liner_four:
            {
                //公告
                Intent noticeV = new Intent(OtherActivity.this, NoticeActivity.class);
                startActivity(noticeV);
            }
            break;
            case R.id.liner_five:
            {
                //客服中心
                Intent kefuV = new Intent(OtherActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
            }
            break;
            case R.id.liner_six:
            {
                //微信客服
                Intent weixinV = new Intent(OtherActivity.this, WeixinKefuActivity.class);
                startActivity(weixinV);
            }
            break;
            case R.id.liner_seven:
            {
                //二维码下载
                Intent intentErweima = new Intent(OtherActivity.this, ErweimaActivity.class);
                startActivity(intentErweima);
            }
            break;
            case R.id.liner_eight:
            {
                //地图导航
                final Uri uri = Uri.parse("http://map.baidu.com/mobile/webapp/index/index");
                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
            break;
            case R.id.liner_nine:
            {
                //农村商业银行
                getNetobj("2");
            }
            break;
            case R.id.liner_eleven:
            {
                //意见反馈
                Intent suggestV = new Intent(OtherActivity.this, AddSuggestActivity.class);
                startActivity(suggestV);
            }
            break;
            case R.id.liner_twelven:
            {
                //修改密码
                Intent updateP = new Intent(OtherActivity.this, FindPwrActivity.class);
                startActivity(updateP);
            }
            break;
            case R.id.liner_aboutus:
            {
                //关于我们
                Intent aboutV = new Intent(OtherActivity.this, AboutUsActivity.class);
                startActivity(aboutV);
            }
            break;

        }
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
                                    Toast.makeText(OtherActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(OtherActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(OtherActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
