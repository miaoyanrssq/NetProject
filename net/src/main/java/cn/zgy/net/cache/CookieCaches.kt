package cn.zgy.net.cache

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*
import kotlin.collections.ArrayList

/**
 存储cookie
@author ke_li
@date 2019/6/28
 */
class CookieCaches (private val cookieManager: CookieManager, val skipUrls: ArrayList<String>?) : CookieJar{
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieManager.add(url,cookies)
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        if(urlMatch(url.toUrl().toString())){
            return Collections.emptyList()
        }
       return cookieManager.get(url)
    }

    /**
     * 剔除不需要传递cookie的url
     */
    private fun urlMatch(url: String): Boolean{
        if(skipUrls == null){
            return false
        }
        skipUrls.forEach {
            if(url.contains(it)){
                return true
            }
        }

        return false
    }
}