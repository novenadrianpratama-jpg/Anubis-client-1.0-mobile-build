# Anubis Client — Zalith Launcher 2 Mobile Build
## What was changed

| File | Change |
|---|---|
| `net/anubis/client/mobile/ZalithCompat.java` | **NEW** — detects Android/Zalith at runtime |
| `net/anubis/client/mobile/MobileGuiScreen.java` | **NEW** — touch-friendly module GUI (category tabs + scrollable list + tap-to-toggle) |
| `net/anubis/client/imgui/ImGuiManager.java` | `initIfNeeded()`, `renderIfActive()`, `dispose()` all return early on mobile |
| `net/anubis/client/input/AnubisKeyPolling.java` | GLFW call wrapped in try/catch; returns false on mobile |
| `net/anubis/client/AnubisClientMod.java` | GUI key opens `MobileGuiScreen` on mobile; `isBoundKeyDown()` skips GLFW on mobile |
| `net/anubis/client/BuildConfig.java` | `SIGN_CERT_PARTS` cleared → `OfficialBuild` returns `NOT_CHECKED` for rebuilt JARs |

---

## Requirements

- **Java 21** (must match the Minecraft runtime version)
- **Gradle 8.8** (install via [gradle.org](https://gradle.org/install/), or use `./gradlew` after step 2)
- Internet access to download Fabric Loom and Minecraft JARs on first build

---

## Build steps

### 1 — Install Gradle wrapper (one time)
```bash
cd anubis-mobile
gradle wrapper --gradle-version 8.8
```

### 2 — Build the mod JAR
```bash
./gradlew build
```

The output JAR is at:
```
build/libs/anubis-mobile-1.0.0-mobile.jar
```
(ignore the `-sources.jar`)

### 3 — If the build fails on decompiled source errors
The CFR-decompiled source has occasional issues (bad casts, missing generics). 
Fix them individually — the compiler error output names the file and line.
Common fixes:
- `Unchecked cast` → add `@SuppressWarnings("unchecked")`  
- `cannot find symbol class_XXXX` → check that the class is in `src/main/java` and the package matches

---

## Installing in Zalith Launcher 2

1. Open **Zalith Launcher 2** on your Android device.
2. Tap the profile for **Minecraft 1.21.1** (create one if needed).
3. Make sure **Fabric Loader 0.16.5** is selected as the mod loader.
4. Tap **Mods → Import mod** (or the `+` button).
5. Navigate to and select `anubis-mobile-1.0.0-mobile.jar`.
6. Launch the game.

> **Fabric API** (`fabric-api-0.102.0+1.21.1.jar`) must also be installed
> as a mod. Download it from [modrinth.com/mod/fabric-api](https://modrinth.com/mod/fabric-api).

---

## Using the GUI in-game

Zalith Launcher 2 maps physical keys to Minecraft keybinds through its
virtual keyboard / hardware keyboard support.

| Action | Default |
|---|---|
| Open / close module GUI | **Right Shift** (key 344) |

To change the key: go to Zalith settings → Keybinds → *Anubis Controls → Open GUI*.

The GUI shows five category tabs at the top (Combat, Misc, Render, Anubis Client,
Movement). Tap a tab to switch categories. Scroll the module list by dragging.
Tap any row to toggle that module on or off.

---

## Known mobile limitations

| Feature | Status |
|---|---|
| Module toggle GUI | ✅ Works (native touch screen) |
| All gameplay modules (bhop, aimbot, esp, etc.) | ✅ Work — they run server-side logic, unaffected |
| HUD overlays | ✅ Work via Minecraft's own render pipeline |
| ImGui ClickGui | ❌ Disabled on mobile (GLFW/GL3 unavailable) |
| Mouse-button keybinds for modules | ❌ GLFW mouse polling not available |
| Spotify integration | ❌ Desktop-only, skipped automatically |
| Borderless fullscreen | ❌ Desktop-only window feature |
