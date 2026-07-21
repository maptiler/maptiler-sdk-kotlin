/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import android.content.Context
import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.layer.line.MTLineLayer
import com.maptiler.maptilersdk.map.style.source.MTGeoJSONSource
import com.maptiler.maptilersdk.offline.MTBoundingBox
import com.maptiler.maptilersdk.offline.MTOfflineManager
import com.maptiler.maptilersdk.offline.MTOfflinePack
import com.maptiler.maptilersdk.offline.MTOfflinePackState
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition
import com.maptiler.maptilersdk.offline.MTOfflineRegionGeometry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

private object OfflineConstants {
    object DownloadStateLabel {
        const val IDLE = "Idle"
        const val ALL_READY = "Regions ready on disk"
        const val UNTERAGERI_READY = "Unterägeri ready on disk"
        const val BRNO_READY = "Brno ready on disk"
        const val YELLOWSTONE_READY = "Yellowstone ready on disk"
        const val ROUTE_READY = "Route ready on disk"
        const val ESTIMATING = "Estimating..."
        const val UNTERAGERI_DOWNLOADING = "Downloading Unterägeri..."
        const val BRNO_DOWNLOADING = "Downloading Brno in Background..."
        const val YELLOWSTONE_DOWNLOADING = "Downloading Yellowstone..."
        const val ROUTE_DOWNLOADING = "Downloading Route..."
        const val LOADING_OFFLINE_STYLE = "Loading offline style..."
    }

    object PackName {
        const val UNTERAGERI = "Unterägeri Offline"
        const val BRNO = "Brno Offline"
        const val YELLOWSTONE = "Yellowstone Offline"
        const val ROUTE = "Route Offline"
    }

    object ActiveCityName {
        const val UNTERAGERI = "Unterägeri"
        const val BRNO = "Brno"
        const val YELLOWSTONE = "Yellowstone"
        const val ROUTE = "Route"
    }

    const val NAME_DICT_KEY = "name"
    val UNTERAGERI_COORDINATES = LngLat(8.581651, 47.137765)
    val BRNO_COORDINATES = LngLat(16.6068, 49.1951)
    val YELLOWSTONE_COORDINATES = LngLat(-110.5885, 44.4280)
    val ROUTE_COORDINATES = LngLat(14.42533, 50.08051)

    const val LINE_GEOJSON = """
    {
        "type": "Feature",
        "properties": {},
        "geometry": {
            "type": "LineString",
            "coordinates": [
                [14.41790, 50.08182],
                [14.42398, 50.08149],
                [14.42533, 50.07923],
                [14.43129, 50.08051],
                [14.43328, 50.07835]
            ]
        }
    }
    """
}

class OfflineViewModel(private val context: Context) : ViewModel() {
    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    private val _downloadState = MutableStateFlow(OfflineConstants.DownloadStateLabel.IDLE)
    val downloadState: StateFlow<String> = _downloadState.asStateFlow()

    private val _packInfo = MutableStateFlow("")
    val packInfo: StateFlow<String> = _packInfo.asStateFlow()

    private val _unterageriPack = MutableStateFlow<MTOfflinePack?>(null)
    val unterageriPack: StateFlow<MTOfflinePack?> = _unterageriPack.asStateFlow()

    private val _brnoPack = MutableStateFlow<MTOfflinePack?>(null)
    val brnoPack: StateFlow<MTOfflinePack?> = _brnoPack.asStateFlow()

    private val _yellowstonePack = MutableStateFlow<MTOfflinePack?>(null)
    val yellowstonePack: StateFlow<MTOfflinePack?> = _yellowstonePack.asStateFlow()

    private val _routePack = MutableStateFlow<MTOfflinePack?>(null)
    val routePack: StateFlow<MTOfflinePack?> = _routePack.asStateFlow()

    private val _isUnterageriReady = MutableStateFlow(false)
    val isUnterageriReady: StateFlow<Boolean> = _isUnterageriReady.asStateFlow()

    private val _isBrnoReady = MutableStateFlow(false)
    val isBrnoReady: StateFlow<Boolean> = _isBrnoReady.asStateFlow()

    private val _isYellowstoneReady = MutableStateFlow(false)
    val isYellowstoneReady: StateFlow<Boolean> = _isYellowstoneReady.asStateFlow()

