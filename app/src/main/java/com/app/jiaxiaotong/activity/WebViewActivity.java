package com.app.jiaxiaotong.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.model.UserModel;
import com.app.jiaxiaotong.utils.ToolBarUtils;
import com.app.jiaxiaotong.weight.ProgressWebView;

public class WebViewActivity extends BaseActivity {

    private ProgressWebView webView;

    String url;

    private int type;//0为考勤，1为公告

    private static final String BASE_URL = "http://121.40.193.59:7891";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        type  = getIntent().getExtras().getInt("type");
        UserModel userModel = UserInfoKeeper.readUserInfo(this);
        if (type == 0){
            ToolBarUtils.initToolBar(this,"考勤中心");
            if (userModel.getType().equalsIgnoreCase(Constant.TEACHER)){
                url = BASE_URL + "/api/app/statisticsindex.do/"+ userModel.getUid();
            }else {
                url = BASE_URL + "/api/app/checkchild/" + userModel.getUid();
            }
        }else{
            ToolBarUtils.initToolBar(this,"公告");
            if (userModel.getType().equalsIgnoreCase(Constant.TEACHER)){
                url = BASE_URL + "/api/app/teacher/annoucementlist/"+ userModel.getUid();
            }else {
                url = BASE_URL + "/api/app/family/annoucementlist/" + userModel.getUid();
            }
        }
//        url = "http://www.baidu.com";
        initView();
    }
    private void initweb(){

    }
    private void initView() {
        webView = (ProgressWebView) findViewById(R.id.nes_detail_webview);
        //   设置WebClient(可不要)
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //不管缓存，都从网络加载
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //如果存在缓存，则从缓存加载
//        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setBuiltInZoomControls(true);//开启缩放
        webView.getSettings().setDisplayZoomControls(false);//关闭缩放按钮
        webView.getSettings().setSupportZoom(true);//默认自适应缩小
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
//        doWebViewSetting();

        webView.loadUrl(url);
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

}
