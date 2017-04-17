package com.aiohtea.aiohteacollection;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;;

/**
 * Created by Nguyen Truong on 4/14/2017.
 */

public class About extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        WebView image = (WebView) findViewById(R.id.wait_image);

        //image.loadUrl("file:///android_asset/loader.gif");

        image.loadDataWithBaseURL("file:///android_res/drawable/",
                "<img style=\"display:block; margin-left:auto; margin-right:auto;\" src='loader.gif' />", "text/html", "utf-8", null);

    }
}
