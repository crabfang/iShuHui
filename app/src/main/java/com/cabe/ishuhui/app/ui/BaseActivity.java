package com.cabe.ishuhui.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Base Activity
 * Created by cabe on 16/5/10.
 */
public class BaseActivity extends FragmentActivity {
    public final static String KEY_WEB_URL = "keyWebUrl";
    protected String TAG = "BaseActivity";
    protected Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        activity = this;
    }
}
