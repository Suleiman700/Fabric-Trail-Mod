# Trail Mod (Fabric)

## Media

![Screenshot 1](media/screenshot%201.png)

![Screenshot 2](media/screenshot%202.png)

![Screenshot 3](media/screenshot%203.png)

![Screenshot 4](media/screenshot%204.png)

![Screenshot 5](media/screenshot%205.png)

![Screenshot 6](media/screenshot%206.png)

![Screenshot 7](media/screenshot%207.png)

<video src="media/mod%20preview%201.mp4" controls muted loop></video>

<video src="media/mod%20preview%202.mp4" controls muted loop></video>

<video src="media/mod%20preview%203.mp4" controls muted loop></video>

<video src="media/mod%20preview%204.mp4" controls muted loop></video>

<video src="media/mod%20preview%20-%20static%20mode.mp4" controls muted loop></video>

<video src="media/mod%20preview%20-%20gradient%20mode.mp4" controls muted loop></video>

<video src="media/mod%20preview%20-%20motion%20mode.mp4" controls muted loop></video>

A small client-side mod that draws a **trail/line behind your player** to help visualize where you have been.

## Compatibility

- **Mod Loader**: Fabric
- **Minecraft**: this mod builds on 1.21.11

## Installation

1. Install **Fabric Loader** to match the Minecraft version.
2. Install **Fabric API**.
3. (Optional) Install **Mod Menu** to open the config UI easily.
4. Download the mod jar and place it into:

   `/.minecraft/mods/`

5. Start the game.

## How to use (in game)

- The trail is rendered in-world behind you while you move.
- If enabled, trails are also drawn for other players within the configured range.
- You can adjust colors, mode, arrow indicators, width, and TTL in the config screen.

### Hotkey behavior

Default hotkey is **V**.

- **Single press**: pause/resume drawing (stops/starts adding new trail points)
- **Double press** (quickly): clear the trail

There is also a configurable hotkey for other players' trails (default: **B**).

- **Single press**: toggle other-player trails
- **Double press**: clear other-player trails

## Configuration

### Config screen

You'll need **Mod Menu** installed to edit the mod settings.

### Config options

- **Enable**
    - Turns the mod on/off.

- **Trail TTL**
  - How long trail points remain before disappearing.
  - Range: `1` to `10800` (in seconds).
  - Note: higher values can reduce FPS (more trail points to render).

- **Hotkey**
  - Key code for the trail hotkey (default: `V`).
  - Single press pauses/resumes drawing; double press clears.

- **Mode**
  - `STATIC`: one constant color
  - `GRADIENT`: color blends from start to end
  - `MOTION`: changes color based on vertical movement
    - going up = green, going down = red

- **Color**
  - The main trail color.

- **Gradient end color**
  - The end color of the gradient.
  - Used only when `mode` is `GRADIENT`.

- **Line width**
  - Trail thickness.
  - Range: `1` to `10`.

- **Arrows enabled**
  - Show/hide arrowheads along the trail (shows direction).

- **Arrow spacing**
  - Distance between arrows in blocks.
  - Range: `1` to `40`.

- **Arrow size**
  - Arrowhead size.
  - Range: `1` to `10`.

- **Pause on sneak**
  - When sneaking, the mod stops adding new trail points.

- **Line Opacity**
  - How transparent the trail is.
  - Range: `0` (invisible) to `100` (fully visible).

- **Erase on Retrace**
  - When you return close to an older part of your trail, erase everything newer than that point.
  - Radius: `0.75` blocks.

### Other Players

- **Enable**
  - Turns drawing trails for other players on/off (disabled by default).
  - Note: can be performance-heavy if many players are nearby.

- **Range**
  - Only track other players within this distance (blocks).
  - Range: `0` to `4096`.

- **Hotkey**
  - Key code for the other-player trails hotkey (default: `B`).
  - Single press toggles; double press clears.

- **Point Cap**
  - Safety cap for total other-player trail points to prevent lag.
  - Range: `1000` to `200000`.

## Building from Source

For developers who want to build the mod from source:

1. Clone this repository.
2. Ensure you have JDK 21 or newer installed.
3. Run `./gradlew build` (Linux/Mac) or `gradlew.bat build` (Windows).
4. The mod jar will be in `build/libs/`.

## Links

- Homepage: http://brightpixel.work/
- GitHub: https://github.com/Suleiman700
