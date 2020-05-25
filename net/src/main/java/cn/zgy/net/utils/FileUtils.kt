package cn.zgy.net.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.net.URLConnection

class FileUtils {

    companion object {

        fun createMediaType(filename: String): MediaType? {
            return URLConnection.getFileNameMap().getContentTypeFor(filename).toMediaTypeOrNull()
        }
    }
}