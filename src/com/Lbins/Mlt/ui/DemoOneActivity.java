package com.Lbins.Mlt.ui;

import android.os.Bundle;
import android.view.View;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;

/**
 * Created by zhanghailong on 2016/3/20.
 */
public class DemoOneActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_one_activity);

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
}
