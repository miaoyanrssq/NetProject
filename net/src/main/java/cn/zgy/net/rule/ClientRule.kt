package cn.zgy.net.rule

import okhttp3.CookieJar
import okhttp3.OkHttpClient

/**
@author ke_li
@date 2018/5/23
 */
interface ClientRule {
    fun getHttpClient():OkHttpClient.Builder
    fun getHttpsClient():OkHttpClient.Builder
    fun getCustomnClient():OkHttpClient.Builder
    fun setTimeOut(time:Long)
    fun isLogShow(boolean: Boolean)
    fun setCookie(cookieJar: CookieJar?):OkHttpClient.Builder
    fun setFollowRedirects(allowRedirect: Boolean)
}