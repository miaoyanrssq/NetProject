package cn.zgy.net.cache

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.internal.and
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
  *
  * @Description:     操作cookie类
  * @Author:         zhengy
  * @CreateDate:     2020/6/16 下午2:25
  * @Version:        1.0
 */

class CookieManager private constructor(val context: Context) : CookieRule {

    private val COOKIE_HOST_KEY = "COOKIE_HOST_KEY"
    private val COOKIE_NAME_KEY = "COOKIE_NAME_KEY"
    private val COOKIE_PREFS = "Cookies_Prefs"
    private val cookiePrefs: SharedPreferences

    private val cookies: HashMap<String, ConcurrentHashMap<String, Cookie>> = hashMapOf()

    companion object {
//        val instance by lazy { CookieManager() }
        fun getInstance(context: Context) = CookieManager(context.applicationContext)
        
    }

    init {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0)
        //将持久化的cookies缓存到内存中 即map cookies
        val prefsMap = cookiePrefs.all
        for ((key, value) in prefsMap) {
            val cookieNames =
                TextUtils.split(value as String?, ",")
            for (name in cookieNames) {
                val encodedCookie = cookiePrefs.getString(name, null)
                if (encodedCookie != null) {
                    val decodedCookie: Cookie? = decodeCookie(encodedCookie)
                    if (decodedCookie != null) {
                        if (!cookies.containsKey(key)) {
                            cookies[key!!] = ConcurrentHashMap()
                        }
                        cookies[key]!![name!!] = decodedCookie
                    }
                }
            }
        }
    }



    override fun add(httpUrl: HttpUrl, cookie: Cookie) {
//        if (!cookie.persistent) return
        val hostKey = doHost(httpUrl)
        val nameKey = doName(cookie) ?: return
        if (!cookies.containsKey(hostKey)) {
            cookies[hostKey] = ConcurrentHashMap()
        }
        cookies[hostKey]?.set(nameKey, cookie)

        //将cookies持久化到本地
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putString(hostKey, TextUtils.join(",", cookies[hostKey]!!.keys))
        prefsWriter.putString(nameKey, encodeCookie(SerializableOkHttpCookies(cookie)))
        prefsWriter.apply()
    }

    override fun add(httpUrl: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach {
            if (!isCookieTimeout(it))
                add(httpUrl, it)
        }
    }

    override fun get(httpUrl: HttpUrl): MutableList<Cookie> {
        return getCookies(doHost(httpUrl))
    }

    override fun getCookies(): MutableList<Cookie> {
        val result = arrayListOf<Cookie>()
        cookies.keys.forEach {
            result.addAll(getCookies(it))
        }
        return result
    }

    override fun remove(httpUrl: HttpUrl, cookie: Cookie): Boolean {
        return removeCookie(doHost(httpUrl),cookie)
    }

    override fun removeAll(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }

    fun isCookieTimeout(cookie: Cookie): Boolean {
        return cookie.expiresAt < System.currentTimeMillis()
    }

    /**
     * 返回cookievalue
     */
    fun getCookieValue(name: String): String {
        val cookies = getCookies()
        cookies.forEach {
            if (it.name == name) return it.value
        }
        return ""
    }

    private fun getCookies(hostKey: String): ArrayList<Cookie> {
        val result = arrayListOf<Cookie>()
        if (cookies.containsKey(hostKey)) {
            val currentCookie = this.cookies[hostKey]?.values
            currentCookie?.forEach {
                if (isCookieTimeout(it)){
                    removeCookie(hostKey,it)
                }else
                    result.add(it)
            }
        }
        return result
    }

    private fun removeCookie(hostKey: String, cookie: Cookie) = let {
        val name = doName(cookie)
        val back =
            try {
                if (this.cookies.containsKey(hostKey) && this.cookies[hostKey]!!.containsKey(name!!)) {
                    // 从内存中移除httpUrl对应的cookie
                    this.cookies[hostKey]?.remove(name)
                    val prefsWriter = cookiePrefs.edit()
                    if (cookiePrefs.contains(name)) {
                        prefsWriter.remove(name)
                    }
                    prefsWriter.putString(hostKey, TextUtils.join(",", cookies[hostKey]!!.keys))
                    prefsWriter.apply()
                    true
                } else false
            } catch (e: Exception) {
                false
            }
        back
    }


    private fun doHost(httpUrl: HttpUrl) =
        if (httpUrl.host.startsWith(COOKIE_HOST_KEY)) httpUrl.host
        else COOKIE_HOST_KEY + httpUrl.host


    private fun doName(cookie: Cookie?) =
        if (cookie == null) null
        else cookie.name + cookie.domain


    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    protected fun encodeCookie(cookie: SerializableOkHttpCookies?): String? {
        if (cookie == null) return null
        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            return null
        }
        return byteArrayToHexString(os.toByteArray())
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    protected fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream =
                ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as SerializableOkHttpCookies).getCookies()
        } catch (e: IOException) {
        } catch (e: ClassNotFoundException) {
        }
        return cookie
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected fun byteArrayToHexString(bytes: ByteArray): String? {
        val sb = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v: Int = element and 0xff
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v))
        }
        return sb.toString().toUpperCase(Locale.US)
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(
                hexString[i],
                16
            ) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }


}