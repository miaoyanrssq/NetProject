package cn.zgy.net.manager

import okhttp3.Call
import java.util.*
import kotlin.collections.HashMap

object CallManager{

    private val mCallMaps: MutableMap<Any, MutableSet<Call?>?> = HashMap()


    /**
     * 缓存call
     */
    fun addCall(tag: Any?, call: Call?) {
        if(null == tag){
            return
        }
        var tagCalls = mCallMaps.get(tag)
        if(null == tagCalls){
            synchronized(this){
                if(null == tagCalls){
                    // 高效比synchronized加在此方法上
                    tagCalls = Collections.synchronizedSet(HashSet())
                    mCallMaps[tag] = tagCalls
                }
            }
        }
        tagCalls?.add(call)
    }
    /**
     * 移除call
     */
    fun removeCall(tag: Any?, call: Call?){
        if(null == tag || null == call){
            return
        }
        var tagCalls = mCallMaps.get(tag)
        tagCalls?.remove(call)
        if(tagCalls?.size == 0){
            mCallMaps.remove(tag)
        }
    }

    /**
     * 移除tag
     */
    fun cancel(tag: Any?){
        if(null == tag){
            return
        }
        var tagCalls = mCallMaps.get(tag)
        synchronized(this){
            tagCalls?.forEach {
                it?.cancel()
            }
        }
        tagCalls?.clear()
        mCallMaps.remove(tag)
    }
}