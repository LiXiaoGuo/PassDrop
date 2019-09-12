package com.linxiao.framework.net.interceptor

import android.util.Log
import com.linxiao.framework.net.cache.CacheManager
import okhttp3.*
import okio.Buffer
import okio.BufferedSource
import java.io.ByteArrayInputStream


/**
 * 缓存拦截器
 * Created by Extends on 2017/9/28 10:52
 */
class CacheInterceptor: BaseInterceptor() {
    private val TAG = "CacheInterceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val time = try {
            (request.header("time")?.toIntOrNull()?:0)*1000L
        }catch (e:Exception){
            -1L
        }
        //移除time响应头,以免出现未知bug
        request = request.newBuilder().removeHeader("time").build()
        //获取缓存文件名
        val key = request.url().toString().split("?")[0] + getRequestParams(request)
        if (time > 0 && CacheManager.isExist(key)){
            Log.i(TAG,"using the cache")
            val s =CacheManager.getData(key)!!

            //返回自己构造的Response
            return Response.Builder().body(object : ResponseBody(){
                override fun contentLength(): Long = s.length.toLong()
                override fun contentType(): MediaType? = MediaType.parse("application/json; charset=utf-8")
                override fun source(): BufferedSource= Buffer().readFrom(ByteArrayInputStream(s.toByteArray()))

            })
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .build()
        }
        Log.i(TAG,"not use the cache")
        val response = chain.proceed(request)
        if(time>0){
            val value = getResponBodyString(response)
            if (value!=null){
                CacheManager.saveData(key,value,time)
            }
        }
        return response
    }
}