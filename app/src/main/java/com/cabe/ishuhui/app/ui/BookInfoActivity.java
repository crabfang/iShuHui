package com.cabe.ishuhui.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cabe.ishuhui.app.R;
import com.cabe.ishuhui.app.adapter.MyBaseAdapter;
import com.cabe.ishuhui.app.model.CartoonBook;
import com.cabe.ishuhui.app.model.CartoonInfo;
import com.cabe.lib.cache.CacheSource;
import com.cabe.lib.cache.http.HttpTransformer;
import com.cabe.lib.cache.http.RequestParams;
import com.cabe.lib.cache.impl.HttpCacheUseCase;
import com.cabe.lib.cache.interactor.impl.SimpleViewPresenter;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Book Info
 * Created by cabe on 16/5/11.
 */
public class BookInfoActivity extends BaseActivity {
    @Bind(R.id.activity_book_cover)
    ImageView coverImg;
    @Bind(R.id.activity_book_name)
    TextView bookName;
    @Bind(R.id.activity_book_tips)
    TextView bookTips;
    @Bind(R.id.activity_book_introduce)
    TextView bookIntro;
    @Bind(R.id.activity_book_list_chapter)
    GridView chapterList;

    private BookAdapter adapter = new BookAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);

        chapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CartoonInfo item = (CartoonInfo) adapter.getItem(position);
                Log.w(TAG, "onClick:" + item);
                Intent intent = new Intent(activity, BrowserActivity.class);
                intent.putExtra(BaseActivity.KEY_WEB_URL, item.url);
                startActivity(intent);
            }
        });
        chapterList.setAdapter(adapter);
        loadData(getIntent().getStringExtra(KEY_WEB_URL));
    }

    private void updateInfo(CartoonBook book) {
        if(book != null) {
            Glide.with(activity).load(book.cover).into(coverImg);
            bookName.setText(book.name);
            bookTips.setText(book.tips);
            bookIntro.setText(book.introduce);
            adapter.setData(book.list);
        }
    }

    private void loadData(String url) {
        RequestParams params = new RequestParams();
        params.host = url;
        HttpCacheUseCase<CartoonBook> useCase = new HttpCacheUseCase<>(new TypeToken<CartoonBook>(){}, params);
        useCase.getHttpRepository().setResponseTransformer(new HttpTransformer<CartoonBook>() {
            @Override
            public CartoonBook buildData(String responseStr) {
                CartoonBook book = new CartoonBook();

                Document doc = Jsoup.parse(responseStr);

                Elements titleImg = doc.select("div.mangaInfoMainImg");
                if(!isEmptyElements(titleImg)) {
                    book.cover = titleImg.first().child(0).attr("src");
                }

                Elements titleE = doc.select("div.mangaInfoTitle");
                if(!isEmptyElements(titleE)) {
                    book.name = titleE.first().child(0).text();
                }

                Elements tipsE = doc.select("div.mangaInfoDate");
                if(!isEmptyElements(tipsE)) {
                    book.tips = tipsE.first().text();
                }

                Elements introE = doc.select("div.mangaInfoTextare");
                if(!isEmptyElements(introE)) {
                    book.introduce = introE.first().text();
                }

                Elements listE = doc.select("div.volumeControl");
                if(!isEmptyElements(listE)) {
                    Elements list = listE.first().children();

                    List<CartoonInfo> cartoonInfos = new ArrayList<>();
                    for(int i=0;i<list.size();i++) {
                        CartoonInfo info = new CartoonInfo();
                        info.title = list.get(i).text();
                        info.url = "http://www.ishuhui.net" + list.get(i).attr("href");
                        cartoonInfos.add(info);
                    }
                    book.list = cartoonInfos;
                }

                return book;
            }
        });
        useCase.execute(new SimpleViewPresenter<CartoonBook>(){
            @Override
            public void load(CacheSource from, CartoonBook data) {
                updateInfo(data);
            }
        });
    }

    private boolean isEmptyElements(Elements elements) {
        return elements == null || elements.isEmpty();
    }

    private class BookAdapter extends MyBaseAdapter<CartoonInfo> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView title;
            if(convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_book_chapter, parent, false);
                title = (TextView) convertView.findViewById(R.id.item_book_chapter_title);
                convertView.setTag(title);
            } else {
                title = (TextView) convertView.getTag();
            }
            CartoonInfo item = (CartoonInfo) getItem(position);
            title.setText(item.title);
            return convertView;
        }
    }
}
