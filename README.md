# Trail Mod (Fabric)

A small client-side mod that draws a **trail/line behind your player** to help visualize where you have been.

## Media

![Screenshot 1](media/screenshot%201.png)

![Screenshot 2](media/screenshot%202.png)

![Screenshot 3](media/screenshot%203.png)

![Screenshot 4](media/screenshot%204.png)

![Screenshot 5](media/screenshot%205.png)

![Screenshot 6](media/screenshot%206.png)

![Screenshot 7](media/screenshot%207.png)

https://github.com/user-attachments/assets/4f9028d9-073e-4d43-a27c-d2b24c63d72f

<video width="320" height="240" controls>
  <source src="https://youtu.be/3Sr16XOKQZM" type="video/mp4">
</video>


## Compatibility

- **Mod Loader**: Fabric
- **Minecraft**: this mod builds on 1.21.11

## Installation

1. Install **Fabric Loader** to match the Minecraft version.
2. Install **Fabric API**.
3. Install **Mod Menu** to open the config UI easily.
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

| Setting | Config key | Default | Range / Values | Notes |
| --- | --- | --- | --- | --- |
| Enable | `enabled` | `true` | `true` / `false` | Turns the mod on/off. |
| Trail TTL (seconds) | `ttlSeconds` | `10` | `1` to `10800` | Higher values can reduce FPS (more trail points to render). |
| Hotkey | `keyCode` | `V` | Any key | Single press pauses/resumes drawing; double press clears the trail. |
| Mode | `mode` | `STATIC` | `STATIC`, `GRADIENT`, `MOTION` | Motion: red when falling, green when rising. |
| Color | `color` | `WHITE` | One of the built-in colors | The main trail color. |
| Gradient end color | `gradientEndColor` | `WHITE` | One of the built-in colors | Used only when `mode` is `GRADIENT`. |
| Line width | `lineWidth` | `2` | `1` to `10` | Trail thickness. |
| Line arrows | `arrowsEnabled` | `true` | `true` / `false` | Show arrowheads on the trail to indicate direction. |
| Arrow spacing (blocks) | `arrowSpacingBlocks` | `2` | `1` to `40` | Distance between arrows. |
| Arrow size | `arrowSize` | `1` | `1` to `10` | Arrowhead size. |
| Pause on sneak | `pauseOnSneak` | `true` | `true` / `false` | When sneaking, stop adding new trail points. |
| Line opacity | `lineOpacity` | `100` | `0` to `100` | `0` = invisible, `100` = fully visible. |
| Erase on retrace | `eraseOnRetrace` | `false` | `true` / `false` | When you return close to an older part of your trail, erase everything newer than that point (radius: `0.75` blocks). |

### Other Players

| Setting | Config key | Default | Range / Values | Notes |
| --- | --- | --- | --- | --- |
| Enable other-player trails | `otherPlayersEnabled` | `false` | `true` / `false` | Can be performance-heavy if many players are nearby. |
| Range (blocks) | `otherPlayersRangeBlocks` | `524` | `0` to `4096` | Only track other players within this distance. |
| Hotkey | `otherPlayersKeyCode` | `B` | Any key | Single press toggles; double press clears other-player trails. |
| Point cap | `otherPlayersPointCap` | `30000` | `1000` to `200000` | Safety cap for total other-player trail points to prevent lag. |

## Building from Source

For developers who want to build the mod from source:

1. Clone this repository.
2. Ensure you have JDK 21 or newer installed.
3. Run `./gradlew build` (Linux/Mac) or `gradlew.bat build` (Windows).
4. The mod jar will be in `build/libs/`.

## Links

- Homepage: http://brightpixel.work/
- GitHub: https://github.com/Suleiman700
