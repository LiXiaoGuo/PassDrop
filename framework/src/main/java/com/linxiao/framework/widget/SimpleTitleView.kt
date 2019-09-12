package com.linxiao.framework.widget

/**
 *
 * @author Extends
 * @date 2019/2/15/015
 */


import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import com.linxiao.framework.R
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import android.content.res.TypedArray
import android.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils


/**
 * 简单的标题栏控件
 * @author Extends
 * @date 2018/7/18/018
 */

class SimpleTitleView : ConstraintLayout {
    /**
     * title控件
     */
    private var titleView: TextView? = null
    /**
     * 左边的图标控件，一般来说是返回按钮
     */
    private var leftImageView: ImageView? = null
    /**
     * 右边的图标控件
     */
    private var rightImageView: ImageView? = null
    /**
     * 左边的文字控件，默认字体大小会是titleView的0.8倍
     */
    private var leftTextView: TextView? = null
    /**
     * 右边的文字控件，默认字体大小会是titleView的0.8倍
     */
    private var rightTextView: TextView? = null


    private var line: View? = null

    /**
     * 控件的默认高度
     */
    private val actionBarSize by lazy { getActionBarSize() }

    /**
     * 默认的图片边长
     */
    private val defImageSide = dip(40)

    /**
     * 默认的文字的长度
     */
    private val defTextWidth = dip(70)

    /**
     * 默认的图片内边距
     */
    private val defImagePadding = dip(7)

    /**
     * 图片内边距
     */
    private var imagePadding = defImagePadding

    /**
     * 右边图标padding
     */
    private var imageRightPadding = imagePadding

    /**
     * 左边图标padding
     */
    private var imageleftPadding = imagePadding


//    /**
//     * 默认的图片内边距
//     */
//    private var ImagePadding = dip(7)
//    /**
//     * 右边图片内边距
//     */
//    private val rightImagePadding = dip(10)

    /**
     * 保存的信息
     */
    private var build: Build = Build()

