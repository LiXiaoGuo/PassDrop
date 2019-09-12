package com.linxiao.framework.net.download

import okhttp3.Interceptor
import okhttp3.Response

/**
 *
 * @author Extends
 * @date 2019/6/18/018
 */
class DownloadInterceptor(private val downloadListener:DownloadListener): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder().body(
                DownloadResponseBody(response.body()!!, downloadListener)).build()
    }
}