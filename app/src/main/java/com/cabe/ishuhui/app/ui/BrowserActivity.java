package com.cabe.ishuhui.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebView;

import com.cabe.ishuhui.app.R;
import com.cabe.lib.cache.CacheSource;
import com.cabe.lib.cache.http.HttpTransformer;
import com.cabe.lib.cache.http.RequestParams;
import com.cabe.lib.cache.impl.DoubleCacheUseCase;
import com.cabe.lib.cache.impl.HttpCacheUseCase;
import com.cabe.lib.cache.interactor.impl.SimpleViewPresenter;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Browser Activity
 * Created by cabe on 16/5/11.
 */
public class BrowserActivity extends BaseActivity {
    private final static String ENCODING = "UTF-8";

    @Bind(R.id.activity_browser_container)
    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);
        webView.getSettings().setDefaultTextEncodingName(ENCODING);

        loadData(getIntent().getStringExtra(KEY_WEB_URL));
    }

    private void setInfo(String html) {
        webView.loadData(html, "text/html; charset=UTF-8", null);
    }

    private void loadData(String url) {
        Log.w(TAG, "loadData:" + url);
        RequestParams params = new RequestParams();
        params.host = url;
        DoubleCacheUseCase<String> useCase = new HttpCacheUseCase<>(new TypeToken<String>(){}, params);
        useCase.getHttpRepository().setResponseTransformer(new HttpTransformer<String>() {
            @Override
            public String buildData(String responseStr) {

                Document doc = Jsoup.parse(responseStr);
                //隐藏顶部
                hideElement(doc.getElementById("header"));
                //隐藏标题区
                hideElement(doc.getElementById("mbx-dh"));
                //隐藏吐槽区
                hideElement(doc.getElementById("article-comments"));
                //隐藏底部
                hideElement(doc.getElementById("footer"));
                hideElements(doc.select("div.page-header"));
                hideElements(doc.select("div.article-footer"));

                return doc.html();
            }
        });
        useCase.execute(new SimpleViewPresenter<String>(){
            @Override
            public void load(CacheSource from, String data) {
                setInfo(data);
            }
        });
    }

    private void hideElements(Elements elements) {
        if(elements != null) {
            elements.attr("style", "display:none");
        }
    }

    private void hideElement(Element element) {
        if(element != null) {
            element.attr("style", "display:none");
        }
    }
}
