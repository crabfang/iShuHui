package com.cabe.ishuhui.app.adapter;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Base Adapter
 * Created by cabe on 16/5/10.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    private List<T> dataList;
    public void setData(List<T> list) {
        dataList = list;
        notifyDataSetChanged();
    }
    public void addData(List<T> list) {
        if(list != null) {
            if(dataList == null) {
                dataList = list;
            } else {
                dataList.addAll(list);
            }
            notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }
    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
}