    private val _isRouteReady = MutableStateFlow(false)
    val isRouteReady: StateFlow<Boolean> = _isRouteReady.asStateFlow()

    private val _isMapReady = MutableStateFlow(false)
    val isMapReady: StateFlow<Boolean> = _isMapReady.asStateFlow()

    private val _isTerrainEnabled = MutableStateFlow(false)
    val isTerrainEnabled: StateFlow<Boolean> = _isTerrainEnabled.asStateFlow()

    private val _showingRedownloadAlert = MutableStateFlow(false)
    val showingRedownloadAlert: StateFlow<Boolean> = _showingRedownloadAlert.asStateFlow()

    private val _activeCityName = MutableStateFlow("")
    val activeCityName: StateFlow<String> = _activeCityName.asStateFlow()

    val mapController = MTMapViewController(context)

    init {
        viewModelScope.launch {
            refreshPacks()
        }
    }

    fun toggleTerrain() {
        val current = _isTerrainEnabled.value
        if (current) {
            mapController.style?.disableTerrain()
        } else {
            mapController.style?.enableTerrain()
        }
        _isTerrainEnabled.value = !current
    }

    suspend fun refreshPacks() {
        try {
            val packs = MTOfflineManager.getPacks(context)

            var uPack: MTOfflinePack? = null
            var bPack: MTOfflinePack? = null
            var yPack: MTOfflinePack? = null
            var rPack: MTOfflinePack? = null

            for (pack in packs) {
                val name = getPackName(pack)
                when (name) {
                    OfflineConstants.PackName.UNTERAGERI -> uPack = pack
                    OfflineConstants.PackName.BRNO -> bPack = pack
                    OfflineConstants.PackName.YELLOWSTONE -> yPack = pack
                    OfflineConstants.PackName.ROUTE -> rPack = pack
                }
            }

            _unterageriPack.value = uPack
            _brnoPack.value = bPack
            _yellowstonePack.value = yPack
            _routePack.value = rPack

            _isUnterageriReady.value = uPack?.state == MTOfflinePackState.COMPLETED
            _isBrnoReady.value = bPack?.state == MTOfflinePackState.COMPLETED
            _isYellowstoneReady.value = yPack?.state == MTOfflinePackState.COMPLETED
            _isRouteReady.value = rPack?.state == MTOfflinePackState.COMPLETED

            updateStatusLabel()
        } catch (e: Exception) {
            _downloadState.value = "Failed to load packs: ${e.message}"
        }
    }

    private fun getPackName(pack: MTOfflinePack): String? {
        val data = pack.contextData ?: return null
        return try {
            val json = JSONObject(String(data))
            json.optString(OfflineConstants.NAME_DICT_KEY)
        } catch (e: Exception) {
            null
        }
    }

    private fun updateStatusLabel() {
        val uReady = _isUnterageriReady.value
        val bReady = _isBrnoReady.value
        val yReady = _isYellowstoneReady.value
        val rReady = _isRouteReady.value

        // If the download state is showing progress, don't overwrite it immediately
        if (_downloadState.value.contains("Downloading")) return

        val active = _activeCityName.value
        
        _downloadState.value = when {
            uReady && bReady && yReady && rReady -> OfflineConstants.DownloadStateLabel.ALL_READY
            active == OfflineConstants.ActiveCityName.ROUTE && rReady -> OfflineConstants.DownloadStateLabel.ROUTE_READY
            active == OfflineConstants.ActiveCityName.YELLOWSTONE && yReady -> OfflineConstants.DownloadStateLabel.YELLOWSTONE_READY
            active == OfflineConstants.ActiveCityName.BRNO && bReady -> OfflineConstants.DownloadStateLabel.BRNO_READY
            active == OfflineConstants.ActiveCityName.UNTERAGERI && uReady -> OfflineConstants.DownloadStateLabel.UNTERAGERI_READY
            rReady -> OfflineConstants.DownloadStateLabel.ROUTE_READY
            uReady -> OfflineConstants.DownloadStateLabel.UNTERAGERI_READY
            bReady -> OfflineConstants.DownloadStateLabel.BRNO_READY
            yReady -> OfflineConstants.DownloadStateLabel.YELLOWSTONE_READY
            else -> OfflineConstants.DownloadStateLabel.IDLE
        }

        val packs = listOf(_unterageriPack.value, _brnoPack.value, _yellowstonePack.value, _routePack.value)
        
        // Find pack matching active name, or just the first completed one
        var packToShow = packs.firstOrNull { getPackName(it!!) == "$active Offline" } 
        if (packToShow == null || packToShow.state != MTOfflinePackState.COMPLETED) {
            packToShow = packs.firstOrNull { it?.state == MTOfflinePackState.COMPLETED }
        }

        packToShow?.let {
            _packInfo.value = generatePackInfo(it)
        } ?: run {
            _packInfo.value = ""
        }
    }

