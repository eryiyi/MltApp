package com.Lbins.Mlt.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.base.InternetURL;
import com.Lbins.Mlt.util.StringUtil;
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
public class AddSuggestActivity extends BaseActivity implements View.OnClickListener {
    private EditText face_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_suggest_activity);
        face_content = (EditText) this.findViewById(R.id.face_content);
        this.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public void sureSub(View view) {
        //
        if (StringUtil.isNullOrEmpty(face_content.getText().toString())) {
            showMsg(AddSuggestActivity.this, getResources().getString(R.string.please_input_text));
            return;
        }
        add();

    }

    void add() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_SUGGEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    showMsg(AddSuggestActivity.this, getResources().getString(R.string.suggest_add_one));
                                    finish();
                                } else {
                                    showMsg(AddSuggestActivity.this, getResources().getString(R.string.suggest_add_two));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            showMsg(AddSuggestActivity.this, getResources().getString(R.string.suggest_add_two));
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
                        showMsg(AddSuggestActivity.this, getResources().getString(R.string.suggest_add_two));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_suggest_cont", face_content.getText().toString());
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
