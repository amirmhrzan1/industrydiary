package com.weareforge.qms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.weareforge.qms.R;
import com.weareforge.qms.utils.FontHelper;

/**
 * Created by prajit on 4/14/16.
 */
public class TermsAndConditions extends BaseActivity implements View.OnClickListener {

    public WebView webView;
    private TextView txtBack;
    private ImageButton btn_back;

    private FontHelper fontHelper;

    private ProgressDialog progressBar;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_events;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        progressBar = new ProgressDialog(TermsAndConditions.this);
        progressBar.setMessage("Loading...");
        progressBar.show();
        webView.loadUrl("file:///android_asset/terms-conditions.html");
        this.webView.setPadding(10,10,10,10);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(MoreInfoASAQ.this, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               // view.loadUrl(url);
                if(url.endsWith(".com.au")){
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent .setType("plain/text");
                    emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{url.substring(7)});
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    return true;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });

        fontHelper = new FontHelper(this);



        this.btn_back = (ImageButton) findViewById(R.id.back_img);
        this.txtBack = (TextView) findViewById(R.id.txt_back);

        //Visible Buttons
        this.btn_back.setVisibility(View.VISIBLE);
        this.txtBack.setVisibility(View.VISIBLE);

        this.txtBack.setTypeface(fontHelper.getDefaultFont("bold"));

        this.btn_back.setOnClickListener(this);
        this.txtBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.btn_back.getId()) {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } else if (v.getId() == this.txtBack.getId()) {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
