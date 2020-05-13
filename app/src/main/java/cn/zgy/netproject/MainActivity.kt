package cn.zgy.netproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.zgy.net.KTHttp
import com.stormkid.okhttpkt.rule.StringCallback
import com.stormkid.okhttpkt.utils.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.setOnClickListener { test() }
    }

    private fun test() {
        KTHttp.instance.Builder().setUrl("/login")
            .putBody(hashMapOf("username" to "dfadfa", "password" to "dfaf"))
            .postString(object : StringCallback {
                override suspend fun onSuccess(entity: String, flag: String) {
                    Log.e("ASFSF", entity)
                    hello.text = entity
                }

                override suspend fun onFailed(error: String) {
                    Log.e("ASFSF", error)
                    hello.text = error
                }

            })
    }
}
