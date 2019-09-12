package com.linxiao.framework.net.download

import com.blankj.utilcode.util.FileIOUtils
import com.linxiao.framework.net.BaseAPI
import com.linxiao.framework.util.bindingLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.reactivestreams.Subscriber
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 *
 * @author Extends
 * @date 2019/6/18/018
 */


/**
 * 下载文件
 * @param url 文件地址
 * @param file 下载后的文件
 * @param onStart 开始下载的回调
 * @param onDownload 正在下载的回调
 * @param onError 下载错误时的回调
 * @param onComplete 下载完成的回调
 */
fun downloadFile(url:String,file:File,onStart:((length:Long)->Unit)?=null,onDownload:((progress: Long, length: Long)->Unit)?=null,
                 onError:((throwable:Throwable)->Unit)?=null,onComplete: (()->Unit)?=null){
    val mInterceptor = DownloadInterceptor(object : DownloadListener{
        override fun onStartDownload(length: Long) {
            onStart?.invoke(length)
        }

        override fun onProgress(progress: Long, length: Long) {
            onDownload?.invoke(progress, length)
        }

        override fun onFail(errorInfo: String) {}
    })
    val httpClient = OkHttpClient.Builder()
            .addInterceptor(mInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()

    Retrofit.Builder()
            .baseUrl("http://www.baidu.com/")
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(BaseAPI::class.java)
            .downloadFile(url)
//            .bindingLifecycle(lifecycle)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .map {it.byteStream()}
            .observeOn(Schedulers.computation()) // 用于计算任务
            .doOnNext { FileIOUtils.writeFileFromIS(file,it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},{ it.printStackTrace();onError?.invoke(it) },{onComplete?.invoke()})
}