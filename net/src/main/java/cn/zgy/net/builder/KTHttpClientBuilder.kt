package cn.zgy.net.builder

import cn.zgy.net.utils.HttpsUtils
import com.stormkid.okhttpkt.cache.CookieCaches
import com.stormkid.okhttpkt.cache.CookieManager
import com.stormkid.okhttpkt.rule.ClientRule
import com.stormkid.okhttpkt.rule.FactoryRule
import com.stormkid.okhttpkt.utils.Log
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
/**
  *
  * @Description:     配置
  * @Author:         zhengy
  * @CreateDate:     2020/5/11 下午5:26
  * @Version:        1.0
 */

class KTHttpClientBuilder private constructor() : ClientRule {

    private val logBody = HttpLoggingInterceptor.Level.BODY
    private val logNone = HttpLoggingInterceptor.Level.NONE
    /**
     * 超时
     */
    private var ERR_TIME = 5000L

    /**
     * 是否需要处理cookie
     */
    private var IS_NEED_COOKIE = false

    /**
     * 是否需要重定向
     */
    private var IS_REDIRECT_ALLOW = true

    companion object {
        private val httpClient: OkHttpClient.Builder by lazy { OkHttpClient.Builder() }
        private val heads = hashMapOf<String, String>()
        private var showLog = false
        private val instance: KTHttpClientBuilder by lazy { KTHttpClientBuilder() }
    }

    /**
     * 自定义httpclient 时调用的类
     */
    object Builder : FactoryRule {
        /**
         *  只添加写入时间
         */
        override fun writeTimeOut(time: Long) = apply {
            httpClient.writeTimeout(time, TimeUnit.MILLISECONDS)
        }
        /**
         *  添加socket 请求
         */
        override fun socketFactory(socketFactory: SocketFactory)= apply {
            httpClient.socketFactory(socketFactory)
        }
        /**
         * 自定义添加请求认证证书
         */
        override fun SSLSocketFactory(
            sslSocketFactory: SSLSocketFactory,
            x509TrustManager: X509TrustManager
        ) = apply {
            httpClient.sslSocketFactory(sslSocketFactory, x509TrustManager)
        }
        /**
         *  设置超时时间
         */
        override fun setTimeOut(time: Long) = apply{
            httpClient.readTimeout(time, TimeUnit.MILLISECONDS)
            httpClient.writeTimeout(time, TimeUnit.MILLISECONDS)
            httpClient.readTimeout(time, TimeUnit.MILLISECONDS)
        }
        /**
         * 添加cookie
         */
        override fun setCookie(isNeed: Boolean) = apply {
            if (isNeed) {
                httpClient.cookieJar(CookieCaches(CookieManager.instance))
            }
        }
        /**
         * 配置是否重定向
         */
        override fun setFollowRedirects(allowRedirect: Boolean) = apply {
            httpClient.followRedirects(allowRedirect)
        }

        /**
         * 清理拦截器
         */
        override fun cleanInterceptor() = apply { httpClient.interceptors().clear() }

        /**
         * 添加拦截器
         */
        fun addInterceptor(interceptor: Interceptor) = apply {
            httpClient.addInterceptor(interceptor)
        }
        /**
         * 添加请求拦截器
         */
        fun addNetworkInterceptor(interceptor: Interceptor) = this.apply {
            httpClient.addNetworkInterceptor(interceptor)
        }

        /**
         * TODO 添加Dns 过滤
         */
        fun dns(dns: Dns) = apply {
            httpClient.dns(dns)
        }


        /**
         * TODO 添加缓存处理
         */
        fun cache(cache: Cache) = apply {
            httpClient.cache(cache)
        }

        fun setHead(hashMap: HashMap<String, String>) = apply {
            heads.clear()
            heads.putAll(hashMap)
        }

        fun build(): KTHttpClientBuilder {
            return instance
        }

    }
    /**
     * 获取默认httpclient
     */
    override fun getHttpClient() = httpClient.apply {
        /**
         * 清理interceptors 防止重复添加
         */
        interceptors().clear()
        if(!heads.isEmpty()) {
            addInterceptor {
                val myHead = heads.toHeaders()
                val builder = it.request().newBuilder()
                it.proceed(builder.headers(myHead).build())
            }
        }
            if(showLog){
                addInterceptor {
                    Log.setEnable(true)
                    Log.i("okhttpUrl", it.request().url.toString())
                    val res = it.proceed(it.request())
                    res
                }
            }else {
                Log.setEnable(false)
            }

        if (IS_NEED_COOKIE) httpClient.cookieJar(CookieCaches(CookieManager.instance))
        followRedirects(IS_REDIRECT_ALLOW)
        connectTimeout(ERR_TIME, TimeUnit.MILLISECONDS)
        readTimeout(ERR_TIME, TimeUnit.MILLISECONDS)
        writeTimeout(ERR_TIME, TimeUnit.MILLISECONDS)
    }
    /**
     * 获取默认httpsclient
     */
    override fun getHttpsClient()= httpClient.apply {
        getHttpClient().apply {
            val ssl = HttpsUtils.getSslSocketFactory()
            sslSocketFactory(ssl.sSLSocketFactory, ssl.trustManager)
            followSslRedirects(IS_REDIRECT_ALLOW)
        }
    }
    /**
     * 获取自定义的client
     */
    override fun getCustomnClient() =
        httpClient
    /**
     * 超市时间（毫秒）
     */
    override fun setTimeOut(time: Long) {
        ERR_TIME = time
    }

    override fun isLogShow(isShow: Boolean) {
        showLog = isShow
    }
    /**
     * 是否需要cookie
     */
    override fun isNeedCookie(isNeed: Boolean) {
        IS_NEED_COOKIE = isNeed
    }

    /**
     * 是否重定向
     */
    override fun setFollowRedirects(allowRedirect: Boolean) {
        IS_REDIRECT_ALLOW = allowRedirect
    }
}