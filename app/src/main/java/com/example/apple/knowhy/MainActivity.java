package com.example.apple.knowhy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.apple.knowhy.Remen.Remen;
import com.example.apple.knowhy.Ribao.Ribao;
import com.example.apple.knowhy.Zhuanlan.Zhuanlan;
import com.example.apple.knowhy.Zhuti.Zhuti;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PAGE_NUM = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSystemBar(this);

        TextView KNOWHY = (TextView) findViewById(R.id.knowhy);
        Typeface SegoeSemibold = Typeface.createFromAsset(this.getAssets(),"fonts/Segoe WP Semibold.TTF");
        assert KNOWHY != null;
        KNOWHY.setTypeface(SegoeSemibold);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        List<Fragment> fragmentList = new ArrayList<>();
        Fragment ribao = new Ribao();
        Fragment zhuti = new Zhuti();
        Fragment zhuanlan = new Zhuanlan();
        Fragment remen  =  new Remen();
        fragmentList.add(ribao);
        fragmentList.add(zhuti);
        fragmentList.add(remen);
        fragmentList.add(zhuanlan);

        List<String> titleList = new ArrayList<>();
        titleList.add("今日");
        titleList.add("主题");
        titleList.add("热门");
        titleList.add("专栏");

        assert viewPager != null;
        viewPager.setOffscreenPageLimit(PAGE_NUM);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),fragmentList,titleList);
        viewPager.setAdapter(mainPagerAdapter);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
    }

    private class MainPagerAdapter extends FragmentPagerAdapter{

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titleList = new ArrayList<>();

        public MainPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titleList = titleList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return PAGE_NUM;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position%titleList.size());
        }
    }

    public static void initSystemBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);}
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(R.color.white);
    }

    @TargetApi(19)

    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}