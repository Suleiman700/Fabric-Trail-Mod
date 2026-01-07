# ✨ Trail Mod (Fabric)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft: 1.21+](https://img.shields.io/badge/Minecraft-1.21+-blue.svg)](https://www.minecraft.net/)
[![Mod Loader: Fabric](https://img.shields.io/badge/Mod_Loader-Fabric-lightgrey.svg)](https://fabricmc.net/)

A high-performance, client-side Minecraft mod that renders customizable trails behind players. Perfect for parkour, navigation, or just adding a splash of color to your world. Works seamlessly in single-player, multiplayer, and Realms.

[**Download Releases**](https://github.com/Suleiman700/Fabric-Trail-Mod/releases) | [**Report a Bug**](https://github.com/Suleiman700/Fabric-Trail-Mod/issues)

---

## 📸 Media Gallery

<p align="center">
  <img src="media/screenshot 1.png" width="48%" />
  <img src="media/screenshot 2.png" width="48%" />
  <img src="media/screenshot 3.png" width="48%" />
  <img src="media/screenshot 4.png" width="48%" />
  <img src="media/screenshot 5.png" width="48%" />
  <img src="media/screenshot 6.png" width="48%" />
  <img src="media/screenshot 7.png" width="97%" />
</p>

### 🎥 Video Demonstration
https://github.com/user-attachments/assets/4f9028d9-073e-4d43-a27c-d2b24c63d72f

> **Note:** Click the thumbnail below to watch the full YouTube guide.
[![Trail Mod Demo Video](https://img.youtube.com/vi/3Sr16XOKQZM/0.jpg)](https://www.youtube.com/watch?v=3Sr16XOKQZM)

---

## 🚀 Features

* **Customizable Aesthetics:** Adjust color, width, and opacity to fit your style.
* **Dynamic Modes:**
    * `STATIC`: One solid color.
    * `GRADIENT`: Smooth transition between two colors.
    * `MOTION`: Changes color based on movement (Red while falling, Green while rising).
* **Arrow Indicators:** Optional arrowheads to show the direction of movement.
* **Multiplayer Ready:** Enable trails for other players within a configurable range.
* **Performance Optimized:** Includes a "Point Cap" and distance limits to ensure your FPS stays high.
* **Smart Tracing:** *Erase on Retrace* feature automatically clears paths when you double back.

---

## 🛠️ Installation

1.  **Requirement:** Install the [Fabric Loader](https://fabricmc.net/use/installer/).
2.  **Dependencies:** * [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
    * [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) (Required for the config UI).
3.  **Setup:** Drop the downloaded `.jar` file into your `/.minecraft/mods/` directory.

---

## ⌨️ Controls (Default)

| Key | Action | Double Press Action |
| :--- | :--- | :--- |
| **V** | Pause/Resume your trail | Clear your trail |
| **B** | Toggle other players' trails | Clear all other player trails |

---

## ⚙️ Configuration

Access the settings via **Mod Menu** in-game.

### Main Settings
| Setting | Default | Range / Values |
| :--- | :--- | :--- |
| **Trail TTL** | `10s` | 1 to 10800s |
| **Line Width** | `2` | 1 to 10 |
| **Mode** | `STATIC` | STATIC, GRADIENT, MOTION |
| **Opacity** | `100` | 0 to 100 |
| **Pause on Sneak** | `true` | true / false |
| **Erase on Retrace** | `false` | true / false |

### Other Players
| Setting | Default | Description |
| :--- | :--- | :--- |
| **Enable** | `false` | Show trails for nearby players. |
| **Range** | `524` | Block radius for tracking. |
| **Point Cap** | `30,000` | Prevents lag in crowded areas. |

---

## 👨‍💻 Building from Source

1. Clone the repo: `git clone https://github.com/Suleiman700/Fabric-Trail-Mod.git`
2. Ensure you have **JDK 21** installed.
3. Build the project:
    * **Windows:** `gradlew.bat build`
    * **Linux/Mac:** `./gradlew build`
4. Find the jar in `build/libs/`.

---

## 🔗 Links

* **Homepage:** [brightpixel.work](http://brightpixel.work/)
* **Author:** [Suleiman700](https://github.com/Suleiman700)

---
<p align="center">Made with ❤️ for the Minecraft Fabric Community</p>
