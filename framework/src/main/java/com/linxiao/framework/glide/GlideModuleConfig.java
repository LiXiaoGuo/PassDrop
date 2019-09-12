package com.linxiao.framework.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.linxiao.framework.util.Config;

import java.io.File;
import java.io.InputStream;

/**
 * * Created by Extends on 2016/8/3 0003.
 */
@com.bumptech.glide.annotation.GlideModule
public class GlideModuleConfig extends AppGlideModule {
    public static String path = Config.INSTANCE.FILE_SAVE_PATH() + File.separator + "imgCache" + File.separator;
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        builder.setBitmapPool(new LruBitmapPool((int)(defaultBitmapPoolSize*1.2)));//设置缓存内存大小
//        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);//设置图片解码格式
        builder.setDiskCache(new DiskLruCacheFactory(path,1024*1024*30));//设置Glide磁盘缓存大小
        builder.setMemoryCache(new LruResourceCache((int)(defaultMemoryCacheSize*1.2)));//设置Glide内存缓存大小
    }

    /**
     * 禁止解析Manifest
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        //加载https的图片资源
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(UnsafeOkHttpClient.getUnsafeOkHttpClient()));
    }
}
