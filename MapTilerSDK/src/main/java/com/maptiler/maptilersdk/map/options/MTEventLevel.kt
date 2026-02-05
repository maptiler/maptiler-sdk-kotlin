/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

/**
 * Controls which map events are forwarded from the map object.
 *
 * - ESSENTIAL: Default. Low-frequency lifecycle events only (ready, load, moveend, etc.) plus taps.
 * - ALL: Forwards all events including high-frequency move/zoom/touch/render (use with caution on low-end devices).
 * - OFF: Minimal wiring to keep internal lifecycle (ready/load) functioning; all other events are suppressed.
 */
enum class MTEventLevel {
    ESSENTIAL,
    ALL,
    OFF,
}
