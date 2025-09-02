<!-- AGENT_DIRECTIVES v1 -->

Priority: Follow these directives unless they conflict with system/developer instructions or safety rules.

## System Context

You are an AI agent specialized in Kotlin (Android/Compose) and JavaScript development. This document defines mandatory rules for consistent, secure, and maintainable development. Follow the sections below precisely:

- Project Overview
- Pre-Implementation Checklist
- Bridge Rules
- Code Style Guidelines
- ktlint Compliance
- Development Best Practices
- Kotlin Coroutines
- Project Structure
- Style Lifecycle
- Error Handling
- Privacy
- Tests
- Pull Request Template

## Project Overview

This Android library wraps the MapTiler JS SDK via a typed Kotlin↔JS bridge located in `MapTilerSDK/src/main/java/com/maptiler/maptilersdk/bridge`. It renders a web map inside an Android `WebView` hosted by a Compose `MTMapView` and managed by `MTMapViewController`. The HTML container (`src/main/assets/MapTilerMap.html`) loads `maptiler-sdk.umd.min.js`. Kotlin APIs translate to JS via `MTCommand`, executed by `MTBridge` through `WebViewExecutor`. The JS SDK API reference is embedded under `assets` (open `maptiler-sdk.umd.min.js.map` for sources mapping) and the JS docs live in the JS repo; search in this repo and JS bundle for the needed API. Always consult the docs/bundle before wrapping any function.

### Main Components

- UI: `MTMapView` is a Compose `@Composable` that attaches a configured `WebView`.
- Controller: `MTMapViewController` binds coroutines, holds `MTStyle` and `MTMapOptions`, and exposes navigation/style APIs and events.
- Bridge: Public Kotlin APIs construct `MTCommand`s serialized to JS, executed by `WebViewExecutor`; results decode via `MTBridgeReturnType`.
- Lifecycle: `EventProcessor` listens to JS events, updates state, and notifies `MTMapViewDelegate` (`ON_READY`, `ON_IDLE`, etc.).
- Safety: Mutate map after `ON_READY`. Style changes reapply queued layers; `MTStyle` manages layer queueing.
- Responsibilities: `MTBridge` executes commands; `MTCommand` defines JS; `WebViewExecutor` calls `evaluateJavascript` on `WebView` on the main thread.

## Pre-Implementation Checklist
Before writing ANY new code, you MUST:
- Search for existing related types: `rg -n "MT[TypeName]|[RelatedConcept]"` in this repo.
- Read similar implementations completely (commands, workers, options, style, types).
- Identify established patterns and shared types to reuse (e.g., options models, helpers, workers).
- Confirm no existing types can be reused before creating new ones.
- Follow ktlint rules enforced by the Gradle plugin.
- Run `./gradlew ktlintCheck` and fix all violations locally. Optionally run `./gradlew :MapTilerSDK:ktlintCheck` for module scope.
- If you add public API, add KDoc for it and ensure Dokka can generate docs (`./gradlew :MapTilerSDK:dokkaHtml`).

## Bridge Rules

MUST follow this end-to-end flow when wrapping a JS API into Kotlin:

1) Discover and design
- Read the JS API in MapTiler SDK for JS docs/bundle; determine parameters, defaults, and return type.
- Define an internal Kotlin `data class` implementing `MTCommand` with strongly typed parameters. Use `@Serializable` surrogates for JSON payloads when needed.

2) Encode parameters
- Use `kotlinx.serialization` via `JsonConfig.json.encodeToString(...)` to build a compact JSON payload; avoid manual string concatenation where possible.
- Validate/clamp numeric ranges in Kotlin prior to execution (zoom, pitch, bearing, durations).

3) Implement `toJS()`
- Build a `JSString` that calls the JS API. Prefer passing a single options JSON object.
- If easing functions or callbacks are needed, express as a JS expression string compatible with the bundle.

4) Choose execution and return type
- For commands with no meaningful return: set `isPrimitiveReturnType = false` and just return the invocation string.
- For numeric/boolean/string results: set `isPrimitiveReturnType = true`; the result decodes into `MTBridgeReturnType` variants: `DoubleValue`, `BoolValue`, `StringValue`, `StringDoubleDict`, `Null`.
- If a new return shape is required, extend `MTBridgeReturnType` in a focused change with tests.

5) Public API surface
- Add a thin convenience method on `MTMapViewController` (or appropriate worker/service) that:
  - Ensures the map/style are ready (`ON_READY`).
  - Validates inputs and applies sensible defaults.
  - Launches on the appropriate coroutine scope and calls the bridge.
  - Uses `suspend` functions for getters and `Result`/exceptions for failures where appropriate.

