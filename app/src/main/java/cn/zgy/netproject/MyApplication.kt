package cn.zgy.netproject

import android.app.Application
import android.content.Context
import cn.zgy.net.KTHttp
import cn.zgy.net.annptation.Client
import cn.zgy.net.annptation.NetClientType
import cn.zgy.net.cache.CookieCaches
import cn.zgy.net.cache.CookieManager

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        initOkHttp(this)
    }


    /**
     * 初始化网络请求框架
     */
    fun initOkHttp(context: Context) {

        KTHttp.instance.setBaseUrl("http://testmc.tmuyun.com")
            .isLogShow(false).isNeedBaseResponse(true).setErr("xxx")
            .setNetClientType(NetClientType.HTTPS_TYPE).setTimeOut(5000L).setCookie(CookieCaches(
                CookieManager.getInstance(this), null))
            .initHttpClient()
    }
}