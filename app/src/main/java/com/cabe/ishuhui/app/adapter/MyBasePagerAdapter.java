package com.cabe.ishuhui.app.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * Base Pager Adapter
 * Created by cabe on 16/5/10.
 */
public abstract class MyBasePagerAdapter<T> extends PagerAdapter {
    private List<T> dataList;
    private List<View> viewList = null;

    public void setData(List<T> list) {
        dataList = list;
        if(getCount() > 0 && viewList == null) {
            viewList = new LinkedList<>();
            for(int i=0;i<getCount();i++) {
                viewList.add(null);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public T getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View pageView = viewList.get(position);
        if(pageView != null) {
            container.removeView(pageView);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View pageView = viewList.get(position);
        if(pageView == null) {
            pageView = createItemView(container, position);
            viewList.set(position, pageView);
        }
        container.addView(pageView);
        return pageView;
    }

    public abstract View createItemView(ViewGroup parent, int position);
}
