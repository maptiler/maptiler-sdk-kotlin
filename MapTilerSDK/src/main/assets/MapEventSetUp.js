/*
 - This script is designed to be used within WebView to interact with the native Kotlin.
 - It does not use ES Modules or TypeScript to ensure compatibility with non-module environments.
 */

function setUpMapEvents(map) {
    // BRIDGE - Error event propagation
    map.on('error', function(errorEvent) {
        Android.onError(errorEvent?.error?.message);
    });

    // BRIDGE - WebGL event propagation
    map.on('webglcontextlost', function() {
        Android.onWebGLContextLost()
    });

    // BRIDGE - Map events propagation

    const events = [
        'boxzoomcancel',
        'boxzoomend',
        'boxzoomstart',
        'contextmenu',
        'cooperativegestureprevented',
        'dblclick',
        'idle',
        'load',
        'loadWithTerrain',
        'move',
        'moveend',
        'movestart',
        'pitch',
        'pitchend',
        'pitchstart',
        'projectiontransition',
        'ready',
        'remove',
        'render',
        'resize',
        'terrain',
        'terrainAnimationStart',
        'terrainAnimationStop'
    ];

    events.forEach(event => {
        map.on(event, function() {
            Android.onEvent(event, '');
        });
    });

    // MapTapEvent
    map.on('click', function(e) {
        var data = {
            lngLat: {
                lng: e.lngLat.lng,
                lat: e.lngLat.lat
            },
            point: {
                x: e.point.x,
                y: e.point.y
            }
        };

        Android.onEvent('click', JSON.stringify(data));
    });

    // MapImageEvent
    map.on('styleimagemissing', function(e) {
        var data = {
            id: e.id,
        };

         Android.onEvent('styleimagemissing', JSON.stringify(data));
    });

    // MapDataEvents
    const mapDataEvents = [
        'data',
        'dataabort',
        'dataloading',
        'sourcedata',
        'sourcedataabort',
        'sourcedataloading',
        'styledata',
        'styledataloading',
        'rotate',
        'rotateend',
        'rotatestart'
    ];

    mapDataEvents.forEach(event => {
        map.on(event, function(e) {
            var data = {
                dataType: e.dataType,
                isSourceLoaded: e.isSourceLoaded,
                source: e.source,
                sourceDataType: e.sourceDataType
            };

             Android.onEvent(event, JSON.stringify(data));
        });
    });

    // MapTouchEvents
    const mapTouchEvents = [
        'touchcancel',
        'touchend',
        'touchmove',
        'touchstart',
        'drag',
        'dragend',
        'dragstart',
        'zoom',
        'zoomend',
        'zoomstart'
    ];

    mapTouchEvents.forEach(event => {
        map.on(event, function(e) {

            var data = {
                lngLat: e.lngLat ? {
                   lng: e.lngLat.lng,
                   lat: e.lngLat.lat
                } : null,
                lngLats: Array.isArray(e.lngLats)
                    ? e.lngLats.map(coord => ({
                            lng: coord.lng,
                            lat: coord.lat
                        }))
                    : [],
                point: e.point ? {
                   x: e.point.x,
                   y: e.point.y
                } : null,
                points: Array.isArray(e.points)
                    ? e.points.map(point => ({
                            x: point.x,
                            y: point.y
                         }))
                    : [],
            };

             Android.onEvent(event, JSON.stringify(data));
        });
    });
}
