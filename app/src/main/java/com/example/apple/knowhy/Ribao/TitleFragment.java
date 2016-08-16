package com.example.apple.knowhy.Ribao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.knowhy.ArticalActivity;
import com.example.apple.knowhy.HttpUtil;
import com.example.apple.knowhy.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Rawght Steven on 8/8/16, 15.
 * Email:rawghtsteven@gmail.com
 */
@SuppressLint("ValidFragment")
public class TitleFragment extends Fragment {

    private TextView textView;
    private ImageView imageView;
    private String title;
    private String url;
    private int id;
    Handler handler = new Handler(){
      public void handleMessage(Message message){
          switch (message.what){
              case 0x321:
                  Bitmap bitmap = (Bitmap) message.obj;
                  imageView.setImageBitmap(bitmap);
                  imageView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Intent intent = new Intent(getActivity(),ArticalActivity.class);
                          intent.putExtra("id",id);
                          startActivity(intent);
                      }
                  });
                  break;
          }
      }
    };

    @SuppressLint("ValidFragment")
    public TitleFragment(String title,String url,int id) {
        this.title = title;
        this.url = url;
        this.id = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ribao_title_fragment,container,false);
        textView = (TextView) view.findViewById(R.id.title_text);
        imageView = (ImageView) view.findViewById(R.id.title_image);
        Typeface Segoe = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Segoe WP.TTF");
        textView.setTypeface(Segoe);
        textView.setText(title);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream stream = HttpUtil.getImageViewInputStream(url);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    Message message = new Message();
                    message.what = 0x321;
                    message.obj = bitmap;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }


}
