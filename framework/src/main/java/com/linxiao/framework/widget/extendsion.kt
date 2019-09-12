package com.linxiao.framework.widget

import android.content.Context
import android.view.ViewManager
import android.widget.RadioButton
import android.widget.RadioGroup
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView

/**
 *
 * @author Extends
 * @date 2019/2/15/015
 */


///**
// *  给smartRefreshLayout配置dsl方式
// */
//inline fun ViewManager.smartRefreshLayout(init: (@AnkoViewDslMarker SmartRefreshLayout).() -> Unit): SmartRefreshLayout {
//    return ankoView({ctx: Context -> SmartRefreshLayout(ctx) }, theme = 0) {
//        layoutParams = android.view.ViewGroup.LayoutParams(-1,-1)
//        init()
//    }
//}

/**
 * 给navigationView配置dsl方式
 */
inline fun ViewManager.navigationView(init: (@AnkoViewDslMarker NavigationView).() -> Unit): NavigationView {
    return ankoView({ctx: Context -> NavigationView(ctx) }, theme = 0) {
        layoutParams = android.view.ViewGroup.LayoutParams(-1,-2)
        init()
    }
}


/**
 *  给simpleTitleView配置dsl方式
 */
inline fun ViewManager.simpleTitleView(init: (@AnkoViewDslMarker SimpleTitleView).() -> Unit): SimpleTitleView {
    return ankoView({ctx: Context -> SimpleTitleView(ctx) }, theme = 0) {
        layoutParams = android.view.ViewGroup.LayoutParams(-1,-2)
        init()
    }
}



/**
 *  给roundImageView配置dsl方式
 */
inline fun ViewManager.roundImageView(init: (@AnkoViewDslMarker RoundImageView).() -> Unit): RoundImageView {
    return ankoView({ctx: Context -> RoundImageView(ctx) }, theme = 0) {
        layoutParams = android.view.ViewGroup.LayoutParams(-1,-2)
        init()
    }
}

/**
 * 快速通过tag选中子项
 */
fun RadioGroup.quickSelect(stag:String){
    (0 until childCount).map { getChildAt(it).apply { isSelected = false } as RadioButton }
            .forEach {
                if(it.tag.toString() == stag){
                    it.isChecked = true
                    return
                }
            }
}

/**
 * 快速获取选中项的tag
 */
fun RadioGroup.quickGetSelectTag() = (0 until childCount).map { getChildAt(it) as RadioButton }.firstOrNull { it.isChecked }?.tag?.toString()