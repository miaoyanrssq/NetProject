# NetProject
kotlin封装的okhttp,基于okhttp4.6.0

# 引用
implementation 'cn.zgy.net:KTHttp:0.0.1

# 使用

## 初始化

```
 KTHttp.instance.setBaseUrl("baseUrl").setClientType(Client.FACTORY_CLIENT)
            .isLogShow(false).isNeedBaseResponse(true).setErr("xxx").setNetClientType(NetClientType.HTTPS_TYPE).setTimeOut(5000L).isNeedCookie(false).initHttpClient()
```


### 使用

```
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
```

```
    KTHttp.instance.Builder().setUrl("/login")
            .setDialog(LoadingDialog(this))
            .putBody(hashMapOf("username" to "dfadfa", "password" to "dfaf"))
            .setNeedBaseResponse(false)
            .post(object : CallbackRule<LoginBean>{
                override suspend fun onSuccess(entity: LoginBean, flag: String) {

                }

                override suspend fun onFailed(error: String) {
                    Log.e(error)
                }
            })
```