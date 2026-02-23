package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.map.types.MTCountryLanguage
import com.maptiler.maptilersdk.map.types.MTLanguage
import com.maptiler.maptilersdk.map.types.MTSpecialLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetSecondaryLanguageTest {
    @Test
    fun `toJS returns proper string with country language`() {
        val command = SetSecondaryLanguage(MTLanguage.Country(MTCountryLanguage.FRENCH))
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setSecondaryLanguage(\"fr\");",
            command.toJS(),
        )
    }

    @Test
    fun `toJS returns proper string with special language`() {
        val command = SetSecondaryLanguage(MTLanguage.Special(MTSpecialLanguage.LOCAL))
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setSecondaryLanguage(maptilersdk.Language.LOCAL);",
            command.toJS(),
        )
    }
}