6) Threading and lifecycle
- MUST execute JS on the main thread. `WebViewExecutor` already enforces `Dispatchers.Main`; do not bypass it.
- Avoid firing commands before the `WebView`/bridge is available; prefer queuing via style/lifecycle or guard on readiness.

7) Testing
- Unit test: parameter encoding and range clamping.
- Contract test: `toJS()` string for simple cases (e.g., duration only).

Example skeleton:

```kotlin
@Serializable
private data class RotateToOptions(
    val bearing: Double,
    val duration: Double? = null,
)

internal data class RotateTo(
    private val bearing: Double,
    private val durationMs: Double? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): JSString {
        val opts = RotateToOptions(bearing = bearing, duration = durationMs)
        val json = JsonConfig.json.encodeToString(opts)
        return "${MTBridge.MAP_OBJECT}.rotateTo($json);"
    }
}

// Controller convenience API
fun MTMapViewController.setBearing(
    bearing: Double,
    durationMs: Double? = null,
) {
    val clamped = ((bearing % 360 + 360) % 360)
    coroutineScope?.launch { bridge?.execute(RotateTo(clamped, durationMs)) }
}
```

### Add a new command (checklist)
- Read target JS API and confirm params/return.
- Create command data class + `@Serializable` surrogate if needed.
- Implement `toJS()` with a single options object.
- Set `isPrimitiveReturnType` correctly and handle `MTBridgeReturnType` if you need the value.
- Add `MTMapViewController` (or worker) convenience API; validate readiness and inputs.
- Tests: encoding, clamping, and `toJS()` contract.

## Code Style Guidelines (MANDATORY)

- Public entities use the `MT` prefix (e.g., `MTMapView`, `MTStyle`). Domain types like `LngLat` are established exceptions.
- Types use PascalCase; variables/functions use camelCase. Constants use UPPER_SNAKE_CASE where applicable.
- Prefer `val` over `var`; minimize mutable state; use `data class` for value objects.
- 4-space indentation; default parameters at the end of parameter lists.
- End files with exactly one trailing newline.
- Line length: target 120 characters max (code and comments). Wrap KDoc accordingly.
- Nullability: Prefer non-null types; use `?` and safe calls sparingly, with clear invariants.
- Visibility: Keep implementation details `internal`/`private`. Avoid unnecessary public APIs.
- Packages: `com.maptiler.maptilersdk.[area]` and mirror directory structure.

## ktlint Compliance (MANDATORY)
- ALWAYS follow the rules enforced by the `org.jlleitschuh.gradle.ktlint` plugin.
- Key rules: spacing, imports order, trailing newline, indentation, annotation/kdoc formatting, no wildcard imports.
- Pre-commit: a Git hook runs `./gradlew ktlintCheck`. Ensure zero violations before committing.
- CI/Local requirement: run `./gradlew ktlintCheck` and ensure zero warnings/errors. PRs must be lint-clean.
- Formatting: the project disables `ktlintFormat` by default; fix violations manually or enable formatting locally if needed.

## Development Best Practices

- API surface: use `public` only for intended external API; default to `internal` within the SDK module.
- Encapsulation: keep workers/services internal to their domains (navigation, zoomable, style, gestures).
- Errors: represent SDK failures with typed `MTError` and propagate meaningful messages.
- Documentation: document every public declaration with concise KDoc (`/** ... */`).
- Tests: prioritize bridge commands, options encoding, and `toJS()` contracts.
- Logging: use `MTLogger` with appropriate `MTLogType`; never log secrets.

## Kotlin Coroutines

- Main thread safety:
  - Interacting with `WebView` must occur on `Dispatchers.Main` (handled in `WebViewExecutor`).
  - Compose UI code runs on the main thread; do not perform blocking work in Composables.
- Scope:
  - Use the controller’s bound `CoroutineScope` for map operations.
  - Prefer `suspend` APIs for bridge reads (e.g., getters like `getZoom()`).
- Sendability/Immutability:
  - Use immutable `data class` models; avoid shared mutable state across coroutines.
  - Avoid `@OptIn(DelicateCoroutinesApi::class)`; keep structured concurrency.

## Project Structure

### Top-Level

