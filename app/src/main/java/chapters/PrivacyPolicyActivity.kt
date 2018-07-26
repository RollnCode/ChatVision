package chapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import base.BActivity

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.07.25
 */
class PrivacyPolicyActivity : BActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://www.freeprivacypolicy.com/privacy/view/fa8c59331547ddc0edd8050022456617")
    }
}