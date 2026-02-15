# Slot Click Macros - Mod Concept Documentation

## Overview
A Minecraft Fabric mod for recording and replaying slot click sequences in container GUIs. Designed to automate repetitive container interactions through recorded macros.

---

## Core Functionality

### Recording System
- **Records slot interactions** in any container GUI (chests, hoppers, crafting tables, etc.)
- **Captures two data points per action:**
  - Slot ID (numeric position in container)
  - Click type (see Click Types below)
- **No timing data recorded** - uses configurable default delay between actions
- **Hotbar key support** - Records hotbar slot swaps (1-9, offhand) as slot IDs, not the actual keybind pressed
  - Example: If player has slot 1 bound to "A" and presses it, the macro stores "HOTBAR_1", not "A"

### Click Types (Based on Minecraft's SlotActionType)
```
PICKUP          - Normal left/right click to pick up or place items
QUICK_MOVE      - Shift-click to quick transfer
SWAP            - Number key press to swap with hotbar
CLONE           - Middle-click (creative mode)
THROW           - Q key to drop item
QUICK_CRAFT     - Drag crafting
PICKUP_ALL      - Double-click to gather all matching items
HOTBAR_1-9      - Hotbar slot swap via keybind (not mouse click)
```

### File Format
- **Simple text-based syntax:** `SlotID,ClickType`
- **Example macro file:**
```
12,PICKUP
15,QUICK_MOVE
3,HOTBAR_1
27,PICKUP
```
- **Storage location:** `.minecraft/config/slotclickmacros/` (or similar)
- **Import/Export:** Users can share macro files

---

## User Interface

### Recording Workflow
1. Press **record keybind** (configurable) to start recording
2. Perform actions in any container GUI
3. Press **stop keybind** to end recording
4. **Save dialog appears** (similar to Replay Mod style)
   - Enter macro name
   - Click "Save" button
5. Macro saved as `.txt` file in config folder

### Config GUI Features
- **Default delay setting** (milliseconds between actions during playback)
- **Record keybind** (single key or combination)
- **Macro management list:**
  - View all saved macros
  - Assign playback keybind per macro
  - Assign playback command per macro
  - Delete/rename macros
  - Import external macro files

---

## Playback System

### Trigger Methods (User Choice)
**Option 1: Keybind Activation**
- Press assigned keybind while **no GUI is open**
- Macro queued for execution
- **Automatically starts** when player opens next container

**Option 2: Command Activation**
- Run command like `/slotmacro play <name>`
- Macro queued for execution
- **Automatically starts** when player opens next container

### Advanced Keybind System
- **Custom keybind listener** supporting key combinations
- Examples: `Ctrl+A`, `Shift+R`, `Alt+1`
- Not limited to Minecraft's default single-key binds

---

## Technical Requirements

### Configuration Settings
```
- defaultDelay: int (milliseconds, default: 50)
- recordKeybind: String (e.g., "R", "CTRL+R")
- macros: List<Macro>
  - name: String
  - filePath: String
  - playbackKeybind: String (optional)
  - playbackCommand: String (optional)
```

### Macro Execution Logic
1. Player triggers macro (keybind or command)
2. Mod sets "macro queued" state
3. On next container GUI open event:
   - Read macro file
   - Parse slot actions
   - Execute sequence with configured delay
4. Reset state after completion

---

## Use Cases
- **Sorting systems** - One-click container organization
- **Crafting patterns** - Automate repetitive recipes
- **Item transfers** - Quick chest-to-chest movements
- **Shop interactions** - Fast villager trading
- **Storage management** - Consistent item placement

---

## Key Design Principles
1. **Simplicity** - Text-based format, easy to share and edit
2. **Flexibility** - Multiple trigger methods (keybind/command)
3. **Precision** - Slot ID-based, not coordinate-based
4. **Compatibility** - Works with any container GUI
5. **Control** - User-defined delays and keybinds