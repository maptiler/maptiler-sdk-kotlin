/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

data class MTException(
    val code: Int,
    val reason: String,
)

/**
 * Represents all the errors that can occur in the MapTiler SDK.
 *
 * All methods within the SDK throw MTError.
 */
sealed class MTError : Exception() {
    /**
     * Method execution failed with an exception.
     *
     * @param body Exception details.
     */
    data class ExceptionError(
        val body: MTException,
    ) : MTError()

    /**
     * Method execution returned an invalid result.
     *
     * @param description Debug description of the return type.
     */
    data class InvalidResultType(
        val description: String,
    ) : MTError()

    /**
     * Method execution returned an unsupported type.
     *
     * @param description Debug description of the command that returned an unsupported type.
     */
    data class UnsupportedReturnType(
        val description: String,
    ) : MTError()

    /**
     * Method execution resulted in an unknown error.
     *
     * @param description Debug description of the error.
     */
    data class Unknown(
        val description: String,
    ) : MTError()

    /**
     * Method execution halted. Bridge and/or Map are not loaded.
     */
    data object BridgeNotLoaded : MTError() {
        private fun readResolve(): Any = BridgeNotLoaded
    }

    /**
     * Method execution failed due to a missing parent entity.
     */
    data object MissingParent : MTError() {
        private fun readResolve(): Any = MissingParent
    }

    /**
     * Numerical code of the exception.
     */
    val code: Int
        get() =
            when (this) {
                is ExceptionError -> body.code
                is InvalidResultType -> 90
                is UnsupportedReturnType -> 91
                is Unknown -> 92
                BridgeNotLoaded -> 93
                MissingParent -> 95
            }

    /**
     * Explanation of the exception.
     */
    val reason: String
        get() =
            when (this) {
                is ExceptionError -> body.reason
                is InvalidResultType -> description
                is UnsupportedReturnType -> description
                is Unknown -> description
                BridgeNotLoaded -> "Bridge and/or Map are not loaded."
                MissingParent -> "Missing parent entity."
            }
}