- `README.md`: Usage, Compose snippet, sources/layers, annotations, installation.
- `CHANGELOG.md`, `CONTRIBUTING.md`, `LICENSE.txt`: Project meta.
- Gradle: `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, wrapper.
- Lint/Docs: ktlint plugin configured in Gradle; Dokka for KDoc.
- `.github/`, `scripts/`: CI, hooks, and scripts scaffolding.

### Library: `MapTilerSDK`

- `map/`: Core UI and map API.
  - `MTMapView` (Compose) attaches a `WebView`; exposes map/style APIs via controller and lifecycle events.
  - `MTMapViewController`: camera, events, gesture service, workers binding.
  - `MTMapOptions` + `options/`: camera, padding, animation, gestures config.
  - `style/`: `MTStyle`, reference styles/variants, glyphs/terrain/tile scheme, style errors.
  - `gestures/`: gesture types and services (pan, pinch/rotate/zoom, double tap).
  - `types/`: shared types (e.g., `LngLat`, `MTPoint`, colors, source data).
- `bridge/`: Kotlin ↔ JS bridge via `WebView`.
  - `MTCommand`, `MTBridge`, `WebViewExecutor`, `MTBridgeReturnType`, `MTError`.
  - `MTJavaScriptInterface` for events and error propagation from JS.
- `commands/`: Strongly-typed wrappers that turn Kotlin calls into JS invocations.
  - `navigation/`: flyTo, easeTo, jumpTo, pan/zoom/bearing/pitch/roll, bounds, padding.
  - `style/`: add/remove sources and layers, set style, language, light, glyphs, projection, terrain.
  - `annotations/`: add/remove markers and text popups, set coordinates, batch ops.
  - `gestures/`: enable/disable gesture types.
- `annotations/`: Public annotation APIs (`MTMarker`, `MTTextPopup`, base `MTAnnotation`).
- `events/`: Event pipeline that feeds `MTMapViewDelegate` and content delegates.
- `helpers/`: serialization config, color/coordinates converters, image helpers.
- `logging/`: `MTLogger`, types, and adapters.
- `assets/`: Embedded JS/CSS/HTML for the web map container.

### Tests

- `MapTilerSDK/src/test/java`: Unit tests using JUnit/MockK.
  - Helpers: coordinate and color conversions, camera helpers.
  - Suites: navigation and bridge tests (toJS contracts, encoding, return type handling).

### Kotlin API Surface (Prefer Stronger Kotlin Types)
- Prefer expressive Kotlin-first APIs that hide JS-specific details while encoding the correct JS schema under the hood.
- For values that are strings in JS but have richer domain types (e.g., colors), expose ergonomic initializers and helpers.
- Validate inputs and clamp numeric ranges in Kotlin before bridging to JS.
- Default parameter values should reflect sensible SDK defaults.

#### Developer-Facing Simplicity (MANDATORY)
- Prefer Kotlin-first types over raw strings in public APIs (e.g., enums, value classes, typed models).
- Numbers/angles: provide `.constant(Double)` and zoom-stop helpers where domain types exist.
- Mixed unions: accept both collections and strings where JS unions require it; encode internally.
- Add minimal tests to verify encoding and `toJS()` contracts for these conveniences.

## Style Lifecycle

MUST wait for `ON_READY` before mutating style or layers. Changing the reference style resets layers; re-add required sources/layers after style changes. Prefer batch commands where available. When enabling terrain or projection changes, verify map idleness before subsequent camera moves.

## Error Handling

- Retry once on transient bridge failures; log verbosely under `MTLogLevel.Debug`.
- Return clear, user-facing messages with the failed command and suggested fix.
- Treat unsupported return types as warnings; choose a safer path or request input.

## Privacy

- Never log API keys. Redact sensitive values from structured logs.
- Avoid sending exact user coordinates unless necessary; round or fuzz where acceptable.

## Tests

- Before you make the Pull Request ALWAYS run unit tests to validate the code and fix potential issues.
- Commands:
  - `./gradlew ktlintCheck`
  - `./gradlew :MapTilerSDK:test`
  - Optional: `./gradlew :MapTilerSDK:dokkaHtml` to validate docs generation
- Add or update unit tests as required (encoding, clamping, `toJS()` contract), but leave full execution to the user/CI.
- Prefer small, focused tests near the code you change; avoid introducing unrelated tests.

## Glossary

- `JSString`: A Kotlin `String` containing JS source to evaluate in the `WebView`.
- `MTCommand`: A Kotlin type describing a JS-callable command (`toJS()` returns `JSString`).
- Bridge executor: The component that feeds `JSString` into `evaluateJavascript` on the Android `WebView`.
- `MTBridgeReturnType`: Decoders for typed return values from JS.

Cross-reference implementation against original prompt requirements before making pull request, and make sure to follow the Pull Request Template below:

## Pull Request Template

[Link to related issue]

## Objective
What is the goal?

## Description
What changed, how and why?

## Acceptance
How were changes tested?

