package com.linxiao.framework.glide

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.linxiao.framework.BaseApplication
import com.linxiao.framework.glide.GlideApp
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.jetbrains.anko.dip

/**
 * 图片加载管理器
 * Created by Extends on 2018/2/4/004.
 */
object ImgManager {
    var IMGURL = ""     //图片地址域名
    var ERROR = 0       //错误图
    var PLACEHOLDER = 0 //占位图

    /**
     * @param url
     * @param image
     * @param isCircleImage
     * @param placeHolder
     * @param rdp
     * @param rely
     * @param ay
     */
    @SuppressLint("CheckResult")
    fun showImg(url: Any?, image: ImageView, isCircleImage: Boolean = false, @DrawableRes placeHolder: Int = -1, rdp: Int = 0, isCenterCrop:Boolean = true, imgRouondType: RoundedCornersTransformation.CornerType?, rely: Any = BaseApplication.getAppContext(), ay: (ExtendTransformation) -> Unit? = {}) {

        //如果url是String
        val urls = if (url == null) {
            ""
        } else if (url is String) {
            if (url.isEmpty()) ""
            val isExists = FileUtils.isFileExists(url)
            if(isExists){
                url
            }else if (!url.startsWith("http")) {
                IMGURL + url
            } else url
        } else url

        //初始化requestOptions
        val requestOptions = if (placeHolder == -1) {
            RequestOptions()
                    .placeholder(PLACEHOLDER)
                    .error(ERROR)
                    .centerCrop()
//                    .let { if(isCenterCrop) it.centerCrop() else it.fitCenter() }
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .dontAnimate()
        } else {
            RequestOptions().placeholder(placeHolder)
                    .error(placeHolder)
//                    .let { if(isCenterCrop) it.centerCrop() else it.fitCenter() }
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        }
        //初始化Transformation
        val ma = arrayListOf<Transformation<Bitmap>>()
//        ma.add(if(isCenterCrop) CenterCrop() else FitCenter())


        //后置处理形状，避免ay方法里突破形状的束缚
        val native = arrayListOf<Transformation<Bitmap>>()
        native.add(if(isCenterCrop) CenterCrop() else FitCenter())
        if(isCircleImage){
            native.add(CircleCrop())
        }else if(rdp > 0){
            native.add(RoundedCornersTransformation(image.context.dip(rdp), 0,imgRouondType?:RoundedCornersTransformation.CornerType.ALL))
        }

        //获取ay里面设置的Transformation
        val et = ExtendTransformation()
        ay(et)
        ma.addAll(et.getPre())
        ma.addAll(native)
        ma.addAll(et.getPost())

        // 考虑一下，ma需不需要去重？

        if(ma.size>0){
            requestOptions.transforms(MultiTransformation(ma)).dontAnimate()
        }

        //判断图片依赖的容器是什么
        when (rely) {
            is Fragment -> GlideApp.with(rely)
            is androidx.fragment.app.Fragment -> GlideApp.with(rely)
            is FragmentActivity -> GlideApp.with(rely)
            is Activity -> GlideApp.with(rely)
            is Context -> GlideApp.with(rely)
            is View -> GlideApp.with(rely)
            else -> return
        }
                // 加载数据源
                .load(urls)
                //加载 占位图、错误图
                .apply(requestOptions)
                .dontAnimate()
//                .dontTransform()
                .into(image)
    }
}

/**
 * 给ImageView设置图片
 *
 * @param url 图片源 ，可以是String，可以是Int，可以是Uri
 * @param isCircleImage 是否裁剪成圆形
 * @param placeHolder 占位图
 * @param rdp 图片圆角 单位：dp
 * @param isCenterCrop 是否使用CenterCrop ，默认是true，如果为false则使用FitCenter
 * @param rely 图片依赖的容器
 * @param ay 补充方法，如果方法不能满足要求而扩展的补充方法
 */
fun ImageView.setImageUrl(url: Any?, isCircleImage: Boolean = false, @DrawableRes placeHolder: Int = -1, rdp: Int = 0,isCenterCrop:Boolean = true, imgRouondType: RoundedCornersTransformation.CornerType?=null,rely: Any = BaseApplication.getAppContext(), ay: (ExtendTransformation) -> Unit = {}) {
    ImgManager.showImg(url, this, isCircleImage, placeHolder, rdp, isCenterCrop, imgRouondType, rely, ay)
}

data class ExtendTransformation(
        /**
         * 前置的Transform
         */
        private var preTransform : ArrayList<Transformation<Bitmap>> = arrayListOf(),
        /**
         * 后置的Transform
         */
        private var postTransform : ArrayList<Transformation<Bitmap>> = arrayListOf()
){
    /**
     * 添加前置的Transform
     */
    fun addPre(transformation : Transformation<Bitmap>){
        preTransform.add(transformation)
    }

    /**
     * 添加后置的Transform
     */
    fun addPost(transformation : Transformation<Bitmap>){
        postTransform.add(transformation)
    }

    fun getPre() = preTransform

    fun getPost() = postTransform
}