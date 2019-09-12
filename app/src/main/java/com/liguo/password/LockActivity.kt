package com.liguo.password

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.liguo.password.view.patternlockview.PatternLockView
import com.liguo.password.view.patternlockview.listener.PatternLockViewListener
import com.liguo.password.view.patternlockview.utils.PatternLockUtils
import com.linxiao.framework.activity.BaseActivity
import com.linxiao.framework.util.MD5Util
import com.linxiao.framework.util.logi
import com.linxiao.framework.util.preference
import com.linxiao.framework.util.toast
import kotlinx.android.synthetic.main.activity_lock.*
import org.jetbrains.anko.startActivity

/**
 *
 * @author Extends
 * @date 2019/9/11/011
 */
class LockActivity : BaseActivity(),PatternLockViewListener {

    private var lock by preference("lock", "", MD5Util.getMD5Str("LockActivity",MD5Util.MD5_LOWER_CASE))
    private var status = 0 // 1 无密码 2 录入一次密码，等待确认密码  3 已有密码
    private var tempLock = ""
    private var errorCount = 0
    private var startTime = 0L
    private val handles = object : Handler(){
        override fun handleMessage(msg: Message?) {
            if(msg?.what == 0 && msg?.obj == startTime){
                al_patternlock?.clearPattern()
            }
        }
    }

    override fun onCreateRootView() = R.layout.activity_lock

    override fun onInitView(savedInstanceState: Bundle?) {
        changeStatusBarColor(Color.WHITE)
        status = if(lock == "") 1 else 3
        changeMessage()
    }

    override fun initListener() {
        al_patternlock.correctStateColor
        al_patternlock.addPatternLockListener(this)
    }

    override fun initData() {

    }

    private fun changeMessage(){
        al_message?.text = when(status){
            1 -> "第一次登录请录入手势密码"
            2 -> "请再次确认密码"
            3 -> "请输入已录入的手势密码"
            else -> "未知状态"
        }
    }

    override fun onStarted() {
        al_patternlock?.setViewMode(PatternLockView.PatternViewMode.CORRECT)
        startTime = System.currentTimeMillis()
    }

    override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {}

    override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
        if(status == 1){
            tempLock = PatternLockUtils.patternToString(al_patternlock,pattern)
            al_patternlock?.clearPattern()
            status = 2
            changeMessage()
        }else if(status == 2){
            if(tempLock == PatternLockUtils.patternToString(al_patternlock,pattern)){
                //已录入，进入主页
                lock = tempLock
                finish()
                startActivity<MainActivity>()
            }else if(errorCount < 3){
                al_patternlock?.setViewMode(PatternLockView.PatternViewMode.WRONG)
                "密码错误，请再次输入".toast()
                errorCount++
                handles.sendMessageDelayed(Message.obtain(handles,0,startTime),2000)
            }else{
                "密码错误次数已达上限".toast()
                finish()
            }
        }else if(status == 3){
            if(lock == PatternLockUtils.patternToString(al_patternlock,pattern)){
                //已录入，进入主页
                finish()
                startActivity<MainActivity>()
            }else if(errorCount < 3){
                al_patternlock?.setViewMode(PatternLockView.PatternViewMode.WRONG)
                "密码错误，请再次输入".toast()
                errorCount++
                handles.sendMessageDelayed(Message.obtain(handles,0,startTime),2000)
            }else{
                "密码错误次数已达上限".toast()
                finish()
            }
        }
    }

    override fun onCleared() {}

    override fun onDestroy() {
        al_patternlock.removePatternLockListener(this)
        handles.removeMessages(0)
        super.onDestroy()
    }


}