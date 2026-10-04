package com.hackbitskannada.lab.data

import java.net.URI
import java.util.Locale

internal object ContentApiAddress {
    fun normalize(value: String, allowLocalHttp: Boolean): String? {
        val candidate = value.trim().trimEnd('/')
        if (candidate.isEmpty() || candidate.length > 2048) return null
        return try {
            val uri = URI(candidate).normalize()
            val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
            val host = uri.host?.lowercase(Locale.ROOT) ?: return null
            val localHost = host == "localhost" || host == "127.0.0.1" || host == "10.0.2.2"
            if (uri.userInfo != null || uri.query != null || uri.fragment != null) return null
            if (scheme != "https" && !(allowLocalHttp && scheme == "http" && localHost)) return null
            if (uri.rawPath?.contains("/../") == true || uri.rawPath?.endsWith("/..") == true) return null
            uri.toASCIIString().trimEnd('/')
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
