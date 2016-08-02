package com.Lbins.Mlt.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.Lbins.Mlt.R;
import com.Lbins.Mlt.base.BaseActivity;

/**
 * Created by Administrator on 2015/5/14.
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener {
    private WebView detail_webview;
    private TextView menu;
    private String strurl;

    private static final String APP_CACAHE_DIRNAME = "/webcache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        strurl = getIntent().getExtras().getString("strurl");
        initView();
        detail_webview.getSettings().setJavaScriptEnabled(true);


        detail_webview.loadUrl(strurl);
        detail_webview.setWebViewClient(new HelloWebViewClient());

    }

    private void initView() {
        menu = (TextView) this.findViewById(R.id.back);
        menu.setOnClickListener(this);
        detail_webview = (WebView) this.findViewById(R.id.detail_webview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        detail_webview.reload();
        super.onPause();
    }


}
