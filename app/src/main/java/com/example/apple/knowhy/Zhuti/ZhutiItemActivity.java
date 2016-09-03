package com.example.apple.knowhy.Zhuti;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.List;

/**
 * Created by Rawght Steven on 8/12/16, 12.
 * Email:rawghtsteven@gmail.com
 */
@SuppressLint("ValidFragment")
public class ZhutiItemActivity extends AppCompatActivity{

    private int id;
    private RequestQueue queue;
    private TextView title, description;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuti_item_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        title = (TextView) findViewById(R.id.zhuti_item_title);
        description = (TextView) findViewById(R.id.zhuti_item_des);
        linearLayout = (LinearLayout) findViewById(R.id.zhuti_item_layout);
        recyclerView = (RecyclerView) findViewById(R.id.zhuti_item_recycler);
        queue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        String url = "http://news-at.zhihu.com/api/4/theme/"+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ZhutiItemBean bean = gson.fromJson(response,ZhutiItemBean.class);
                display(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ZHUTI ITEM ERROR","");
            }
        });
        queue.add(stringRequest);
    }


    private void display(final ZhutiItemBean bean) {
        Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
        Typeface SegoeLight = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP Light.TTF");
        title.setTypeface(Segoe);
        description.setTypeface(SegoeLight);
        title.setText(bean.getName());
        description.setText(bean.getDescription());
        String url = bean.getBackground();
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Bitmap response) {
                BitmapDrawable drawable = new BitmapDrawable(response);
                linearLayout.setBackground(drawable);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("IMAGE FAILED!","");
            }
        });
        queue.add(imageRequest);

        List<ZhutiItemBean.Stories> storiesList = bean.getStories();
        MyAdapter myAdapter = new MyAdapter(storiesList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myAdapter);

    }


    private class MyAdapter extends RecyclerView.Adapter{

        private List<ZhutiItemBean.Stories> storiesList;

        public MyAdapter(List<ZhutiItemBean.Stories> storiesList) {
            this.storiesList = storiesList;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout.zhuti_item_recycler,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            TextView recyclerTitle = (TextView) holder.itemView.findViewById(R.id.zhuti_item_recycler_title);
            final ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.zhuti_item_recycler_image);
            Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
            recyclerTitle.setTypeface(Segoe);
            recyclerTitle.setText(storiesList.get(position).getTitle());
            if (storiesList.get(position).getImages()==null){
                imageView.setImageResource(R.drawable.knowhy);
            }else {
                String url =  storiesList.get(position).getImages().get(0);
                ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ZHUTI ITEM ERROR","IMAGE FAILED");
                        imageView.setImageResource(R.drawable.knowhy);
                    }
                });
                queue.add(imageRequest);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = storiesList.get(position).getId();
                    Intent intent = new Intent(getApplication(),ArticalActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return storiesList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
