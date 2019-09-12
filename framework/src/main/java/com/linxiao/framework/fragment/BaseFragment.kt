package com.linxiao.framework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.linxiao.framework.util.loge
import org.greenrobot.eventbus.EventBus

/**
 *
 * Created by Extends on 2017/9/25 17:20
 */
abstract class BaseFragment : androidx.fragment.app.Fragment(), LifecycleOwner {
    private var event = false
    private var isInit = false//判断数据是否刷新过了
    private var isViewInit = false//判断控件是否创建了

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initContentView(inflater,container,savedInstanceState)
    }

    /**
     * 初始化内容View
     */
    private fun initContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        val view = onCreatRootView(inflater,container,savedInstanceState)
        return when(view){
            is Int -> inflater.inflate(view,container,false)
            is View -> view
            else -> throw NullPointerException(this::class.java.simpleName + "'s contentView is null")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 判断是否关闭懒加载，默认是开启的
        if(this::class.java.isAnnotationPresent(NoLazyFragment::class.java)){
            onInitView(savedInstanceState)
            judgeEvent()
            if(event){
                EventBus.getDefault().register(this)
            }
        }else{
            isViewInit = true
//          println("是否隐藏："+isHidden)
            if(!isHidden){
                lazyInitData(savedInstanceState)
            }
        }
       "baseFragmnet:${javaClass.simpleName }".loge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (event&& EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * 判断是否开启事件通知
     */
    private fun judgeEvent(){
        //如果event值为false时才判断是否有注解
        if(!event){
            event = this::class.java.isAnnotationPresent(StartEvent::class.java)
        }
    }




    /**
     * @return ResLayout | View
     */
    protected abstract fun onCreatRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):Any
    protected abstract fun onInitView(savedInstanceState: Bundle?)


    /************************      生命周期感知     ****************************/
    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }

    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }

    /**
     * 在普通的activity中判断是否隐藏
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(hidden) onUserHint() else {lazyInitData(null);onUserVisible()}

    }

    /**
     * 对用户是否可见
     * 注意：这个方法仅仅工作在FragmentPagerAdapter中，不能被使用在一个普通的activity中
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        lazyInitData(null)
        if(isVisibleToUser) {
            onUserVisible()
        }else
            onUserHint()
    }

    /**
     * 懒加载数据
     */
    private fun lazyInitData(savedInstanceState: Bundle?){
        if(isViewInit && !isInit && userVisibleHint){
            isInit = true
            onInitView(savedInstanceState)
            judgeEvent()
            if(event){
                EventBus.getDefault().register(this)
            }
        }
    }

    /**
     * 用户看见时触发
     */
    open fun onUserVisible(){ }

    /**
     * 用户看不见时触发
     */
    open fun onUserHint(){ }

//    override fun onResume() {
//        super.onResume()
//        MobclickAgent.onPageStart(javaClass.simpleName)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        MobclickAgent.onR(javaClass.simpleName)
//    }
}