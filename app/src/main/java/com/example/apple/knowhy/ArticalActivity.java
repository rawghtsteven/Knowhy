package com.example.apple.knowhy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/9/16, 14.
 * Email:rawghtsteven@gmail.com
 */
@SuppressLint("ValidFragment")
public class ArticalActivity extends AppCompatActivity{

    @BindView(R.id.article_image)ImageView imageView;
    @BindView(R.id.article_title)TextView textView;
    @BindView(R.id.article_web)WebView webView;
    @BindView(R.id.share)Button share;
    @BindView(R.id.comment)Button comment;
    @BindView(R.id.like)Button like;

    public static final String TAG = "文章";
    private int id;
    private String shareUrl = "";

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artical_activity);
        ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
        textView.setTypeface(Segoe);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        loadData(id);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String shareText = "来自Knowhy的阅读分享";
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,shareText+"\n"+shareUrl);
                intent.setType("text/html");
                startActivity(Intent.createChooser(intent,"选择你要分享的应用"));
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),CommentActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InternetService service = ServiceGenerator.createService(InternetService.class);
                service.getExtraInfo(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ExtraInfoBean>() {
                            @Override
                            public void onCompleted() {
                                Log.e(TAG,"Extra Info loaded");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG,"Extra info load failed");
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(ExtraInfoBean extraInfoBean) {
                                Toast.makeText(getApplicationContext(),"点赞数:"+extraInfoBean.getPopularity(),Toast.LENGTH_SHORT).show();
                                like.setBackgroundResource(R.drawable.liked);
                            }
                        });
            }
        });
    }

    private void loadData(int id) {
        InternetService service = ServiceGenerator.createService(InternetService.class);
        service.getArtical(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArticalBean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"Data loaded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Data load failed");
                        e.printStackTrace();
                    }

                    @SuppressLint("SetJavaScriptEnabled")
                    @Override
                    public void onNext(ArticalBean bean) {
                        String body = bean.getBody();
                        String title = bean.getTitle();
                        String url = bean.getImage();

                        List<String> js = bean.getJs();
                        List<String> css = bean.getCss();
                        String html = " <link rel=\"stylesheet\" type=\"text/css\" href=\""+css.get(0)+"\">"+body;

                        String share_url = bean.getShare_url();
                        Log.e("SHARE_URL",share_url);
                        setShareUrl(share_url);

                        WebSettings settings = webView.getSettings();
                        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                        settings.setJavaScriptEnabled(true);
                        webView.loadDataWithBaseURL(null,html,"text/html","utf-8",null);

                        textView.setText(title);
                        Picasso.with(getApplicationContext()).load(url).error(R.drawable.xperia3).into(imageView);
                    }
                });
    }
}
