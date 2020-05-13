package cn.zgy.net.annptation

import androidx.annotation.StringDef

/**
 * 请求方式
 */
@StringDef(RequestType.GET, RequestType.POST_FORM, RequestType.POST_JSON, RequestType.FILE_UPLOAD)
@Retention(AnnotationRetention.SOURCE)
annotation class RequestType {

    companion object{
        const val GET = "GET"
        const val POST_FORM = "POST"
        const val POST_JSON = "POST_JSON"
        const val FILE_UPLOAD = "FILE"
    }
}