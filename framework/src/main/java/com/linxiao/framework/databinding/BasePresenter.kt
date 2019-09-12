package com.linxiao.framework.databinding

import androidx.lifecycle.ViewModel
import com.linxiao.framework.adapter.BaseAdapter

/**
 *databinding事件的基类,所有事件必须继承
 * Created by Extends on 2017/9/25 18:00
 */
open class BasePresenter:ViewModel(),BaseAdapter.Presenter