    /**
     * 状态栏的高度
     */
    private var statusBarHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            initView(context, attrs)
        }

    }

    private fun initView(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SimpleTitleView)
        build.setTitle(ta.getString(R.styleable.SimpleTitleView_title))
                .setTitleSize(ta.getDimensionPixelSize(R.styleable.SimpleTitleView_titleSize, build.getTitleSize()))
                .setTitleColor(ta.getColor(R.styleable.SimpleTitleView_titleColor, build.getTitleColor()))
                .setLeftImageRes(ta.getResourceId(R.styleable.SimpleTitleView_leftImageRes, build.getLeftImageRes()))
                .setRightImageRes(ta.getResourceId(R.styleable.SimpleTitleView_rightImageRes, build.getRightImageRes()))
                .setLeftText(ta.getString(R.styleable.SimpleTitleView_leftText))
                .setLeftTextSize(ta.getDimensionPixelSize(R.styleable.SimpleTitleView_leftTextSize, build.getLeftTextSize()))
                .setLeftTextColor(ta.getColor(R.styleable.SimpleTitleView_leftTextColor, build.getLeftTextColor()))
                .setRightText(ta.getString(R.styleable.SimpleTitleView_rightText))
                .setRightTextSize(ta.getDimensionPixelSize(R.styleable.SimpleTitleView_rightTextSize, build.getRightTextSize()))
                .setRightTextColor(ta.getColor(R.styleable.SimpleTitleView_rightTextColor, build.getRightTextColor()))
                .setIsCilpStatusBar(ta.getBoolean(R.styleable.SimpleTitleView_isCilpStatusBar,false))
        imageRightPadding = ta.getDimension(R.styleable.SimpleTitleView_rightImagePadding, imagePadding.toFloat()).toInt()
        imageleftPadding = ta.getDimension(R.styleable.SimpleTitleView_leftImagePadding, imagePadding.toFloat()).toInt()
        ta.recycle()
        initFromBuild(build)
    }

    private fun initFromBuild(build: Build) {
        /*********************************   配置各个控件的属性  **********************************/

        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)
        val attribute = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
        val typedArray = context.theme.obtainStyledAttributes(typedValue.resourceId, attribute)

        if (build.getImagePadding() != 0) {
            imagePadding = build.getImagePadding()
        }

        // 先判断imagePadding是否修改过，如果修改过则直接使用imagePadding，如果没有修改过再判断
        // Bulid.defImagePadding是否是-1，如果==-1表示没有预设的默认值，则使用defImagePadding
        // 反之则使用Build.defImagePadding
        imagePadding = if (imagePadding == defImagePadding) {
            if (Build.defImagePadding == -1) defImagePadding else Build.defImagePadding
        } else imagePadding

        //配置title属性
        if (build.getTitle() != null) {
            if (titleView == null) {
                titleView = TextView(context)
            }
            titleView!!.setSingleLine()
            titleView!!.ellipsize = TextUtils.TruncateAt.END
            titleView!!.text = build.getTitle()
            titleView!!.gravity = Gravity.CENTER
            if (build.getTitleColor() != 0) {
                titleView!!.setTextColor(build.getTitleColor())
            }
            if (build.getTitleSize() != 0) {
                titleView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, build.getTitleSize().toFloat())
            }
        }
        //配置左边imageView
        if (build.getLeftImageRes() != 0) {
            if (leftImageView == null) {
                leftImageView = ImageView(context)
            }
            leftImageView!!.apply {
                padding = imagePadding
                imageResource = build.getLeftImageRes()
                isClickable = true
                background = typedArray.getDrawable(0)
            }
            if (build.getOnLeftClick() != null) {
                leftImageView!!.onClick { build.getOnLeftClick()?.invoke(it) }
            }
        }
        //配置右边imageView
        if (build.getRightImageRes() != 0) {
            if (rightImageView == null) {
                rightImageView = ImageView(context)
            }
            rightImageView!!.apply {
                padding = imagePadding
                imageResource = build.getRightImageRes()
                isClickable = true
                background = typedArray.getDrawable(0)
            }
            if (build.getOnRightClick() != null) {
                rightImageView!!.onClick { build.getOnRightClick()?.invoke(it) }
            }
        }
        //配置左边文字
        if (build.getLeftText() != null) {
            if (leftTextView == null) {
                leftTextView = TextView(context)
            }
            leftTextView!!.apply {
                text = build.getLeftText()
                singleLine = true
                verticalPadding = dip(5)
            }
            //配置文字的大小，如果没有单独设置大小，则默认使用titleView的文字大小乘以系数
            if (build.getLeftTextSize() != 0) {
                leftTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, build.getLeftTextSize().toFloat())
            } else if (titleView != null) {
                leftTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleView!!.textSize * build.getCoefficient())
            }
            //配置文字的颜色，如果没有单独设置颜色，则默认使用titleView的文字颜色
            if (build.getLeftTextColor() != 0) {
                leftTextView!!.setTextColor(build.getLeftTextColor())
            } else if (titleView != null) {
                leftTextView!!.setTextColor(titleView!!.textColors)
            }
            if (build.getOnLeftClick() != null) {
                leftTextView!!.onClick {
                    build.getOnLeftClick()?.invoke(leftTextView) }
            }
        }
        //配置右边文字
        if (build.getRightText() != null) {
            if (rightTextView == null) {
                rightTextView = TextView(context)
            }
            rightTextView!!.apply {
                text = build.getRightText()
                singleLine = true
                gravity = Gravity.RIGHT
                verticalPadding = dip(5)
            }
            //配置文字的大小，如果没有单独设置大小，则默认使用titleView的文字大小乘以系数
            if (build.getRightTextSize() != 0) {
                rightTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, build.getRightTextSize().toFloat())
            } else if (titleView != null) {
                rightTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleView!!.textSize * build.getCoefficient())
            }
            //配置文字的颜色，如果没有单独设置颜色，则默认使用titleView的文字颜色
            if (build.getRightTextColor() != 0) {
                rightTextView!!.setTextColor(build.getRightTextColor())
            } else if (titleView != null) {
                rightTextView!!.setTextColor(titleView!!.textColors)
            }
            if (build.getOnRightClick() != null) {
                rightTextView!!.onClick { build.getOnRightClick()?.invoke(it) }
            }
        }

        /***********************************   把控件添加到布局中   ********************************/

        //添加基准线
        if (line == null) {
            line = View(context)
            line!!.id = R.id.simple_title_line
        }
        val lineLp = Constraints.LayoutParams(1, 1)
        lineLp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        //判断是否需要留出状态栏的高度
        lineLp.topMargin = if (build.isCilpStatusBar()) getStatusBarHeight() else 0
        if (line?.parent == null) {
            addView(line, lineLp)
        } else {
            line!!.layoutParams = lineLp
        }


        if (leftImageView != null && leftImageView?.parent == null) {
            val lp = ConstraintLayout.LayoutParams(defImageSide, defImageSide)
            lp.topToTop = line!!.id
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            lp.leftMargin = imageleftPadding
            addView(leftImageView, lp)
        }
        if (rightImageView != null && rightImageView?.parent == null) {
            val lp = ConstraintLayout.LayoutParams(defImageSide, defImageSide)
            lp.topToTop = line!!.id
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            lp.rightMargin = imageRightPadding
            addView(rightImageView, lp)
        }
        if (leftTextView != null && leftImageView == null && leftTextView?.parent == null) {
            val lp = ConstraintLayout.LayoutParams(defTextWidth, -2)
            lp.topToTop = line!!.id
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            lp.leftMargin = imageleftPadding
            addView(leftTextView, lp)
        }
        if (rightTextView != null && rightImageView == null && rightTextView?.parent == null) {
            val lp = ConstraintLayout.LayoutParams(defTextWidth, -2)
            lp.topToTop = line!!.id
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            lp.rightMargin = imageRightPadding
            addView(rightTextView, lp)
        }
        if (titleView != null && titleView?.parent == null) {
            val lp = Constraints.LayoutParams(0, -2)
            lp.topToTop = line!!.id
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
            lp.horizontalMargin = Math.max(defImageSide, defTextWidth + imagePadding) + dip(10)
            addView(titleView, lp)
        }
    }

    /**
     * 如果高度是wrap_content，则使用默认的高度
     * 默认高度为android.R.attr.actionBarSize
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //状态栏高度
        val sbh = if (build.isCilpStatusBar()) getStatusBarHeight() else 0

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        /**
         * 默认的标题栏的高度，默认为0
         * 如果>0,且测量模式为AT_MOST，则使用defHeight
         * 如果>0,且不满足测量模式为AT_MOST，则使用代码中给height的赋值
         * 如果<=0,且测量模式为AT_MOST，则使用actionBarSize
         * 如果<=0,且不满足测量模式为AT_MOST，则使用代码中给height的赋值
         */
        val heightSize = if (Build.defHeight > 0) {
            if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) Build.defHeight else MeasureSpec.getSize(heightMeasureSpec)
        } else {
            if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) actionBarSize else MeasureSpec.getSize(heightMeasureSpec)
        } + sbh
