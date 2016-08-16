package com.example.apple.knowhy.Zhuanlan;

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
import com.example.apple.knowhy.R;
import com.google.gson.Gson;

/**
 * Created by Rawght Steven on 8/7/16, 14.
 * Email:rawghtsteven@gmail.com
 */
public class Zhuanlan extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhuanlan_fragment,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.zhuanlan_recycler);
        queue = Volley.newRequestQueue(getActivity());
        String url = "http://news-at.zhihu.com/api/3/sections";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ZhuanlanBean bean = gson.fromJson(response,ZhuanlanBean.class);
                display(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR IN ZHUANLAN","DATA ACCESS FAILED");
            }
        });
        queue.add(stringRequest);
        return view;
    }

    private void display(ZhuanlanBean bean) {
        Myadapter myadapter = new Myadapter(bean);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myadapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class Myadapter extends RecyclerView.Adapter{
        private ZhuanlanBean bean;

        public Myadapter(ZhuanlanBean bean) {
            this.bean = bean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.zhuanlan_recycler,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView title = (TextView) holder.itemView.findViewById(R.id.zhuanlan_recycler_title);
            final ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.zhuanlan_recycler_image);
            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            title.setTypeface(Segoe);
            title.setText(bean.getData().get(position).getName());
            String thumbnail = bean.getData().get(position).getThumbnail();
            ImageRequest imageRequest = new ImageRequest(thumbnail, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imageView.setImageBitmap(response);
                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR IN ZHUANLAN","IMAGE FAILED");
                    imageView.setImageResource(R.drawable.knowhy);
                }
            });
            queue.add(imageRequest);

            final int id = bean.getData().get(position).getId();
            final String desc = bean.getData().get(position).getDescription();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),ZhuanlanItemActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("desc",desc);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return bean.getData().size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
