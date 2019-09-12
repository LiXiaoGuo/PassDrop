package com.linxiao.framework.viewModel

import androidx.lifecycle.ViewModel
import com.linxiao.framework.activity.BaseActivity
import com.linxiao.framework.fragment.BaseFragment
import java.lang.ref.WeakReference

/**
 * 持有activity的viewmodel
 * 主要用来显示加载框之类的
 * 继承于ViewModel的作用是可以在转动屏幕等情况时持久化数据
 * :activity...是弱引用,只要gc必然回收
 * Created by Extends on 2017/9/27 11:03
 */
open class BaseViewModel :ViewModel() {
    lateinit var activity:WeakReference<BaseActivity>
    lateinit var fragment:WeakReference<BaseFragment>
}