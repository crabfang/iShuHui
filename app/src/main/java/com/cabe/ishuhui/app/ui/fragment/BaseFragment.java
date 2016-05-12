package com.cabe.ishuhui.app.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 *
 * Created by cabe on 16/5/11.
 */
public class BaseFragment extends Fragment {
    protected String TAG = "BaseFragment";
    protected Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        activity = getActivity();
    }
}
