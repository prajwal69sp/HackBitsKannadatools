package com.hackbitskannada.lab

import com.hackbitskannada.lab.data.CommandSeed
import com.hackbitskannada.lab.data.LearningSeed
import com.hackbitskannada.lab.data.LibraryContent
import com.hackbitskannada.lab.data.search
import com.hackbitskannada.lab.ui.EducationalFallback
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineLearningTest {
    @Test
    fun partialSearchFindsNetworkContentAcrossOfflineSections() {
        val results = LibraryContent(
            commands = CommandSeed.commands,
            scripts = LearningSeed.scripts,
            tools = LearningSeed.tools,
            tutorials = LearningSeed.tutorials,
            categories = CommandSeed.categories
        ).search("network")

        assertTrue(results.any { it.type == "command" && it.id == "ping" })
        assertTrue(results.any { it.type == "command" && it.id == "curl" })
        assertTrue(results.any { it.type == "script" && it.id == "local-web-check" })
        assertTrue(results.any { it.type == "tutorial" && it.id == "networking" })
        assertTrue(results.any { it.type == "category" && it.id == "Networking" })
    }

    @Test
    fun localAssistantExplainsPermissionsWithoutClaimingRemoteAi() {
        val answer = EducationalFallback.explain("Explain chmod 755")

        assertTrue(answer.contains("read, write, and execute"))
        assertTrue(answer.contains("not an AI service"))
    }

    @Test
    fun blankSearchDoesNotReturnEveryItem() {
        val results = LibraryContent(
            commands = CommandSeed.commands,
            scripts = LearningSeed.scripts,
            tools = LearningSeed.tools,
            tutorials = LearningSeed.tutorials,
            categories = CommandSeed.categories
        ).search("  ")

        assertTrue(results.isEmpty())
        assertFalse(CommandSeed.commands.isEmpty())
    }
}
