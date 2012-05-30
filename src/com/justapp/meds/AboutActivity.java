package com.justapp.meds;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView aboutTextView = (TextView) findViewById(R.id.aboutText);
        aboutTextView.setText(Html.fromHtml(getString(R.string.about_text)));
    }
}
