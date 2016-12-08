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
import com.example.apple.knowhy.InternetService;
import com.example.apple.knowhy.R;
import com.example.apple.knowhy.ServiceGenerator;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.http.GET;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/7/16, 14.
 * Email:rawghtsteven@gmail.com
 */
public class Remen extends Fragment {

    public static final String TAG = "热门";
    @BindView(R.id.remen_recycler) RecyclerView recyclerView;
    private Unbinder unbinder;
    private MyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remen_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);

        adapter = new MyAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        loadData();

        return view;
    }

    private void loadData() {
        InternetService service = ServiceGenerator.createService(InternetService.class);
        service.getRemen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RemenBean>() {
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
                    public void onNext(RemenBean remenBean) {
                        adapter.setBean(remenBean);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class MyAdapter extends RecyclerView.Adapter{

        private RemenBean bean;

        public MyAdapter() {
        }

        public void setBean(RemenBean bean) {
            this.bean = bean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.remen_recycler,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MyViewHolder viewHolder = (MyViewHolder) holder;

            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            viewHolder.title.setTypeface(Segoe);
            viewHolder.title.setText(bean.getRecent().get(position).getTitle());

            String thumbnail = bean.getRecent().get(position).getThumbnail();
            Picasso.with(getActivity()).load(thumbnail).error(R.drawable.knowhy).into(viewHolder.imageView);

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

        public class MyViewHolder extends RecyclerView.ViewHolder{

            public TextView title;
            public ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.remen_recycler_title);
                imageView = (ImageView) itemView.findViewById(R.id.remen_recycler_image);
            }
        }
    }
}
