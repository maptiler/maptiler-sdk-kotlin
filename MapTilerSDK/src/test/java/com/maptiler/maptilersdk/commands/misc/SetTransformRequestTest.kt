package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.options.MTRequestTransformAction
import com.maptiler.maptilersdk.map.options.MTRequestTransformRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetTransformRequestTest {
    @Test
    fun `toJS emits expected invocation for empty rules`() {
        val commandNull = SetTransformRequest(null)
        val commandEmpty = SetTransformRequest(emptyList())

        assertEquals("window.transformRequestRules = []; map.setTransformRequest(undefined);", commandNull.toJS())
        assertEquals("window.transformRequestRules = []; map.setTransformRequest(undefined);", commandEmpty.toJS())
        assertFalse(commandNull.isPrimitiveReturnType)
    }

    @Test
    fun `toJS emits expected invocation for single rule`() {
        val rules =
            listOf(
                MTRequestTransformRule(
                    matchUrlPrefix = "https://example.com",
                    action =
                        MTRequestTransformAction(
                            addQueryParameters = mapOf("key" to "value"),
                        ),
                ),
            )
        val command = SetTransformRequest(rules)

        val expectedJson = """[{"matchUrlPrefix":"https://example.com","action":{"addQueryParameters":{"key":"value"}}}]"""
        assertEquals(
            "window.transformRequestRules = $expectedJson; map.setTransformRequest(window.createTransformRequestFn());",
            command.toJS(),
        )
    }
}
