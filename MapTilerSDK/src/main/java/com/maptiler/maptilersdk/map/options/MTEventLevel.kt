/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

/**
 * Controls which map events are forwarded from the map object.
 *
 * - ESSENTIAL: Low-frequency lifecycle events only (ready, load, moveend, etc.) plus taps.
 * - CAMERA_ONLY: Default. Forwards only camera events (move, zoom) in addition to minimal lifecycle.
 * - ALL: Forwards all events including high-frequency move/zoom/touch/render (use with caution on low-end devices).
 * - OFF: Minimal wiring to keep internal lifecycle (ready/load) functioning; all other events are suppressed.
 */
enum class MTEventLevel {
    ESSENTIAL,

    /**
     * Forwards only camera motion events (move, zoom) plus minimal lifecycle (ready/load is implicit).
     * Use this to support overlays that track camera without wiring all touch/render events.
     */
    CAMERA_ONLY,
    ALL,
    OFF,
}
