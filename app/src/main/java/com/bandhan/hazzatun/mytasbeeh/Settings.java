package com.bandhan.hazzatun.mytasbeeh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        MainActivity mActivity= new MainActivity();

                mActivity.resets();
    }



}