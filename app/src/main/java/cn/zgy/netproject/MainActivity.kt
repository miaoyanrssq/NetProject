package cn.zgy.netproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.zgy.net.KTHttp
import cn.zgy.net.ui.LoadingDialog
import cn.zgy.net.rule.CallbackRule
import cn.zgy.net.utils.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.setOnClickListener { test() }
    }

    private fun test() {
//        KTHttp.instance.Builder().setUrl("/login")
//            .putBody(hashMapOf("username" to "dfadfa", "password" to "dfaf"))
//            .postString(object : StringCallback {
//                override suspend fun onSuccess(entity: String, flag: String) {
//                    Log.e("ASFSF", entity)
//                    hello.text = entity
//                }
//
//                override suspend fun onFailed(error: String) {
//                    Log.e("ASFSF", error)
//                    hello.text = error
//                }
//
//            })
        KTHttp.instance.Builder().setUrl("/endpoint/live/ids/login")
            .setDialog(LoadingDialog(this))
            .putBody(
                hashMapOf(
                    "userName" to "59_zjrb",
                    "passWord" to "WVdSdGFXND19TFJtSGo4UDJEaw=="
                )
            )
            .post(object : CallbackRule<LoginBean> {
                override suspend fun onSuccess(entity: LoginBean, flag: String) {
                    test2()
                }

                override suspend fun onFailed(error: String) {
                    Log.e(error)
                }
            })




    }

    private fun test2() {
        KTHttp.instance.Builder()
            .setUrl("/endpoint/live/user/selectProductOrGroup")
            .setParams(
                hashMapOf(
                    "type" to "1",
                    "typeId" to "4157534840580"
                )
            )
            .get(object : CallbackRule<String> {
                override suspend fun onFailed(error: String) {
                }

                override suspend fun onSuccess(entity: String, flag: String) {
                }
            })
    }
}
