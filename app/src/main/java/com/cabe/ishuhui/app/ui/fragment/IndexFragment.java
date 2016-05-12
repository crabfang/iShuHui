package com.cabe.ishuhui.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cabe.ishuhui.app.R;
import com.cabe.ishuhui.app.adapter.CartoonAdapter;
import com.cabe.ishuhui.app.adapter.MyBasePagerAdapter;
import com.cabe.ishuhui.app.model.CartoonInfo;
import com.cabe.ishuhui.app.model.IndexInfo;
import com.cabe.ishuhui.app.ui.BaseActivity;
import com.cabe.ishuhui.app.ui.BrowserActivity;
import com.cabe.lib.cache.CacheSource;
import com.cabe.lib.cache.http.HttpTransformer;
import com.cabe.lib.cache.http.RequestParams;
import com.cabe.lib.cache.impl.DoubleCacheUseCase;
import com.cabe.lib.cache.impl.HttpCacheUseCase;
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
public class IndexFragment extends BaseFragment {
    protected final static String TAG = "IndexFragment";
    private final static int MAX_PAGE_INDEX = 10;

    @Bind(R.id.fragment_home_index_head)
    ViewPager headList;
    @Bind(R.id.fragment_home_index_list)
    GridView gridList;

    private HeadAdapter headAdapter = new HeadAdapter();
    private CartoonAdapter cartoonAdapter;

    private boolean flagLoading;
    private int pageIndex = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_home_index, container, false);
        ButterKnife.bind(this, contentView);

        gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CartoonInfo item = (CartoonInfo) cartoonAdapter.getItem(position);
                Log.w(TAG, "onClick:" + item);
                Intent intent = new Intent(activity, BrowserActivity.class);
                intent.putExtra(BaseActivity.KEY_WEB_URL, item.url);
                startActivity(intent);
            }
        });
        cartoonAdapter = new CartoonAdapter(activity);
        headList.setAdapter(headAdapter);
        gridList.setAdapter(cartoonAdapter);
        gridList.setOnScrollListener(new AbsListView.OnScrollListener() {
            int scrollState;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount <= totalItemCount) {
                    if(pageIndex < MAX_PAGE_INDEX) {
                        loadHtml(pageIndex);
                    }
                }
            }
        });

        loadHtml(pageIndex);

        return contentView;
    }

    private void loadHtml(final int index) {
        if(flagLoading) return;

        flagLoading = true;
        HttpTransformer<IndexInfo> httpTransformer = new HttpTransformer<IndexInfo>() {
            @Override
            public IndexInfo buildData(String responseStr) {
                IndexInfo info = new IndexInfo();

                Document doc = Jsoup.parse(responseStr);

                Element elementHead = doc.getElementById("slide");
                Elements headList = elementHead.children();
                if(headList != null && !headList.isEmpty()) {
                    List<CartoonInfo> list = new ArrayList<>();
                    for(int i=0;i<headList.size();i++) {
                        Element element = headList.get(i);
                        String styleStr = element.attr("style");
                        String img = styleStr.substring(styleStr.indexOf("url") + 4, styleStr.length() - 2);
                        Element elementHref = element.child(0);
                        String title = elementHref.attr("title");
                        String url = elementHref.attr("href");

                        CartoonInfo bean = new CartoonInfo();
                        bean.pic = img;
                        bean.title = title;
                        bean.url = url;
                        list.add(bean);
                    }
                    info.headList = list;
                }

                Elements dataList = doc.getElementsByAttributeValue("class", "col-xs-6 col-sm-4 col-md-3 show-lump");
                if(dataList != null && !dataList.isEmpty()) {
                    List<CartoonInfo> list = new ArrayList<>();
                    for(int i=0;i<dataList.size();i++) {
                        Elements div = dataList.get(i).getElementsByClass("thumbnail");
                        if(div != null && !div.isEmpty()) {
                            Element elementA = div.first().child(0);
                            String title = elementA.attr("title");
                            String url = elementA.attr("href");
                            Elements imgElements = div.first().getElementsByClass("img-rounded");
                            String picUrl = "";
                            if(imgElements != null && !imgElements.isEmpty()) {
                                picUrl = imgElements.get(0).attr("src");
                            }
                            CartoonInfo bean = new CartoonInfo();
                            bean.title = title;
                            bean.pic = picUrl;
                            bean.url = url;
                            list.add(bean);
                        }
                    }
                    info.dataList = list;
                }

                return info;
            }
        };

        ViewPresenter<IndexInfo> viewPresenter = new SimpleViewPresenter<IndexInfo>(){
            @Override
            public void error(CacheSource from, int code, String info) {
                Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void load(CacheSource from, final IndexInfo data) {
                if(data == null) return;

                if(index == 1) {
                    headAdapter.setData(data.headList);
                    cartoonAdapter.setData(data.dataList);
                } else {
                    cartoonAdapter.addData(data.dataList);
                }
            }
            @Override
            public void complete(CacheSource from) {
                if(from == CacheSource.HTTP) {
                    pageIndex ++;
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
        params.host = "http://www.ishuhui.com/page/" + index;
        Log.w("HomeActivity", "loadHtml:" + params.host);
        DoubleCacheUseCase<IndexInfo> useCase;
        if(index == 1) {
            useCase = new DoubleCacheUseCase<>(new TypeToken<IndexInfo>(){}, params);
        } else {
            useCase = new HttpCacheUseCase<>(new TypeToken<IndexInfo>(){}, params);
        }
        useCase.getHttpRepository().setResponseTransformer(httpTransformer);
        useCase.execute(viewPresenter);
    }

    private class HeadAdapter extends MyBasePagerAdapter<CartoonInfo> {
        @Override
        public View createItemView(ViewGroup parent, int position) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_cartoon_book, parent, false);
            ImageView pic = (ImageView) view.findViewById(R.id.item_cartoon_book_cover);
            TextView title = (TextView) view.findViewById(R.id.item_cartoon_book_title);

            final CartoonInfo item = getItem(position);
            title.setText(Html.fromHtml(item.title));
            Glide.with(activity).load(item.pic).into(pic);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(TAG, "onClick:" + item);
                    Intent intent = new Intent(activity, BrowserActivity.class);
                    intent.putExtra(BaseActivity.KEY_WEB_URL, item.url);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
