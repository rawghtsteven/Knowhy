package com.example.apple.knowhy.Zhuti;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
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

import com.example.apple.knowhy.ArticalActivity;
import com.example.apple.knowhy.InternetService;
import com.example.apple.knowhy.R;
import com.example.apple.knowhy.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/12/16, 12.
 * Email:rawghtsteven@gmail.com
 */
@SuppressLint("ValidFragment")
public class ZhutiItemActivity extends AppCompatActivity{

    @BindView(R.id.zhuti_item_title)TextView title;
    @BindView(R.id.zhuti_item_des)TextView description;
    @BindView(R.id.zhuti_item_recycler)RecyclerView recyclerView;
    @BindView(R.id.zhuti_item_background)ImageView background;

    public static final String TAG = "专栏ITEM";
    private int id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuti_item_activity);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
        Typeface SegoeLight = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP Light.TTF");
        title.setTypeface(Segoe);
        description.setTypeface(SegoeLight);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        loadData(id);
    }

    private void loadData(int id) {
        InternetService service = ServiceGenerator.createService(InternetService.class);
        service.getZhutiItem(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ZhutiItemBean>() {
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
                    public void onNext(ZhutiItemBean bean) {
                        title.setText(bean.getName());
                        description.setText(bean.getDescription());
                        String url = bean.getBackground();
                        Picasso.with(getApplicationContext()).load(url).into(background);

                        List<ZhutiItemBean.Stories> storiesList = bean.getStories();
                        MyAdapter myAdapter = new MyAdapter(storiesList);
                        recyclerView.setAdapter(myAdapter);
                    }
                });
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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
            viewHolder.recyclerTitle.setTypeface(Segoe);
            viewHolder.recyclerTitle.setText(storiesList.get(position).getTitle());
            if (storiesList.get(position).getImages()==null){
                Picasso.with(getApplicationContext()).load(R.drawable.knowhy).into(viewHolder.recyclerImage);
            }else {
                String url =  storiesList.get(position).getImages().get(0);
                Picasso.with(getApplicationContext()).load(url).error(R.drawable.knowhy).into(viewHolder.recyclerImage);
            }

            final int id = storiesList.get(position).getId();
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
            return storiesList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView recyclerTitle;
            ImageView recyclerImage;

            public MyViewHolder(View itemView) {
                super(itemView);
                recyclerTitle = (TextView) itemView.findViewById(R.id.zhuti_item_recycler_title);
                recyclerImage = (ImageView) itemView.findViewById(R.id.zhuti_item_recycler_image);
            }
        }
    }
}