//        println(heightSize)
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    /**获取ActionBar的高度 */
    private fun getActionBarSize(context: Context = getContext()): Int {
        val attrs = intArrayOf(android.R.attr.actionBarSize)
        val values = context.theme.obtainStyledAttributes(attrs)
        try {
            return values.getDimensionPixelSize(0, dip(48))//第一个参数数组索引，第二个参数 默认值
        } finally {
            values.recycle()
        }
    }

    /**
     * 设置标题
     */
    fun setTitleText(title: String) {
        build.setTitle(title)
        if (titleView != null){
            titleView!!.text = title
        }
    }

    /**
     * 设置左边的文本或者图片的点击事件
     */
    fun setOnLeftClick(click: (v: View?) -> Unit) {
        build.setOnLeftClick(click)
        if (leftImageView != null) {
            leftImageView!!.onClick { click(it) }
        } else if (leftTextView != null) {
            leftTextView!!.onClick { click(it) }
        }
    }

    fun setLeftImageRes(imageRes:Int){
        build.setLeftImageRes(imageRes)
        if(leftImageView == null){
            initFromBuild(build)
        }else{
            leftImageView?.setImageResource(imageRes)
        }
    }

    /**
     * 设置右边的文本或者图片的点击事件
     */
    fun setOnRightClick(click: (v: View?) -> Unit) {
        build.setOnRightClick(click)
        if (rightImageView != null) {
            rightImageView!!.onClick { click(it) }
        } else if (rightTextView != null) {
            rightTextView!!.onClick { click(it) }
        }
    }

    fun setRightImageRes(imageRes:Int){
        build.setRightImageRes(imageRes)
        if(rightImageView == null){
            initFromBuild(build)
        }else{
            rightImageView?.setImageResource(imageRes)
        }
    }

    /**
     * 改变配置数据
     */
    fun change(change: Build.() -> Unit) {
        build.change()
        initFromBuild(build)
    }

    /**
     * 获取Build
     */
    fun getBuild(): Build {
        return build
    }

    private fun getStatusBarHeight(): Int {
        // 获得状态栏高度
        if (statusBarHeight == 0) {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }


    class Build {
        /**
         * title文字
         */
        private var title: String? = null
        /**
         * title字体大小，单位是px
         */
        private var titleSize: Int = 0
        /**
         * title字体颜色
         */
        private var titleColor: Int = 0
        /**
         * 左边的图片资源，一般是返回按钮
         */
        private var leftImageRes: Int = 0
        /**
         * 右边的图片资源
         */
        private var rightImageRes: Int = 0
        /**
         * 左边的文字
         */
        private var leftText: String? = null
        /**
         * 左边文字大小，单位是px
         */
        private var leftTextSize: Int = 0
        /**
         * 左边文字颜色
         */
        private var leftTextColor: Int = 0
        /**
         * 右边的文字
         */
        private var rightText: String? = null
        /**
         * 右边文字大小，单位是px
         */
        private var rightTextSize: Int = 0
        /**
         * 右边文字颜色
         */
        private var rightTextColor: Int = 0

        /**
         * 系数，当没有给leftText和rightText设置文字大小时
         * 默认给它们设置title字体大小的系数倍
         */
        private var coefficient = 0.8f

        /**
         * 右边按钮的点击事件
         */
        private var onRightClick: ((v: View?) -> Unit)? = null

        /**
         * 左边按钮的点击事件
         */
        private var onLeftClick: ((v: View?) -> Unit)? = null

        /**
         * 是否在顶部留出状态栏的高度
         */
        private var isCilpStatusBar = false

        /**
         * 图标的内边距，单位是px
         */
        private var imagePadding = 0

        fun getImagePadding() = imagePadding

        fun setImagePadding(imagepadding: Int) {
            this.imagePadding = imagepadding
        }


        fun getTitle(): String? {
            return title
        }

        fun setTitle(title: String?): Build {
            this.title = title
            return this
        }

        fun getTitleSize(): Int {
            return titleSize
        }

        fun setTitleSize(titleSize: Int): Build {
            this.titleSize = titleSize
            return this
        }

        fun getTitleColor(): Int {
            return titleColor
        }

        fun setTitleColor(titleColor: Int): Build {
            this.titleColor = titleColor
            return this
        }

        fun getLeftImageRes(): Int {
            return leftImageRes
        }

        fun setLeftImageRes(leftImageRes: Int): Build {
            this.leftImageRes = leftImageRes
            return this
        }

        fun getRightImageRes(): Int {
            return rightImageRes
        }

        fun setRightImageRes(rightImageRes: Int): Build {
            this.rightImageRes = rightImageRes
            return this
        }

        fun getLeftText(): String? {
            return leftText
        }

        fun setLeftText(leftText: String?): Build {
            this.leftText = leftText
            return this
        }

        fun getLeftTextSize(): Int {
            return leftTextSize
        }

        fun setLeftTextSize(leftTextSize: Int): Build {
            this.leftTextSize = leftTextSize
            return this
        }

        fun getLeftTextColor(): Int {
            return leftTextColor
        }

        fun setLeftTextColor(leftTextColor: Int): Build {
            this.leftTextColor = leftTextColor
            return this
        }

        fun getRightText(): String? {
            return rightText
        }

        fun setRightText(rightText: String?): Build {
            this.rightText = rightText
            return this
        }

        fun getRightTextSize(): Int {
            return rightTextSize
        }

        fun setRightTextSize(rightTextSize: Int): Build {
            this.rightTextSize = rightTextSize
            return this
        }

        fun getRightTextColor(): Int {
            return rightTextColor
        }

        fun setRightTextColor(rightTextColor: Int): Build {
            this.rightTextColor = rightTextColor
            return this
        }

        fun getCoefficient(): Float {
            return coefficient
        }

        fun setCoefficient(coefficient: Float): Build {
            this.coefficient = coefficient
            return this
        }

        fun setOnRightClick(click: (v: View?) -> Unit): Build {
            this.onRightClick = click
            return this
        }

        fun setOnLeftClick(click: (v: View?) -> Unit): Build {
            this.onLeftClick = click
            return this
        }

        fun getOnLeftClick() = onLeftClick

        fun getOnRightClick() = onRightClick

        fun setIsCilpStatusBar(isCilpStatusBar: Boolean): Build {
            this.isCilpStatusBar = isCilpStatusBar
            return this
        }

        fun isCilpStatusBar() = this.isCilpStatusBar

        companion object {
            /**
             * 默认的标题栏的高度，默认为0
             * 如果>0,且测量模式为AT_MOST，则使用defHeight
             * 如果>0,且不满足测量模式为AT_MOST，则使用代码中给height的赋值
             * 如果<=0,且测量模式为AT_MOST，则使用actionBarSize
             * 如果<=0,且不满足测量模式为AT_MOST，则使用代码中给height的赋值
             */
            var defHeight = 0

            /**
             * 默认的图片内边距，默认-1，表示使用控件内部的默认值
             * 如果>-1,且控件内的内边距值也是默认值，表示使用当前的默认值，而不是使用控件内部的默认值
             * 如果控件内不是默认值，则还是优先使用控件内的值
             */
            var defImagePadding = -1
        }
    }
}