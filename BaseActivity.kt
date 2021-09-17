package com.app.sweetzy.base

import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.app.sweetzy.R
import com.app.sweetzy.utils.ProgressDialogCustom
import com.app.sweetzy.utils.ProgressDialogHorizontal
import com.app.sweetzy.utils.helper.SharedPreferenceHelper

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var preferenceHelper: SharedPreferenceHelper
    private var mProgressDialog: ProgressDialog? = null

    protected var context: Context? = null

    private var _binding: ViewBinding? = null
    protected abstract fun getViewBinding(): VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() {
            return _binding as VB
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.setLanguage(this@BaseActivity)
        _binding = getViewBinding()
        setContentView(requireNotNull(_binding).root)
        context = this

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        preferenceHelper = SharedPreferenceHelper.getInstance()!!

        initFontScale()
    }

    override fun onRestart() {
        super.onRestart()
        initFontScale()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard(getRootView())
        hideProgressDialog()
        hideProgress()

        _binding = null
    }

    protected open fun showProgress() {
        if (!ProgressDialogHorizontal.isShowing())
            ProgressDialogHorizontal.show(this)
    }

    protected open fun showProgressCustom() {
        if (!ProgressDialogCustom.isShowing())
            ProgressDialogCustom.show(this)
    }

    protected open fun hideProgress() {
        ProgressDialogHorizontal.hide()
    }

    protected open fun hideProgressCustom() {
        ProgressDialogCustom.hide()
    }

    protected open fun showProgressDialog(message: String?) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(message)
            mProgressDialog!!.setIndeterminate(true)
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.setCancelable(false)
        }
        mProgressDialog!!.show()
    }



    protected open fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
    }

    protected open fun getRootView(): View {
        val contentViewGroup = findViewById<ViewGroup>(R.id.content)
        var rootView: View? = null
        if (contentViewGroup != null) rootView = contentViewGroup.getChildAt(0)
        if (rootView == null) rootView = window.decorView.rootView
        return rootView!!
    }

    protected open fun hideKeyboard(view: View?) {
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    open fun replaceFragment(id: Int, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(id, fragment, fragment.javaClass.simpleName)
        transaction.commitAllowingStateLoss()
    }

    protected open fun replaceFragmentWithAnimation(id: Int, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(
//            R.anim.slide_in_right,
//            R.anim.slide_out_left,
//            R.anim.slide_in_left,
//            R.anim.slide_out_right
//        )
        transaction.replace(id, fragment, fragment.javaClass.simpleName)
        transaction.commitAllowingStateLoss()
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        if (configuration.fontScale != 1f) {
            initFontScale()
        }
    }

    private fun initFontScale() {
        val configuration = resources.configuration
        configuration.fontScale = 1f
        val metrics = resources.displayMetrics
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(metrics)
        }
        metrics.scaledDensity = configuration.fontScale * metrics.density
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context = createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, metrics)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}