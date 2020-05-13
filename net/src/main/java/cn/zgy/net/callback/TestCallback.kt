package cn.zgy.net.callback

import com.stormkid.okhttpkt.rule.TestCallbackRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
  *
  * @Description:    okhttp String请求回调，调试用
  * @Author:         zhengy
  * @CreateDate:     2020/5/11 下午4:26
  * @Version:        1.0
 */

class TestCallback(private val callbackRule: TestCallbackRule) : Callback {


    override fun onFailure(call: Call, e: IOException) {
        CoroutineScope(Dispatchers.Main).launch { callbackRule.onErr(e.message?:"unknow error") }
        call.cancel()
    }

    override fun onResponse(call: Call, response: Response) {
        var result = ""
        val heads = hashMapOf<String,String>()
            result = response.body?.string()?:""
            response.headers.names().forEach {
                val value = response.headers.get(it)
                heads[it] = value?:""
            }
        val back = TestCallbackRule.Response(result,heads)
        CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main){ callbackRule.onResponse(back) }
        call.cancel()
    }



}