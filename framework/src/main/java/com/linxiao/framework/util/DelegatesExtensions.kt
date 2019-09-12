package com.linxiao.framework.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.linxiao.framework.BaseApplication
import com.yuzhua.aspectj.FastClick
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 一些扩展方法
 */


/**
 * 使用委托，可以保存基本类型到sp
 */
class Preference<T>(val context: Context, val name: String, val default: T, private val defaultFileName:String = "kotlinDefault") : ReadWriteProperty<Any?, T> {
    private val prefs by lazy {
        context.getSharedPreferences(defaultFileName, Context.MODE_PRIVATE)
    }
    override fun getValue(thisRef: Any?,property: KProperty<*>) :T{
        return findPreference(name,default)
    }
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    fun setValueFromAny(value:Any){
        putPreference(name, value)
    }

    private fun <T> findPreference(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
//            is List<*> -> getList(name,default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

        res as T
    }

    private fun <U> putPreference(name: String, value: U)= with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
//            is List<*> -> putList(name,value)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }.apply()
    }
}

    /**
     * 返回颜色
     */
    fun Context.color(@ColorRes id:Int):Int {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return resources.getColor(id,null)
        }
        return resources.getColor(id)
    }

    fun androidx.fragment.app.Fragment.color(@ColorRes id:Int):Int {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return resources.getColor(id,null)
        }
        return resources.getColor(id)
    }

    /**
     * 获取RadioGroup中，选中状态的button是第几个
     * 如果没有则返回-1
     */
    fun RadioGroup.getCheckOrder():Int=(0..childCount).firstOrNull { (getChildAt(it) as RadioButton).isChecked }?: -1


    /**
     * 简化Gson转化
     */
    inline fun <reified T> Gson.fromJson(s:String): T? {
        return fromJson<T>(s,object: TypeToken<T>(){}.type)
    }

    /**
     * 简化Gson转化
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    inline fun <reified T> Gson.toJson(s: T): String = toJson(s,object: TypeToken<T>(){}.type)

    /**
     * 判断字符串是否包含html代码
     * 其实只是简单判断是否有</font>表情
     * 主要用来判断文字是否有变颜色的font代码
     */
    fun String.isHtml() = indexOf("</font>") != -1




    fun SharedPreferences.Editor.putList(name: String,list:List<*>): SharedPreferences.Editor {
        val s = list.map{"\"${it.toString()}\""}.reduce { acc, s -> acc+","+s }
        return putString(name,"[$s]")
    }

    fun SharedPreferences.getList(name: String,default: List<*>):List<String>{
        val s = getString(name,"")
        return s.split(",").toList()
    }

    /**
     * 默认的获取FragmentStatePagerAdapter
     */
    fun getBaseFragmentStatePagerAdapter(fm: androidx.fragment.app.FragmentManager, list:List<androidx.fragment.app.Fragment>): androidx.fragment.app.FragmentPagerAdapter {
        return object: androidx.fragment.app.FragmentPagerAdapter(fm) {
            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return list[position]
            }
            override fun getCount(): Int {
                return list.size
            }
            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            }
        }
    }

    /**
     * 给Viewpager设置适配器，并设置离屏加载数为list.size
     */
    fun androidx.viewpager.widget.ViewPager.setBaseAdapter(fm: androidx.fragment.app.FragmentManager, list:List<androidx.fragment.app.Fragment>){
        adapter = getBaseFragmentStatePagerAdapter(fm,list)
        offscreenPageLimit = list.size
    }

    /**
     * 通过LayoutId便捷获取ViewDataBinding的方法
     * 主要还是为了在 Fragment 里面写R.layout.xxxxxx,方便点击跳转
     */
    fun < T : ViewDataBinding> androidx.fragment.app.Fragment.getDataBinding(@LayoutRes id:Int, parent:ViewGroup?=null, attachToParent:Boolean=parent!=null):T =  DataBindingUtil.inflate(LayoutInflater.from(context),id,parent,attachToParent)

    /**
     * 通过LayoutId便捷获取ViewDataBinding的方法
     * 主要还是为了在 Activity 里面写R.layout.xxxxxx,方便点击跳转
     */
    fun < T : ViewDataBinding> Activity.getDataBinding(@LayoutRes id:Int,parent:ViewGroup?=null,attachToParent:Boolean=parent!=null):T =  DataBindingUtil.inflate(layoutInflater,id,parent,attachToParent)


    /**
     * ----------------------------------------JSON--------------------------------------------------------------------------------
     */
    fun <V> jsonOf(vararg pairs: Pair<String, V>): JSONObject {
        val json = JSONObject()

        pairs.forEach {
            val value = it.second
            when(value){
                null ->{}
                is Int -> json.put(it.first,value)
                is Long -> json.put(it.first,value)
                is Double -> json.put(it.first,value)
                is Boolean -> json.put(it.first,value)
                is Any -> json.put(it.first,value)
            }
        }
        return json
    }

    fun jsonOf(vararg value:Any):JSONArray{
        val json = JSONArray()
        value.forEach {
            json.put(it)
        }
        return json
    }

    /**
     * 生成argb的颜色
     */
    fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
        return (alpha * 255.0f + 0.5f).toInt() shl 24 or
                ((red * 255.0f + 0.5f).toInt() shl 16) or
                ((green * 255.0f + 0.5f).toInt() shl 8) or
                (blue * 255.0f + 0.5f).toInt()
    }

    /**
     * 快速保存sp
     */
    fun <T:Any> preference( name:String, defaultValue:T,defaultFileName: String="kotlinDefault",context: Context
    = BaseApplication.getAppContext())= Preference(context,name,defaultValue,defaultFileName)

    /**
     * 让字符串直接生成toast
     */
    fun String.toast(){
        //鱼爪2.0要求把toast放在中间部分
        ToastUtils.setGravity(Gravity.CENTER,0,0)
        ToastUtils.showShort(this)
    }

    /**
     * 在dsl中使用recyclerView
     * 注意：必须要设置id
     */
    inline fun ViewManager.recyclerView(init: (@AnkoViewDslMarker RecyclerView).() -> Unit): RecyclerView {
        return ankoView({ctx: Context -> RecyclerView(ctx) }, theme = 0) { init() }
    }

    /**
     * 简便获取viewModel
     */
    inline fun <reified T : ViewModel> AppCompatActivity.obtainViewModel()
            = ViewModelProviders.of(this).get(T::class.java)


    /**
     * 快速点击
     * 默认的所有click都是慢速点击的，如果需要快速点击，请调用这个方法
     */
    fun android.view.View.onFastClick(
            handler: (v: android.view.View?) -> Unit
    ) {
        //这里不能使用lambda表达式，因为需要给onClick添加@FastClick注解
        setOnClickListener(object : android.view.View.OnClickListener{
            @FastClick
            override fun onClick(v: View?) {
                handler(v)
            }
        })
    }


