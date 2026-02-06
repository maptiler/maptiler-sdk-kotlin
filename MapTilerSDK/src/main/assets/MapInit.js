/*
 - This script is designed to be used within WebView to interact with the native Kotlin.
 - It does not use ES Modules or TypeScript to ensure compatibility with non-module environments.
 */

function initializeMap(apiKey, style, options, session, eventLevel, throttleMs) {
    maptilersdk.config.apiKey = apiKey;
    maptilersdk.config.session = session;

    const baseOptions = {
        container: 'map',
        style: style
    }
    
    const mapOptions = {...baseOptions, ...options}
    const map = new maptilersdk.Map(mapOptions);

    if (typeof setUpMapEventsWithLevel === 'function') {
        setUpMapEventsWithLevel(map, eventLevel || 'ESSENTIAL', throttleMs || 0);
    } else {
        // Fallback to legacy wiring if the leveled setup is not available.
        setUpMapEvents(map);
    }
    window.map = map;
}
