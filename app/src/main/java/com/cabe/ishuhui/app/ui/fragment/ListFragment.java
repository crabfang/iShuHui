package com.cabe.ishuhui.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.cabe.ishuhui.app.R;
import com.cabe.ishuhui.app.adapter.CartoonAdapter;
import com.cabe.ishuhui.app.model.CartoonInfo;
import com.cabe.ishuhui.app.ui.BaseActivity;
import com.cabe.ishuhui.app.ui.BookInfoActivity;
import com.cabe.lib.cache.CacheSource;
import com.cabe.lib.cache.http.HttpTransformer;
import com.cabe.lib.cache.http.RequestParams;
import com.cabe.lib.cache.impl.DoubleCacheUseCase;
import com.cabe.lib.cache.interactor.ViewPresenter;
import com.cabe.lib.cache.interactor.impl.SimpleViewPresenter;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by cabe on 16/5/11.
 */
public class ListFragment extends BaseFragment {
    @Bind(R.id.fragment_home_list_view)
    GridView gridList;

    private CartoonAdapter cartoonAdapter;
    private boolean flagLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_home_list, container, false);
        ButterKnife.bind(this, contentView);

        gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CartoonInfo item = (CartoonInfo) cartoonAdapter.getItem(position);
                Log.w(TAG, "onClick:" + item);
                Intent intent = new Intent(activity, BookInfoActivity.class);
                intent.putExtra(BaseActivity.KEY_WEB_URL, item.url);
                startActivity(intent);
            }
        });
        cartoonAdapter = new CartoonAdapter(activity);
        gridList.setAdapter(cartoonAdapter);
        loadData();

        return contentView;
    }

    private void loadData() {
        if(flagLoading) return;

        flagLoading = true;
        HttpTransformer<List<CartoonInfo>> httpTransformer = new HttpTransformer<List<CartoonInfo>>() {
            @Override
            public List<CartoonInfo> buildData(String responseStr) {
                List<CartoonInfo> list = null;

                Document doc = Jsoup.parse(responseStr);

                Elements ulElements = doc.getElementsByClass("chinaMangaContentList");
                if(ulElements != null && !ulElements.isEmpty()) {
                    list = new ArrayList<>();

                    Elements bookList = ulElements.first().children();
                    for(int i=0;i<bookList.size();i++) {
                        Element element = bookList.get(i);
                        String img = element.getElementsByTag("img").first().attr("src");

                        Element aElement = element.getElementsByTag("a").get(1);

                        String url = "http://www.ishuhui.net" + aElement.attr("href");
                        String title = aElement.text();

                        CartoonInfo bean = new CartoonInfo();
                        bean.pic = img;
                        bean.title = title;
                        bean.url = url;
                        list.add(bean);
                    }
                }

                return list;
            }
        };

        ViewPresenter<List<CartoonInfo>> viewPresenter = new SimpleViewPresenter<List<CartoonInfo>>(){
            @Override
            public void error(CacheSource from, int code, String info) {
                Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void load(CacheSource from, final List<CartoonInfo> dataList) {
                if(dataList == null) return;

                cartoonAdapter.setData(dataList);
            }
            @Override
            public void complete(CacheSource from) {
                if(from == CacheSource.HTTP) {
                    gridList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flagLoading = false;
                        }
                    }, 400);
                }
            }
        };

        RequestParams params = new RequestParams();
        params.host = "http://www.ishuhui.net/ComicBookList/";
        DoubleCacheUseCase<List<CartoonInfo>> useCase = new DoubleCacheUseCase<>(new TypeToken<List<CartoonInfo>>(){}, params);
        useCase.getHttpRepository().setResponseTransformer(httpTransformer);
        useCase.execute(viewPresenter);
    }
}
