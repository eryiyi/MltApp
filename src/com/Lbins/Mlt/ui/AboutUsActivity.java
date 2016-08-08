package com.Lbins.Mlt.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.data.AboutUsData;
import com.Lbins.Mlt.module.AboutUs;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.SelectTelPopWindow;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/22.
 */
public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tel;
    private TextView address;
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity);

        this.findViewById(R.id.back).setOnClickListener(this);
        tel = (TextView) this.findViewById(R.id.tel);
        address = (TextView) this.findViewById(R.id.address);
        content = (TextView) this.findViewById(R.id.content);
        initData();
        tel.setOnClickListener(this);

    }

    public void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_ABOUT_US_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    AboutUsData data = getGson().fromJson(s, AboutUsData.class);
                                    if (data.getData() != null && data.getData().size() > 0) {
                                        AboutUs aboutUs = data.getData().get(0);
                                        tel.setText(aboutUs.getMm_about_tel());
                                        address.setText(aboutUs.getMm_abou_address());
                                        content.setText(aboutUs.getMm_about_cont());
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AboutUsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AboutUsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tel:
                showTel("", tel.getText().toString(),"苗连通", "");
                break;
        }
    }

    private SelectTelPopWindow telphonePop;
    private String tmpTel = "";
    private void showTel(String cover, String tel, String nickname,String company) {
        tmpTel = tel;
        telphonePop = new SelectTelPopWindow(AboutUsActivity.this, itemsOnClick, nickname, company, cover);
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
}