    private fun generatePackInfo(pack: MTOfflinePack): String {
        val metadata = pack.metadata
        val progress = pack.progress
        val sizeStr = Formatter.formatFileSize(context, metadata.size)
        
        return """
            Size: $sizeStr
            Resources: ${progress.downloadedResources}/${progress.totalResources}
            Created: ${metadata.createdAt}
            Pixel Ratio: ${metadata.region.pixelRatio}x
        """.trimIndent()
    }

    fun loadPack(pack: MTOfflinePack?) {
        if (pack == null) return
        viewModelScope.launch {
            _downloadState.value = OfflineConstants.DownloadStateLabel.LOADING_OFFLINE_STYLE
            try {
                val name = getPackName(pack)
                // Limit region rigidly but allow interaction via the definition's padding and zoom range
                mapController.loadOfflinePack(pack, limitToRegion = true)
                
                val center = when (name) {
                    OfflineConstants.PackName.BRNO -> OfflineConstants.BRNO_COORDINATES
                    OfflineConstants.PackName.YELLOWSTONE -> OfflineConstants.YELLOWSTONE_COORDINATES
                    OfflineConstants.PackName.ROUTE -> OfflineConstants.ROUTE_COORDINATES
                    else -> OfflineConstants.UNTERAGERI_COORDINATES
                }

                mapController.jumpTo(MTCameraOptions(center, zoom = 12.0))

                // Re-add marker or route
                if (name == OfflineConstants.PackName.ROUTE) {
                    val source = MTGeoJSONSource("route-source", OfflineConstants.LINE_GEOJSON)
                    val layer = MTLineLayer("route-layer", "route-source")
                    layer.color = Color.Blue.toArgb()
                    layer.width = 5.0
                    mapController.style?.addSource(source)
                    mapController.style?.addLayer(layer)
                } else {
                    val marker = MTMarker(center)
                    mapController.style?.addMarker(marker)
                }

                _activeCityName.value = name?.replace(" Offline", "") ?: ""
                _downloadState.value = "Loaded ${name ?: "Pack"}!"
            } catch (e: Exception) {
                _downloadState.value = "Failed to load pack: ${e.message}"
            }
        }
    }

    fun downloadUnterageri() {
        if (_isUnterageriReady.value) {
            _activeCityName.value = OfflineConstants.ActiveCityName.UNTERAGERI
            _showingRedownloadAlert.value = true
            return
        }
        performDownload(
            OfflineConstants.PackName.UNTERAGERI,
            MTBoundingBox(8.55, 47.10, 8.62, 47.16),
            10, 14, MTMapReferenceStyle.STREETS
        )
    }

    fun downloadBrno() {
        if (_isBrnoReady.value) {
            _activeCityName.value = OfflineConstants.ActiveCityName.BRNO
            _showingRedownloadAlert.value = true
            return
        }
        performDownload(
            OfflineConstants.PackName.BRNO,
            MTBoundingBox(16.52, 49.13, 16.70, 49.25),
            12, 16, MTMapReferenceStyle.STREETS,
            useBackground = true
        )
    }

    fun downloadYellowstone() {
        if (_isYellowstoneReady.value) {
            _activeCityName.value = OfflineConstants.ActiveCityName.YELLOWSTONE
            _showingRedownloadAlert.value = true
            return
        }
        performDownload(
            OfflineConstants.PackName.YELLOWSTONE,
            MTBoundingBox(-111.15, 44.12, -109.81, 45.10),
            7, 13, MTMapReferenceStyle.STREETS,
            isTerrainEnabled = true
        )
    }

