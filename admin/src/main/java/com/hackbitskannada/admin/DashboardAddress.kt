package com.hackbitskannada.admin

import java.net.URI
import java.util.Locale

internal object DashboardAddress {
    fun normalize(value: String, allowLocalHttp: Boolean): String? {
        val candidate = value.trim()
        if (candidate.isEmpty() || candidate.length > 2048) return null

        return try {
            val uri = URI(candidate).normalize()
            val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
            val host = uri.host?.lowercase(Locale.ROOT) ?: return null
            val isLocalHost = host == "localhost" || host == "127.0.0.1" || host == "10.0.2.2"
            if (uri.userInfo != null || uri.fragment != null) return null
            if (scheme != "https" && !(allowLocalHttp && scheme == "http" && isLocalHost)) return null
            if (uri.rawPath?.contains("/../") == true || uri.rawPath?.endsWith("/..") == true) return null
            uri.toASCIIString().trimEnd('/')
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
