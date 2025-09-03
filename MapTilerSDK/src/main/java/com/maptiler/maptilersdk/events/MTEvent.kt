/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Events triggered by the SDK.
 */
@Serializable
enum class MTEvent {
    /**
     * Triggered when the user cancels a "box zoom" interaction,
     * or when the bounding box does not meet the minimum size threshold.
     */
    @SerialName("boxzoomcancel")
    ON_BOX_ZOOM_CANCEL,

    /**
     * Triggered when a "box zoom" interaction ends.
     */
    @SerialName("boxzoomend")
    ON_BOX_ZOOM_END,

    /**
     * Triggered when a "box zoom" interaction starts.
     */
    @SerialName("boxzoomstart")
    ON_BOX_ZOOM_START,

    /**
     * Triggered when user taps and releases at the same point on the map.
     */
    @SerialName("click")
    ON_TAP,

    /**
     * Triggered whenever the cooperativeGestures option prevents a gesture from being handled by the map.
     */
    @SerialName("cooperativegestureprevented")
    ON_COOPERATIVE_GESTURE_PREVENTED,

    /**
     * Triggered when any map data loads or changes.
     */
    @SerialName("data")
    ON_DATA_UPDATE,

    /**
     * Triggered when a request for one of the map's sources' tiles is aborted.
     * Triggered when a request for one of the map's sources' data is aborted.
     */
    @SerialName("dataabort")
    ON_DATA_UPDATE_ABORT,

    /**
     * Triggered when any map data (style, source, tile, etc) begins loading or changing asynchronously.
     * All dataloading events are followed by a dataDidUpdate, dataUpdateDidAbort or error events.
     */
    @SerialName("dataloading")
    ON_DATA_UPDATE_START,

    /**
     * Triggered when a user taps and releases twice at the same point on the map in rapid succession.
     */
    @SerialName("dblclick")
    ON_DOUBLE_TAP,

    /**
     * Triggered repeatedly during a "drag to pan" interaction.
     */
    @SerialName("drag")
    ON_DRAG,

    /**
     * Triggered when a "drag to pan" interaction ends.
     */
    @SerialName("dragend")
    ON_DRAG_END,

    /**
     * Triggered when a "drag to pan" interaction starts.
     */
    @SerialName("dragstart")
    ON_DRAG_START,

    /**
     * Triggered after the last frame rendered before the map enters an "idle" state.
     * Idle state means that no camera transitions are in progress, all currently requested tiles have loaded,
     * and all fade/transition animations have completed.
     */
    @SerialName("idle")
    ON_IDLE,

    /**
     * Triggered immediately after all necessary resources have been downloaded
     * and the first visually complete rendering of the map has occurred.
     */
    @SerialName("load")
    ON_LOAD,

    /**
     * Triggered only once in a Map instance lifecycle, when both the load event
     * and the terrain event with non-null terrain are triggered.
     */
    @SerialName("loadWithTerrain")
    ON_LOAD_WITH_TERRAIN,

    /**
     * Triggered repeatedly during an animated transition from one view to another,
     * as the result of either user interaction or methods such as flyTo.
     */
    @SerialName("move")
    ON_MOVE,

    /**
     * Triggered just after the map completes a transition from one view to another,
     * as the result of either user interaction or methods such as jumpTo.
     */
    @SerialName("moveend")
    ON_MOVE_END,

    /**
     * Triggered just before the map begins a transition from one view to another,
     * as the result of either user interaction or methods such as jumpTo.
     */
    @SerialName("movestart")
    ON_MOVE_START,

    /**
     * Triggered repeatedly during the map's pitch (tilt) animation between one state and another
     * as the result of either user interaction or methods such as flyTo.
     */
    @SerialName("pitch")
    ON_PITCH_UPDATE,

    /**
     * Triggered immediately after the map's pitch (tilt) finishes changing as the result
     * of either user interaction or methods such as flyTo.
     */
    @SerialName("pitchend")
    ON_PITCH_UPDATE_END,

    /**
     * Triggered whenever the map's pitch (tilt) begins a change as the result
     * of either user interaction or methods such as flyTo.
     */
    @SerialName("pitchstart")
    ON_PITCH_UPDATE_START,

    /**
     * Triggered when map's projection is modified in other ways than by map being moved.
     */
    @SerialName("projectiontransition")
    ON_PROJECTION_MODIFY,

