package com.linxiao.framework.net


import com.blankj.utilcode.util.LogUtils

import okhttp3.Headers

/**
 * 承载Http信息实体
 * Created by linxiao on 2017/1/19.
 */
class HttpInfoEntity {

    /**Http协议 */
    var protocol: String? = null
    /**请求方式 */
    var method: String? = null
    /**请求地址 */
    var url: String? = null
    /**请求耗时 */
    var tookMills: Long = 0

    /*---------------request params----------------*/

    var requestHeaders: Headers? = null

    var requestContentType: String? = null

    var requestContentLength: Long = 0

    var requestBody: String? = null

    /*---------------response params----------------*/

    var responseHeaders: Headers? = null

    var responseCode: Int = 0

    var responseMessage: String? = null

    var responseContentLength: Long = 0

    var responseBody: String? = null


    fun logOut() {
        val sb = buildString {
            appendln("url: $url")
            appendln("protocol: $protocol,  method: $method")
            appendln("request took time: $tookMills ms")
            appendln("response code: $responseCode,  message: $responseMessage")
            appendln("----------request-----------")
            appendln("Headers:")
            for (headerName in requestHeaders!!.names()) {
                appendln("$headerName : ${requestHeaders?.get(headerName)}")
            }
            appendln("Body:")
            appendln(requestBody)
            appendln("----------response----------")
            appendln("Headers:")
            for (headerName in responseHeaders!!.names()) {
                appendln("$headerName : ${responseHeaders?.get(headerName)}")
            }
            appendln("Body:")
            appendln(responseBody)
        }
        LogUtils.iTag("HttpInfo Logout",sb)
    }

    override fun toString(): String {
        return "HttpInfoEntity{" +
                "protocol='" + protocol + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", tookMills=" + tookMills +
                ", requestHeaders=" + requestHeaders +
                ", requestContentType='" + requestContentType + '\'' +
                ", requestContentLength=" + requestContentLength +
                ", requestBody='" + requestBody + '\'' +
                ", responseHeaders=" + responseHeaders +
                ", responseCode=" + responseCode +
                ", responseMessage='" + responseMessage + '\'' +
                ", responseContentLength=" + responseContentLength +
                ", responseBody='" + responseBody + '\'' +
                '}'
    }
}
