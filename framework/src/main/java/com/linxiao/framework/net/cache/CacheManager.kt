package com.linxiao.framework.net.cache

import androidx.collection.ArrayMap
import com.blankj.utilcode.util.FileUtils
import com.linxiao.framework.util.Config
import com.linxiao.framework.util.MD5Util
import org.jetbrains.anko.doAsync
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException

/**
 * 缓存实现
 * Created by Extends on 2017/10/9 17:08
 */
object CacheManager {
    //默认的缓存地址
    var CACHE_PATH = Config.FILE_SAVE_PATH() + File.separator + "cache" + File.separator
    val dlc:DiskLruCache by lazy { DiskLruCache.open(with(null){FileUtils.createOrExistsDir(CACHE_PATH);File(CACHE_PATH)},1,1, 10*1024*1024) }
    val memoryCache = ArrayMap<String, String>()

    /**
     * 缓存数据
     * @param name 文件名
     * @param value 文件值
     * @param time 过期时间13位毫秒值,默认不过期,
     *              传时间间隔,eg:3000,表示3s后过期
     */
    @JvmStatic fun saveData(name:String, value:String,time:Long=9_9999_9999_9999L){
        if(time<0){
            throw NumberFormatException("time not less than zero")
        }
        //文件名
        val t_name = MD5Util.getMD5Str(name,MD5Util.MD5_UPPER_CASE)
        doAsync {
            //获取过期时间
            val t = if(time!=9_9999_9999_9999L) time+System.currentTimeMillis() else time
            //存储在磁盘
            val editor = dlc.edit(t_name)
            editor.set(0,t.toString()+value)
            editor.commit()
            dlc.flush()
            //存储在内存
            memoryCache.put(t_name,t.toString()+value)
        }
    }

    /**
     * 判断name对应的data是否有效
     * 如果存在对应的data,但是时间已过期也返回false
     */
    @JvmStatic fun isExist(name: String):Boolean = getData(name)!=null

    /**
     * 通过名称获取缓存
     */
    @JvmStatic fun getData(name: String):String?{
        val t_name = MD5Util.getMD5Str(name,MD5Util.MD5_UPPER_CASE)
        try {
            //先从内存中取值,如果为null再从磁盘中取值
            val memoryValue = memoryCache[t_name]
            val value = if (memoryValue==null){
                val snapshot = dlc[t_name]
                val temp_value = snapshot?.getString(0)
                //手动关闭Snapshot
                snapshot?.close()
                temp_value ?: return null
            }else memoryValue
            val time = value.substring(0,13)
            //过期时间大于当前时间 表示没有过期
            if(time.toLong()>System.currentTimeMillis()){
                return value.substring(13)
            }else{
                //如果时间过期了,就删掉内存缓存
                memoryCache.remove(t_name)
            }
        }catch (e:IOException){
            e.printStackTrace()
        }
        return null
    }

    /**
     * 删除缓存数据
     */
    @JvmStatic fun delectData(name: String){
        try {
            val t_name = MD5Util.getMD5Str(name,MD5Util.MD5_UPPER_CASE)
            dlc.remove(t_name)
            memoryCache.remove(t_name)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    /**
     * 获取缓存数据的总字节数
     */
    @JvmStatic fun size():Long=dlc.size()

    /**
     * 删除全部缓存数据
     */
    @JvmStatic fun delectAllData(){
        try {
            dlc.delete()
            memoryCache.clear()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

}

