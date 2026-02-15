# Minecraft 1.21.9 Input System Changes

## Major Breaking Changes

### 1. Event-Based Input System

- `keyPressed(int keyCode, int scanCode, int modifiers)` → `keyPressed(KeyEvent event)`
- `mouseClicked(double mouseX, double mouseY, int button)` → `mouseClicked(MouseButtonEvent event, boolean consumed)`

### 2. KeyMapping Category

- Category is now an object, not a String
- Use `KeyMapping.Category.create("category.slot-click-macros")` instead of direct string

### 3. Window Handle Access

- `Window.getWindow()` method removed
- Need to use GLFW directly or alternative methods

### 4. InputConstants Changes

- `InputConstants.getKey(int keyCode, int scanCode)` → `InputConstants.getKey(KeyEvent event)`

### 5. AbstractButton Changes

- `onPress()` now takes `InputWithModifiers` parameter

## Files Requiring Updates

1. KeybindManager.java - Category and Window.getWindow()
2. KeybindButton.java - All input methods
3. MacroListEntry.java - Mouse and key events
4. KeybindConfigEntry.java - Mouse and key events
5. SaveMacroScreen.java - Key events

## Migration Strategy

This is a massive breaking change that requires careful refactoring of the entire input handling system.
The mod may need to be significantly rewritten for 1.21.9+ compatibility.
