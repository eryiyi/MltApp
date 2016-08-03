package com.Lbins.Mlt.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;
import com.Lbins.Mlt.fragment.RecordOneFragment;
import com.Lbins.Mlt.fragment.RecordTwoFragment;
import com.Lbins.Mlt.util.HttpUtils;
import com.Lbins.Mlt.util.StringUtil;

/**
 * Created by Administrator on 2016/3/27 0027.
 */
public class RecordGzActivity extends BaseActivity implements View.OnClickListener {
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private RecordOneFragment oneFragment;
    private RecordTwoFragment twoFragment;
    private ImageView foot_one;
    private ImageView foot_two;
    //设置底部图标
    Resources res;
    private int index;
    //    public static GuanzhuAreaObj guanzhuAreaObj;//关注的区域
    public static String idPostion;//关注的区域
    public static String name;//关注的区域
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        guanzhuAreaObj = (GuanzhuAreaObj) getIntent().getExtras().get("guanzhuAreaObj");
        idPostion = getIntent().getExtras().getString("idPostion");
        name = getIntent().getExtras().getString("name");
        setContentView(R.layout.record_main);
        res = getResources();
        fm = getSupportFragmentManager();
        initView();
        title.setText(name);
        switchFragment(R.id.foot_one);
    }

    private void initView() {
        foot_one = (ImageView) this.findViewById(R.id.foot_one);
        foot_two = (ImageView) this.findViewById(R.id.foot_two);
        title = (TextView) this.findViewById(R.id.title);
        foot_one.setOnClickListener(this);
        foot_two.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
    }


    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_one:
                if (oneFragment == null) {
                    oneFragment = new RecordOneFragment();
                    fragmentTransaction.add(R.id.content_frame, oneFragment);
                } else {
                    fragmentTransaction.show(oneFragment);
                }
                foot_one.setImageResource(R.drawable.tree_toolbar_wanted_p);
                foot_two.setImageResource(R.drawable.tree_toolbar_sell);

                break;
            case R.id.foot_two:
                if (twoFragment == null) {
                    twoFragment = new RecordTwoFragment();
                    fragmentTransaction.add(R.id.content_frame, twoFragment);
                } else {
                    fragmentTransaction.show(twoFragment);
                }

                foot_one.setImageResource(R.drawable.tree_toolbar_wanted);
                foot_two.setImageResource(R.drawable.tree_toolbar_wanted);
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (oneFragment != null) {
            ft.hide(oneFragment);
        }
        if (twoFragment != null) {
            ft.hide(twoFragment);
        }
    }

    boolean isMobileNet, isWifiNet;

    @Override
    public void onClick(View v) {
        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
            isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(this, R.string.net_work_error, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (v.getId() == R.id.back) {
            finish();
        } else {
            if ((StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("isLogin", ""), String.class)) || "0".equals(getGson().fromJson(getSp().getString("isLogin", ""), String.class))) && (v.getId() == R.id.foot_four)) {
                //未登录
                showLogin();
            } else {
                switchFragment(v.getId());
            }
        }

    }

    // 登陆注册选择窗口
    private void showLogin() {
        final Dialog picAddDialog = new Dialog(RecordGzActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(RecordGzActivity.this, R.layout.login_dialog, null);
        TextView btn_sure = (TextView) picAddInflate.findViewById(R.id.btn_sure);
        final TextView jubao_cont = (TextView) picAddInflate.findViewById(R.id.jubao_cont);
        jubao_cont.setText(getResources().getString(R.string.please_reg_or_login));
        //登陆
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(RecordGzActivity.this, LoginActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        //注册
        TextView btn_cancel = (TextView) picAddInflate.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginV = new Intent(RecordGzActivity.this, RegistActivity.class);
                startActivity(loginV);
                picAddDialog.dismiss();
            }
        });
        TextView kefuzhongxin = (TextView) picAddInflate.findViewById(R.id.kefuzhongxin);
        kefuzhongxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kefuV = new Intent(RecordGzActivity.this, SelectTelActivity.class);
                startActivity(kefuV);
                picAddDialog.dismiss();
            }
        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }
}
