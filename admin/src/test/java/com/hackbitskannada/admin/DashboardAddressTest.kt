package com.hackbitskannada.admin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DashboardAddressTest {
    @Test
    fun acceptsAndNormalizesHttpsDashboardUrls() {
        assertEquals(
            "https://admin.example.com/dashboard",
            DashboardAddress.normalize("  https://admin.example.com/dashboard/  ", allowLocalHttp = false),
        )
    }

    @Test
    fun allowsHttpOnlyForLocalDebugHosts() {
        assertEquals("http://10.0.2.2:3000", DashboardAddress.normalize("http://10.0.2.2:3000/", true))
        assertNull(DashboardAddress.normalize("http://admin.example.com", allowLocalHttp = true))
        assertNull(DashboardAddress.normalize("http://10.0.2.2", allowLocalHttp = false))
    }

    @Test
    fun rejectsCredentialsFragmentsAndMalformedAddresses() {
        assertNull(DashboardAddress.normalize("https://user:password@admin.example.com", true))
        assertNull(DashboardAddress.normalize("https://admin.example.com/#session", true))
        assertNull(DashboardAddress.normalize("javascript:alert(1)", true))
        assertNull(DashboardAddress.normalize("not-a-url", true))
    }
}
