package com.stormkid.okhttpkt.asyc

import com.stormkid.okhttpkt.rule.ProGressRule
import com.stormkid.okhttpkt.utils.FileCallbackNeed
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
  *
  * @Description:    下载管理
  * @Author:         zhengy
  * @CreateDate:     2020/5/12 上午10:58
  * @Version:        1.0
 */

class DownloadManager(private val fileCallbackNeed: FileCallbackNeed,
                         private val proGressRule: ProGressRule) : Callback {
    override fun onFailure(call: Call, e: IOException) {

    }

    override fun onResponse(call: Call, response: Response) {
        CoroutineScope(Dispatchers.Main).launch{ proGressRule.onFinished() }
    }




    /**
     * 断点续传
     */
    private fun writePerFile() {}


    private fun caculateProgress(current: Long) = let {
        val percent = current * 100 / fileCallbackNeed.initTotal
        percent.toInt()
    }

}