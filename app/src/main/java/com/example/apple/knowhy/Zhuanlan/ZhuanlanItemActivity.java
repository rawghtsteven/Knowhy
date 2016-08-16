package com.example.apple.knowhy.Zhuanlan;

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
import com.example.apple.knowhy.ArticalActivity;
import com.example.apple.knowhy.R;
import com.google.gson.Gson;

/**
 * Created by Rawght Steven on 8/15/16, 11.
 * Email:rawghtsteven@gmail.com
 */
public class ZhuanlanItemActivity extends AppCompatActivity {

    private int id;
    private RequestQueue queue;
    private TextView title, description;
    private RecyclerView recyclerView;
    private String desc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuanlan_item_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        title = (TextView) findViewById(R.id.zhuanlan_item_title);
        description = (TextView) findViewById(R.id.zhuanlan_item_des);
        recyclerView = (RecyclerView) findViewById(R.id.zhuanlan_item_recycler);
        queue = Volley.newRequestQueue(this);
        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        desc = intent.getStringExtra("desc");

        String url = "http://news-at.zhihu.com/api/3/section/"+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ZhuanlanItemBean bean = gson.fromJson(response,ZhuanlanItemBean.class);
                display(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR IN ZHUANLAN","DATA ACCESS FAILED");
            }
        });
        queue.add(stringRequest);
    }

    private void display(ZhuanlanItemBean bean) {
        Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
        Typeface SegoeLight = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP Light.TTF");
        title.setTypeface(Segoe);
        title.setText(bean.getName());
        description.setTypeface(SegoeLight);
        description.setText(desc);
        Myadapter myadapter = new Myadapter(bean);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myadapter);
    }

    private class Myadapter extends RecyclerView.Adapter{

        private ZhuanlanItemBean bean;

        public Myadapter(ZhuanlanItemBean bean) {
            this.bean = bean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout.zhuanlan_item_recycler,parent,false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView title = (TextView) holder.itemView.findViewById(R.id.zhuanlan_item_recycler_title);
            final ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.zhuanlan_item_recycler_image);
            TextView date = (TextView) holder.itemView.findViewById(R.id.zhuanlan_item_date);
            Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
            title.setTypeface(Segoe);
            title.setText(bean.getStories().get(position).getTitle());
            Typeface SegoeLight = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP Light.TTF");
            date.setTypeface(SegoeLight);
            date.setText(bean.getStories().get(position).getDisplay_date());
            String thumbnail = bean.getStories().get(position).getImages().get(0);
            ImageRequest imageRequest = new ImageRequest(thumbnail, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imageView.setImageBitmap(response);
                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR IN ZHUANLAN","RECYCLER IMAGE FAILED");
                    imageView.setImageResource(R.drawable.knowhy);
                }
            });
            queue.add(imageRequest);

            final int id = bean.getStories().get(position).getId();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(),ArticalActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return bean.getStories().size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
