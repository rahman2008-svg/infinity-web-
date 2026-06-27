package com.example.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.webkit.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayInputStream

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CustomWebView(
    url: String,
    isAdBlockEnabled: Boolean,
    isDataSaverEnabled: Boolean,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onAdBlocked: () -> Unit,
    onDownloadRequested: (String) -> Unit,
    updateWebViewRef: (WebView?) -> Unit,
    modifier: Modifier = Modifier
) {
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Configure WebView settings
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    supportZoom()
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                }

                // Setup WebViewClient
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        url?.let { onPageStarted(it) }
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        url?.let {
                            onPageFinished(it, view?.title ?: "")
                        }
                    }

                    // Real Functional AdBlocker & Speed Mode (Data Saver) Interceptor!
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val requestUrl = request?.url?.toString() ?: ""

                        // 1. Ad Blocker Interception
                        if (isAdBlockEnabled && isAdUrl(requestUrl)) {
                            mainHandler.post { onAdBlocked() }
                            return WebResourceResponse(
                                "text/plain",
                                "UTF-8",
                                ByteArrayInputStream("".toByteArray())
                            )
                        }

                        // 2. Data Saver / Speed Mode Image Blocking Interception
                        if (isDataSaverEnabled && isImageUrl(requestUrl)) {
                            return WebResourceResponse(
                                "image/png",
                                "UTF-8",
                                ByteArrayInputStream("".toByteArray())
                            )
                        }

                        return super.shouldInterceptRequest(view, request)
                    }
                }

                // Setup WebChromeClient
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        onProgressChanged(newProgress)
                    }
                }

                // Setup DownloadListener
                setDownloadListener { url, _, _, _, _ ->
                    onDownloadRequested(url)
                }

                updateWebViewRef(this)
            }
        },
        update = { webView ->
            // Update WebView reference
            updateWebViewRef(webView)
            
            // Adjust JavaScript and resources based on settings
            webView.settings.javaScriptEnabled = !isDataSaverEnabled
            
            // Load URL if it's different and not blank
            if (url != "about:blank" && webView.url != url) {
                webView.loadUrl(url)
            }
        },
        modifier = modifier.fillMaxSize(),
        onRelease = {
            updateWebViewRef(null)
        }
    )
}

// Simple and highly effective pattern-matching helper to identify Ads & Trackers
fun isAdUrl(url: String): Boolean {
    val adKeywords = listOf(
        "googleads", "googlesyndication", "doubleclick", "adsystem", "adnxs",
        "adskeeper", "adserver", "popads", "mgid", "taboola", "outbrain",
        "yieldlove", "pubmatic", "rubiconproject", "openx", "appnexus",
        "adcolony", "mopub", "admob", "adsense", "analytics", "tracking",
        "telemetry", "amazon-adsystem", "criteo", "smartadserver"
    )
    val lowerUrl = url.lowercase()
    return adKeywords.any { lowerUrl.contains(it) }
}

// Check if resource request is an image for Speed Mode / Data Saver
fun isImageUrl(url: String): Boolean {
    val lowerUrl = url.lowercase()
    return lowerUrl.endsWith(".png") || lowerUrl.endsWith(".jpg") ||
           lowerUrl.endsWith(".jpeg") || lowerUrl.endsWith(".gif") ||
           lowerUrl.endsWith(".webp") || lowerUrl.endsWith(".svg") ||
           lowerUrl.contains(".png?") || lowerUrl.contains(".jpg?") ||
           lowerUrl.contains(".jpeg?") || lowerUrl.contains(".webp?")
}
