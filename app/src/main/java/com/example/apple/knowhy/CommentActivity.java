package com.example.apple.knowhy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rawght Steven on 8/11/16, 10.
 * Email:rawghtsteven@gmail.com
 */
@SuppressLint("ValidFragment")
public class CommentActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RequestQueue queue;
    private List<CommentBean.Comment> commentList = new ArrayList<>();
    private int id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        TextView pinglun = (TextView) findViewById(R.id.pinglun);
        Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
        assert pinglun != null;
        pinglun.setTypeface(Segoe);

        recyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        queue = Volley.newRequestQueue(this);

        String url = "http://news-at.zhihu.com/api/4/story/"+id+"/long-comments";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                CommentBean bean = gson.fromJson(response,CommentBean.class);
                commentList = bean.getComments();

                //MyAdapter myAdapter = new MyAdapter(commentList);
                //LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                //recyclerView.setLayoutManager(manager);
                //recyclerView.setAdapter(myAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOAD COMMENT FAILED!","");
            }
        });
        queue.add(stringRequest);

        String url2 = "http://news-at.zhihu.com/api/4/story/"+id+"/short-comments";
        StringRequest stringRequest2 = new StringRequest(url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SHORT COMMENT",response);
                Gson gson = new Gson();
                CommentBean bean = gson.fromJson(response,CommentBean.class);
                List<CommentBean.Comment> commentList2 = bean.getComments();
                commentList.addAll(commentList2);
                MyAdapter myAdapter = new MyAdapter(commentList);
                LinearLayoutManager manager = new LinearLayoutManager(getApplication());
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(myAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR","LOAD SHORT COMMENT FAILED!");
            }
        });
        queue.add(stringRequest2);
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
            TextView userName = (TextView) holder.itemView.findViewById(R.id.user_name);
            TextView comment = (TextView) holder.itemView.findViewById(R.id.comment);
            final ImageView userPortrait = (ImageView) holder.itemView.findViewById(R.id.user_portrait);
            if (commentList==null){
                userName.setText("目前还没有评论");
            }else {
                Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
                userName.setTypeface(Segoe);
                userName.setText(commentList.get(position).getAuthor());
                Typeface SegoeSemiLight = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP SemiLight.TTF");
                comment.setTypeface(SegoeSemiLight);
                comment.setText(commentList.get(position).getContent());
                String url = commentList.get(position).getAvatar();
                ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        userPortrait.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("IMAGE LOAD FAILED!","");
                        userPortrait.setImageResource(R.drawable.skull);
                    }
                });
                queue.add(imageRequest);
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
            }
        }
    }


}
