package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Test

class ListImagesTest {
    @Test
    fun toJS_GeneratesCorrectJS() {
        val command = ListImages()
        assertEquals(
            "map.listImages();",
            command.toJS(),
        )
    }
}
