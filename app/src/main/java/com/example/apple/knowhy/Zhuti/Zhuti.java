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
 * Created by Rawght Steven on 8/7/16, 14.
 * Email:rawghtsteven@gmail.com
 */
public class Zhuti extends Fragment {

    @BindView(R.id.zhuti_recycler)RecyclerView recyclerView;
    public static final String TAG = "主题";
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhuti_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        loadData();

        return view;
    }

    private void loadData() {
        InternetService service = ServiceGenerator.createService(InternetService.class);
        service.getZhuti()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ZhutiBean>() {
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
                    public void onNext(ZhutiBean zhutiBean) {
                        MyAdapter adapter = new MyAdapter(zhutiBean.getOthers());
                        recyclerView.setAdapter(adapter);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class MyAdapter extends RecyclerView.Adapter{

        private List<ZhutiBean.Others> beanList;

        public MyAdapter(List<ZhutiBean.Others> beanList) {
            this.beanList = beanList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.zhuti_recycler,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
            viewHolder.zhutiName.setTypeface(Segoe);
            viewHolder.zhutiName.setText(beanList.get(position).getName());
            String url = beanList.get(position).getThumbnail();
            Picasso.with(getActivity()).load(url).error(R.drawable.skull).into(viewHolder.zhutiImage);

            final int id = beanList.get(position).getId();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

        public class MyViewHolder extends RecyclerView.ViewHolder{

            TextView zhutiName;
            ImageView zhutiImage;

            public MyViewHolder(View itemView) {
                super(itemView);
                zhutiName = (TextView) itemView.findViewById(R.id.zhuti_title);
                zhutiImage = (ImageView) itemView.findViewById(R.id.zhuti_image);
            }
        }
    }
}
