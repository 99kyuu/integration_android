package com.virtusize.libsource.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.*
import android.webkit.*
import androidx.fragment.app.DialogFragment
import com.virtusize.libsource.BrowserIdentifier
import com.virtusize.libsource.Constants
import com.virtusize.libsource.R
import com.virtusize.libsource.data.local.VirtusizeMessageHandler
import com.virtusize.libsource.data.parsers.VirtusizeEventJsonParser
import kotlinx.android.synthetic.main.web_activity.*
import org.json.JSONObject

class VirtusizeView: DialogFragment() {

    private var virtusizeWebAppUrl = "https://static.api.virtusize.jp/a/aoyama/latest/sdk-webview.html"
    private var vsParamsFromSDKScript = ""
    private var vsEventFromSDKScript = "javascript:vsEventFromSDK({ name: 'sdk-back-button-tapped'})"

    private lateinit var virtusizeMessageHandler: VirtusizeMessageHandler
    private lateinit var virtusizeButton: VirtusizeButton

    private lateinit var browserIdentifier: BrowserIdentifier

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.VirtusizeDialogFragmentAnimation
        browserIdentifier = BrowserIdentifier(
            sharedPrefs =
            requireContext().getSharedPreferences(
                Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sets style to dialog to show it as full screen
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.web_activity, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Enable JavaScript in the web view
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        // Add the Javascript interface to interface the web app with the web view
        webView.addJavascriptInterface(WebAppInterface(), Constants.JSBridgeName)
        // Set up the web view client that adds JavaScript scripts for the interaction between the SDK and the web
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if(url != null && url.contains(virtusizeWebAppUrl)) {
                    executeJavascript(view, vsParamsFromSDKScript)
                    getBrowserIDFromCookies()?.let { bid ->
                        if(bid != browserIdentifier.getBrowserId()) {
                            browserIdentifier.storeBrowserId(bid)
                        }
                    }
                }
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                // To prevent multiple views in the WebView when a user selects a different display language
                if(url != null && url.contains("i18n")) {
                    webView.removeAllViews()
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message): Boolean {
                if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
                    val popupWebView = WebView(view.context)
                    popupWebView.settings.javaScriptEnabled = true
                    popupWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                    popupWebView.settings.setSupportMultipleWindows(true)
                    popupWebView.settings.userAgentString = System.getProperty("http.agent")
                    popupWebView.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                            if (isExternalLink(url)) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                try {
                                    startActivity(intent)
                                } finally {
                                    return true
                                }
                            }
                            return false
                        }
                    }
                    popupWebView.webChromeClient = object : WebChromeClient(){
                        override fun onCloseWindow(window: WebView) {
                            webView.removeAllViews()
                        }
                    }
                    val transport = resultMsg.obj as WebView.WebViewTransport
                    webView.addView(popupWebView)
                    transport.webView = popupWebView
                    resultMsg.sendToTarget()
                }
                return true
            }
        }

        webView.setOnKeyListener{ v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP) {
                when {
                    webView.canGoBack() -> webView.goBack()
                    webView.childCount > 0 -> {
                        webView.removeAllViews()
                        webView.reload()
                    }
                    else -> executeJavascript(webView, vsEventFromSDKScript)
                }
            }
            true
        }

        // Get the Virtusize URL passed in fragment arguments
        arguments?.getString(Constants.URL_KEY)?.let {
            virtusizeWebAppUrl = it
        }

        // Get the Virtusize params script passed in fragment arguments.
        // If the script is not passed, we dismiss this dialog fragment.
        arguments?.getString(Constants.VIRTUSIZE_PARAMS_SCRIPT_KEY)?.let {
            vsParamsFromSDKScript = it
        } ?: run {
            dismiss()
        }

        webView.loadUrl(virtusizeWebAppUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.stopLoading()
        webView.destroy()
    }

    /**
     * Sets up a Virtusize message handler
     */
    internal fun setupMessageHandler(messageHandler: VirtusizeMessageHandler, virtusizeButton: VirtusizeButton) {
        virtusizeMessageHandler = messageHandler
        this.virtusizeButton = virtusizeButton
    }

    /**
     * Executes a Javascript function in the Virtusize web app from the SDK
     * @param webView the web view to load the script
     * @param script the string value of the script in JavaScript
     */
    private fun executeJavascript(webView: WebView?, script: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView?.evaluateJavascript(script, null)
        } else {
            webView?.loadUrl(script)
        }
    }

    /**
     * Returns virtusize.bid from the web view cookies
     */
    private fun getBrowserIDFromCookies(): String? {
        var bidValue: String? = null
        val cookieManager = CookieManager.getInstance()
        val cookies = cookieManager.getCookie(virtusizeWebAppUrl)
        if (cookies != null) {
            val cookiesArray = cookies.split(";".toRegex()).toTypedArray()
            for (cookie in cookiesArray) {
                if (cookie.contains("virtusize.bid")) {
                    bidValue = cookie.split("=".toRegex()).toTypedArray()[1]
                }
            }
        }
        return bidValue
    }

    /**
     * Checks if the URL is an external link to be opened with a browser app
     */
    private fun isExternalLink(url: String): Boolean {
        return url.contains("privacy") || url.contains("survey")
    }
    /**
     * The JavaScript interface to interact the web app with the web view
     */
    private inner class WebAppInterface {

        /**
         * Receives any event information from the Virtusize web app
         * @param eventInfo The String value of the event info
         */
        @JavascriptInterface
        fun eventHandler(eventInfo: String) {
            val event = VirtusizeEventJsonParser().parse(JSONObject(eventInfo))
            event?.let { virtusizeMessageHandler.onEvent(virtusizeButton, it) }
            if (event?.name =="user-closed-widget") {
                dismiss()
            }
        }
    }
}