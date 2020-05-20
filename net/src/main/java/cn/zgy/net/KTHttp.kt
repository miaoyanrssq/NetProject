package cn.zgy.net

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import cn.zgy.net.annptation.Client
import cn.zgy.net.annptation.Client.Companion.FACTORY_CLIENT
import cn.zgy.net.annptation.Client.Companion.SINGLE_CLIENT
import cn.zgy.net.annptation.NetClientType
import cn.zgy.net.annptation.NetClientType.Companion.COMMOM_TYPE
import cn.zgy.net.annptation.NetClientType.Companion.HTTPS_TYPE
import cn.zgy.net.annptation.NetClientType.Companion.HTTP_TYPE
import cn.zgy.net.annptation.RequestType
import cn.zgy.net.annptation.RequestType.Companion.FILE_UPLOAD
import cn.zgy.net.annptation.RequestType.Companion.GET
import cn.zgy.net.annptation.RequestType.Companion.POST_FORM
import cn.zgy.net.annptation.RequestType.Companion.POST_JSON
import cn.zgy.net.bean.BuildData
import cn.zgy.net.builder.KTHttpClientBuilder
import cn.zgy.net.callback.DownloadCallback
import cn.zgy.net.callback.KTCallback
import cn.zgy.net.callback.KTStringCallback
import cn.zgy.net.callback.TestCallback
import cn.zgy.net.rule.*
import cn.zgy.net.ui.LoadingDialog
import com.google.gson.Gson
import com.stormkid.okhttpkt.rule.*
import cn.zgy.net.utils.CallbackNeed
import cn.zgy.net.utils.FileCallbackNeed
import cn.zgy.net.utils.FileResponseBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class KTHttp private constructor(){
    companion object {



        @JvmStatic
        val instance by lazy { KTHttp() }


    }


    /**
     * 是否是工厂模式
     */
    private var isFactory = false
    /**
     * 默认的clientType为单例模式
     */
    @NetClientType
    private var clientType = SINGLE_CLIENT

    /**
     * 默认获取http对象
     */
    @Client
    private var clientNetType = HTTP_TYPE

    /**
     * 默认为http请求单例对象
     */
    private var okHttpClient: OkHttpClient = KTHttpClientBuilder.Builder.build().getHttpClient().build()


    /**
     * 设置baseUrl
     */
    private var baseUrl = ""

    /**
     * 设置错误指令，默认不处理
     */
    private var error = "网络链接失效，请检查网络连接"
    /**
     * 是否设置BaseResponse，如果设置了，回调会自动解析Base层
     */
    private var isNeedBase = false


    /**
     * 设置获取的okhttpclient
     */
    fun setClientType(@Client type: String) = apply {
        clientType = type
    }

    /**
     * 设置请求方式，http请求，https请求或者自定义全局方式
     */
    fun setNetClientType(@NetClientType type: String) = apply {
        clientNetType = type
    }

    /**
     * 设置response的第一层通用解析
     */
    fun isNeedBaseResponse(need: Boolean) = apply {
        isNeedBase = need
    }

    /**
     *  可调用不init采取默认调用
     */
    fun initHttpClient() {
        initNetType(KTHttpClientBuilder.Builder.build())
        when (clientType) {
            FACTORY_CLIENT -> isFactory = true
        }
    }

    private fun initNetType(clientRule: ClientRule) {
        when (clientNetType) {
            HTTP_TYPE -> okHttpClient = clientRule.getHttpClient().build()
            HTTPS_TYPE -> okHttpClient = clientRule.getHttpsClient().build()
            COMMOM_TYPE -> okHttpClient = clientRule.getCustomnClient().build()
        }
    }
    /**
     * 获取相应的对象
     */
    private fun getHttpClient() = okHttpClient.apply {
        if(isFactory){
            initNetType(KTHttpClientBuilder.Builder.build())
        }
    }

    private fun getFactoryClient() = KTHttpClientBuilder.Builder.build().getHttpClient().build()
    /**
     * 更新头部布局
     */
    fun initHead(map: HashMap<String, String>) = apply {
        initNetType(KTHttpClientBuilder.Builder.setHead(map).build())
    }
    /**
     * 是否需要cookie
     */
    fun isNeedCookie(isNeed: Boolean) = apply {
        KTHttpClientBuilder.Builder.build().isNeedCookie(isNeed)
    }

    /**
     * 超时时间
     */
    fun setTimeOut(time: Long) = apply {
        KTHttpClientBuilder.Builder.build().setTimeOut(time)
    }
    /**
     * 是否显示log
     */
    fun isLogShow(boolean: Boolean) = apply {
        KTHttpClientBuilder.Builder.build().isLogShow(boolean)
    }

    /**
     * 是否需要重定向
     */
    fun isAllowRedirect(isNeed: Boolean) = apply{
        KTHttpClientBuilder.Builder.build().setFollowRedirects(isNeed)
    }

    /**
     * 设置主体url
     */
    fun setBaseUrl(url: String) = apply {
        baseUrl = url
    }

    fun getBaseUrl() = baseUrl

    fun setErr(err: String) = apply {
        error = err
    }

    /**
     * 拼接url
     */
    private fun initUrl(map: HashMap<String, String>) = "".let {
        var isFirlst = true
        var result = StringBuilder()
        map.forEach{
            if(isFirlst){
                result.append("?").append(it.key).append("=").append(it.value)
                isFirlst = false
            }else{
                result.append("&").append(it.key).append("=").append(it.value)
            }
        }
        result.toString()
    }

    private fun requestInit(data: BuildData, @RequestType type: String): Call?{
        val url = data.url + initUrl(data.params)
        val builder = Request.Builder().url(url)
        return when(type){
            GET->{
                val request = builder.build()
                getHttpClient().newCall(request)
            }
            POST_FORM->{
                val requestBody = FormBody.Builder().apply {
                    data.body.forEach{
                        add(it.key, it.value)
                    }
                }.build()
                getHttpClient().newCall(builder.post(requestBody).build())
            }
            POST_JSON->{
                val json = if (data.json.isEmpty()) Gson().toJson(data.body) else data.json
                val requestBody =
                    json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                getHttpClient().newCall(builder.post(requestBody).build())
            }
            FILE_UPLOAD->{
                if(data.file.exists()){
                    val body = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                        data.body.forEach { addFormDataPart(it.key, it.value) }
                        addFormDataPart(data.fileNameKey, data.file.name,
                            data.file.asRequestBody(MultipartBody.FORM)
                        )
                    }
                    val multipartBody = body.build()
                    setTimeOut(60000)
                    getFactoryClient().newCall(builder.post(multipartBody).build())
                }else null
            }
            else-> null
        }
    }


    /**
     * 系统下载器下载文件
     */
    fun download(url: String, title: String, desc: String, context: Context, downLoadRule: DownLoadRule) = let {
        val uri = Uri.parse(url)
        val req = DownloadManager.Request(uri).apply {
            //设置WIFI下进行更新
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            //下载中和下载完后都显示通知栏
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            //使用系统默认的下载路径 此处为应用内 /android/data/packages ,所以兼容7.0
            setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, title)
            //通知栏标题
            setTitle(title)
            //通知栏描述信息
            setDescription(desc)
            //设置类型为.apk
            setMimeType("application/vnd.android.package-archive")

        }

        //获取下载任务ID
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        context.registerReceiver(DownloadCallback(downLoadRule), IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        try {
            dm.enqueue(req)
        } catch (exception: Exception) {
            downLoadRule.onNetErr()
            -1L
        }

    }

    fun checkId(id: Long, context: Context) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        try {
            dm.remove(id)
        } catch (exception: Exception) {

        }
    }


    inner class TestBuilder {
        private val data = BuildData()

        fun setUrl(url: String): TestBuilder {
            data.url = url
            return this
        }

        /**
         * 传入url拼接属性
         */
        fun setParams(params: HashMap<String, String>): TestBuilder {
            data.params.clear()
            data.params.putAll(params)
            return this
        }


        fun putBody(params: HashMap<String, String>): TestBuilder {
            data.body.clear()
            data.body.putAll(params)
            return this
        }


        fun testGet(callback: TestCallbackRule) {
            requestInit(data, GET)?.enqueue(TestCallback(callback))
        }

        fun testPost(callback: TestCallbackRule) {
            requestInit(data, POST_FORM)?.enqueue(TestCallback(callback))
        }

        fun testPostJson(callback: TestCallbackRule) {
            requestInit(data, POST_JSON)?.enqueue(TestCallback(callback))
        }

        fun testPostJson(json: String, callback: TestCallbackRule) {
            data.json = json
            requestInit(data, POST_JSON)?.enqueue(TestCallback(callback))
        }


    }

    inner class Builder(){
        private val data = BuildData()

        /**
         * 输入url
         */
        fun setUrl(url: String) = apply {
            data.url = baseUrl + url
        }
        /**
         * 输入的全部url
         */
        fun setFullUrl(url: String) = apply {
            data.url = url
        }

        /**
         * 获取独有的请求标识,多连接的时候进行回调处理
         */
        fun setFlag(flag: String) = apply {
            data.flag = flag
        }


        /**
         * 输入请求body
         */
        fun putBody(params: HashMap<String, String>) = apply {
            data.body.clear()
            data.body.putAll(params)
        }

        /**
         * 传入file
         */
        fun putFile(file: File) = apply {
            data.file = file
        }

        fun setFilePath(filePath: String) = apply {
            data.filePath = filePath
        }

        /**
         * 传入fileNameKey
         */
        fun putFileNameKey(key: String) = apply {
            data.fileNameKey = key
        }

        /**
         * 是否需要解析基类，优先于KTHttp中设置的isNeedBase
         */
        fun setNeedBaseResponse(need: Boolean) = apply {
            data.needBaseResponse = need
        }

        /**
         * 传入url拼接属性
         */
        fun setParams(params: HashMap<String, String>) = apply {
            data.params.clear()
            data.params.putAll(params)
        }

        fun setDialog(dialog: LoadingDialog) = apply {
            data.dialog = dialog
        }

        ////////////////////////////////请求返回 String 数据////////////////////////////////////////
        fun getString(callback: StringCallback){
            data.dialog?.show()
            requestInit(data, GET)?.enqueue(KTStringCallback(callback, CallbackNeed(data.flag, error, false, data.dialog)))
        }
        fun postString(callback: StringCallback) {
            data.dialog?.show()
            requestInit(data, POST_FORM)?.enqueue(KTStringCallback(callback, CallbackNeed(data.flag, error, false, data.dialog)))
        }

        fun postStringJson(callback: StringCallback) {
            data.dialog?.show()
            requestInit(data, POST_JSON)?.enqueue(KTStringCallback(callback, CallbackNeed(data.flag, error, false, data.dialog)))
        }

        fun postStringJson(json: String, callback: StringCallback) {
            data.dialog?.show()
            data.json = json
            requestInit(data, POST_JSON)?.enqueue(KTStringCallback(callback, CallbackNeed(data.flag, error, false, data.dialog)))
        }

        ////////////////////////////////////////////普通请求///////////////////////////////////////////////////////

        fun <T> get(callback: CallbackRule<T>) {
            data.dialog?.show()
            requestInit(data, GET)?.enqueue(KTCallback(callback, CallbackNeed(data.flag, error, data.needBaseResponse ?: isNeedBase, data.dialog)))
        }

        fun <T> post(callback: CallbackRule<T>) {
            data.dialog?.show()
            requestInit(data, POST_FORM)?.enqueue(KTCallback(callback, CallbackNeed(data.flag, error, data.needBaseResponse ?:isNeedBase, data.dialog)))
        }

        fun <T> postJson(callback: CallbackRule<T>) {
            data.dialog?.show()
            requestInit(data, POST_JSON)?.enqueue(KTCallback(callback, CallbackNeed(data.flag, error, data.needBaseResponse ?:isNeedBase, data.dialog)))
        }

        fun <T> postJson(json: String, callback: CallbackRule<T>) {
            data.dialog?.show()
            data.json = json
            requestInit(data, POST_JSON)?.enqueue(KTCallback(callback, CallbackNeed(data.flag, error, data.needBaseResponse ?:isNeedBase, data.dialog)))
        }

        /**
         * 直传文件
         */
        fun <T> postFile(callback: CallbackRule<T>) {
            data.dialog?.show()
            requestInit(data, FILE_UPLOAD)?.enqueue(KTCallback(callback, CallbackNeed(data.flag, error, false, data.dialog)))

        }

        /**
         * 下载文件
         */
        fun downLoad(context: Context, proGressRule: ProGressRule){
            val url = data.url + initUrl(data.params)
            val request = Request.Builder().url(url)
            val fileCallbackNeed = FileCallbackNeed(data.filePath, context, 0)
            CoroutineScope(Dispatchers.Main).launch { proGressRule.onStartRequest() }
            setTimeOut(60000L)
            getFactoryClient().newBuilder().addNetworkInterceptor{chain ->
                val response = chain.proceed(chain.request())
                val body = FileResponseBody(response.body!!, fileCallbackNeed, proGressRule)
                response.newBuilder().body(body).build()
            }.build().newCall(request.build())
                .enqueue(cn.zgy.net.manager.DownloadManager(fileCallbackNeed, proGressRule))
        }
    }
}