package com.linxiao.framework.net

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.linxiao.framework.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

/**
 * 网络请求的一些封装
 * Created by Extends on 2017/10/19 10:19
 */
class NetManager {
    companion object {
        var api : (()->RetrofitApiBuilder)? = null
        var downApi : (()->RetrofitApiBuilder)? = null
        fun getDownNetManager(): RetrofitApiBuilder  = downApi?.invoke()?:throw Exception(" NetManager api method is not null")
        fun getNetManager(): RetrofitApiBuilder  = api?.invoke()?:throw Exception(" NetManager api method is not null")
        var baseUrl = ""// 接口请求域名
        var prefix = "" //接口请求前缀，一般写在了baseUrl里面，也可以单独拿出来写到prefix里
//        val baseAPI: BaseAPI by lazy { getNetManager() }

    }
}

/**
 * 请求方法枚举
 */
enum class Method {
    GET, POST, DELETE, PUT, PATCH
}

inline fun <reified T> request(path: String, map: Map<String, String>, onNext: Consumer<T>,
                               onComplete: Action = Action { }, time: Int = 0, headersMap: MutableMap<String, String> = mutableMapOf(), context: Context? = null,
                               lifecycle:Lifecycle, method: Method = Method.POST, netClient: BaseAPI?=null): Disposable? {
    //判断是否返回字符串
    val isString = T::class.java.simpleName == "String"

    val token = object : TypeToken<T>() {}.type
    val progressDialog = if (context != null) LoadingDialogUtil.initCPD(context) else null
    progressDialog?.show()
    //拼接请求地址的前缀,去掉重复的`//`
    val prefixPath = if(netClient!=null){
        path
    }else if(path.startsWith("http:") || path.startsWith("https:")){
        path
    }else{
        if(NetManager.prefix.isEmpty()){
            if(path.startsWith("/")) path.substring(1) else path
        }else{
            NetManager.prefix + if(NetManager.prefix.endsWith("/")){
                if(path.startsWith("/")) path.substring(1) else path
            }else{
                if(path.startsWith("/")) path else ("/"+path)
            }
        }
    }


    return whenMethod(method, prefixPath, map, time,headersMap,netClient).subscribeOn(Schedulers.io())
            .retry(3)
            .let {
                if (!isString) {
                    it.map { Gson().fromJson<T>(it, token) }
                } else {
                    it.map { it as T }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .bindingLifecycle(lifecycle)
            .subscribe(onNext, Consumer {
                it.message.loge()
                it.printStackTrace()
                when (it) {
                    is HttpException -> "服务暂不可用"
                    is IOException -> "您的网络不给力，请稍后再试"
                    is NoNetworkException ->"您的网络不给力，请稍后再试"
                    else -> it.message+""
                }.toast()
                //当出错了后回掉onComplete方法，主要是执行onComplete里可能有的如Dialog.dismiss()方法
                LoadingDialogUtil.dimssDialog(progressDialog)
                onComplete.run()
            }, Action {
                LoadingDialogUtil.dimssDialog(progressDialog)
                onComplete.run()
            })
}



/**
 * 判断具体使用什么请求方式
 */
fun whenMethod(method: Method, prefixPath: String, map: Map<String, String>, time: Int,headersMap:MutableMap<String,String>,
               netClient: BaseAPI?=null): Observable<String> {
    val api = netClient ?: NetManager.getNetManager().build(BaseAPI::class.java)
    headersMap["time"] = time.toString()
    return when (method) {
        Method.GET -> {
            if((prefixPath.startsWith("http:") || prefixPath.startsWith("https:")) && map.isEmpty()){
                api.getApi(prefixPath, headersMap)
            }else{
                api.getApi(prefixPath, map, headersMap)
            }
        }
        Method.POST -> api.postApi(prefixPath, map, headersMap)
        Method.DELETE -> api.deleteApi(prefixPath, map, headersMap)
        Method.PUT -> api.putApi(prefixPath, map, headersMap)
        Method.PATCH -> api.patchApi(prefixPath, map, headersMap)
    }
}

/**
 * 返回字符串
 * @param map 键值对参数
 * @param onNext Observer的onNext方法
 * @param onComplete Observer的onComplete方法
 * @param time 数据缓存时间，0表示不缓存，1表示缓存1s
 * @param context 如果需要公用的加载框，就传入activity的context，Application的不行
 */
private fun requestByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                            onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), context: Context? = null,
                            lifecycle:Lifecycle,method: Method = Method.POST, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap, context, lifecycle,method,netClient)
}

/**
 * BaseActivity 和 BaseFragment 的网络请求通道
 * 为了方便管理Disposable，避免内存泄漏
 *  todo 尽量不要在BaseAdapter中的onBind里调用网络方法，会导致报错，可能是Lambda嵌套过多导致泛型失效
 *
 * @param path 请求路径
 * @param map 请求的参数
 * @param onNext 返回回调
 * @param onComplete 完成回调
 * @param time 保存时间
 * @param isShowProgress 是否显示加载框
 * @param netClient 网络客户端
 */
