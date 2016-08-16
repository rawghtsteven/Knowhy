package com.example.apple.knowhy.Zhuti;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rawght Steven on 8/7/16, 14.
 * Email:rawghtsteven@gmail.com
 */
public class Zhuti extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhuti_fragment,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.zhuti_recycler);
        queue = Volley.newRequestQueue(getActivity());
        String url = "http://news-at.zhihu.com/api/4/themes";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ZHUTI ERROR","FAILED ACCESS");
            }
        });

        queue.add(stringRequest);
        return view;
    }

    private void parseJSON(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONArray jsonArray = object.getJSONArray("others");
            List<ZhutiBean> beanList = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                ZhutiBean bean = new ZhutiBean();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String thumbnail = jsonObject.getString("thumbnail");
                String description = jsonObject.getString("description");
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");

                bean.setThumbnail(thumbnail);
                bean.setDescription(description);
                bean.setId(id);
                bean.setName(name);
                beanList.add(bean);
            }
            display(beanList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void display(List<ZhutiBean> beanList) {
        MyAdapter adapter = new MyAdapter(beanList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class MyAdapter extends RecyclerView.Adapter{
        private List<ZhutiBean> beanList;

        public MyAdapter(List<ZhutiBean> beanList) {
            this.beanList = beanList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.zhuti_recycler,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            TextView zhutiName = (TextView) holder.itemView.findViewById(R.id.zhuti_title);
            final ImageView zhutiImage = (ImageView) holder.itemView.findViewById(R.id.zhuti_image);
            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            zhutiName.setTypeface(Segoe);
            zhutiName.setText(beanList.get(position).getName());
            String url = beanList.get(position).getThumbnail();
            ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    zhutiImage.setImageBitmap(response);
                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ZHUTI ERROR","IMAGE FAILED!");
                    zhutiImage.setImageResource(R.drawable.skull);
                }
            });
            queue.add(imageRequest);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = beanList.get(position).getId();
                    Intent intent = new Intent(getActivity(),ZhutiItemActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return beanList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView zhutiName;
            ImageView zhutiImage;

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
