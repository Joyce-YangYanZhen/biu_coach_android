package com.noplugins.keepfit.coachplatform.util.net;

import com.noplugins.keepfit.coachplatform.bean.*;
import com.noplugins.keepfit.coachplatform.util.net.entity.Bean;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

import java.util.List;

public interface ChangGuanService {
    /**
     * 获取标签
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("searchDict")
    Observable<Bean<List<TagBean>>> get_biaoqians(@Body RequestBody json);

    /**
     * 获取七牛token
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("getPicToken")
    Observable<Bean<QiNiuToken>> get_qiniu_token(@Body RequestBody json);

    /**
     * 获取字典
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("searchDict")
    Observable<Bean<List<ZiDIanBean>>> get_zidian(@Body RequestBody json);

    /**
     * 获取房间类型
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("findAreaPlace")
    Observable<Bean<List<ClassTypeEntity>>> get_class_type(@Body RequestBody json);

    /**
     * 获取首页类型
     *
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("searchDict")
    Observable<Bean<List<DictionaryeBean>>> get_types(@Body RequestBody json);
}
