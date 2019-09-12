package com.linxiao.framework.net.interceptor

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

/**
 *
 * @author Extends
 * @date 2019/6/14/014
 */
abstract class BaseInterceptor:Interceptor {
    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    @Throws(EOFException::class)
    fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = (if (buffer.size() < 64) buffer.size() else 64).toLong()
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }

    private val UTF8 = Charset.forName("UTF-8")
    /**
     * 获取respon的数据
     */
    fun getResponBodyString(response: Response):String?{
        val responseBody = response.body()
        if(responseBody!=null){
            val contentLength = responseBody.contentLength()
            if (!bodyEncoded(response.headers())) {
                val source = responseBody.source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()

                var charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        return null
                    }
                }
                if (!isPlaintext(buffer)) {
                    return null
                }
                if (contentLength != 0L) {
                    val result = buffer.clone().readString(charset)
                    return result
                }
            }
        }
        return null
    }

    fun getRequestParams(request: Request):String?{
        return when(request.method().toUpperCase()){
            "GET" -> request.url().encodedQuery()
            "POST" -> {
                val buffer = Buffer()
                val requestBody = request.body()
                if(requestBody == null){
                    null
                } else {
                    requestBody.writeTo(buffer)
                    buffer.readString(Charsets.UTF_8)
                }
            }
            else -> null
        }
    }
}