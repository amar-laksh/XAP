package com.app.amar.XAP;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.TextView;

import com.apps.amar.xap.R;

public class AttendanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String attendance="";
        String studentClass="";
        String studentName="";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            attendance = extras.getString("attendance");
            studentClass = extras.getString("studentClass");
            studentName = extras.getString("studentName");

        }
        setContentView(R.layout.activity_show_attedance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView ta = (TextView) findViewById(R.id.textView);
        WebView browser = (WebView) findViewById(R.id.webview);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.getSettings().setSupportZoom(true);
        browser.loadData(attendance, "text/html", "UTF-8");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
