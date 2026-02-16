# Slot Click Macros

![Environment](https://img.shields.io/badge/Environment-Client-purple)
[![Java 21](https://img.shields.io/badge/Language-Java%2021-orange)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Modrinth](https://img.shields.io/badge/Modrinth-Slot--Click--Macros-00AF5C?logo=modrinth)](https://modrinth.com/mod/slot-click-macros)

<p align="left">
  <a href="https://modrinth.com/mod/slot-click-macros">
    <img src="https://github.com/user-attachments/assets/6bc92930-84f9-4eb1-ae1d-8f79775b87c6" width="200" alt="Download on Modrinth">
  </a>
</p>

A Minecraft Fabric mod that records and replays inventory click sequences to automate repetitive container interactions.

**⚠️ Warning: USE AT YOUR OWN RISK!** This mod can be detected by anti-cheat systems and may result in bans on multiplayer servers if your not careful. Always use safe delay settings (100ms+ base, 50ms+ random) and check server rules before using. The developers are not responsible for any consequences.

### Features

- **Record any container interaction** - Works with chests, shops, crafting tables, and custom GUIs
- **Simple controls** - Press a keybind to start/stop recording, then save with a name
- **Multiple playback methods** - Trigger via keybind or `/slotmacro play <name>` command
- **Anti-detection delays** - Configurable base delay (10-500ms) with random variance (0-100ms)
- **Queue system** - Macros automatically execute when you open the next container
- **Easy management** - Assign keybinds, view, edit, and delete macros via config GUI

![](https://cdn.modrinth.com/data/cached_images/3ce19f22c5fdf5b943678c21f948fb5277ba2899_0.webp)

### Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Cloth Config](https://modrinth.com/mod/cloth-config)
4. Place Slot Click Macros in your `mods` folder

<a href="https://modrinth.com/mod/fabric-api"><img src="https://raw.githubusercontent.com/FeelZoR/Fleeing-Animals/develop/src/main/resources/assets/fleeinganimals/requirements/fabric-api.png" width="200" alt="Fabric API auf Modrinth"></a>
<a href="https://modrinth.com/mod/cloth-config"><img src="https://raw.githubusercontent.com/FeelZoR/Fleeing-Animals/develop/src/main/resources/assets/fleeinganimals/requirements/cloth-config.png" width="200" alt="Cloth Config auf Modrinth"></a>

### Quick Start

**Recording**

1. Press **R** (default) to start recording
2. Open a container and perform your actions
3. Press **R** again to stop
4. Enter a name and save

**Playing**

1. Assign a keybind in the config GUI **or** run `/slotmacro play <name>`
2. Open the target container
3. Macro plays automatically

**Stopping**

- Marco automatically stops after finish
- To stop early, press your configured stop keybind **or** run `/slotmacro stop`

### Commands

- `/slotmacro play <name>` - Queue a macro
- `/slotmacro stop` - Stop playback
- `/slotmacro list` - List all macros
- `/slotmacro delete <name>` - Delete a macro

### Recommended Settings

**Normal Servers**

- Base Delay: 100-120ms
- Random Range: 50-60ms

**Strict Anti-Cheat**

- Base Delay: 150-180ms
- Random Range: 70-90ms

**Maximum Safety**

- Base Delay: 200-250ms
- Random Range: 80-120ms

⚠️ **Never use delays below 80ms** - this will trigger most anti-cheat systems.

### File Format

Macros are stored as text files in `config/slotclickmacros/`:

```
12,PICKUP,0        # Left-click slot 12
15,QUICK_MOVE,0    # Shift-click slot 15
3,HOTBAR_1,0       # Press hotbar key 1 on slot 3
27,PICKUP,1        # Right-click slot 27
```

**Format:** `SlotID,ClickType,Button`

1. **ClickType** (string): The type of click action
   - `PICKUP` - Normal left/right click
   - `QUICK_MOVE` - Shift-click to transfer items
   - `SWAP` - Number key to swap with hotbar
   - `CLONE` - Middle-click (creative mode)
   - `THROW` - Q key to drop item
   - `QUICK_CRAFT` - Drag crafting
   - `PICKUP_ALL` - Double-click to gather matching items
   - `HOTBAR_1` through `HOTBAR_9` - Hotbar slot swap via keybind

2. **Button** (integer): Mouse button used
   - `0` - Left mouse button
   - `1` - Right mouse button
   - `2` - Middle mouse button

### FAQ

**Will I get banned?**
Possibly. Many servers prohibit automation. Always check server rules and use appropriate delays.

**Macro clicks wrong slots?**
Container layouts may differ. Re-record in the specific container you're using.

**How to stay safe?**
✅ Use 100ms+ delays with 50ms+ variance
✅ Test on private servers first
✅ Read server rules
✅ Use responsibly

### License

CC0-1.0

### Disclaimer

This mod is provided "as is" without warranty. The developers are not responsible for bans or other consequences. Use ethically and at your own risk.
