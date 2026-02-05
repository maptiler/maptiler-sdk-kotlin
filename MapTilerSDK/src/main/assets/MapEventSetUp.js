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

// New leveled event setup with optional throttling for high-frequency events.
function setUpMapEventsWithLevel(map, level, throttleMs) {
    // Always wire critical error pathways
    map.on('error', function(errorEvent) {
        Android.onError(errorEvent?.error?.message);
    });
    map.on('webglcontextlost', function() {
        Android.onWebGLContextLost()
    });

    // Helper: throttle wrapper
    function throttle(fn, wait) {
        if (!wait || wait <= 0) return fn;
        let last = 0;
        let timeout = null;
        let lastArgs, lastThis;
        const later = () => {
            last = Date.now();
            timeout = null;
            fn.apply(lastThis, lastArgs);
        };
        return function() {
            const now = Date.now();
            const remaining = wait - (now - last);
            lastThis = this; // eslint-disable-line no-invalid-this
            lastArgs = arguments;
            if (remaining <= 0 || remaining > wait) {
                if (timeout) {
                    clearTimeout(timeout);
                    timeout = null;
                }
                later();
            } else if (!timeout) {
                timeout = setTimeout(later, remaining);
            }
        };
    }

    const essentialEventsNoData = [
        'idle', 'load', 'loadWithTerrain', 'movestart', 'moveend', 'ready', 'resize', 'remove',
    ];

    const dataEvents = [
        'data', 'dataabort', 'dataloading',
        'sourcedata', 'sourcedataabort', 'sourcedataloading',
        'styledata', 'styledataloading',
    ];

    const touchEvents = [
        'touchcancel', 'touchend', 'touchmove', 'touchstart',
        'drag', 'dragend', 'dragstart',
        'zoom', 'zoomend', 'zoomstart',
        'rotate', 'rotateend', 'rotatestart',
        'pitch', 'pitchend', 'pitchstart',
    ];

    const heavyFrameEvents = ['move', 'render'];

    // Register click with data for all levels except 'OFF'
    function registerClick() {
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
    }

    function registerDataEvents() {
        dataEvents.forEach(event => {
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
    }

    function registerTouchEvents(throttleMs) {
        const throttled = new Set(['touchmove', 'drag', 'zoom', 'rotate', 'pitch']);
        touchEvents.forEach(event => {
            var handler = function(e) {
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
            };

            map.on(event, throttled.has(event) ? throttle(handler, throttleMs || 0) : handler);
        });
    }

    function registerNoDataEvents(list, throttleMs) {
        const throttled = new Set(heavyFrameEvents);
        list.forEach(event => {
            const cb = function() { Android.onEvent(event, ''); };
            map.on(event, throttled.has(event) ? throttle(cb, throttleMs || 0) : cb);
        });
    }

    function registerStyleImageMissing() {
        map.on('styleimagemissing', function(e) {
            var data = { id: e.id };
            Android.onEvent('styleimagemissing', JSON.stringify(data));
        });
    }

    const lvl = (level || 'ESSENTIAL').toUpperCase();
    if (lvl === 'OFF') {
        // Minimal lifecycle: ready/load required for internal orchestration
        registerNoDataEvents(['ready', 'load'], 0);
        return;
    }

    // ESSENTIAL default
    registerNoDataEvents(essentialEventsNoData, 0);
    registerClick();
    registerStyleImageMissing();

    if (lvl === 'ALL') {
        registerNoDataEvents(heavyFrameEvents, throttleMs);
        registerDataEvents();
        registerTouchEvents(throttleMs);
    }
}
