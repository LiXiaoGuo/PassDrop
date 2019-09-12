package com.linxiao.framework.net.download

/**
 *
 * @author Extends
 * @date 2019/6/18/018
 */
interface DownloadListener {
    /**
     * 开始下载
     */
    fun onStartDownload(length : Long )

    /**
     * 正在下载
     */
    fun onProgress(progress:Long,length : Long )

    /**
     * 下载失败
     */
    fun onFail(errorInfo:String)
}