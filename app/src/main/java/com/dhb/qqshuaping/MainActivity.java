package com.dhb.qqshuaping;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView tv;
    private EditText et;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("settings", MODE_WORLD_READABLE);
        tv = (TextView) findViewById(R.id.donate);
        tv.setOnClickListener(this);
        et = (EditText) findViewById(R.id.editText);
        et.setText(prefs.getString("text",""));
        et.addTextChangedListener(new EditTextWatcher());
    }

    @Override
    public void onClick(View view) {
        if (view == tv) {
            Uri uri = Uri.parse("https://ds.alipay.com/?from=mobilecodec&scheme=alipayqr%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttps%253A%252F%252Fqr.alipay.com%252Fapx03387fsyaq8us5kabh8a%253F_s%253Dweb-other");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    class EditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            prefs.edit().putString("text", charSequence.toString()).apply();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