    fun downloadRoute() {
        if (_isRouteReady.value) {
            _activeCityName.value = OfflineConstants.ActiveCityName.ROUTE
            _showingRedownloadAlert.value = true
            return
        }
        performRouteDownload()
    }

    private fun performDownload(
        name: String,
        bbox: MTBoundingBox,
        minZoom: Int,
        maxZoom: Int,
        style: MTMapReferenceStyle,
        useBackground: Boolean = false,
        isTerrainEnabled: Boolean = false
    ) {
        viewModelScope.launch {
            _downloadState.value = OfflineConstants.DownloadStateLabel.ESTIMATING
            _downloadProgress.value = 0f
            
            val definition = MTOfflineRegionDefinition(
                bbox = bbox,
                minZoom = minZoom,
                maxZoom = maxZoom,
                referenceStyle = style,
                isTerrainEnabled = isTerrainEnabled
            )

            val contextData = JSONObject().put(OfflineConstants.NAME_DICT_KEY, name).toString().toByteArray()

            try {
                val pack = MTOfflineManager.createPack(context, definition, contextData)
                observePack(pack)
                _downloadState.value = "Downloading $name..."
                pack.download(useBackground)
            } catch (e: Exception) {
                _downloadState.value = "Error: ${e.message}"
            }
        }
    }

    private fun performRouteDownload() {
        viewModelScope.launch {
            _downloadState.value = "Parsing GeoJSON..."
            _downloadProgress.value = 0f

            try {
                val geometry = MTOfflineRegionGeometry.Route.fromGeoJson(OfflineConstants.LINE_GEOJSON)
                val definition = MTOfflineRegionDefinition(
                    geometry = geometry,
                    minZoom = 9,
                    maxZoom = 15,
                    referenceStyle = MTMapReferenceStyle.OUTDOOR,
                    padding = 5000.0 // 5km buffer around the route for map interaction wiggle room
                )

                val contextData = JSONObject().put(OfflineConstants.NAME_DICT_KEY, OfflineConstants.PackName.ROUTE).toString().toByteArray()
                val pack = MTOfflineManager.createPack(context, definition, contextData)
                observePack(pack)
                _downloadState.value = OfflineConstants.DownloadStateLabel.ROUTE_DOWNLOADING
                pack.download()
            } catch (e: Exception) {
                _downloadState.value = "Error: ${e.message}"
            }
        }
    }

    private fun observePack(pack: MTOfflinePack) {
        viewModelScope.launch {
            pack.progressFlow.collect { progress ->
                val percentage = if (progress.totalResources > 0) {
                    progress.downloadedResources.toFloat() / progress.totalResources
                } else 0f
                _downloadProgress.value = percentage
                _downloadState.value = "Downloading... (${progress.downloadedResources}/${progress.totalResources})"
            }
        }
        viewModelScope.launch {
            pack.stateFlow.collect { state ->
                if (state == MTOfflinePackState.COMPLETED) {
                    refreshPacks()
                } else if (state == MTOfflinePackState.FAILED) {
                    _downloadState.value = "Download Failed!"
                }
            }
        }
    }

    fun confirmRedownload() {
        _showingRedownloadAlert.value = false
        viewModelScope.launch {
            val name = _activeCityName.value
            val pack = when (name) {
                OfflineConstants.ActiveCityName.UNTERAGERI -> _unterageriPack.value
                OfflineConstants.ActiveCityName.BRNO -> _brnoPack.value
                OfflineConstants.ActiveCityName.YELLOWSTONE -> _yellowstonePack.value
                OfflineConstants.ActiveCityName.ROUTE -> _routePack.value
                else -> null
            }
            pack?.remove()
            when (name) {
                OfflineConstants.ActiveCityName.UNTERAGERI -> downloadUnterageri()
                OfflineConstants.ActiveCityName.BRNO -> downloadBrno()
                OfflineConstants.ActiveCityName.YELLOWSTONE -> downloadYellowstone()
                OfflineConstants.ActiveCityName.ROUTE -> downloadRoute()
            }
        }
    }

    fun dismissRedownloadAlert() {
        _showingRedownloadAlert.value = false
    }

