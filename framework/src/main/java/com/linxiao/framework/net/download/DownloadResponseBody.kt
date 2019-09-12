package com.linxiao.framework.net.download

import android.util.Log

import java.io.IOException

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Source

/**
 * @author Extends
 * @date 2019/6/18/018
 */
class DownloadResponseBody(private val responseBody: ResponseBody, private val downloadListener: DownloadListener?=null) : ResponseBody() {
    // BufferedSource 是okio库中的输入流，这里就当作inputStream来使用。
    private var bufferedSource: BufferedSource? = null

    init {
        downloadListener?.onStartDownload(responseBody.contentLength())
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            internal var totalBytesRead = 0L
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
//                Log.i("download", "read: " + (totalBytesRead * 100 / responseBody.contentLength()).toInt())
                if (bytesRead != -1L) {
                    downloadListener?.onProgress(totalBytesRead,responseBody.contentLength())
                }
                return bytesRead
            }
        }
    }
}
