package com.example.apple.knowhy.Ribao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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

import com.example.apple.knowhy.ArticalActivity;
import com.example.apple.knowhy.InternetService;
import com.example.apple.knowhy.R;
import com.example.apple.knowhy.ServiceGenerator;
import com.mmga.metroloading.MetroLoadingView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rawght Steven on 8/7/16, 13.
 * Email:rawghtsteven@gmail.com
 */
public class Ribao extends Fragment {

    @BindView(R.id.ribao_recycler)RecyclerView recyclerView;

    public static final String TAG = "日报";
    private static int current = 1;
    private Unbinder unbinder;
    private RecyclerAdapter adapter;

    Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ribao_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        requestData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    Date now = new Date(System.currentTimeMillis());
                    Date date = getDateBefore(now, current);
                    current = current+1;
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    String time = format.format(date);
                    Log.e(TAG,"TIME "+time);
                    requestHistory(time);
                }
            }
        });

        return view;
    }

    public static Date getDateBefore(Date d, int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
    }

    private void requestHistory(String time){
        InternetService ribaoService = ServiceGenerator.createService(InternetService.class);
        ribaoService.getHistory(time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InfoBean>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"History data loaded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"History data load failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(InfoBean infoBean) {
                        List<InfoBean.Stories> storiesList = infoBean.getStories();
                        adapter.updateList(storiesList);
                        adapter.notifyDataSetChanged();
                    }
                });
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
                        List<Fragment> fragmentList = new ArrayList<>();
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
                        List<InfoBean.Stories> storiesList = infoBean.getStories();
                        adapter = new RecyclerAdapter(getActivity(), storiesList, fragmentList);
                        recyclerView.setAdapter(adapter);
                        MetroLoadingView view = (MetroLoadingView) getActivity().findViewById(R.id.loadingView);
                        view.stop();
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

    public class RecyclerAdapter extends RecyclerView.Adapter{

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private static final int TYPE_FOOTER = 2;
        private Context context;
        private LayoutInflater mLayoutInflater;

        private List<InfoBean.Stories> storiesList;
        private List<Fragment> fragmentList;

        public RecyclerAdapter(Context context, List<InfoBean.Stories> storiesList, List<Fragment> fragmentList) {
            this.context = context;
            this.storiesList = storiesList;
            this.fragmentList = fragmentList;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == TYPE_ITEM){
                view = mLayoutInflater.inflate(R.layout.ribao_recycler, parent, false);
                return  new MyViewHolder(view);
            }else if (viewType == TYPE_HEADER){
                view = mLayoutInflater.inflate(R.layout.ribao_header_viewpager, parent, false);
                return new HeaderViewHolder(view);
            }else if (viewType == TYPE_FOOTER){
                view = mLayoutInflater.inflate(R.layout.ribao_footer, parent, false);
                return new FooterViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyViewHolder){
                int index = position-1;
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.imageView.setImageResource(R.drawable.knowhy);
                final String url = storiesList.get(index).getImages().get(0);
                viewHolder.imageView.setTag(url);

                Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
                viewHolder.textView.setTypeface(Segoe);

                if (url.equals(viewHolder.imageView.getTag())){
                    viewHolder.textView.setText(storiesList.get(index).getTitle());
                    Picasso.with(getActivity()).load(storiesList.get(index).getImages().get(0)).error(R.drawable.knowhy).into(viewHolder.imageView);
                    final int id = storiesList.get(index).getId();
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(),ArticalActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                    });
                }

            }else if (holder instanceof HeaderViewHolder){
                Log.e("VIEWPAGER", String.valueOf(position));
                HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
                RibaoAdapter adapter = new RibaoAdapter(getChildFragmentManager(), fragmentList);
                viewHolder.pager.setAdapter(adapter);
            }else if (holder instanceof FooterViewHolder){
                FooterViewHolder viewHolder = (FooterViewHolder) holder;
                Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
                viewHolder.textView.setTypeface(Segoe);
            }
        }

        public void updateList(List<InfoBean.Stories> stories){
            storiesList.addAll(stories);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return storiesList.size()+1+1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0){
                return TYPE_HEADER;
            }else if (position == getItemCount()-1){
                return TYPE_FOOTER;
            }else {
                return TYPE_ITEM;
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.list_title) TextView textView;
            @BindView(R.id.list_image) ImageView imageView;

            MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.ribao_pager)ViewPager pager;

            HeaderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }

        class FooterViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.ribao_footer_text)TextView textView;

            FooterViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
