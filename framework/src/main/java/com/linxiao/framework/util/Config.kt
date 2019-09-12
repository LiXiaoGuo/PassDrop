package com.linxiao.framework.util

import android.os.Environment
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 *
 * @author Extends
 * @date 2018/7/23/023
 */
object Config {
    /**
     * sdcard根路径
     * Environment.getExternalStorageDirectory()获取出来的路径是：/storage/emulated/0
     */
    var FILE_ROOT_PATH = Utils.getApp().cacheDir.absolutePath//Environment.getExternalStorageDirectory().path
    /**
     * 应用名
     */
    var APP_NAME="framework"

    /**
     * 保存的文件夹的路径
     */
    fun FILE_SAVE_PATH() = FILE_ROOT_PATH+ File.separator+ APP_NAME
}