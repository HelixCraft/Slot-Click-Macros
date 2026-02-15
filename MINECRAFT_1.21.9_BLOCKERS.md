# Minecraft 1.21.9+ Migration Blockers

## Summary

Minecraft 1.21.9 introduced a completely rewritten input system that is incompatible with the current mod implementation and its dependencies.

## Critical Issues

### 1. Input Event System Overhaul

**Problem**: Minecraft 1.21.9 changed all input handling methods from the old signature:

```java
// OLD (1.21-1.21.8)
boolean keyPressed(int keyCode, int scanCode, int modifiers)
boolean mouseClicked(double mouseX, double mouseY, int button)
```

To a new event-based system:

```java
// NEW (1.21.9+)
boolean keyPressed(KeyEvent event)
boolean mouseClicked(MouseButtonEvent event, boolean consumed)
void onPress(InputWithModifiers input)
```

**Impact**: ALL GUI classes that handle input need to be rewritten:

- `KeybindButton.java` - Custom button for keybind input
- `SaveMacroScreen.java` - Save dialog screen
- `MacroListEntry.java` - Macro list entries
- `KeybindConfigEntry.java` - Config entries

### 2. Missing Input Event Classes

**Problem**: The new input event classes (`KeyEvent`, `MouseButtonEvent`, `InputWithModifiers`) cannot be found in the expected package `net.minecraft.client.input`.

**Attempted Solutions**:

- Searched for classes in `net.minecraft.client.input` - NOT FOUND
- Tried `net.minecraft.client.gui.input` - NOT FOUND
- Tried `net.minecraft.client.gui.components.events` - NOT FOUND

**Status**: Unable to locate the correct package for these classes. They may:

- Be in a different package entirely
- Require a newer Fabric API version
- Not be properly mapped in Mojang mappings yet

### 3. Window Handle Access Changed

**Problem**: The method to get the GLFW window handle changed:

```java
// OLD
long window = Minecraft.getInstance().getWindow().getWindow();

// TRIED (1.21.9)
long window = Minecraft.getInstance().getWindow().getHandle(); // Method not found
long window = Minecraft.getInstance().getWindow().window; // Field not found
```

**Impact**: Cannot access window handle for modifier key detection in:

- `KeybindManager.java`
- `KeybindButton.java`

### 4. KeyMapping.Category Type Changed

**Problem**: `KeyMapping.Category` constructor changed:

```java
// OLD
private static final String CATEGORY = "slot-click-macros.general";

// NEW (attempted)
private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
    ResourceLocation.fromNamespaceAndPath("slot-click-macros", "general")
); // Type mismatch error
```

**Status**: Cannot determine correct way to create category in 1.21.9.

### 5. Cloth Config Compatibility

**Problem**: Cloth Config 20.0.149 (for 1.21.9) expects the new input event signatures, but we cannot implement them without access to the event classes.

**Impact**: All config GUI elements fail to compile.

## Compilation Errors Count

- 24 errors total
- 8 "method does not override" errors
- 10 "incompatible types" errors
- 6 "symbol not found" errors

## Affected Files

1. `src/main/java/com/helixcraft/slotclickmacros/keybinds/KeybindManager.java`
2. `src/main/java/com/helixcraft/slotclickmacros/gui/KeybindButton.java`
3. `src/main/java/com/helixcraft/slotclickmacros/gui/SaveMacroScreen.java`
4. `src/main/java/com/helixcraft/slotclickmacros/gui/MacroListEntry.java`
5. `src/main/java/com/helixcraft/slotclickmacros/gui/KeybindConfigEntry.java`

## Recommended Actions

### Option 1: Wait for Documentation (RECOMMENDED)

Wait for official Fabric/Mojang documentation on the 1.21.9 input system changes before attempting migration.

### Option 2: Decompile and Research

Decompile Minecraft 1.21.9 to find:

- Correct package for input event classes
- Correct Window API for getting GLFW handle
- Correct KeyMapping.Category constructor

### Option 3: Limit Support to 1.21.8

Keep the `1.21.9+` branch but document that 1.21.9+ support is blocked pending:

- Official documentation
- Fabric API updates
- Cloth Config compatibility fixes

## Current Branch Status

- `1.21-1.21.5`: ✅ Working
- `1.21.6-1.21.8`: ✅ Working (2D rendering fixes applied)
- `1.21.9+`: ❌ BLOCKED (input system incompatibility)

## Next Steps

1. Document the blockers (this file)
2. Commit current progress to `1.21.9+` branch
3. Wait for community/official guidance on 1.21.9 input system
4. Revisit when proper documentation is available
