package com.Lbins.Mlt.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.util.StringUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/5/11.
 */
public class AddCompanyLocationActivity extends BaseActivity implements View.OnClickListener {
    private TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location_activity);

        location = (TextView) this.findViewById(R.id.location);
        this.findViewById(R.id.back).setOnClickListener(this);
        location.setText("经度：" + UniversityApplication.lat +
                "\n纬度：" + UniversityApplication.lng + "\n地址：" + UniversityApplication.address);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public void sureLocation(View view) {
        //
        if (StringUtil.isNullOrEmpty(UniversityApplication.lat) || StringUtil.isNullOrEmpty(UniversityApplication.lng)) {
            showMsg(AddCompanyLocationActivity.this, "请打开GPS，重新定位！");
            return;
        }
        sendLocation();
    }

    void sendLocation() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_COMPANY_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    showMsg(AddCompanyLocationActivity.this, "上传成功！");
                                    finish();
                                } else {
                                    showMsg(AddCompanyLocationActivity.this, "上传失败！");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showMsg(AddCompanyLocationActivity.this, "上传失败！");
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showMsg(AddCompanyLocationActivity.this, "上传失败！");
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", (UniversityApplication.lat == null ? "" : UniversityApplication.lat));
                params.put("lng", (UniversityApplication.lng == null ? "" : UniversityApplication.lng));
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
}