    fun setMapReady(ready: Boolean) {
        _isMapReady.value = ready
    }

    override fun onCleared() {
        super.onCleared()
        mapController.destroy()
    }
}

@Composable
fun OfflineScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    val viewModel: OfflineViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OfflineViewModel(context) as T
        }
    })

    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val packInfo by viewModel.packInfo.collectAsState()
    val isMapReady by viewModel.isMapReady.collectAsState()

    val isUnterageriReady by viewModel.isUnterageriReady.collectAsState()
    val isBrnoReady by viewModel.isBrnoReady.collectAsState()
    val isYellowstoneReady by viewModel.isYellowstoneReady.collectAsState()
    val isRouteReady by viewModel.isRouteReady.collectAsState()

    val showingRedownloadAlert by viewModel.showingRedownloadAlert.collectAsState()
    val activeCityName by viewModel.activeCityName.collectAsState()

    val unterageriPack by viewModel.unterageriPack.collectAsState()
    val brnoPack by viewModel.brnoPack.collectAsState()
    val yellowstonePack by viewModel.yellowstonePack.collectAsState()
    val routePack by viewModel.routePack.collectAsState()
    
    val isTerrainEnabled by viewModel.isTerrainEnabled.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        MTMapView(
            referenceStyle = MTMapReferenceStyle.STREETS,
            options = MTMapOptions(),
            controller = viewModel.mapController,
            modifier = Modifier.fillMaxSize()
        )
        
        LaunchedEffect(Unit) {
            viewModel.setMapReady(true)
        }
        
        // Terrain Toggle Button (Top Right)
        Button(
            onClick = { viewModel.toggleTerrain() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTerrainEnabled) Color(0xFF00A1C2) else Color.White,
                contentColor = if (isTerrainEnabled) Color.White else Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(text = if (isTerrainEnabled) "3D On" else "3D Off", fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Offline Region Download",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (downloadProgress > 0 && downloadProgress < 1) {
                LinearProgressIndicator(
                    progress = downloadProgress,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF00A1C2)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Status: $downloadState",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                if (packInfo.isNotEmpty()) {
                    Text(
                        text = packInfo,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OfflineButtonColumn(
                    label = "Unterägeri",
                    onDownload = { viewModel.downloadUnterageri() },
                    onLoad = { viewModel.loadPack(unterageriPack) },
                    isDownloadEnabled = isMapReady,
                    isLoadEnabled = isUnterageriReady,
                    color = Color.Blue,
                    modifier = Modifier.weight(1f)
                )
                OfflineButtonColumn(
                    label = "Brno",
                    onDownload = { viewModel.downloadBrno() },
                    onLoad = { viewModel.loadPack(brnoPack) },
                    isDownloadEnabled = isMapReady,
                    isLoadEnabled = isBrnoReady,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OfflineButtonColumn(
                    label = "Yellowstone",
                    onDownload = { viewModel.downloadYellowstone() },
                    onLoad = { viewModel.loadPack(yellowstonePack) },
                    isDownloadEnabled = isMapReady,
                    isLoadEnabled = isYellowstoneReady,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                OfflineButtonColumn(
                    label = "Route",
                    onDownload = { viewModel.downloadRoute() },
                    onLoad = { viewModel.loadPack(routePack) },
                    isDownloadEnabled = isMapReady,
                    isLoadEnabled = isRouteReady,
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showingRedownloadAlert) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRedownloadAlert() },
            title = { Text("Re-download $activeCityName?") },
            text = { Text("This region is already ready on disk. Do you want to delete it and download again?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmRedownload() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Re-download")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.dismissRedownloadAlert() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OfflineButtonColumn(
    label: String,
    onDownload: () -> Unit,
    onLoad: () -> Unit,
    isDownloadEnabled: Boolean,
    isLoadEnabled: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            onClick = onDownload,
            modifier = Modifier.fillMaxWidth(),
            enabled = isDownloadEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 2.dp)
        ) {
            Text("DL $label", fontSize = 10.sp, maxLines = 1)
        }
        Button(
            onClick = onLoad,
            modifier = Modifier.fillMaxWidth(),
            enabled = isLoadEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 2.dp)
        ) {
            Text("Load $label", fontSize = 10.sp, maxLines = 1)
        }
    }
}
