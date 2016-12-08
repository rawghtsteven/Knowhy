package com.example.apple.knowhy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/11/16, 10.
 * Email:rawghtsteven@gmail.com
 */
@SuppressLint("ValidFragment")
public class CommentActivity extends AppCompatActivity{

    @BindView(R.id.comment_recycler)RecyclerView recyclerView;
    @BindView(R.id.pinglun)TextView pinglun;

    public static final String TAG = "评论";
    private List<CommentBean.Comment> commentList = new ArrayList<>();
    private int id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
        assert pinglun != null;
        pinglun.setTypeface(Segoe);

        LinearLayoutManager manager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(manager);

        loadData(id);
    }

    private void loadData(int id) {
        InternetService service = ServiceGenerator.createService(InternetService.class);
        service.getLongComments(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentBean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"Long comment loaded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Long comment load failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CommentBean commentBean) {
                        commentList = commentBean.getComments();
                    }
                });

        InternetService internetService = ServiceGenerator.createService(InternetService.class);
        internetService.getShortComments(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentBean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"Short comment loaded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Short comment load failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CommentBean commentBean) {
                        List<CommentBean.Comment> commentList2 = commentBean.getComments();
                        commentList.addAll(commentList2);
                        MyAdapter myAdapter = new MyAdapter(commentList);
                        recyclerView.setAdapter(myAdapter);
                    }
                });
    }

    private class MyAdapter extends RecyclerView.Adapter{

        private List<CommentBean.Comment> commentList;

        public MyAdapter(List<CommentBean.Comment> commentList) {
            this.commentList = commentList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout.comment_recycler,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            if (commentList==null){
                viewHolder.userName.setText("目前还没有评论");
            }else {
                viewHolder.userName.setText(commentList.get(position).getAuthor());
                viewHolder.comment.setText(commentList.get(position).getContent());
                String url = commentList.get(position).getAvatar();
                Picasso.with(getApplicationContext()).load(url).error(R.drawable.skull).into(viewHolder.userPortrait);
            }
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView userName, comment;
            ImageView userPortrait;

            public MyViewHolder(View itemView) {
                super(itemView);
                userName = (TextView) itemView.findViewById(R.id.user_name);
                comment = (TextView) itemView.findViewById(R.id.comment);
                userPortrait = (ImageView) itemView.findViewById(R.id.user_portrait);

                Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
                userName.setTypeface(Segoe);
                Typeface SegoeSemiLight = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP SemiLight.TTF");
                comment.setTypeface(SegoeSemiLight);
            }
        }
    }


}
