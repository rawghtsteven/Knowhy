package com.example.apple.knowhy;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Rawght Steven on 8/17/16, 10.
 * Email:rawghtsteven@gmail.com
 */
public class EmptyFragment extends Fragment {

    onInternetAccessChanged changed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.empty_fragment,container,false);
        Button refresh = (Button) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager con=(ConnectivityManager)getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
                boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
                if (wifi|internet){
                    changed.accessChanged(true);
                }else {
                    Toast.makeText(getActivity(),"刷新失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public interface onInternetAccessChanged{
        void accessChanged(boolean InternetState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        changed = (onInternetAccessChanged) activity;
    }
}
