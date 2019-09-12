package com.linxiao.framework.util

import android.view.View
import androidx.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.TimeUtils
import com.linxiao.framework.glide.setImageUrl
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Extends on 2017/3/22.
 */

object BindingAdapterUtil {
    /**
     * 加载本地图片
     */
    @BindingAdapter("imgSrc")
    @JvmStatic
    fun loadImage(view: ImageView, imgId: Int) {
        view.setImageResource(imgId)
    }

    /**
     * 加载网络图片
     * @param imgUrl Any 图片源，也可以是本地图片
     * @param imgIsCircle Boolean 是否裁剪成圆形
     * @param imgRoundCorners Int 圆角大小,单位dp
     * @param imgIsBlur Boolean 是否使用高斯模糊
     * @param imgIsCenterCrop 是否使用CenterCrop ，默认是true，如果为false则使用FitCenter
     * 注：如果isCircleImage==true则roundCorners不会起作用
     */
    @BindingAdapter(value = ["imgUrl", "imgIsCircle", "imgRoundCorners", "imgIsBlur", "imgIsCenterCrop", "imgRoundType"], requireAll = false)
    @JvmStatic
    fun loadImage(view: ImageView, imgUrl: Any?, imgIsCircle: Boolean?, imgRoundCorners: Int?,
                  imgIsBlur: Boolean?,imgIsCenterCrop:Boolean?,imgRoundType: RoundedCornersTransformation.CornerType?) {
        view.setImageUrl(imgUrl, imgIsCircle ?: false,isCenterCrop = imgIsCenterCrop?:true, rdp = imgRoundCorners ?: 0,imgRouondType = imgRoundType) {
            if (imgIsBlur == true) {
                it.addPre(BlurTransformation())
            }
        }
    }

    /**
     * 格式化时间
     */
    @BindingAdapter(value = ["formatTime", "timeStyle"], requireAll = false)
    @JvmStatic
    fun formatTime(v: TextView, time:Any?, style:String?){
        val vt = time.toString()
        var vt1 = 0L
        if(vt.length == 10){
            vt1 = (vt.toLongOrNull()?:0L)*1000
        }else if(vt.length == 13){
            vt1 = vt.toLongOrNull()?:0L
        }
        v.text = if(style==null){
            TimeUtils.millis2String(vt1)
        }else{
            TimeUtils.millis2String(vt1, SimpleDateFormat(style, Locale.getDefault()))
        }
    }

    /**
     * 设置背景
     */
    @BindingAdapter("bd_background")
    @JvmStatic
    fun background(view: View, data: Int?) {
        if (data == null) return
        try {
            view.background = view.resources.getDrawable(data,null)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * 设置文字颜色
     */
    @BindingAdapter("bd_textColor")
    @JvmStatic
    fun textColor(view: TextView, data: Int?) {
        if (data == null) return
        try {
            view.setTextColor(view.context.color(data))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
