package com.example.apple.knowhy.Remen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Rawght Steven on 8/7/16, 14.
 * Email:rawghtsteven@gmail.com
 */
public class Remen extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remen_fragment,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.remen_recycler);
        queue = Volley.newRequestQueue(getActivity());
        String url = "http://news-at.zhihu.com/api/3/news/hot";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                RemenBean bean = gson.fromJson(response,RemenBean.class);
                display(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR IN REMEN","DATA ACCESS FAILED");
            }
        });
        queue.add(stringRequest);
        return view;
    }

    private void display(RemenBean bean) {
        MyAdapter adapter = new MyAdapter(bean);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class MyAdapter extends RecyclerView.Adapter{

        private RemenBean bean;

        public MyAdapter(RemenBean bean) {
            this.bean = bean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.remen_recycler,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView title = (TextView) holder.itemView.findViewById(R.id.remen_recycler_title);
            final ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.remen_recycler_image);
            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            title.setTypeface(Segoe);
            title.setText(bean.getRecent().get(position).getTitle());

            String thumbnail = bean.getRecent().get(position).getThumbnail();
            ImageRequest imageRequest = new ImageRequest(thumbnail, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imageView.setImageBitmap(response);
                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR IN REMEN","RECYCLER IMAGE FAILED");
                    imageView.setImageResource(R.drawable.knowhy);
                }
            });
            queue.add(imageRequest);

            final int id = bean.getRecent().get(position).getNews_id();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),ArticalActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return bean.getRecent().size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
