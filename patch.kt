    /**
     * Adds the MapTiler navigation control to the map.
     *
     * @param position The position of the control.
     * @param showCompass Show compass button.
     * @param showZoom Show zoom buttons.
     * @param visualizePitch Visualize pitch on the compass.
     */
    fun addNavigationControl(
        position: com.maptiler.maptilersdk.map.types.MTMapCorner = com.maptiler.maptilersdk.map.types.MTMapCorner.TOP_RIGHT,
        showCompass: Boolean = true,
        showZoom: Boolean = true,
        visualizePitch: Boolean = false,
    ) {
        coroutineScope?.launch {
            bridge?.execute(
                com.maptiler.maptilersdk.commands.misc.AddNavigationControl(
                    showCompass,
                    showZoom,
                    visualizePitch,
                    position,
                ),
            )
        }
    }

