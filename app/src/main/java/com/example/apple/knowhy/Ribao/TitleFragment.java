package com.example.apple.knowhy.Ribao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.knowhy.ArticalActivity;
import com.example.apple.knowhy.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Rawght Steven on 8/8/16, 15.
 * Email:rawghtsteven@gmail.com
 */
@SuppressLint("ValidFragment")
public class TitleFragment extends Fragment {

    @BindView(R.id.title_text)TextView textView;
    @BindView(R.id.title_image)ImageView imageView;

    private String title;
    private String url;
    private int id;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ribao_title_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);

        Bundle bundle = getArguments();
        id = bundle.getInt("id");
        title = bundle.getString("title");
        url = bundle.getString("url");

        Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
        textView.setTypeface(Segoe);
        textView.setText(title);
        Picasso.with(getActivity()).load(url).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ArticalActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
