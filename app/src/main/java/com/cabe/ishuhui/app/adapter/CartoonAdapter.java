package com.cabe.ishuhui.app.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cabe.ishuhui.app.R;
import com.cabe.ishuhui.app.model.CartoonInfo;

/**
 * Cartoon Adapter
 * Created by cabe on 16/5/11.
 */
public class CartoonAdapter extends MyBaseAdapter<CartoonInfo> {
    protected String TAG = "CartoonAdapter";
    private Context context;
    public CartoonAdapter(Context context) {
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cartoon_book, parent, false);
            holder = new GridHolder();
            holder.pic = (ImageView) convertView.findViewById(R.id.item_cartoon_book_cover);
            holder.title = (TextView) convertView.findViewById(R.id.item_cartoon_book_title);
            convertView.setTag(holder);
        } else {
            holder = (GridHolder) convertView.getTag();
        }
        final CartoonInfo item = (CartoonInfo) getItem(position);
        holder.title.setText(Html.fromHtml(item.title));
        Glide.with(context).load(item.pic).into(holder.pic);
        return convertView;
    }

    private static class GridHolder {
        public ImageView pic;
        public TextView title;
    }
}