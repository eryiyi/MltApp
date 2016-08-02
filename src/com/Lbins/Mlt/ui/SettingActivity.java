package com.Lbins.Mlt.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.UniversityApplication;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.module.SetFontColor;
import com.Lbins.Mlt.module.SetFontSize;
import com.Lbins.Mlt.util.StringUtil;
import com.Lbins.Mlt.widget.CustomerSpinner;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/19.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private CustomerSpinner textSize;
    ArrayAdapter<String> adapterEmpType;
    private ArrayList<SetFontSize> empTypeList = new ArrayList<SetFontSize>();
    private ArrayList<String> empTypeListStr = new ArrayList<String>();
    private SetFontSize mm_emp_type;//

    private CustomerSpinner textColor;
    ArrayAdapter<String> adapterCompanyType;
    private ArrayList<SetFontColor> companyTypeList = new ArrayList<SetFontColor>();
    private ArrayList<String> companyTypeListStr = new ArrayList<String>();
    private SetFontColor mm_emp_company_type;//

    private ImageView switch_shengyin;
    private ImageView switch_zhendong;

    private LinearLayout switch_zhendong_liner;
    private LinearLayout switch_shengyin_liner;

    private TextView fontsize_text;
    private TextView fontcolor_text;
    private TextView check_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        registerBoradcastReceiver();
        initView();
        changeColorOrSize();
    }

    private void initView() {
        fontsize_text = (TextView) this.findViewById(R.id.fontsize_text);
        fontcolor_text = (TextView) this.findViewById(R.id.fontcolor_text);
        check_version = (TextView) this.findViewById(R.id.check_version);
        check_version.setOnClickListener(this);
        check_version.setText(getVersion());
        switch_shengyin = (ImageView) this.findViewById(R.id.switch_shengyin);
        switch_zhendong = (ImageView) this.findViewById(R.id.switch_zhendong);

        switch_zhendong_liner = (LinearLayout) this.findViewById(R.id.switch_zhendong_liner);
        switch_shengyin_liner = (LinearLayout) this.findViewById(R.id.switch_shengyin_liner);
        switch_zhendong_liner.setOnClickListener(this);
        switch_shengyin_liner.setOnClickListener(this);

        this.findViewById(R.id.back).setOnClickListener(this);
        textSize = (CustomerSpinner) this.findViewById(R.id.textSize);
        textColor = (CustomerSpinner) this.findViewById(R.id.textColor);

        empTypeList.add(new SetFontSize(getResources().getString(R.string.font_zhengchang), "16"));
        empTypeList.add(new SetFontSize(getResources().getString(R.string.font_small), "14"));
        empTypeList.add(new SetFontSize(getResources().getString(R.string.font_zhong), "18"));
        empTypeList.add(new SetFontSize(getResources().getString(R.string.font_big), "22"));
        empTypeList.add(new SetFontSize(getResources().getString(R.string.font_big_big), "26"));
        empTypeListStr.add(getResources().getString(R.string.font_zhengchang));
        empTypeListStr.add(getResources().getString(R.string.font_small));
        empTypeListStr.add(getResources().getString(R.string.font_zhong));
        empTypeListStr.add(getResources().getString(R.string.font_big));
        empTypeListStr.add(getResources().getString(R.string.font_big_big));
        adapterEmpType = new ArrayAdapter<String>(SettingActivity.this, android.R.layout.simple_spinner_item, empTypeListStr);
        textSize.setList(empTypeListStr);
        textSize.setAdapter(adapterEmpType);

        companyTypeList.add(new SetFontColor(getResources().getString(R.string.black), "black"));
        companyTypeList.add(new SetFontColor(getResources().getString(R.string.gray), "gray"));
        companyTypeList.add(new SetFontColor(getResources().getString(R.string.blue), "blue"));
        companyTypeList.add(new SetFontColor(getResources().getString(R.string.orange), "orange"));
        companyTypeList.add(new SetFontColor(getResources().getString(R.string.red), "red"));
        companyTypeListStr.add(getResources().getString(R.string.black));
        companyTypeListStr.add(getResources().getString(R.string.gray));
        companyTypeListStr.add(getResources().getString(R.string.blue));
        companyTypeListStr.add(getResources().getString(R.string.orange));
        companyTypeListStr.add(getResources().getString(R.string.red));
        adapterCompanyType = new ArrayAdapter<String>(SettingActivity.this, android.R.layout.simple_spinner_item, companyTypeListStr);
        textColor.setList(companyTypeListStr);
        textColor.setAdapter(adapterCompanyType);

        textColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mm_emp_company_type = companyTypeList.get(position);
                save("font_color", mm_emp_company_type.getFontColor());
                //调用广播，刷新主页
                Intent intent1 = new Intent("change_color_size");
                sendBroadcast(intent1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mm_emp_type = empTypeList.get(position);
                save("font_size", mm_emp_type.getSizeStr());
                //调用广播，刷新主页
                Intent intent1 = new Intent("change_color_size");
                sendBroadcast(intent1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.findViewById(R.id.btn_kf).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.switch_zhendong_liner:
                if ("1".equals(getGson().fromJson(getSp().getString("switch_zhendong", ""), String.class))) {
                    //打开的
                    switch_zhendong.setImageResource(R.drawable.switch_close);
                    save("switch_zhendong", "0");//0关闭  1打开
                } else {
                    switch_zhendong.setImageResource(R.drawable.switch_open);
                    save("switch_zhendong", "1");
                }
                break;
            case R.id.switch_shengyin_liner:
                if ("1".equals(getGson().fromJson(getSp().getString("switch_shengyin", ""), String.class))) {
                    //打开的
                    switch_shengyin.setImageResource(R.drawable.switch_close);
                    save("switch_shengyin", "0");//0关闭  1打开
                } else {
                    switch_shengyin.setImageResource(R.drawable.switch_open);
                    save("switch_shengyin", "1");
                }
                break;
            case R.id.check_version:
                //检查版本
            {
                //
                Resources res = getBaseContext().getResources();
                String message = res.getString(R.string.check_new_version).toString();
                progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setMessage(message);
                progressDialog.show();

                UmengUpdateAgent.forceUpdate(this);

                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                        progressDialog.dismiss();
                        switch (i) {
                            case UpdateStatus.Yes:
//                                Toast.makeText(mContext, "有新版本发现", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.No:
                                Toast.makeText(SettingActivity.this, R.string.new_version_also, Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout:
                                Toast.makeText(SettingActivity.this, R.string.net_pass, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
            break;
            case R.id.btn_kf:
                Intent kefuV = new Intent(SettingActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
                break;
        }
    }

    public void surequite(View view) {
        AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this)
                .setIcon(R.drawable.logo)
                .setTitle(getResources().getString(R.string.sure_quite))
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        save("password", "");
                        save("isLogin", "0");
                        //调用广播，刷新主页
                        Intent intent1 = new Intent("sure_quite");
                        sendBroadcast(intent1);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialog.show();
    }

    void changeColorOrSize() {
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
            fontsize_text.setTextSize(Float.valueOf(getGson().fromJson(getSp().getString("font_size", ""), String.class)));
            fontcolor_text.setTextSize(Float.valueOf(getGson().fromJson(getSp().getString("font_size", ""), String.class)));
            if ("16".equals(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
                textSize.setSelection(0, true);
            }
            if ("14".equals(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
                textSize.setSelection(1, true);
            }
            if ("18".equals(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
                textSize.setSelection(2, true);
            }
            if ("22".equals(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
                textSize.setSelection(3, true);
            }
            if ("26".equals(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
                textSize.setSelection(4, true);
            }

        }
        if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
            if ("black".equals(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
                fontsize_text.setTextColor(Color.BLACK);
                fontcolor_text.setTextColor(Color.BLACK);
                textColor.setSelection(0, true);
            }
            if ("gray".equals(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
                fontsize_text.setTextColor(Color.GRAY);
                fontcolor_text.setTextColor(Color.GRAY);
                textColor.setSelection(1, true);
            }
            if ("blue".equals(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
                fontsize_text.setTextColor(Color.BLUE);
                fontcolor_text.setTextColor(Color.BLUE);
                textColor.setSelection(2, true);
            }
            if ("orange".equals(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
                fontsize_text.setTextColor(Color.YELLOW);
                fontcolor_text.setTextColor(Color.YELLOW);
                textColor.setSelection(3, true);
            }
            if ("red".equals(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
                fontsize_text.setTextColor(Color.RED);
                fontcolor_text.setTextColor(Color.RED);
                textColor.setSelection(4, true);
            }

        }
    }


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("change_color_size")) {
                changeColorOrSize();
                //控制字体 颜色和大小
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("font_size", ""), String.class))) {
                    UniversityApplication.fontSize = getGson().fromJson(getSp().getString("font_size", ""), String.class);
                }
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("font_color", ""), String.class))) {
                    UniversityApplication.fontColor = getGson().fromJson(getSp().getString("font_color", ""), String.class);
                }
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("change_color_size");//
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version_name) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }
}