    /**
     * Triggered only once after load and wait for all the controls managed by the Map constructor to be dealt with.
     * Since the ready event waits that all the basic controls are nicely positioned,
     * it is safer to use ready than load if you plan to add other custom controls.
     */
    @SerialName("ready")
    ON_READY,

    /**
     * Triggered immediately after the map has been removed.
     */
    @SerialName("remove")
    ON_REMOVE,

    /**
     * Triggered whenever the map is drawn to the screen.
     * Drawing occurs with a change to the map's position, zoom, pitch, or bearing,
     * a change to the map's style,
     * a change to a GeoJSON source,
     * or the loading of a vector tile, GeoJSON file, glyph, or sprite.
     */
    @SerialName("render")
    ON_RENDER,

    /**
     * Triggered immediately after the map has been resized.
     */
    @SerialName("resize")
    ON_RESIZE,

    /**
     * Triggered repeatedly during a "drag to rotate" interaction.
     */
    @SerialName("rotate")
    ON_ROTATE,

    /**
     * Triggered when a "drag to rotate" interaction ends.
     */
    @SerialName("rotateend")
    ON_ROTATE_END,

    /**
     * Triggered when a "drag to rotate" interaction starts.
     */
    @SerialName("rotatestart")
    ON_ROTATE_START,

    /**
     * Triggered when one of the map's sources loads or changes,
     * including if a tile belonging to a source loads or changes.
     */
    @SerialName("sourcedata")
    ON_SOURCE_UPDATE,

    /**
     * Triggered when a request for one of the map's sources' data is aborted.
     */
    @SerialName("sourcedataabort")
    ON_SOURCE_UPDATE_ABORT,

    /**
     * Triggered when one of the map's sources begins loading or changing asynchronously.
     * All sourceUpdateDidStart events are followed by a sourceDidUpdate, sourceUpdateDidAbort or error events.
     */
    @SerialName("sourcedataloading")
    ON_SOURCE_UPDATE_START,

    /**
     * Triggered when the map's style loads or changes.
     */
    @SerialName("styledata")
    ON_STYLE_UPDATE,

    /**
     * Triggered when the map's style begins loading or changing asynchronously.
     * All styleUpdateDidStart events are followed by a styleDidUpdate or error events.
     */
    @SerialName("styledataloading")
    ON_STYLE_UPDATE_START,

    /**
     * Triggered when an icon or pattern needed by the style is missing.
     */
    @SerialName("styleimagemissing")
    ON_STYLE_IMAGE_MISSING,

    /**
     * Triggered when a terrain event occurs within the map.
     */
    @SerialName("terrain")
    ON_TERRAIN_CHANGE,

    /**
     * The terrainAnimationDidStart event is triggered when the animation begins
     * transitioning between terrain and non-terrain states.
     */
    @SerialName("terrainAnimationStart")
    ON_TERRAIN_ANIMATION_START,

    /**
     * The terrainAnimationDidStop event is triggered when the animation
     * between terrain and non-terrain states ends.
     */
    @SerialName("terrainAnimationStop")
    ON_TERRAIN_ANIMATION_STOP,

    /**
     * Triggered when a touch is cancelled within the map.
     */
    @SerialName("touchcancel")
    ON_TOUCH_CANCEL,

    /**
     * Triggered when a touch ends within the map.
     */
    @SerialName("touchend")
    ON_TOUCH_END,

    /**
     * Triggered when a touch moves within the map.
     */
    @SerialName("touchmove")
    ON_TOUCH_MOVE,

    /**
     * Triggered when a touch starts within the map.
     */
    @SerialName("touchstart")
    ON_TOUCH_START,

    /**
     * Triggered repeatedly during an animated transition from one zoom level to another,
     * as the result of either user interaction or methods such as flyTo.
     */
    @SerialName("zoom")
    ON_ZOOM,

    /**
     * Triggered just after the map completes a transition from one zoom level to another,
     * as the result of either user interaction or methods such as flyTo.
     */
    @SerialName("zoomend")
    ON_ZOOM_END,

    /**
     * Triggered just before the map begins a transition from one zoom level to another,
     * as the result of either user interaction or methods such as flyTo.
     */
    @SerialName("zoomstart")
    ON_ZOOM_START,
}
