package com.cabe.ishuhui.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.cabe.ishuhui.app.R;
import com.cabe.ishuhui.app.ui.fragment.BaseFragment;
import com.cabe.ishuhui.app.ui.fragment.IndexFragment;
import com.cabe.ishuhui.app.ui.fragment.ListFragment;
import com.cabe.lib.cache.disk.DiskCacheManager;
import com.cabe.lib.cache.http.StringHttpFactory;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RestAdapter;

/**
 * Index Activity
 * Created by cabe on 16/5/10.
 */
public class HomeActivity extends BaseActivity {

    @Bind(R.id.activity_home_bottom_tab)
    RadioGroup bottomTab;

    private IndexFragment indexFragment;
    private ListFragment listFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StringHttpFactory.logLevel = RestAdapter.LogLevel.NONE;
        DiskCacheManager.DISK_CACHE_PATH = getExternalCacheDir() + File.separator + "data";

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        bottomTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeTab(checkedId);
            }
        });
        changeTab(0);
    }

    private void hideFragment(FragmentTransaction ft, BaseFragment fragment) {
        if(fragment != null) {
            ft.hide(fragment);
        }
    }

    private void changeTab(int checkedId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        hideFragment(ft, indexFragment);
        hideFragment(ft, listFragment);

        switch(checkedId) {
            default:
            case R.id.activity_home_tab_index:
                if(indexFragment == null) {
                    indexFragment = new IndexFragment();
                    ft.add(R.id.activity_home_fragment_container, indexFragment);
                } else {
                    ft.show(indexFragment);
                }
                break;
            case R.id.activity_home_tab_list:
                if(listFragment == null) {
                    listFragment = new ListFragment();
                    ft.add(R.id.activity_home_fragment_container, listFragment);
                } else {
                    ft.show(listFragment);
                }
                break;
        }

        ft.commitAllowingStateLoss();
    }
}
