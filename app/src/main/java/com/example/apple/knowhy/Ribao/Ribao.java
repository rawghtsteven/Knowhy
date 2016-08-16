package com.example.apple.knowhy.Ribao;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.android.volley.toolbox.Volley;
import com.example.apple.knowhy.ArticalActivity;
import com.example.apple.knowhy.HttpUtil;
import com.example.apple.knowhy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rawght Steven on 8/7/16, 13.
 * Email:rawghtsteven@gmail.com
 */
public class Ribao extends Fragment {
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private RequestQueue queue;

    Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case 0x123:
                    List<Fragment> fragmentList = new ArrayList<>();
                    InfoBean bean = (InfoBean) message.obj;
                    List<InfoBean.Top_stories> topStoriesList = bean.getTop_stories();
                    List<Integer> idList = new ArrayList<>();
                    for (int i=0;i<topStoriesList.size();i++){
                        String title = bean.getTop_stories().get(i).getTitle();
                        String image = bean.getTop_stories().get(i).getImage();
                        int id = bean.getTop_stories().get(i).getId();
                        TitleFragment titleFragment = new TitleFragment(title,image,id);
                        fragmentList.add(titleFragment);
                        idList.add(id);
                    }
                    RibaoAdapter adapter = new RibaoAdapter(getChildFragmentManager(),fragmentList);
                    viewPager.setAdapter(adapter);
                    List<InfoBean.Stories> storiesList = bean.getStories();
                    RecyclerAdapter recyclerAdapter = new RecyclerAdapter(storiesList);
                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(recyclerAdapter);
                    break;
                case 0x345:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ribao_fragment,container,false);
        viewPager = (ViewPager) view.findViewById(R.id.ribao_pager);
        recyclerView = (RecyclerView) view.findViewById(R.id.ribao_recycler);
        queue = Volley.newRequestQueue(getActivity());
        requestData();
        return view;
    }

    private void requestData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://news-at.zhihu.com/api/4/news/latest";
                String response = HttpUtil.HttpGet(url,"GET");
                Log.e("RESPONSE",response);
                InfoBean bean = parseJSON(response);
                Message message = new Message();
                message.what = 0x123;
                message.obj = bean;
                handler.sendMessage(message);
            }
        }).start();
    }

    private InfoBean parseJSON(String response) {
        InfoBean bean = new InfoBean();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int date = jsonObject.getInt("date");
            bean.setDate(date);
            JSONArray stories = jsonObject.getJSONArray("stories");
            JSONArray top_stories = jsonObject.getJSONArray("top_stories");
            List<InfoBean.Stories> storiesList = new ArrayList<>();
            for (int i=0;i<stories.length();i++){
                InfoBean.Stories storis = new InfoBean.Stories();
                JSONObject object = stories.getJSONObject(i);
                String title = object.getString("title");
                storis.setTitle(title);
                JSONArray images = object.getJSONArray("images");
                List<String> imageList = new ArrayList<>();
                for (int j=0;j<images.length();j++){
                    String image = (String) images.get(j);
                    imageList.add(image);
                }
                storis.setImages(imageList);
                int id = object.getInt("id");
                storis.setId(id);
                storiesList.add(storis);
            }
            bean.setStories(storiesList);

            List<InfoBean.Top_stories> topStoriesList = new ArrayList<>();
            for (int i=0;i<top_stories.length();i++){
                InfoBean.Top_stories topStories1 = new InfoBean.Top_stories();
                JSONObject object = top_stories.getJSONObject(i);
                String title = object.getString("title");
                String image = object.getString("image");
                int id = object.getInt("id");
                topStories1.setImage(image);
                topStories1.setTitle(title);
                topStories1.setId(id);
                topStoriesList.add(topStories1);
            }
            bean.setTop_stories(topStoriesList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class RibaoAdapter extends FragmentPagerAdapter{

        private List<Fragment> fragmentList;

        public RibaoAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }


    private class RecyclerAdapter extends RecyclerView.Adapter{

        private List<InfoBean.Stories> storiesList;

        public RecyclerAdapter(List<InfoBean.Stories> storiesList) {
            this.storiesList = storiesList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.ribao_recycler,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView.findViewById(R.id.list_title);
            final ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.list_image);
            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            textView.setTypeface(Segoe);
            textView.setText(storiesList.get(position).getTitle());
            ImageRequest imageRequest = new ImageRequest(storiesList.get(position).getImages().get(0),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            imageView.setImageBitmap(response);
                        }
                    }, 0, 0, Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    imageView.setImageResource(R.drawable.knowhy);
                }
            });
            queue.add(imageRequest);
            final int id = storiesList.get(position).getId();
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
            return storiesList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView textView;
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
