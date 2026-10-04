package com.hackbitskannada.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.net.URI

class AdminActivity : Activity() {
    private val preferences by lazy { getSharedPreferences("admin-dashboard", MODE_PRIVATE) }
    private lateinit var root: LinearLayout
    private lateinit var content: FrameLayout
    private lateinit var progress: ProgressBar
    private lateinit var addressLabel: TextView
    private var webView: WebView? = null
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private var dashboardOrigin: String? = null
    private var savedAddress: String? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 11, 10)
        window.navigationBarColor = Color.rgb(8, 11, 10)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        buildShell()

        val configured = DashboardAddress.normalize(
            preferences.getString("url", null) ?: BuildConfig.DEFAULT_DASHBOARD_URL,
            BuildConfig.DEBUG,
        )
        if (configured != null) {
            savedAddress = configured
            preferences.edit().putString("url", configured).apply()
            loadDashboard(configured)
        } else {
            showAddressForm()
        }
    }

    private fun buildShell() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(8, 11, 10))
        }
        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(10), dp(10), dp(10))
            setBackgroundColor(Color.rgb(9, 13, 11))
        }
        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        brand.addView(label("HackBitsKannada Admin", 15, Color.rgb(237, 244, 239), true))
        addressLabel = label("Content & App Management Dashboard", 10, Color.rgb(131, 149, 139))
        brand.addView(addressLabel)
        toolbar.addView(brand, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        toolbar.addView(actionButton("URL", false) { showAddressForm() })
        toolbar.addView(actionButton("↻", false) { webView?.reload() })
        root.addView(toolbar)

        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progressTintList = android.content.res.ColorStateList.valueOf(Color.rgb(66, 229, 138))
            indeterminateTintList = android.content.res.ColorStateList.valueOf(Color.rgb(66, 229, 138))
            visibility = View.GONE
        }
        root.addView(progress, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(3)))
        content = FrameLayout(this)
        root.addView(content, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)

    }

    @Deprecated("Uses the Activity back contract for API 26 compatibility.")
    override fun onBackPressed() {
        if (webView?.canGoBack() == true) webView?.goBack()
        else if (savedAddress == null) showAddressForm()
        else super.onBackPressed()
    }

    private fun loadDashboard(address: String) {
        val normalized = DashboardAddress.normalize(address, BuildConfig.DEBUG)
        if (normalized == null) {
            showAddressForm("Enter a valid HTTPS dashboard URL.")
            return
        }
        savedAddress = normalized
        dashboardOrigin = originOf(normalized)
        addressLabel.text = URIFormatter.host(normalized)
        preferences.edit().putString("url", normalized).apply()

        val browser = webView ?: WebView(this).apply {
            setBackgroundColor(Color.rgb(8, 11, 10))
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = false
                allowContentAccess = true
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                javaScriptCanOpenWindowsAutomatically = false
                setSupportMultipleWindows(false)
                safeBrowsingEnabled = true
            }
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, false)
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    this@AdminActivity.progress.progress = newProgress
                    this@AdminActivity.progress.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?,
                ): Boolean {
                    this@AdminActivity.fileChooserCallback?.onReceiveValue(null)
                    this@AdminActivity.fileChooserCallback = filePathCallback
                    return try {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/webp", "image/avif"))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        startActivityForResult(intent, FILE_PICKER_REQUEST)
                        true
                    } catch (_: android.content.ActivityNotFoundException) {
                        this@AdminActivity.fileChooserCallback = null
                        false
                    }
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    if (request == null) return true
                    val target = request.url
                    if (originOf(target.toString()) == dashboardOrigin) return false
                    return openExternal(target)
                }

                @Suppress("DEPRECATION")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return true
                    if (originOf(url) == dashboardOrigin) return false
                    return openExternal(Uri.parse(url))
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    if (request?.isForMainFrame == true) showLoadError()
                }

                override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, response: WebResourceResponse?) {
                    if (request?.isForMainFrame == true && response != null && response.statusCode >= 500) showLoadError()
                }
            }
            webView = this
        }
        if (browser.parent !== content) {
            content.removeAllViews()
            content.addView(browser, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
        content.removeView(content.findViewWithTag("connection-error"))
        browser.loadUrl(normalized)
    }

    private fun showAddressForm(message: String? = null) {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(26), dp(24), dp(26), dp(24))
            setBackgroundColor(Color.rgb(8, 11, 10))
        }
        form.addView(label(">_  SECURE ADMIN CONNECTION", 11, Color.rgb(66, 229, 138), true))
        form.addView(label("Connect to your dashboard", 23, Color.rgb(237, 244, 239), true).apply {
            setPadding(0, dp(12), 0, dp(7))
        })
        form.addView(label("Enter the HTTPS URL where HackBitsKannada Admin is deployed. Your sign-in and session remain protected by the admin server.", 13, Color.rgb(131, 149, 139)).apply {
            setPadding(0, 0, 0, dp(20))
        })
        val input = EditText(this).apply {
            setSingleLine(true)
            hint = "https://your-admin.example.com"
            setTextColor(Color.rgb(237, 244, 239))
            setHintTextColor(Color.rgb(101, 117, 107))
            textSize = 14f
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_URI
            setPadding(dp(13), dp(13), dp(13), dp(13))
            setBackgroundColor(Color.rgb(15, 22, 18))
            setText(savedAddress ?: BuildConfig.DEFAULT_DASHBOARD_URL)
        }
        form.addView(input, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        if (message != null) form.addView(label(message, 12, Color.rgb(255, 156, 156)).apply { setPadding(0, dp(9), 0, 0) })
        form.addView(actionButton("Connect securely", true) {
            val address = DashboardAddress.normalize(input.text.toString(), BuildConfig.DEBUG)
            if (address == null) {
                showAddressForm("Use a valid HTTPS URL. HTTP is allowed only for localhost/emulator development.")
            } else {
                savedAddress = address
                loadDashboard(address)
            }
        }.apply { setPadding(dp(18), dp(12), dp(18), dp(12)) }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(15) })
        form.addView(label("The web dashboard must be deployed and reachable. This app does not contain a local copy of the admin server.", 11, Color.rgb(112, 129, 119)).apply {
            setPadding(0, dp(18), 0, 0)
        })
        content.removeAllViews()
        content.addView(form, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun showLoadError() {
        if (content.findViewWithTag<View>("connection-error") != null) return
        val panel = LinearLayout(this).apply {
            tag = "connection-error"
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(20), dp(24), dp(20))
            setBackgroundColor(Color.rgb(8, 11, 10))
        }
        panel.addView(label("Dashboard unavailable", 21, Color.rgb(237, 244, 239), true))
        panel.addView(label("Check your connection and dashboard URL, then try again.", 13, Color.rgb(131, 149, 139)).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(18))
        })
        panel.addView(actionButton("Retry", true) { savedAddress?.let(::loadDashboard) })
        panel.addView(actionButton("Change dashboard URL", false) { showAddressForm() })
        content.addView(panel, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        panel.bringToFront()
    }

    private fun openExternal(uri: Uri): Boolean {
        if (uri.scheme != "https" && uri.scheme != "http") return true
        return try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (_: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No browser is available to open this link.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun originOf(value: String): String? = try {
        val uri = URI(value)
        if (uri.scheme != "https" && uri.scheme != "http") null else "${uri.scheme}://${uri.rawAuthority}".lowercase()
    } catch (_: Exception) {
        null
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean = false) = TextView(this).apply {
        this.text = text
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
    }

    private fun actionButton(text: String, primary: Boolean, action: () -> Unit) = Button(this).apply {
        this.text = text
        textSize = 12f
        isAllCaps = false
        setTextColor(if (primary) Color.rgb(6, 18, 10) else Color.rgb(198, 212, 202))
        setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(
                if (primary) Color.rgb(66, 229, 138) else Color.rgb(19, 31, 23),
            ),
        )
        setOnClickListener { action() }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_REQUEST) {
            val result = if (resultCode == RESULT_OK) WebChromeClient.FileChooserParams.parseResult(resultCode, data) else null
            fileChooserCallback?.onReceiveValue(result)
            fileChooserCallback = null
        }
    }

    override fun onDestroy() {
        fileChooserCallback?.onReceiveValue(null)
        webView?.apply {
            stopLoading()
            webChromeClient = null
            webViewClient = WebViewClient()
            destroy()
        }
        webView = null
        super.onDestroy()
    }

    private object URIFormatter {
        fun host(value: String): String = try {
            URI(value).host ?: "Dashboard connected"
        } catch (_: Exception) {
            "Dashboard connected"
        }
    }

    private companion object {
        const val FILE_PICKER_REQUEST = 8124
    }
}