/**
 * rxjava 绑定Android的生命周期
 */
fun <T> io.reactivex.Observable<T>.bindingLifecycle(lo : LifecycleOwner) = `as` (com.uber.autodispose.AutoDispose.autoDisposable(com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider.from(lo,Lifecycle.Event.ON_DESTROY)))
fun <T> io.reactivex.Observable<T>.bindingLifecycle(lo : Lifecycle) = `as` (com.uber.autodispose.AutoDispose.autoDisposable(com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider.from(lo,Lifecycle.Event.ON_DESTROY)))

/**
 * 检查权限
 * @param per 需要检查的权限
 * @param permissionName 显示在对话框中的 权限中文名称
 * @param granted 同意权限后的操作
 */
fun checkPermission(@PermissionConstants.Permission per: Array<String>, permissionName:String="",isLooper:Boolean = false,onDenied:(()->Unit)?=null, granted:()->Unit){
    PermissionUtils.permission(*per)
            .rationale { PermissionDialogHelper.showRationaleDialog(it,permissionName) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    //同意权限
                    "同意权限".println()
                    granted.invoke()
                }

                override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                    "拒绝了权限".println()
                    PermissionDialogHelper.showOpenAppSettingDialog(permissionName){
                        if(onDenied != null){
                            onDenied()
                        }else{
                            if(isLooper){
                                checkPermission(per,permissionName, isLooper,null,granted)
                            }
                        }

                    }

                }
            }).request()
}

///**
// * 显示错误提示，并获取焦点
// */
//fun TextInputLayout.showError(error:String){
//    setError(error)
//    editText?.apply {
//        isFocusable = true
//        isFocusableInTouchMode = true
//        requestFocus()
//    }
//}
