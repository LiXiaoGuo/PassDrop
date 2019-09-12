package com.linxiao.framework.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.linxiao.framework.fragment.LoadingFragment
import com.linxiao.framework.fragment.RouterAutoInject
import com.linxiao.framework.fragment.ScreenLandscape
import com.linxiao.framework.fragment.StartEvent
import com.linxiao.framework.util.StatusBarUtil
import com.linxiao.framework.util.loge
import com.noober.background.BackgroundLibrary
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.contentView
import org.jetbrains.anko.doFromSdk

//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


/**
 * base activity class of entire project
 *
 * template for activities in the project, used to define common methods of activity
 */
abstract class BaseActivity : AppCompatActivity() {
    private val mReceiver by lazy { ActivityBaseReceiver() }
    private var event = false
    private var dialog: androidx.fragment.app.DialogFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        BackgroundLibrary.inject(this)
        if(this::class.java.isAnnotationPresent(RouterAutoInject::class.java)){
            ARouter.getInstance().inject(this)
        }
        super.onCreate(savedInstanceState)
        requestedOrientation = if(this::class.java.isAnnotationPresent(ScreenLandscape::class.java)){
            //设置横屏
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }else{
            //设置竖屏
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }


        //设置接受退出的广播
        val filter = IntentFilter()
        filter.addAction(ACTION_EXIT_APPLICATION)
        registerReceiver(mReceiver, filter)
        //设置内容view
        initContentView()
        //配置view的初始化信息
        onInitView(savedInstanceState)
        //初始化监听回调
        initListener()
        //初始化数据
        initData()
        //判断是否开启EventBus的事件通知
        judgeEvent()
        if (event) {
            EventBus.getDefault().register(this)
        }
        "ActivityName:${javaClass.simpleName}".loge()

    }

    //销毁时释放资源
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
        if (event && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }



//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
//    }

    /**
     * 返回值可以是ResLayout/View/null
     * @return null，那么表示调用者将在onCreatRootView方法里自己调用setContentView方法，否则抛出空指针异常
     */
    protected abstract fun onCreateRootView(): Any?

    protected abstract fun onInitView(savedInstanceState: Bundle?)
    protected abstract fun initListener()
    protected abstract fun initData()


    /**
     * 基础类Activity的BroadcastReceiver
     */
    protected inner class ActivityBaseReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_EXIT_APPLICATION) {
                finish()
            }
        }
    }

    companion object {
        val ACTION_EXIT_APPLICATION = "exit_application"
    }

    /**
     * 判断是否开启事件通知
     */
    private fun judgeEvent() {
        //如果event值为false时才判断是否有注解
        if (!event) {
            event = this::class.java.isAnnotationPresent(StartEvent::class.java)
        }
    }

    /**
     * 初始化内容View
     */
    private fun initContentView() {
        val crv = onCreateRootView()
        if (crv == null) {
            if (contentView == null) throw NullPointerException(this::class.java.simpleName + "'s contentView is null")
        } else {
            when (crv) {
                is Int -> setContentView(crv)
                is View -> setContentView(crv)
                else -> {
                    if (contentView == null) throw NullPointerException(this::class.java.simpleName + "'s contentView is null")
                }
            }
        }
    }

    /**
     * 改变状态栏颜色
     * @param color
      @param isCilp 是否需要padding状态栏高度，如果需要自己实现状态栏逻辑就传入false
     * @param dl 如果要兼容DrawerLayout则传入
     */
    fun changeStatusBarColor(@ColorInt color: Int, isCilp: Boolean = true, dl: androidx.drawerlayout.widget.DrawerLayout? = null) {
        //如果dl不为空则都使用半透明，因为dl可能拉出白色背景
        if (dl != null) {
            StatusBarUtil.setStatusBarLightMode(this, false)
            StatusBarUtil.setColorTranslucentForDrawerLayout(this, dl, color)
            return
        }

        doFromSdk(Build.VERSION_CODES.M) {
            //如果版本号大于等于M，则必然可以修改状态栏颜色
            StatusBarUtil.setColor(this, color, isCilp)
            StatusBarUtil.setStatusBarLightModeByColor(this, color)
            return
        }
        //这里处理的是版本号低于M的系统
        //判断设置的颜色是深色还是浅色，然后设置statusBar的文字颜色
        val status = StatusBarUtil.setStatusBarLightModeByColor(this, color)
        //fixme 如果手机机型不能改状态栏颜色就不允许开启沉浸式,如果业务需求请修改代码逻辑
        if (!status) {//如果状态栏的文字颜色改变失败了则设置为半透明
            StatusBarUtil.setColorTranslucent(this, color, isCilp)
        } else {//如果状态栏的文字颜色改变成功了则设置为全透明
            StatusBarUtil.setColor(this, color, isCilp)
            //改变了状态栏后需要重新设置一下状态栏文字颜色
            StatusBarUtil.setStatusBarLightModeByColor(this, color)
        }

    }

    override fun getResources(): Resources {
        return super.getResources()
    }

    /**
     * 开启正在加载的Dialog
     */
    fun showLoadingDialog() {
        showDialog(LoadingFragment())
    }

    /**
     * 显示Dialog
     */
    fun showDialog(dl: androidx.fragment.app.DialogFragment) {
        dialog = dl
        dialog?.show(supportFragmentManager, dl.javaClass.simpleName)
    }

    /**
     * 关闭Dialog
     */
    fun closeDialog() {
        dialog?.dismissAllowingStateLoss()
        dialog = null
    }

    /**
     * 判断dialog是否显示
     */
    fun isShowDialog() = dialog?.dialog?.isShowing?:false
}
