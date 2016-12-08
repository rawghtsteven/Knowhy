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
import com.example.apple.knowhy.InternetService;
import com.example.apple.knowhy.R;
import com.example.apple.knowhy.ServiceGenerator;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/7/16, 14.
 * Email:rawghtsteven@gmail.com
 */
public class Zhuanlan extends Fragment {

    @BindView(R.id.zhuanlan_recycler)RecyclerView recyclerView;
    public static final String TAG = "专栏";
    private RequestQueue queue;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhuanlan_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        loadData();

        return view;
    }

    private void loadData() {
        InternetService service = ServiceGenerator.createService(InternetService.class);
        service.getZhuanlan()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ZhuanlanBean>() {
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
                    public void onNext(ZhuanlanBean bean) {
                        Myadapter myadapter = new Myadapter(bean);
                        recyclerView.setAdapter(myadapter);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            MyViewHolder viewHolder = (MyViewHolder) holder;
            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            viewHolder.title.setTypeface(Segoe);
            viewHolder.title.setText(bean.getData().get(position).getName());
            String thumbnail = bean.getData().get(position).getThumbnail();
            Picasso.with(getActivity()).load(thumbnail).error(R.drawable.knowhy).into(viewHolder.imageView);

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

            TextView title;
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.zhuanlan_recycler_title);
                imageView = (ImageView) itemView.findViewById(R.id.zhuanlan_recycler_image);
            }
        }
    }
}
