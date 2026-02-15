# Slot Click Macros

A Minecraft Fabric mod for recording and replaying slot click sequences in container GUIs to automate repetitive inventory interactions.

## ⚠️ Important Warning

**USE AT YOUR OWN RISK!** This mod can be detected by anti-cheat systems and may result in bans on multiplayer servers. Always use safe delay settings and be aware that automation mods are often against server rules. The developers are not responsible for any consequences of using this mod.

## Overview

Slot Click Macros allows you to record sequences of inventory clicks and replay them automatically. Perfect for automating repetitive tasks like shop interactions, item sorting, or crafting patterns on multiplayer servers.

## Features

### Recording System

- **Record any container interaction** - Works with chests, hoppers, crafting tables, and custom server GUIs
- **Simple controls** - Press a keybind to start/stop recording
- **Accurate capture** - Records slot IDs, click types (left/right/shift-click), and hotbar swaps
- **Easy saving** - Name and save your macros for later use

### Playback System

- **Multiple trigger methods** - Use keybinds or commands to play macros
- **Queue system** - Macros automatically start when you open the next container
- **Smart container handling** - Automatically adapts to server-side container changes
- **Visual feedback** - See which slots are being clicked during playback

### Anti-Detection Features

- **Variable delays** - Configurable base delay with random variance
- **Natural timing** - Mimics human reaction times
- **Safe defaults** - Pre-configured for maximum safety (100ms + 50ms random)
- **Customizable** - Adjust settings based on server strictness

### Configuration

- **Modern GUI** - Clean Cloth Config interface
- **Delay settings** - Base delay (10-500ms) and random range (0-100ms)
- **Keybind management** - Assign keybinds for recording, stopping, and playing macros
- **Macro management** - View, edit, and delete saved macros

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.4
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download Slot Click Macros and place it in your `mods` folder
4. Launch Minecraft

## How to Use

### Recording a Macro

1. Press your **Record Keybind** (default: `R`) to start recording
2. Open any container (chest, shop GUI, etc.)
3. Perform your desired actions (click items, shift-click, use hotbar keys)
4. Press the **Record Keybind** again to stop
5. Enter a name for your macro and save

### Playing a Macro

**Method 1: Keybind**

1. Assign a keybind to your macro in the Config GUI
2. Press the keybind (macro is queued)
3. Open the target container
4. Macro plays automatically

**Method 2: Command**

1. Run `/slotmacro play <name>`
2. Open the target container
3. Macro plays automatically

### Stopping Playback

- Press your **Stop Keybind** (configurable in settings)
- Or run `/slotmacro stop`

## Settings

### Default Delay (ms)

The base delay between each action during playback.

- **Range**: 10-500ms
- **Default**: 100ms
- **Recommended**: 100-150ms for most servers
- ⚠️ **Warning**: Below 80ms may trigger anti-cheat systems

### Random Delay Range (ms)

Adds random variance to each action delay for natural behavior.

- **Range**: 0-100ms
- **Default**: 50ms
- **Recommended**: 50-70ms for good variance
- **Effect**: With 100ms base + 50ms random = 100-150ms per action

### Keybinds

- **Record Keybind**: Start/stop recording (default: `R`)
- **Stop Keybind**: Stop all macro playback (default: not set)
- **Macro Keybinds**: Assign individual keybinds per macro

## Commands

- `/slotmacro play <name>` - Queue a macro for playback
- `/slotmacro stop` - Stop all macro playback and clear queue
- `/slotmacro list` - List all saved macros
- `/slotmacro delete <name>` - Delete a macro

## Use Cases

### Shop Automation

Automate purchasing items from server shop GUIs. For example, on a server with a shop interface, you can record the sequence of clicks to buy specific items and replay it instantly.

**Example: Buying kits on a server**

1. Record yourself navigating: Main Menu → Shop → Kits → Purchase Kit
2. Save as "buy_pvp_kit"
3. Assign keybind `Ctrl+K`
4. Next time: Press `Ctrl+K`, open shop, kit is purchased automatically

### Item Sorting

Create macros for organizing items in storage systems or moving items between containers efficiently.

### Crafting Patterns

Record complex crafting sequences and replay them for bulk crafting operations.

### Trading

Automate repetitive villager trading or player shop interactions.

## Anti-Cheat Safety

### Recommended Settings by Server Type

**Normal Servers** (Spartan, basic anti-cheat):

- Base Delay: 100-120ms
- Random Range: 50-60ms
- Risk: Low

**Strict Servers** (Matrix, Vulcan):

- Base Delay: 150-180ms
- Random Range: 70-90ms
- Risk: Very Low

**Maximum Safety** (Grim, custom anti-cheat):

- Base Delay: 200-250ms
- Random Range: 80-120ms
- Risk: Minimal

### What Gets You Banned

- ❌ Delays below 80ms (too fast, inhuman)
- ❌ Zero random variance (perfect timing = bot)
- ❌ Using on servers that explicitly ban automation mods
- ❌ Excessive use that disrupts server economy

### How to Stay Safe

- ✅ Use recommended delay settings
- ✅ Enable random delay variance
- ✅ Test on private servers first
- ✅ Read server rules before using
- ✅ Use responsibly and sparingly

## File Format

Macros are stored as simple text files in `config/slotclickmacros/`:

```
12,PICKUP,0
15,QUICK_MOVE,0
3,HOTBAR_1,0
27,PICKUP,1
```

Format: `SlotID,ClickType,Button`

- Easy to share and edit manually
- Import/export by copying files

## Technical Details

- **Minecraft Version**: 1.21.4
- **Mod Loader**: Fabric
- **Dependencies**: Fabric API, Cloth Config
- **Mappings**: Mojang Official

## Building from Source

```bash
git clone https://github.com/yourusername/slot-click-macros.git
cd slot-click-macros
./gradlew build
```

The compiled JAR will be in `build/libs/`

## FAQ

**Q: Will I get banned for using this?**
A: Possibly. Many servers prohibit automation mods. Use at your own risk and always check server rules.

**Q: Why isn't my macro working?**
A: Ensure you're opening the same type of container. Server-side GUIs may have different slot layouts.

**Q: Can I use this in singleplayer?**
A: Yes, it works perfectly in singleplayer and on private servers.

**Q: How do I make my macros undetectable?**
A: Use higher delays (150ms+), enable random variance (50ms+), and avoid excessive use.

**Q: The macro clicks the wrong slots!**
A: Container layouts may differ. Re-record the macro in the specific container you want to use.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## License

This project is licensed under CC0-1.0 - see the LICENSE file for details.

## Disclaimer

This mod is provided "as is" without warranty of any kind. The developers are not responsible for any bans, data loss, or other consequences resulting from the use of this mod. Always respect server rules and use automation responsibly.

**Remember: Just because you can automate something doesn't mean you should. Use this mod ethically and at your own risk.**

## Credits

- Built with [Fabric](https://fabricmc.net/)
- GUI powered by [Cloth Config](https://github.com/shedaniel/cloth-config)
- Inspired by various automation tools in the Minecraft community

---

**⚠️ Final Warning**: This mod can result in permanent bans on multiplayer servers. The safest way to use it is on private servers or in singleplayer. Always read and follow server rules.
