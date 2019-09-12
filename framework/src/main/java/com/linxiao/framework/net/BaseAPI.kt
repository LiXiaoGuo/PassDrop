package com.linxiao.framework.net

import io.reactivex.Observable
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Url
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming





/**
 * 公用基础接口
 * Created by Extends on 2017/10/11 10:37
 */
interface BaseAPI {
    /**
     * 基础get请求
     * @param url 请求地址(绝对地址)
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @Streaming
    @GET
    fun getApi(@Url url: String,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * 基础get请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @Streaming
    @GET("/{path}")
    fun getApi(@Path(value = "path", encoded = true)path:String,@QueryMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * 基础post请求
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @POST("/")
    fun postApi(@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>


    /**
     * 基础post请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @POST("/{path}")
    fun postApi(@Path(value = "path",encoded = true)path: String,@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf() ): Observable<String>

    /**
     * 基础delete请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/", hasBody = true)
    fun deleteApi(@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * 基础delete请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/{path}", hasBody = true)
    fun deleteApi(@Path(value = "path",encoded = true)path: String,@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * 基础put请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @PUT("/")
    fun putApi(@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * 基础put请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @PUT("/{path}")
    fun putApi(@Path(value = "path",encoded = true)path: String,@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String>): Observable<String>


    /**
     * 基础patch请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @HTTP(method = "PATCH", path = "/", hasBody = true)
    fun patchApi(@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * 基础patch请求
     * @param path 请求地址(相对地址)
     * @param options 键值对参数
     * @param time 缓存过期时间,单位是秒,time>0才进行缓存
     *              传时间间隔,eg:3,表示3s后过期
     */
    @FormUrlEncoded
    @HTTP(method = "PATCH", path = "/{path}", hasBody = true)
    fun patchApi(@Path(value = "path",encoded = true)path: String,@FieldMap options: Map<String, String>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * post 表单上传文件
     * @param path
     * @param parts
     * @return
     */
    @Multipart
    @POST("/{path}")
    fun postFile(@Path(value = "path", encoded = true) path: String, @Part parts: List<MultipartBody.Part>,@HeaderMap headers:Map<String, String> = mapOf()): Observable<String>

    /**
     * 下载文件
     */
    @Streaming
    @GET
    fun downloadFile(@Url url: String,@HeaderMap headers:Map<String, String> = mapOf()): Observable<ResponseBody>
}