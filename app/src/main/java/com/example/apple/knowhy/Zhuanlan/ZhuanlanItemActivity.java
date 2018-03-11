package com.example.apple.knowhy.Zhuanlan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/15/16, 11.
 * Email:rawghtsteven@gmail.com
 */
public class ZhuanlanItemActivity extends AppCompatActivity {

    @BindView(R.id.zhuanlan_item_title)TextView title;
    @BindView(R.id.zhuanlan_item_des)TextView description;
    @BindView(R.id.zhuanlan_item_recycler)RecyclerView recyclerView;
    public static final String TAG = "专栏ITEM";
    private int id;
    private String desc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuanlan_item_activity);
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
        desc = intent.getStringExtra("desc");

        loadData(id);
    }

    private void loadData(int id) {
        InternetService service = ServiceGenerator.createService(InternetService.class);
        service.getZhuanlanItem(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ZhuanlanItemBean>() {
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
                    public void onNext(ZhuanlanItemBean zhuanlanItemBean) {
                        Myadapter myadapter = new Myadapter(zhuanlanItemBean);
                        recyclerView.setAdapter(myadapter);
                        title.setText(zhuanlanItemBean.getName());
                        description.setText(desc);
                    }
                });
    }

    private class Myadapter extends RecyclerView.Adapter{

        private ZhuanlanItemBean bean;

        public Myadapter(ZhuanlanItemBean bean) {
            this.bean = bean;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout.zhuanlan_item_recycler,parent,false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            Typeface Segoe = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP.TTF");
            viewHolder.title.setTypeface(Segoe);
            viewHolder.title.setText(bean.getStories().get(position).getTitle());
            Typeface SegoeLight = Typeface.createFromAsset(getAssets(),"fonts/Segoe WP Light.TTF");
            viewHolder.date.setTypeface(SegoeLight);
            viewHolder.date.setText(bean.getStories().get(position).getDisplay_date());

            String thumbnail = bean.getStories().get(position).getImages().get(0);
            Picasso.with(getApplicationContext()).load(thumbnail).error(R.drawable.knowhy).into(viewHolder.imageView);

            final int id = bean.getStories().get(position).getId();
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
            return bean.getStories().size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView title;
            ImageView imageView;
            TextView date;

            public MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.zhuanlan_item_recycler_title);
                imageView = (ImageView) itemView.findViewById(R.id.zhuanlan_item_recycler_image);
                date = (TextView) itemView.findViewById(R.id.zhuanlan_item_date);
            }
        }
    }
}
