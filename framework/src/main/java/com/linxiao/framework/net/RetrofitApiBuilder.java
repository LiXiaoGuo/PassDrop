package com.linxiao.framework.net;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.Internal;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Retrofit构建类
 * Created by LinXiao on 2016-12-31.
 */
public class RetrofitApiBuilder {

    private Retrofit.Builder mRetrofitBuilder;
    private OkHttpClient.Builder okHttpClientBuilder;
    private Map<String, String> universalHeaders;

    private boolean hasNoConvertFactory = true;

    private int time=5;

    private List<Interceptor> mInterceptors;//存放拦截器
    private List<Interceptor> mNetworkInterceptors;//存放网络拦截器
    private List<Class<? extends Interceptor>> mRemoveInterceptors;//移除拦截器

    public RetrofitApiBuilder() {
        mRetrofitBuilder = new Retrofit.Builder();
        okHttpClientBuilder = new OkHttpClient.Builder();
        universalHeaders = new ArrayMap<>();
        mInterceptors = new ArrayList<>();
        mNetworkInterceptors = new ArrayList<>();
        mRemoveInterceptors = new ArrayList<>();
    }

    /**
     * 设置服务端地址
     */
    public RetrofitApiBuilder setServerUrl(String serverUrl) {
        mRetrofitBuilder.baseUrl(serverUrl);
        return this;
    }

    /**
     * 配置请求通用Headers
     *
     * @param name  Header 名字
     * @param value Header 值
     */
    public RetrofitApiBuilder addHeader(String name, String value) {
        universalHeaders.put(name, value);
        return this;
    }

    /**
     * 添加Https支持
     */
    public RetrofitApiBuilder setSSLSocketFactory(@NonNull SSLSocketFactory factory, X509TrustManager trustManager) {
        okHttpClientBuilder.sslSocketFactory(factory, trustManager);
        return this;
    }

    /**
     * 添加 CallAdapterFactory
     */
    public RetrofitApiBuilder addCallAdapterFactory(@NonNull CallAdapter.Factory factory) {
        mRetrofitBuilder.addCallAdapterFactory(factory);
        return this;
    }

    /**
     * 添加 ConvertFactory;
     */
    public RetrofitApiBuilder addConvertFactory(@NonNull Converter.Factory factory) {
        mRetrofitBuilder.addConverterFactory(factory);
        hasNoConvertFactory = false;
        return this;
    }

    /**
     * 给Retrofit的OkHttpClient添加其他拦截器
     */
    public RetrofitApiBuilder addCustomInterceptor(@NonNull Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    public RetrofitApiBuilder addCustomNetworkInterceptor(@NonNull Interceptor interceptor) {
        mNetworkInterceptors.add(interceptor);
        return this;
    }

    public RetrofitApiBuilder removeInterceptor(@NonNull Class<? extends Interceptor> clazz) {
        mRemoveInterceptors.add(clazz);
        return this;
    }

    /**
     * 修改时间
     * @param time
     * 单位TimeUnit.SECONDS
     * @return
     */
    public RetrofitApiBuilder setTime(int time) {
        this.time=time;
        return this;
    }


    public <T> T build(final Class<T> clazzClientApi) {

            okHttpClientBuilder.connectTimeout(time, TimeUnit.SECONDS);

        //自定义拦截器放在后面添加
        for (Interceptor interceptor : mInterceptors) {
            if (mRemoveInterceptors.indexOf(interceptor.getClass()) < 0) {
                okHttpClientBuilder.addInterceptor(interceptor);
            }
        }
        //网络拦截器
        for (Interceptor interceptor : mNetworkInterceptors) {
            if (mRemoveInterceptors.indexOf(interceptor.getClass()) < 0) {
                okHttpClientBuilder.addNetworkInterceptor(interceptor);
            }
        }

        mRetrofitBuilder.client(okHttpClientBuilder.build());
//        if (hasNoConvertFactory) {
//            mRetrofitBuilder.addConverterFactory(GsonConverterFactory.create());
//        }
        return mRetrofitBuilder.build().create(clazzClientApi);
    }

}
