package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Test

class QuerySourceFeaturesTest {
    @Test
    fun `toJS generates correct script for source query`() {
        val command =
            QuerySourceFeatures(
                sourceId = "my-source",
                sourceLayer = "my-layer",
                filter = "[\"==\",\"class\",\"street\"]",
            )
        val js = command.toJS()
        assertEquals(
            "JSON.stringify(map.querySourceFeatures(\"my-source\", " +
                "{\"sourceLayer\":\"my-layer\",\"filter\":\"[\\\"==\\\",\\\"class\\\",\\\"street\\\"]\"}));",
            js,
        )
    }

    @Test
    fun `toJS generates correct script without options`() {
        val command =
            QuerySourceFeatures(
                sourceId = "my-source",
            )
        val js = command.toJS()
        assertEquals(
            "JSON.stringify(map.querySourceFeatures(\"my-source\", {}));",
            js,
        )
    }
}
