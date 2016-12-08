package com.example.apple.knowhy;

import com.example.apple.knowhy.Remen.RemenBean;
import com.example.apple.knowhy.Ribao.InfoBean;
import com.example.apple.knowhy.Zhuanlan.ZhuanlanBean;
import com.example.apple.knowhy.Zhuanlan.ZhuanlanItemBean;
import com.example.apple.knowhy.Zhuti.ZhutiBean;
import com.example.apple.knowhy.Zhuti.ZhutiItemBean;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Rawght Steven on 07/12/2016, 19.
 * Email:rawghtsteven@gmail.com
 */

public interface InternetService {

    @GET("4/news/latest")
    Observable<InfoBean> getRibao();

    @GET("4/news/{id}")
    Observable<ArticalBean> getArtical(@Path("id") int id);

    @GET("4/story/{id}/long-comments")
    Observable<CommentBean> getLongComments(@Path("id")int id);

    @GET("4/story/{id}/short-comments")
    Observable<CommentBean> getShortComments(@Path("id")int id);

    @GET("4/themes")
    Observable<ZhutiBean> getZhuti();

    @GET("4/theme/{id}")
    Observable<ZhutiItemBean> getZhutiItem(@Path("id") int id);

    @GET("3/news/hot")
    Observable<RemenBean> getRemen();

    @GET("3/sections")
    Observable<ZhuanlanBean> getZhuanlan();

    @GET("3/section/{id}")
    Observable<ZhuanlanItemBean> getZhuanlanItem(@Path("id") int id);

    @GET("4/story-extra/{id}")
    Observable<ExtraInfoBean> getExtraInfo(@Path("id")int id);
}
