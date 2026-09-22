package com.maptiler.maptilersdk.map

/**
 * Protocol for creating external modules that can interact with the map's lifecycle
 */
interface MTMapModule {
    /**
     * Unique identifier for the module
     */
    val id: String

    /**
     * Called when the module is attached to the MTMapViewController
     */
    fun onAttach(controller: MTMapViewController)

    /**
     * Called when the underlying MapTiler map is initialized and ready
     */
    fun onMapReady()

    /**
     * Called when the module receives a message from the map context.
     *
     * @param event The name of the event fired from the map context.
     * @param data Optional payload associated with the event, as a JSON string.
     */
    fun onMessageReceived(
        event: String,
        data: String?,
    )
}
