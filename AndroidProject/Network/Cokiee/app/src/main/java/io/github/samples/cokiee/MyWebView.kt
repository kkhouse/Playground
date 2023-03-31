package io.github.samples.cokiee

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity


class MyWebView : AppCompatActivity() {
    private val res: Resources? = null
    private var mWebView: WebView? = null
    private val url: String? = "http://10.113.198.202:3000/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_web_view)
        initWebViewSetting()
    }

    private fun initWebViewSetting() {
        mWebView = findViewById<View>(R.id.webView) as WebView
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.settings.loadWithOverviewMode = true
        mWebView!!.settings.useWideViewPort = true
        mWebView!!.setInitialScale(1)
        mWebView!!.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mWebView!!.settings.saveFormData = false
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.removeAllCookie()
        cookieManager.setCookie(url, "HELLO")
        cookieManager.setAcceptThirdPartyCookies(mWebView, true)
        mWebView!!.loadUrl(url!!)
    }

    companion object {
        private val TAG = MyWebView::class.java.simpleName
    }
}