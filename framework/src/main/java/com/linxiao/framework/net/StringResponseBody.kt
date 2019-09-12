package com.linxiao.framework.net

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.bouncycastle.asn1.ocsp.ResponseBytes
import java.io.ByteArrayInputStream

/**
 * 构造String类型的ResponseBody
 * Created by Extends on 2017/10/26 17:26
 */
class StringResponseBody(val value:String):ResponseBody() {
    override fun contentLength(): Long = value.length.toLong()

    override fun contentType(): MediaType? = MediaType.parse("application/json; charset=utf-8")

    override fun source(): BufferedSource = Buffer().readFrom(ByteArrayInputStream(value.toByteArray()))
}