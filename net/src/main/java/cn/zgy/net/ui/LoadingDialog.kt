package cn.zgy.net.ui

import android.app.Dialog
import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import cn.zgy.net.R

/**
 * 加载中dialog
 */
class LoadingDialog @JvmOverloads constructor(
    context: Context,
    themeResId: Int = R.style.TransparentDialog
) : Dialog(context, themeResId) {
    var ivLoading: ImageView? = null
    private var rotateAnimation: Animation? = null
    private fun initView(context: Context) {
        setCancelable(false)
        setContentView(R.layout.dialog_loading)
        ivLoading = findViewById(R.id.iv_loading)
        rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_always)
        rotateAnimation?.setInterpolator(LinearInterpolator())
    }

    override fun show() {
        super.show()
        ivLoading!!.startAnimation(rotateAnimation)
    }

    override fun dismiss() {
        ivLoading!!.clearAnimation()
        super.dismiss()
    }

    init {
        initView(context)
    }
}