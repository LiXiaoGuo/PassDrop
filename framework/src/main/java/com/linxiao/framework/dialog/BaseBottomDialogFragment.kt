package com.linxiao.framework.dialog

import android.app.Dialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import android.view.*
import androidx.fragment.app.FragmentManager

import com.linxiao.framework.R


/**
 * 从底部显示的全屏Dialog基类
 * Created by LinXiao on 2016-12-12.
 */
abstract class BaseBottomDialogFragment<out T:ViewDataBinding> : BaseDialogFragment() {
    private var mDialogHeight = 0
    private var mDialogWidth = 0
    private var mDimAmount = -1f
    private var mThemeRes = R.style.FrameworkBottomDialogStyle
//    private var rootView: View? = null
    private var mDialog:Dialog?=null
    val binding : T by lazy { DataBindingUtil.inflate<T>(LayoutInflater.from(context),onCreatRootView(),null,false) }


    /**
     * return fragment content view layout id
     */
    @LayoutRes
    protected abstract fun onCreatRootView(): Int

    /**
     * 一次调用
     * 在这里配置Dialog的各项属性和自定义的ContentView
     */
    protected abstract fun onInitView(dialog: Dialog, contentView: View)

    /**
     * 多次调用
     */
    protected abstract fun onMultipleInit(dialog: Dialog, contentView: View)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if(mDialog == null){
            val temp = Dialog(context, mThemeRes)
            temp.setContentView(binding.root)
            onInitView(temp, binding.root)
            mDialog = temp
        }
        onMultipleInit(mDialog!!,binding.root)
        return mDialog!!
    }

    override fun onStart() {
        super.onStart()
        // 一定要设置Background，如果不设置，window属性设置无效
        // 当前的背景是在mThemeRes设置了
        val win = dialog?.window ?: return
        win.decorView.setPadding(0, 0, 0, 0)
        win.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL) //可设置dialog的位置

        val params = win.attributes
        if(mDimAmount>=0){
            params.dimAmount = mDimAmount
        }
        win.setLayout(if(mDialogWidth > 0) mDialogWidth else params.width, if(mDialogHeight > 0) mDialogHeight else params.height)

    }

    /**
     * 设置底部Dialog高度
     */
    fun setDialogHeight(height: Int) {
        mDialogHeight = height
    }

    /**
     * 设置底部Dialog宽度
     */
    fun setDialogWidth(width: Int) {
        mDialogWidth = width
    }

    /**
     * 设置背景
     */
    fun setDimAmount(value:Float){
        mDimAmount = value
    }


    /**
     * 如果不需要使用框架默认样式，可以在这里自定义样式
     */
    fun setCustomStyle(@StyleRes styleRes: Int) {
        mThemeRes = styleRes
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }

}
