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
import com.example.apple.knowhy.InternetService;
import com.example.apple.knowhy.R;
import com.example.apple.knowhy.ServiceGenerator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/7/16, 13.
 * Email:rawghtsteven@gmail.com
 */
public class Ribao extends Fragment {

    @BindView(R.id.ribao_pager)ViewPager viewPager;
    @BindView(R.id.ribao_recycler)RecyclerView recyclerView;

    public static final String TAG = "日报";
    private static int currentItem = 0;
    private Unbinder unbinder;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<InfoBean.Stories> storiesList = new ArrayList<>();
    private RibaoAdapter adapter;
    private RecyclerAdapter recyclerAdapter;

    public static void setCurrentItem(int currentItem) {
        Ribao.currentItem = currentItem;
    }

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ribao_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);

        adapter = new RibaoAdapter(getChildFragmentManager());
        adapter.setFragmentList(fragmentList);
        viewPager.setAdapter(adapter);

        recyclerAdapter = new RecyclerAdapter();
        recyclerAdapter.setStoriesList(storiesList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(recyclerAdapter);

        requestData();
        viewPager.setCurrentItem(currentItem);
        startAutoScroll();
        return view;
    }

    private void startAutoScroll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentItem++;
                if (currentItem == 5){
                    setCurrentItem(0);
                }
                viewPager.setCurrentItem(currentItem);
                Log.e("CURRENT ITEM", String.valueOf(currentItem));
                handler.postDelayed(this,7000);
            }
        }).start();
    }

    private void requestData() {

        InternetService ribaoService = ServiceGenerator.createService(InternetService.class);
        ribaoService
                .getRibao()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InfoBean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"Data loaded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"Data load failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(InfoBean infoBean) {

                        List<InfoBean.Top_stories> topStoriesList = infoBean.getTop_stories();
                        List<Integer> idList = new ArrayList<>();
                        for (int i=0;i<topStoriesList.size();i++){
                            String title = infoBean.getTop_stories().get(i).getTitle();
                            String image = infoBean.getTop_stories().get(i).getImage();
                            int id = infoBean.getTop_stories().get(i).getId();
                            TitleFragment titleFragment = new TitleFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("title",title);
                            bundle.putString("url",image);
                            bundle.putInt("id",id);
                            titleFragment.setArguments(bundle);
                            fragmentList.add(titleFragment);
                            idList.add(id);
                        }
                        adapter.notifyDataSetChanged();

                        storiesList = infoBean.getStories();
                        recyclerAdapter.setStoriesList(storiesList);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public class RibaoAdapter extends FragmentPagerAdapter{

        private List<Fragment> fragmentList;

        public RibaoAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setFragmentList(List<Fragment> fragmentList) {
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


    public class RecyclerAdapter extends RecyclerView.Adapter{

        private List<InfoBean.Stories> storiesList;

        public RecyclerAdapter() {
        }

        public void setStoriesList(List<InfoBean.Stories> storiesList) {
            this.storiesList = storiesList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.ribao_recycler,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            viewHolder.textView.setTypeface(Segoe);
            viewHolder.textView.setText(storiesList.get(position).getTitle());
            Picasso.with(getActivity()).load(storiesList.get(position).getImages().get(0)).error(R.drawable.knowhy).into(viewHolder.imageView);
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

        public class MyViewHolder extends RecyclerView.ViewHolder{

            TextView textView;
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.list_title);
                imageView = (ImageView) itemView.findViewById(R.id.list_image);
            }
        }
    }
}
