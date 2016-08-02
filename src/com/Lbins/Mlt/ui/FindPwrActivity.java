package com.Lbins.Mlt.ui;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import com.Lbins.Mlt.R;
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
 * Created by Administrator on 2016/2/23.
 */
public class FindPwrActivity extends BaseActivity implements View.OnClickListener {
    Resources res;
    private EditText yuanshipwr;
    private EditText password;
    private EditText surepass;

    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pwr_activity);
        res = getResources();
        initView();
    }

    void initView() {
        yuanshipwr = (EditText) this.findViewById(R.id.yuanshipwr);
        password = (EditText) this.findViewById(R.id.password);
        surepass = (EditText) this.findViewById(R.id.surepass);
        btn = (Button) this.findViewById(R.id.btn);

        btn.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                //确定
                if (StringUtil.isNullOrEmpty(yuanshipwr.getText().toString())) {
                    showMsg(FindPwrActivity.this, getResources().getString(R.string.pwr_error_two));
                    return;
                }
                if (StringUtil.isNullOrEmpty(password.getText().toString())) {
                    showMsg(FindPwrActivity.this, getResources().getString(R.string.pwr_error_three));
                    return;
                }
                if (password.getText().toString().length() > 18 || password.getText().toString().length() < 6) {
                    showMsg(FindPwrActivity.this, getResources().getString(R.string.pwr_error_four));
                    return;
                }
                if (StringUtil.isNullOrEmpty(surepass.getText().toString())) {
                    showMsg(FindPwrActivity.this, getResources().getString(R.string.pwr_error_five));
                    return;
                }
                if (!password.getText().toString().equals(surepass.getText().toString())) {
                    showMsg(FindPwrActivity.this, getResources().getString(R.string.pwr_error_six));
                    return;
                }
                //判断原始密码是否正确
                if (!yuanshipwr.getText().toString().equals(getGson().fromJson(getSp().getString("password", ""), String.class))) {
                    showMsg(FindPwrActivity.this, getResources().getString(R.string.pwr_error_one));
                    return;
                }
                progressDialog = new ProgressDialog(FindPwrActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                reg();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    void reg() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_PWR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    showMsg(FindPwrActivity.this, getResources().getString(R.string.pwr_success_one));
                                    save("password", password.getText().toString());
                                    finish();
                                } else {
                                    Toast.makeText(FindPwrActivity.this, R.string.pwr_error_nine, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(FindPwrActivity.this, R.string.pwr_error_nine, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FindPwrActivity.this, R.string.pwr_error_nine, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_mobile", getGson().fromJson(getSp().getString("mm_emp_mobile", ""), String.class));
                params.put("newpass", password.getText().toString());
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


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result" + event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
//                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    reg();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
//                    Toast.makeText(FindPwrActivity.this, R.string.code_msg_one, Toast.LENGTH_SHORT).show();
                }

            } else {
//				((Throwable) data).printStackTrace();
                Toast.makeText(FindPwrActivity.this, R.string.code_msg_two, Toast.LENGTH_SHORT).show();
//					Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(FindPwrActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }


        }

        ;
    };


    public void onDestroy() {
        super.onPause();
    }

    ;

}
