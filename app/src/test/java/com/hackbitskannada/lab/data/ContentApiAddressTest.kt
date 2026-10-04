package com.hackbitskannada.lab.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContentApiAddressTest {
    @Test
    fun acceptsProductionHttpsContentApiAddresses() {
        assertEquals(
            "https://hackbitskannada-admin.onrender.com",
            ContentApiAddress.normalize(" https://hackbitskannada-admin.onrender.com/ ", allowLocalHttp = false),
        )
        assertEquals(
            "https://api.example.test/content",
            ContentApiAddress.normalize("https://api.example.test/content/", allowLocalHttp = false),
        )
    }

    @Test
    fun restrictsHttpToLocalDebugDevelopment() {
        assertEquals("http://10.0.2.2:3000", ContentApiAddress.normalize("http://10.0.2.2:3000", true))
        assertNull(ContentApiAddress.normalize("http://api.example.test", allowLocalHttp = true))
        assertNull(ContentApiAddress.normalize("http://localhost:3000", allowLocalHttp = false))
    }

    @Test
    fun rejectsMalformedAndCredentialBearingAddresses() {
        assertNull(ContentApiAddress.normalize("https://user:secret@api.example.test", true))
        assertNull(ContentApiAddress.normalize("https://api.example.test/?key=value", true))
        assertNull(ContentApiAddress.normalize("https://api.example.test/#fragment", true))
        assertNull(ContentApiAddress.normalize("javascript:alert(1)", true))
        assertNull(ContentApiAddress.normalize("not-a-url", true))
    }
}