inline fun <reified T> ComponentActivity.post(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                              onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                              isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap, if (isShowProgress) this else null,this.lifecycle, Method.POST,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> ComponentActivity.get(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                        onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                        isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) this else null,this.lifecycle, Method.GET,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> ComponentActivity.delete(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                           onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                           isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) this else null,this.lifecycle, Method.DELETE,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> ComponentActivity.put(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                        onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                        isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) this else null,this.lifecycle, Method.PUT,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> ComponentActivity.patch(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                          onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                          isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) this else null,this.lifecycle, Method.PATCH,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> Fragment.post(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                     onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                     isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) activity else null,this.lifecycle, Method.POST,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> Fragment.get(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                        onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                        isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) activity else null,this.lifecycle, Method.GET,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> Fragment.delete(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                           onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                           isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) activity else null,this.lifecycle, Method.DELETE,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> Fragment.put(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                        onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                        isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) activity else null,this.lifecycle, Method.PUT,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
inline fun <reified T> Fragment.patch(path: String, map: Map<String, String>, onNext: Consumer<T>,
                                          onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(),
                                          isShowProgress: Boolean = false, netClient: BaseAPI?=null): Disposable? {
    return request(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) activity else null,this.lifecycle, Method.PATCH,netClient)
}

//---------------

/**
 *  文档看这里
 *  @see post
 */
fun ComponentActivity.postByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                              onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                              netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) this else null,this.lifecycle, Method.POST,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun ComponentActivity.getByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                             onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                             netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) this else null,this.lifecycle, Method.GET,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun ComponentActivity.deleteByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                                onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                                netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time, headersMap, if (isShowProgress) this else null,this.lifecycle, Method.DELETE,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun ComponentActivity.putByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                             onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                             netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) this else null,this.lifecycle, Method.PUT,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun ComponentActivity.patchByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                               onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                               netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time, headersMap, if (isShowProgress) this else null,this.lifecycle, Method.PATCH,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun Fragment.postByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                              onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                              netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time, headersMap, if (isShowProgress) activity else null,this.lifecycle, Method.POST,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun Fragment.getByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                             onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                             netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) activity else null,this.lifecycle, Method.GET,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun Fragment.deleteByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                                onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                                netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time, headersMap, if (isShowProgress) activity else null,this.lifecycle, Method.DELETE,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun Fragment.putByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                             onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                             netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time, headersMap, if (isShowProgress) activity else null,this.lifecycle, Method.PUT,netClient)
}

/**
 *  文档看这里
 *  @see post
 */
fun Fragment.patchByString(path: String, map: Map<String, String>, onNext: Consumer<String>,
                               onComplete: Action = Action { }, time: Int = 0,headersMap: MutableMap<String, String> = mutableMapOf(), isShowProgress: Boolean = false,
                               netClient: BaseAPI?=null): Disposable? {
    return requestByString(path, map, onNext, onComplete, time,headersMap,  if (isShowProgress) activity else null,this.lifecycle, Method.PATCH,netClient)
}


/**************************************************************************/

//inline fun <reified T> postUpdataFile(path: String, map: Map<String, Any>, onNext: Consumer<T>,
//                                      onComplete: Action = Action { }, time: Int = 0, context: Context? = null,lifecycleOwner: LifecycleOwner
//): Disposable? {
//    //判断是否返回字符串
//    val isString = T::class.java.simpleName == "String"
//
//    val token = object : TypeToken<T>() {}.type
//    // todo 如果有需求关闭加载框则取消网络请求，那么这里应该修改
//    // todo 初步逻辑是封装一个具有 dialog 和 Disposable 的对象
//    // todo 监听到dialog关闭则disposable.dispose()
//    val progressDialog = if (context != null) LoadingDialogUtil.initCPD(context) else null
//    progressDialog?.setMessage("加载中...")
//    progressDialog?.show()
////    var dialog = LoadingDialogUtil.initCPD(context)
////    dialog.show()
//    //拼接请求地址的前缀,去掉重复的`//`
//    val prefixPath = if (NetManager.prefix.isEmpty()) {
//        if (path.startsWith("/")) path.substring(1) else path
//    } else {
//        NetManager.prefix + if (NetManager.prefix.endsWith("/")) {
//            if (path.startsWith("/")) path.substring(1) else path
//        } else {
//            if (path.startsWith("/")) path else ("/" + path)
//        }
//    }
//
//    val newList = map.map {
//        val temp = it.value
//        if (temp is File){
//            MultipartBody.Part.createFormData(String(it.key.toByteArray()), temp.name, RequestBody.create(MediaType.parse("multipart/form-data"), temp))
//        }else{
//            MultipartBody.Part.createFormData(String(it.key.toByteArray()),temp.toString())
//        }
//    }
//
//
//    println(prefixPath)
//
//    return NetManager.getNetManager()
//            .build(BaseAPI::class.java)
//            .postFile(prefixPath,newList)
//            .subscribeOn(Schedulers.io())
//            .retry(3)
//            .let {
//                if (!isString) {
//                    it.map { Gson().fromJson<T>(it, token) }
//                } else {
//                    it.map { it as T }
//                }
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .unsubscribeOn(Schedulers.io())
//            .bindingLifecycle(lifecycleOwner)
//            .subscribe(onNext, Consumer {
//                ke(it.message)
//                when (it) {
//                    is HttpException -> "服务暂不可用"
//                    is IOException -> "连接服务器失败"
//                    is NoNetworkException -> "网络不可用"
//                    else -> it.message + ""
//                }.toast()
//                //当出错了后回掉onComplete方法，主要是执行onComplete里可能有的如Dialog.dismiss()方法
//                if (progressDialog?.isShowing == true) {
//                    progressDialog.dismiss()
//                }
//                onComplete.run()
//            }, Action {
//                if (progressDialog?.isShowing == true) {
//                    progressDialog.dismiss()
//                }
//                onComplete.run()
//            })
//}

