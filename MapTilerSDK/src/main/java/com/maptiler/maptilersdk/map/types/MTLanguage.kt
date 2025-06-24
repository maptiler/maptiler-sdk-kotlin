/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import com.maptiler.maptilersdk.bridge.MTBridge
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Language of the map labels.
 */
@Serializable(with = MTLanguageSerializer::class)
sealed interface MTLanguage {
    /**
     * Custom language options.
     */
    @Serializable
    data class Special(
        val language: MTSpecialLanguage,
    ) : MTLanguage

    /**
     * Country-specific language.
     */
    @Serializable
    data class Country(
        val language: MTCountryLanguage,
    ) : MTLanguage
}

/**
 * Custom language options.
 */
@Serializable
enum class MTSpecialLanguage {
    /** The default language of the device. */
    AUTO,

    /** The international name. This option is equivalent to OSM's name int_name. */
    INTERNATIONAL,

    /** Default fallback language in the Latin charset. */
    LATIN,

    /** Local language for each country. */
    LOCAL,

    /** Fallback language in a non-Latin charset. */
    NON_LATIN,

    /** Language defined by the style. */
    STYLE,

    /**
     * Preferred user setting with default name.
     *
     * Useful when traveling to access both local and English names.
     */
    VISITOR,

    /**
     * English + default name.
     *
     * Useful for multilingual context during travel.
     */
    VISITOR_ENGLISH,
}

/**
 * Country-specific map label languages.
 */
@Serializable
enum class MTCountryLanguage(
    val code: String,
) {
    ALBANIAN("sq"),
    AMHARIC("am"),
    ARABIC("ar"),
    ARMENIAN("hy"),
    AZERBAIJANI("az"),
    BASQUE("eu"),
    BELARUSIAN("be"),
    BENGALI("bn"),
    BOSNIAN("bs"),
    BRETON("br"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CHINESE("zh"),
    TRADITIONAL_CHINESE("zh-Hant"),
    SIMPLIFIED_CHINESE("zh-Hans"),
    CORSICAN("co"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESPERANTO("eo"),
    ESTONIAN("et"),
    FINNISH("fi"),
    FRENCH("fr"),
    FRISIAN("fy"),
    GALICIAN("gl"),
    GEORGIAN("ka"),
    GERMAN("de"),
    GREEK("el"),
    HEBREW("he"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    INDONESIAN("id"),
    IRISH("ga"),
    ITALIAN("it"),
    JAPANESE("ja"),
    JAPANESE_HIRAGANA("ja-Hira"),
    JAPANESE_LATIN("ja-Latn"),
    JAPANESE_KANA("ja_kana"),
    KANNADA("kn"),
    KAZAKH("kk"),
    KOREAN("ko"),
    KOREAN_LATIN("ko-Latn"),
    KURDISH("ku"),
    CLASSICAL_LATIN("la"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    LUXEMBOURGISH("lb"),
    MACEDONIAN("mk"),
    MALAY("ml"),
    MALTESE("mt"),
    MARATHI("mr"),
    MONGOLIAN("mn"),
    NEPALI("ne"),
    NORWEGIAN("no"),
    OCCITAN("oc"),
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    PUNJABI("pa"),
    WESTERN_PUNJABI("pnb"),
    ROMANIAN("ro"),
    ROMANSH("rm"),
    RUSSIAN("ru"),
    SARDINIAN("sc"),
    SCOTTISH_GAELIC("gd"),
    SERBIAN_CYRILLIC("sr"),
    SERBIAN_LATIN("sr-Latn"),
    SLOVAK("sk"),
    SLOVENE("sl"),
    SPANISH("es"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    TAGALOG("tl"),
    TAMIL("ta"),
    TELUGU("te"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    VIETNAMESE("vi"),
    WELSH("cy"),
}

object MTLanguageSerializer : KSerializer<MTLanguage> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MTLanguage", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTLanguage,
    ) {
        val languageStr =
            when (value) {
                is MTLanguage.Special -> "${MTBridge.SDK_OBJECT}.Language.${value.language.name}"
                is MTLanguage.Country -> value.language.code
            }

        encoder.encodeString(languageStr)
    }

    override fun deserialize(decoder: Decoder): MTLanguage = throw SerializationException("Deserialization not supported.")
}
