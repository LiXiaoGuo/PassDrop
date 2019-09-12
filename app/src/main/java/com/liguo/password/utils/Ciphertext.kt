package com.liguo.password.utils

import com.linxiao.framework.util.MD5Util
import com.linxiao.framework.util.loge
import com.linxiao.framework.util.logi
import java.lang.Exception
import java.util.*

/**
 *
 * @author Extends
 * @date 2019/9/12/012
 */
object Ciphertext {
    /**
     * 加密
     */
    fun encryption(str:String):ByteArray{
        val strByte = str.toByteArray()

        val bso = BinaryStdOut(16+32+strByte.size)
        val cal = Calendar.getInstance();
        val year = cal.get(Calendar.YEAR)-2000
        val month = cal.get(Calendar.MONTH)+1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        //7bit 年份 最大99
        bso.writeByte(year.toByte(),1)
        //4bit 月份 最大12
        bso.writeByte(month.toByte(),4)
        //5bit 日期 最大31
        bso.writeByte(day.toByte(),3)

        val md5 = MD5Util.getMD5Str("$year$month$day$str", MD5Util.MD5_LOWER_CASE)
        val md5_1 = md5.substring(0,20)
        val md5_2 = md5.substring(20)

        //20bit md5_1
        md5_1.forEach { bso.writeBit(it < 'A') }
        //内容
        bso.writeByteArray(strByte)
        //12bit md5_2
        md5_2.forEach { bso.writeBit(it < 'A') }
        return bso.out.toByteArray()
    }

    /**
     * 解密
     */
    fun decode(bytes:ByteArray):String{
        try {
            val year = bytes[0].toLong() and 0xff shr 1
            val month = (bytes[0].toLong() and 0xff and 0x01 shl 3 ) or (bytes[1].toLong() and 0xff shr 5)
            val day = bytes[1].toLong() and 0xff and 0b00011111
            val jsonBytes = ByteArray(bytes.size-6){0}
            (4 until (bytes.size-2)).forEach {
                jsonBytes[it-4] = ((bytes[it].toLong() and 0xff shl 4) or (bytes[it+1].toLong() and 0xff shr 4)).toByte()
            }
            val json = String(jsonBytes)
            val md5 = MD5Util.getMD5Str("$year$month$day$json", MD5Util.MD5_LOWER_CASE)

            val md5Long = (bytes[2].toLong() and 0xff shl 24) or (bytes[3].toLong() and 0xff shl 16) or
                    (((bytes[4].toLong() and 0xff and 0b11110000) or (bytes[bytes.size-2].toLong() and 0xff and 0b00001111)) shl 8) or
                    (bytes[bytes.size-1].toLong() and 0xff)
            var md5Long1 = 0L
            md5.forEach {
                md5Long1 = md5Long1 shl 1
                if(it < 'A'){
                    md5Long1 = md5Long1 or 1
                }
            }
            //校验成功
            return if(md5Long == md5Long1){
                json
            }else{
                ""
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return ""


    }
}