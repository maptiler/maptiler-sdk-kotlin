/*
 - This script is designed to be used within WebView to interact with the native Kotlin.
 - It does not use ES Modules or TypeScript to ensure compatibility with non-module environments.
 */

window.createTransformRequestFn = function() {
    return function(url, resourceType) {
        if (!window.transformRequestRules || !window.transformRequestRules.length) return { url: url };
        
        let currentUrl = url;
        let headers = undefined;
        let credentials = undefined;
        
        for (const rule of window.transformRequestRules) {
            let isMatch = false;
            if (rule.matchUrlPrefix && currentUrl.startsWith(rule.matchUrlPrefix)) {
                isMatch = true;
            } else if (rule.matchUrlRegex) {
                const regex = new RegExp(rule.matchUrlRegex);
                if (regex.test(currentUrl)) {
                    isMatch = true;
                }
            }
            
            if (isMatch && rule.action) {
                if (rule.action.addQueryParameters) {
                    try {
                        const urlObj = new URL(currentUrl, currentUrl.startsWith('http') ? undefined : 'http://localhost');
                        for (const [key, value] of Object.entries(rule.action.addQueryParameters)) {
                            urlObj.searchParams.set(key, value);
                        }
                        currentUrl = urlObj.toString();
                        if (!url.startsWith('http') && currentUrl.startsWith('http://localhost')) {
                            currentUrl = currentUrl.substring(16); // Remove http://localhost
                        }
                    } catch (e) {
                        // Ignore invalid URL
                    }
                }
                if (rule.action.addHeaders) {
                    headers = headers || {};
                    Object.assign(headers, rule.action.addHeaders);
                }
                if (rule.action.rewriteUrl) {
                    if (rule.matchUrlRegex) {
                        currentUrl = currentUrl.replace(new RegExp(rule.matchUrlRegex), rule.action.rewriteUrl);
                    } else if (rule.matchUrlPrefix) {
                        currentUrl = currentUrl.replace(rule.matchUrlPrefix, rule.action.rewriteUrl);
                    }
                }
                if (rule.action.setCredentials) {
                    credentials = rule.action.setCredentials;
                }
            }
        }
        
        const result = { url: currentUrl };
        if (headers) result.headers = headers;
        if (credentials) result.credentials = credentials;
        return result;
    };
};

function initializeMap(apiKey, style, options, session, eventLevel, throttleMs, useEuEndpoints) {
    maptilersdk.config.apiKey = apiKey;
    maptilersdk.config.session = session;
    maptilersdk.config.useEuEndpoints = useEuEndpoints === true;

    if (options && options.transformRequestRules) {
        window.transformRequestRules = options.transformRequestRules;
        options.transformRequest = window.createTransformRequestFn();
        delete options.transformRequestRules;
    }

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
