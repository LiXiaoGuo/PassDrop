package com.liguo.password.utils

import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 *
 * @author Extends
 * @date 2019/9/12/012
 */
class BinaryStdOut(size: Int?=null) {
    var out : ByteArrayOutputStream = ByteArrayOutputStream(size ?: 1024*8)
    var buffer = 0
    var n = 0

    fun writeBit(bit:Boolean){
        buffer = buffer shl  1
        if(bit){
            buffer = buffer or 1
        }
        n++
        if(n==8) clearBuffer()
    }

    fun writeByte(byte:Byte,start:Int = 0,end:Int = 7){
        if(n == 0 && start == 0 && end == 7){
            buffer = byte.toInt()
            n = 8
            clearBuffer()
        }else{
            val tempByte = byte.toInt()
            ((7-start) downTo (7-end)).forEach {
                writeBit(((tempByte shr  it) and 1) == 1)
            }
        }
    }

    fun writeByteArray(bytes:ByteArray){
        bytes.forEach {
            writeByte(it)
        }
    }

    fun writeByteArraySub(bytes:ByteArray,start:Int,end:Int):ByteArray{
        val c = end-start
//        if(c<0 && c%8!=0) throw Exception("(end - start)%8 must be 0")
        clean()
        if(c%8!=0){
            (0 until (8 - c%8)).forEach{
                writeBit(false)
            }
        }

        (start until  end).forEach {
            writeBit((bytes[it/8].toInt() and 0xff shr (7-it%8) and 1)==1)
        }
        val result = out.toByteArray()
        clean()
        return result
    }

    fun clean(){
        buffer = 0
        n = 0
        out.reset()
    }

    fun clearBuffer(){
        if(n==0)return
        if(n>0){
            buffer = buffer shl (8-n)
        }
        try {
            out.write(buffer)
        }catch (e: IOException){
            e.printStackTrace()
        }
        n = 0
        buffer = 0
    }

}