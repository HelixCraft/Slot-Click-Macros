# MINECRAFT 1.21.9+ COMPLETE MIGRATION GUIDE

# COMPREHENSIVE QUESTIONS FOR SUCCESSFUL IMPLEMENTATION

## DOCUMENT PURPOSE

This document contains EVERY question needed to successfully migrate the Slot Click Macros mod from Minecraft 1.21.8 to 1.21.9+.
Once all questions are answered, the mod can be completed in one implementation pass.

---

## TABLE OF CONTENTS

1. [Input System Architecture Changes](#1-input-system-architecture-changes)
2. [Window & GLFW Access](#2-window--glfw-access)
3. [KeyMapping & Category System](#3-keymapping--category-system)
4. [GUI Component Input Handling](#4-gui-component-input-handling)
5. [InputConstants API Changes](#5-inputconstants-api-changes)
6. [Cloth Config Integration](#6-cloth-config-integration)
7. [AbstractButton Implementation](#7-abstractbutton-implementation)
8. [Screen Input Handling](#8-screen-input-handling)
9. [Event Propagation & Consumption](#9-event-propagation--consumption)
10. [Modifier Key Detection](#10-modifier-key-detection)
11. [Keybind Registration & Management](#11-keybind-registration--management)
12. [Backward Compatibility Strategy](#12-backward-compatibility-strategy)
13. [Testing & Validation](#13-testing--validation)
14. [Complete Code Examples](#14-complete-code-examples)

---

## 1. INPUT SYSTEM ARCHITECTURE CHANGES

### 1.1 Core Input Event Classes

**QUESTION 1.1.1**: What is the EXACT, FULL package path for the `KeyEvent` class in Minecraft 1.21.9?

- Is it `net.minecraft.client.input.KeyEvent`?
- Is it `net.minecraft.client.gui.input.KeyEvent`?
- Is it `net.minecraft.client.gui.components.events.KeyEvent`?
- Is it in a completely different package?
- Does it even exist, or has it been renamed?

**QUESTION 1.1.2**: What is the EXACT, FULL package path for the `MouseButtonEvent` class in Minecraft 1.21.9?

- Same question as 1.1.1 - what is the exact package?

**QUESTION 1.1.3**: What is the EXACT, FULL package path for the `InputWithModifiers` class/interface in Minecraft 1.21.9?

- Same question as above - exact package location?

**QUESTION 1.1.4**: Are these classes part of:

- Vanilla Minecraft (Mojang mappings)?
- Fabric API?
- A new library dependency?
- Do they require a specific Fabric API version?

**QUESTION 1.1.5**: If these classes don't exist with these exact names, what are they actually called in 1.21.9?

- Provide the EXACT class names used for keyboard input events
- Provide the EXACT class names used for mouse input events
- Provide the EXACT class names used for input with modifier keys

### 1.2 KeyEvent Class Structure

**QUESTION 1.2.1**: What is the COMPLETE class signature of `KeyEvent` in 1.21.9?

```java
// Example - fill in the actual signature:
public class KeyEvent extends ??? implements ??? {
    // What fields does it have?
    // What methods does it have?
}
```

**QUESTION 1.2.2**: How do you extract the key code from a `KeyEvent`?

- Is it `event.key()`?
- Is it `event.getKey()`?
- Is it `event.keyCode()`?
- Is it `event.getKeyCode()`?
- Something else?

**QUESTION 1.2.3**: How do you extract the scan code from a `KeyEvent`?

- Method name and signature?

**QUESTION 1.2.4**: How do you extract modifiers from a `KeyEvent`?

- Is there a `modifiers()` method?
- Is there a `getModifiers()` method?
- Are modifiers stored as an int bitmask?
- Are modifiers stored as separate boolean fields?
- Do you need to check modifier keys separately via GLFW?

**QUESTION 1.2.5**: Does `KeyEvent` have any methods for:

- Checking if Ctrl is pressed?
- Checking if Shift is pressed?
- Checking if Alt is pressed?
- If yes, what are the exact method names?

### 1.3 MouseButtonEvent Class Structure

**QUESTION 1.3.1**: What is the COMPLETE class signature of `MouseButtonEvent` in 1.21.9?

```java
// Example - fill in the actual signature:
public class MouseButtonEvent extends ??? implements ??? {
    // What fields does it have?
    // What methods does it have?
}
```

**QUESTION 1.3.2**: How do you extract the mouse button from a `MouseButtonEvent`?

- Is it `event.button()`?
- Is it `event.getButton()`?
- Something else?

**QUESTION 1.3.3**: How do you extract mouse X coordinate from a `MouseButtonEvent`?

- Method name?
- Or is it passed separately?

**QUESTION 1.3.4**: How do you extract mouse Y coordinate from a `MouseButtonEvent`?

- Method name?
- Or is it passed separately?

**QUESTION 1.3.5**: Does `MouseButtonEvent` include modifier key information?

- If yes, how do you access it?

### 1.4 InputWithModifiers Class Structure

**QUESTION 1.4.1**: What is the COMPLETE class/interface signature of `InputWithModifiers` in 1.21.9?

```java
// Is it a class or interface?
public ??? InputWithModifiers ??? {
    // What methods does it define?
}
```

**QUESTION 1.4.2**: What is `InputWithModifiers` used for?

- Is it passed to `onPress()` methods?
- Is it a wrapper for input events?
- Is it a utility class?

**QUESTION 1.4.3**: How do you extract information from `InputWithModifiers`?

- What methods are available?
- What data does it contain?

---

## 2. WINDOW & GLFW ACCESS

### 2.1 Window Class Changes

**QUESTION 2.1.1**: In Minecraft 1.21.9, what is the EXACT way to get the GLFW window handle (long)?

```java
// OLD (1.21.8):
long window = Minecraft.getInstance().getWindow().getWindow();

// NEW (1.21.9) - which one is correct?
// Option A:
long window = Minecraft.getInstance().getWindow().getHandle();
// Option B:
long window = Minecraft.getInstance().getWindow().window;
// Option C:
long window = Minecraft.getInstance().getWindow().getWindowHandle();
// Option D: Something completely different?
```

**QUESTION 2.1.2**: Is the window handle:

- A public field?
- Accessed via a getter method?
- Accessed via a different class?
- No longer directly accessible?

**QUESTION 2.1.3**: If the window handle is not directly accessible, what is the recommended way to:

- Check if a key is pressed via GLFW?
- Check modifier key states?
- Perform low-level input queries?

**QUESTION 2.1.4**: Has the `Window` class been moved to a different package in 1.21.9?

- If yes, what is the new package?

**QUESTION 2.1.5**: Are there any new methods on the `Window` class for:

- Querying key states?
- Querying modifier states?
- If yes, what are they?

### 2.2 GLFW Direct Access

**QUESTION 2.2.1**: Is it still safe/recommended to use GLFW directly in 1.21.9?

```java
GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL)
```

**QUESTION 2.2.2**: Should we use Minecraft's input abstraction instead?

- If yes, what classes/methods should be used?

**QUESTION 2.2.3**: For modifier key detection, should we:

- Continue using GLFW directly?
- Use methods on the input event objects?
- Use a new Minecraft API?

---

## 3. KEYMAPPING & CATEGORY SYSTEM

### 3.1 KeyMapping.Category Changes

**QUESTION 3.1.1**: In Minecraft 1.21.9, what is the EXACT way to create a KeyMapping category?

```java
// OLD (1.21.8):
private static final String CATEGORY = "slot-click-macros.general";

// NEW (1.21.9) - which one is correct?
// Option A:
private static final KeyMapping.Category CATEGORY =
    new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath("slot-click-macros", "general"));
// Option B:
private static final String CATEGORY = "slot-click-macros.general";
// Option C: Something else?
```

**QUESTION 3.1.2**: What is the type of the category parameter in `KeyMapping` constructor in 1.21.9?

- Is it `String`?
- Is it `KeyMapping.Category`?
- Is it `ResourceLocation`?
- Is it `Component`?

**QUESTION 3.1.3**: If `KeyMapping.Category` is a class/record, what is its COMPLETE signature?

```java
// Fill in the actual structure:
public ??? KeyMapping.Category ??? {
    // Constructor?
    // Fields?
    // Methods?
}
```

**QUESTION 3.1.4**: How do you create a custom category that appears in Minecraft's controls menu?

- Exact code example needed

### 3.2 KeyMapping Constructor

**QUESTION 3.2.1**: What is the EXACT signature of the `KeyMapping` constructor in 1.21.9?

```java
// Provide the complete constructor signature(s):
public KeyMapping(???) {
}
```

**QUESTION 3.2.2**: Have any parameters changed order or type?

- List all parameters in order with their types

**QUESTION 3.2.3**: Are there multiple constructor overloads?

- If yes, list all of them

---

## 4. GUI COMPONENT INPUT HANDLING

### 4.1 AbstractButton Changes

**QUESTION 4.1.1**: What is the EXACT signature of the `onPress()` method in `AbstractButton` in 1.21.9?

```java
// OLD (1.21.8):
public abstract void onPress();

// NEW (1.21.9):
public abstract void onPress(???);
```

**QUESTION 4.1.2**: If `onPress()` now takes a parameter:

- What is the parameter type?
- What is the parameter name?
- What information does it contain?
- Is it nullable?

**QUESTION 4.1.3**: What is the EXACT signature of `keyPressed()` in `AbstractButton` in 1.21.9?

```java
// OLD (1.21.8):
public boolean keyPressed(int keyCode, int scanCode, int modifiers)

// NEW (1.21.9):
public boolean keyPressed(???)
```

**QUESTION 4.1.4**: What is the EXACT signature of `mouseClicked()` in `AbstractButton` in 1.21.9?

```java
// OLD (1.21.8):
public boolean mouseClicked(double mouseX, double mouseY, int button)

// NEW (1.21.9):
public boolean mouseClicked(???)
```

**QUESTION 4.1.5**: When overriding these methods in a custom button class:

- Do we need to call `super.keyPressed()`?
- If yes, how do we pass the event object to super?
- What does super return?

**QUESTION 4.1.6**: Are there any new abstract methods in `AbstractButton` that must be implemented?

### 4.2 AbstractWidget Changes

**QUESTION 4.2.1**: What is the EXACT signature of `keyPressed()` in `AbstractWidget` in 1.21.9?

**QUESTION 4.2.2**: What is the EXACT signature of `mouseClicked()` in `AbstractWidget` in 1.21.9?

**QUESTION 4.2.3**: Are there any new input-related methods in `AbstractWidget`?

- List all new methods related to input handling

### 4.3 GuiEventListener Interface

**QUESTION 4.3.1**: Has the `GuiEventListener` interface changed in 1.21.9?

**QUESTION 4.3.2**: What are the EXACT method signatures in `GuiEventListener` for 1.21.9?

```java
public interface GuiEventListener {
    // List ALL methods with exact signatures
}
```

**QUESTION 4.3.3**: Do custom GUI components need to implement any new methods?

---

## 5. INPUTCONSTANTS API CHANGES

### 5.1 InputConstants.getKey() Method

**QUESTION 5.1.1**: What are ALL the overloads of `InputConstants.getKey()` in 1.21.9?

```java
// OLD (1.21.8):
InputConstants.getKey(int keyCode, int scanCode)

// NEW (1.21.9) - list ALL overloads:
// Overload 1:
public static ??? getKey(???)
// Overload 2:
public static ??? getKey(???)
// etc.
```

**QUESTION 5.1.2**: How do you get an `InputConstants.Key` from:

- A GLFW key code (int)?
- A scan code (int)?
- A `KeyEvent` object?
- A key name (String)?

**QUESTION 5.1.3**: In the default case of our switch statement for key names:

```java
default -> InputConstants.getKey(keyCode, -1).getDisplayName().getString();
```

What is the correct way to write this in 1.21.9?

**QUESTION 5.1.4**: Does `InputConstants.Key` still have:

- `getDisplayName()` method?
- If yes, what does it return?
- If no, what's the replacement?

### 5.2 InputConstants.Type

**QUESTION 5.2.1**: Has `InputConstants.Type` changed in 1.21.9?

**QUESTION 5.2.2**: Are `InputConstants.Type.KEYSYM` and `InputConstants.Type.MOUSE` still valid?

---

## 6. CLOTH CONFIG INTEGRATION

### 6.1 AbstractConfigListEntry Changes

**QUESTION 6.1.1**: What is the EXACT signature of `mouseClicked()` in `AbstractConfigListEntry` in Cloth Config 20.0.149?

```java
// Provide exact signature:
public boolean mouseClicked(???)
```

**QUESTION 6.1.2**: What is the EXACT signature of `keyPressed()` in `AbstractConfigListEntry` in Cloth Config 20.0.149?

```java
// Provide exact signature:
public boolean keyPressed(???)
```

**QUESTION 6.1.3**: When we override these methods in our custom entry classes:

- How do we call super?
- How do we pass events to child widgets?
- Example code needed

**QUESTION 6.1.4**: Does Cloth Config 20.0.149 provide any helper methods or utilities for:

- Converting between old and new input event formats?
- Handling input events in custom entries?

### 6.2 ElementEntry Changes

**QUESTION 6.2.1**: What is the EXACT signature of `mouseClicked()` in `ElementEntry` in Cloth Config 20.0.149?

**QUESTION 6.2.2**: What is the EXACT signature of `keyPressed()` in `ElementEntry` in Cloth Config 20.0.149?

**QUESTION 6.2.3**: Our `MacroListEntry` extends `AbstractConfigListEntry` - what methods must we override?

**QUESTION 6.2.4**: Our `KeybindConfigEntry` extends `AbstractConfigListEntry` - what methods must we override?

### 6.3 Cloth Config Compatibility

**QUESTION 6.3.1**: Is Cloth Config 20.0.149 fully compatible with Minecraft 1.21.9?

- Are there any known issues?
- Are there any workarounds needed?

**QUESTION 6.3.2**: Does Cloth Config provide example code for custom entries in 1.21.9?

- If yes, where can we find it?

---

## 7. ABSTRACTBUTTON IMPLEMENTATION

### 7.1 KeybindButton Class

Our `KeybindButton` extends `AbstractButton` and needs to:

1. Listen for key presses to capture keybinds
2. Handle mouse clicks
3. Display the current keybind
4. Support modifier keys (Ctrl, Shift, Alt)

**QUESTION 7.1.1**: Complete implementation of `onPress()` for 1.21.9:

```java
@Override
public void onPress(/* what parameter? */) {
    if (listening) {
        listening = false;
        pressedKeys.clear();
        updateMessage();
    } else {
        listening = true;
        pressedKeys.clear();
        setMessage(Component.literal("§ePress any key..."));
    }
    // Do we need to do anything with the parameter?
}
```

**QUESTION 7.1.2**: Complete implementation of `keyPressed()` for 1.21.9:

```java
@Override
public boolean keyPressed(/* what parameters? */) {
    if (!listening) {
        return super.keyPressed(/* how to pass parameters? */);
    }

    // How do we extract keyCode from the event?
    int keyCode = ???;

    // How do we check for Escape key?
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
        keybind = "";
        listening = false;
        pressedKeys.clear();
        onChange.accept("");
        updateMessage();
        return true;
    }

    // How do we check modifier keys?
    // Can we still use GLFW directly?
    // Or should we use methods on the event object?
    boolean hasCtrl = ???;
    boolean hasShift = ???;
    boolean hasAlt = ???;

    // Rest of implementation...
    return true;
}
```

**QUESTION 7.1.3**: Complete implementation of `mouseClicked()` for 1.21.9:

```java
@Override
public boolean mouseClicked(/* what parameters? */) {
    if (!listening) {
        return super.mouseClicked(/* how to pass parameters? */);
    }

    // How do we extract button from the event?
    int button = ???;

    // Right click cancels
    if (button == 1) {
        listening = false;
        pressedKeys.clear();
        updateMessage();
        return true;
    }

    return super.mouseClicked(/* how to pass parameters? */);
}
```

**QUESTION 7.1.4**: For the `getKeyName()` method default case:

```java
default -> InputConstants.getKey(keyCode, -1).getDisplayName().getString();
```

What is the correct 1.21.9 version?

---

## 8. SCREEN INPUT HANDLING

### 8.1 Screen Class Changes

**QUESTION 8.1.1**: What is the EXACT signature of `keyPressed()` in `Screen` class in 1.21.9?

```java
// OLD (1.21.8):
public boolean keyPressed(int keyCode, int scanCode, int modifiers)

// NEW (1.21.9):
public boolean keyPressed(???)
```

**QUESTION 8.1.2**: What is the EXACT signature of `mouseClicked()` in `Screen` class in 1.21.9?

**QUESTION 8.1.3**: Are there any new input-related methods in `Screen` that we should override?

### 8.2 SaveMacroScreen Implementation

Our `SaveMacroScreen` extends `Screen` and needs to handle Enter and Escape keys.

**QUESTION 8.2.1**: Complete implementation of `keyPressed()` for SaveMacroScreen in 1.21.9:

```java
@Override
public boolean keyPressed(/* what parameters? */) {
    // How do we extract keyCode?
    int keyCode = ???;

    // Enter key saves
    if (keyCode == 257 || keyCode == 335) { // ENTER or NUMPAD_ENTER
        saveMacro();
        return true;
    }

    // Escape cancels
    if (keyCode == 256) { // ESCAPE
        MacroRecorder.getInstance().finishRecording();
        this.minecraft.setScreen(parent);
        return true;
    }

    return super.keyPressed(/* how to pass parameters? */);
}
```

**QUESTION 8.2.2**: Do we need to handle any other input methods in SaveMacroScreen?

---

## 9. EVENT PROPAGATION & CONSUMPTION

### 9.1 Event Consumption Model

**QUESTION 9.1.1**: In 1.21.9, how does event consumption work?

- Do methods return boolean to indicate consumption?
- Is there a separate consumption mechanism?
- Has this changed from 1.21.8?

**QUESTION 9.1.2**: When we have nested components (e.g., KeybindButton inside MacroListEntry):

- How do we properly propagate events?
- How do we prevent event bubbling when needed?
- Example code needed

**QUESTION 9.1.3**: The `mouseClicked()` signature appears to have a `boolean consumed` parameter:

```java
public boolean mouseClicked(MouseButtonEvent event, boolean consumed)
```

- What does the `consumed` parameter mean?
- When should we check it?
- When should we pass `true` vs `false` to child components?

### 9.2 MacroListEntry Event Handling

**QUESTION 9.2.1**: Complete implementation of `mouseClicked()` for MacroListEntry in 1.21.9:

```java
@Override
public boolean mouseClicked(/* what parameters? */) {
    // How do we pass the event to keybindButton?
    if (keybindButton != null && keybindButton.mouseClicked(/* what to pass? */)) {
        return true;
    }
    // How do we pass the event to deleteButton?
    if (deleteButton != null && deleteButton.mouseClicked(/* what to pass? */)) {
        return true;
    }
    return super.mouseClicked(/* what to pass? */);
}
```

**QUESTION 9.2.2**: Complete implementation of `keyPressed()` for MacroListEntry in 1.21.9:

```java
@Override
public boolean keyPressed(/* what parameters? */) {
    if (keybindButton != null && keybindButton.isListening()) {
        return keybindButton.keyPressed(/* what to pass? */);
    }
    return super.keyPressed(/* what to pass? */);
}
```

### 9.3 KeybindConfigEntry Event Handling

**QUESTION 9.3.1**: Complete implementation of `mouseClicked()` for KeybindConfigEntry in 1.21.9:

```java
@Override
public boolean mouseClicked(/* what parameters? */) {
    if (this.button.mouseClicked(/* what to pass? */)) {
        return true;
    }
    return super.mouseClicked(/* what to pass? */);
}
```

**QUESTION 9.3.2**: Complete implementation of `keyPressed()` for KeybindConfigEntry in 1.21.9:

```java
@Override
public boolean keyPressed(/* what parameters? */) {
    if (button.isListening()) {
        return button.keyPressed(/* what to pass? */);
    }
    return super.keyPressed(/* what to pass? */);
}
```

---

## 10. MODIFIER KEY DETECTION

### 10.1 Modifier Key Access Methods

**QUESTION 10.1.1**: In 1.21.9, what is the BEST way to check if Ctrl is pressed?

```java
// Option A: GLFW direct (if window handle is accessible)
boolean hasCtrl = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                  GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;

// Option B: From event object
boolean hasCtrl = event.hasControlDown(); // or similar?

// Option C: From InputWithModifiers
boolean hasCtrl = input.hasControlDown(); // or similar?

// Option D: Something else?
```

**QUESTION 10.1.2**: Same question for Shift key - what's the best way?

**QUESTION 10.1.3**: Same question for Alt key - what's the best way?

**QUESTION 10.1.4**: If using event objects for modifier detection:

- What are the EXACT method names?
- Are they available on `KeyEvent`?
- Are they available on `MouseButtonEvent`?
- Are they available on `InputWithModifiers`?

### 10.2 KeybindManager Modifier Detection

Our `KeybindManager` has two methods that check modifiers:

1. `isKeybindPressed()` - checks if a keybind with modifiers is pressed
2. `isKeybindStringPressed()` - checks raw keyboard state

**QUESTION 10.2.1**: For `isKeybindPressed()` method, complete the implementation:

```java
public boolean isKeybindPressed(String keybindString, KeyMapping keyMapping) {
    if (!keyMapping.isDown()) {
        return false;
    }

    boolean needsCtrl = keybindString.toUpperCase().contains("CTRL+");
    boolean needsShift = keybindString.toUpperCase().contains("SHIFT+");
    boolean needsAlt = keybindString.toUpperCase().contains("ALT+");

    // How do we check actual modifier states in 1.21.9?
    boolean hasCtrl = ???;
    boolean hasShift = ???;
    boolean hasAlt = ???;

    return (needsCtrl == hasCtrl) && (needsShift == hasShift) && (needsAlt == hasAlt);
}
```

**QUESTION 10.2.2**: For `isKeybindStringPressed()` method, complete the implementation:

```java
public boolean isKeybindStringPressed(String keybindString) {
    if (keybindString == null || keybindString.isEmpty()) {
        return false;
    }

    // How do we check modifier states in 1.21.9?
    boolean hasCtrl = ???;
    boolean hasShift = ???;
    boolean hasAlt = ???;

    // How do we check if main key is pressed?
    int keyCode = getKeyCodeFromName(keyName);
    boolean keyPressed = ???;

    return keyPressed && (needsCtrl == hasCtrl) && (needsShift == hasShift) && (needsAlt == hasAlt);
}
```

**QUESTION 10.2.3**: Should these methods be rewritten to use a different approach in 1.21.9?

- If yes, what approach should we use?

---

## 11. KEYBIND REGISTRATION & MANAGEMENT

### 11.1 Fabric KeyBindingHelper

**QUESTION 11.1.1**: Has `KeyBindingHelper.registerKeyBinding()` changed in Fabric API for 1.21.9?

**QUESTION 11.1.2**: What is the EXACT signature of `KeyBindingHelper.registerKeyBinding()` in 1.21.9?

**QUESTION 11.1.3**: Are there any new requirements or best practices for registering keybinds in 1.21.9?

### 11.2 KeyMapping Usage

**QUESTION 11.2.1**: Has the `KeyMapping.isDown()` method changed in 1.21.9?

**QUESTION 11.2.2**: Are there any new methods on `KeyMapping` for:

- Checking if pressed this tick?
- Checking with modifiers?
- Getting the bound key?

**QUESTION 11.2.3**: Our mod registers keybinds dynamically for macros:

```java
KeyMapping keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
    "key.slotclickmacros.macro." + macroName,
    InputConstants.Type.KEYSYM,
    keyCode,
    CATEGORY  // What type should this be?
));
```

Is this still the correct approach in 1.21.9?

---

## 12. BACKWARD COMPATIBILITY STRATEGY

### 12.1 Version Detection

**QUESTION 12.1.1**: Should we use version-specific code paths?

```java
if (minecraftVersion >= 1.21.9) {
    // New input system
} else {
    // Old input system
}
```

Or should we maintain separate branches?

**QUESTION 12.1.2**: Can we detect at runtime which input system is in use?

**QUESTION 12.1.3**: Is there a way to write code that works with both systems?

- Using reflection?
- Using abstraction layers?
- Using Fabric API features?

### 12.2 Build Configuration

**QUESTION 12.2.1**: Should we have separate source sets for different Minecraft versions?

**QUESTION 12.2.2**: Can Gradle handle conditional compilation based on Minecraft version?

**QUESTION 12.2.3**: What's the recommended approach for supporting multiple Minecraft versions with breaking API changes?

---

## 13. TESTING & VALIDATION

### 13.1 Compilation Testing

**QUESTION 13.1.1**: After implementing all changes, what are the expected compilation results?

- Should compile with 0 errors?
- Are there any expected warnings?

**QUESTION 13.1.2**: Are there any deprecated APIs we should avoid?

### 13.2 Runtime Testing

**QUESTION 13.2.1**: What are the critical test cases for input handling?

1. Pressing a key in KeybindButton while listening
2. Clicking a button
3. Using modifier keys (Ctrl+Key, Shift+Key, Alt+Key)
4. Pressing Enter/Escape in SaveMacroScreen
5. Clicking buttons in MacroListEntry
6. What else?

**QUESTION 13.2.2**: Are there any known issues or edge cases with the new input system?

**QUESTION 13.2.3**: Are there any performance considerations with the new input system?

---

## 14. COMPLETE CODE EXAMPLES

### 14.1 Working KeybindButton Example

**QUESTION 14.1.1**: Provide a COMPLETE, WORKING implementation of KeybindButton for 1.21.9:

```java
package com.helixcraft.slotclickmacros.gui;

// What imports are needed?
import ???;

public class KeybindButton extends AbstractButton {
    private String keybind;
    private boolean listening = false;
    private final Consumer<String> onChange;

    public KeybindButton(int x, int y, int width, int height, String initialKeybind, Consumer<String> onChange) {
        super(x, y, width, height, Component.literal(initialKeybind.isEmpty() ? "Set Keybind" : initialKeybind));
        this.keybind = initialKeybind;
        this.onChange = onChange;
    }

    @Override
    public void onPress(/* FILL IN COMPLETE SIGNATURE */) {
        // FILL IN COMPLETE IMPLEMENTATION
    }

    @Override
    public boolean keyPressed(/* FILL IN COMPLETE SIGNATURE */) {
        // FILL IN COMPLETE IMPLEMENTATION
    }

    @Override
    public boolean mouseClicked(/* FILL IN COMPLETE SIGNATURE */) {
        // FILL IN COMPLETE IMPLEMENTATION
    }

    private String getKeyName(int keyCode) {
        return switch (keyCode) {
            case GLFW.GLFW_KEY_A -> "A";
            // ... other cases ...
            default -> /* FILL IN CORRECT WAY TO GET KEY NAME */;
        };
    }

    // Other methods...
}
```

### 14.2 Working SaveMacroScreen Example

**QUESTION 14.2.1**: Provide a COMPLETE, WORKING implementation of SaveMacroScreen.keyPressed() for 1.21.9:

```java
@Override
public boolean keyPressed(/* FILL IN COMPLETE SIGNATURE */) {
    // FILL IN COMPLETE IMPLEMENTATION
}
```

### 14.3 Working MacroListEntry Example

**QUESTION 14.3.1**: Provide COMPLETE, WORKING implementations for MacroListEntry in 1.21.9:

```java
@Override
public boolean mouseClicked(/* FILL IN COMPLETE SIGNATURE */) {
    // FILL IN COMPLETE IMPLEMENTATION
}

@Override
public boolean keyPressed(/* FILL IN COMPLETE SIGNATURE */) {
    // FILL IN COMPLETE IMPLEMENTATION
}
```

### 14.4 Working KeybindConfigEntry Example

**QUESTION 14.4.1**: Provide COMPLETE, WORKING implementations for KeybindConfigEntry in 1.21.9:

```java
@Override
public boolean mouseClicked(/* FILL IN COMPLETE SIGNATURE */) {
    // FILL IN COMPLETE IMPLEMENTATION
}

@Override
public boolean keyPressed(/* FILL IN COMPLETE SIGNATURE */) {
    // FILL IN COMPLETE IMPLEMENTATION
}
```

### 14.5 Working KeybindManager Example

**QUESTION 14.5.1**: Provide COMPLETE, WORKING implementation for KeybindManager in 1.21.9:

```java
// How to create category?
private static final ??? CATEGORY = ???;

// How to register keybind?
public KeybindManager() {
    openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
        "key.slot-click-macros.open_config",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        CATEGORY  // FILL IN CORRECT TYPE/VALUE
    ));
}

// How to check modifiers?
public boolean isKeybindPressed(String keybindString, KeyMapping keyMapping) {
    // FILL IN COMPLETE IMPLEMENTATION
}

public boolean isKeybindStringPressed(String keybindString) {
    // FILL IN COMPLETE IMPLEMENTATION
}
```

---

## 15. ADDITIONAL CRITICAL QUESTIONS

### 15.1 Documentation & Resources

**QUESTION 15.1.1**: Where can we find official documentation for the 1.21.9 input system changes?

- Mojang changelog?
- Fabric wiki?
- Community resources?

**QUESTION 15.1.2**: Are there any example mods that have successfully migrated to 1.21.9?

- If yes, which ones?
- Where can we find their source code?

**QUESTION 15.1.3**: Are there any Fabric API examples for the new input system?

### 15.2 Known Issues & Workarounds

**QUESTION 15.2.1**: Are there any known bugs in Minecraft 1.21.9's input system?

**QUESTION 15.2.2**: Are there any known incompatibilities between:

- Minecraft 1.21.9
- Fabric Loader 0.15.11
- Fabric API 0.134.1+1.21.9
- Cloth Config 20.0.149

**QUESTION 15.2.3**: Are there any workarounds needed for specific issues?

### 15.3 Future Compatibility

**QUESTION 15.3.1**: Is the 1.21.9 input system expected to be stable?

- Will 1.21.10, 1.21.11 use the same system?
- Or are further changes expected?

**QUESTION 15.3.2**: Should we design our code to be flexible for future changes?

### 15.4 Alternative Approaches

**QUESTION 15.4.1**: Instead of using GUI components for keybind input, should we:

- Use a different approach?
- Use a library?
- Use Fabric API utilities?

**QUESTION 15.4.2**: Are there any Fabric API utilities for:

- Keybind configuration screens?
- Input event handling?
- Modifier key detection?

**QUESTION 15.4.3**: Should we consider using a different config library instead of Cloth Config?

- If yes, which one?
- What are the pros/cons?

---

## 16. SPECIFIC ERROR RESOLUTION

### 16.1 Current Compilation Errors

We currently have 24 compilation errors. Let's address each category:

**ERROR CATEGORY 1: "method does not override" (8 errors)**
Files affected:

- KeybindButton.java: `onPress()`, `keyPressed()`, `mouseClicked()`
- SaveMacroScreen.java: `keyPressed()`
- MacroListEntry.java: `mouseClicked()`, `keyPressed()`
- KeybindConfigEntry.java: `mouseClicked()`, `keyPressed()`

**QUESTION 16.1.1**: For EACH of these 8 methods, provide:

- The EXACT signature that should be used
- Whether @Override annotation should be kept or removed
- Complete working implementation

**ERROR CATEGORY 2: "incompatible types" (10 errors)**
Related to passing parameters to super methods and child components.

**QUESTION 16.1.2**: For EACH location where we call:

- `super.keyPressed(...)`
- `super.mouseClicked(...)`
- `button.keyPressed(...)`
- `button.mouseClicked(...)`

Provide the EXACT parameters that should be passed.

**ERROR CATEGORY 3: "symbol not found" (6 errors)**

- Window.window field not accessible (3 errors)
- InputConstants.getKey() wrong signature (1 error)
- KeyMapping.Category type mismatch (2 errors)

**QUESTION 16.1.3**: For Window.window access:

```java
// Current (not working):
long window = Minecraft.getInstance().getWindow().window;

// What should it be?
long window = ???;
```

**QUESTION 16.1.4**: For InputConstants.getKey():

```java
// Current (not working):
InputConstants.getKey(keyCode, -1).getDisplayName().getString()

// What should it be?
???
```

**QUESTION 16.1.5**: For KeyMapping.Category:

```java
// Current (not working):
private static final String CATEGORY = "slot-click-macros.general";
// Used in:
new KeyMapping("key...", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY)

// What should it be?
private static final ??? CATEGORY = ???;
```

---

## 17. STEP-BY-STEP MIGRATION PLAN

Once all questions are answered, we will follow this plan:

### Step 1: Update Imports

- Add correct imports for new input event classes
- Remove any obsolete imports

### Step 2: Fix KeybindManager

- Fix CATEGORY declaration
- Fix window handle access
- Fix modifier key detection methods

### Step 3: Fix KeybindButton

- Fix onPress() signature and implementation
- Fix keyPressed() signature and implementation
- Fix mouseClicked() signature and implementation
- Fix getKeyName() default case

### Step 4: Fix SaveMacroScreen

- Fix keyPressed() signature and implementation

### Step 5: Fix MacroListEntry

- Fix mouseClicked() signature and implementation
- Fix keyPressed() signature and implementation

### Step 6: Fix KeybindConfigEntry

- Fix mouseClicked() signature and implementation
- Fix keyPressed() signature and implementation

### Step 7: Compile and Test

- Run ./gradlew build
- Fix any remaining errors
- Test in-game

### Step 8: Verify All Features

- Test keybind capture
- Test modifier keys
- Test macro execution
- Test config screen
- Test save dialog

---

## 18. DECOMPILATION INSTRUCTIONS

If you need to decompile Minecraft 1.21.9 to answer these questions:

**QUESTION 18.1**: Can you decompile the following classes and provide their complete source?

1. `net.minecraft.client.gui.components.AbstractButton`
2. `net.minecraft.client.gui.screens.Screen`
3. `net.minecraft.client.gui.components.AbstractWidget`
4. `net.minecraft.client.Window`
5. `net.minecraft.client.KeyMapping`
6. `com.mojang.blaze3d.platform.InputConstants`

**QUESTION 18.2**: Can you search the decompiled code for:

- Classes with "Event" in the name related to input
- Classes with "Input" in the name
- Any new input-related interfaces

**QUESTION 18.3**: Can you provide the package structure for all input-related classes?

---

## 19. FABRIC API SPECIFIC QUESTIONS

### 19.1 Fabric API Version

**QUESTION 19.1.1**: Is Fabric API 0.134.1+1.21.9 the correct version for Minecraft 1.21.9?

**QUESTION 19.1.2**: Are there any newer versions available?

**QUESTION 19.1.3**: Does Fabric API provide any input system abstractions?

### 19.2 Fabric Keybinding API

**QUESTION 19.2.1**: Has the Fabric keybinding API changed for 1.21.9?

**QUESTION 19.2.2**: Are there any new Fabric API features for:

- Input event handling?
- Keybind management?
- Modifier key detection?

**QUESTION 19.2.3**: Does Fabric API provide compatibility layers for input system changes?

---

## 20. MOJANG MAPPINGS QUESTIONS

### 20.1 Mapping Completeness

**QUESTION 20.1.1**: Are Mojang mappings complete for Minecraft 1.21.9?

**QUESTION 20.1.2**: Are there any unmapped classes related to input?

**QUESTION 20.1.3**: Should we use intermediary mappings instead?

### 20.2 Mapping Changes

**QUESTION 20.2.1**: Have any input-related classes been renamed in Mojang mappings for 1.21.9?

**QUESTION 20.2.2**: Have any input-related methods been renamed?

**QUESTION 20.2.3**: Is there a mapping changelog we can reference?

---

## 21. PERFORMANCE & OPTIMIZATION

### 21.1 Input Event Performance

**QUESTION 21.1.1**: Are there any performance implications of the new input system?

**QUESTION 21.1.2**: Should we cache any input-related objects?

**QUESTION 21.1.3**: Are there any best practices for efficient input handling in 1.21.9?

### 21.2 GLFW Direct Access

**QUESTION 21.2.1**: Is direct GLFW access slower than using Minecraft's input abstraction?

**QUESTION 21.2.2**: Should we minimize GLFW calls?

**QUESTION 21.2.3**: Are there any threading concerns with GLFW access?

---

## 22. EDGE CASES & SPECIAL SCENARIOS

### 22.1 Multiple Modifier Keys

**QUESTION 22.1.1**: How do we handle keybinds with multiple modifiers?

- Example: Ctrl+Shift+R
- Example: Ctrl+Alt+Shift+F1

**QUESTION 22.1.2**: Is there a maximum number of modifiers supported?

**QUESTION 22.1.3**: How do we display multi-modifier keybinds to the user?

### 22.2 Mouse Button Keybinds

**QUESTION 22.2.1**: Can we bind macros to mouse buttons?

- If yes, how?
- What's the integration with MouseButtonEvent?

**QUESTION 22.2.2**: Can we combine mouse buttons with modifiers?

- Example: Ctrl+MiddleClick

### 22.3 Special Keys

**QUESTION 22.3.1**: How do we handle special keys?

- Numpad keys
- Media keys
- Function keys beyond F12

**QUESTION 22.3.2**: Are there any keys that cannot be bound?

**QUESTION 22.3.3**: How do we handle international keyboard layouts?

### 22.4 Focus & Context

**QUESTION 22.4.1**: When should input events be processed?

- Only when our GUI is open?
- Only when game is focused?
- Always?

**QUESTION 22.4.2**: How do we check if our GUI has focus?

**QUESTION 22.4.3**: How do we prevent conflicts with Minecraft's built-in keybinds?

---

## 23. ERROR HANDLING & VALIDATION

### 23.1 Invalid Input Handling

**QUESTION 23.1.1**: What happens if we receive an invalid KeyEvent?

- Should we validate it?
- Should we catch exceptions?

**QUESTION 23.1.2**: What happens if window handle is null/invalid?

- How do we handle this gracefully?

**QUESTION 23.1.3**: What happens if a keybind string is malformed?

- How do we validate keybind strings?

### 23.2 Null Safety

**QUESTION 23.2.1**: Can input event objects be null?

- KeyEvent?
- MouseButtonEvent?
- InputWithModifiers?

**QUESTION 23.2.2**: Can event fields be null?

- Key code?
- Button?
- Modifiers?

**QUESTION 23.2.3**: Should we add null checks everywhere?

---

## 24. DEBUGGING & TROUBLESHOOTING

### 24.1 Logging

**QUESTION 24.1.1**: What should we log for debugging input issues?

- Raw event objects?
- Extracted values?
- Event flow?

**QUESTION 24.1.2**: How do we log input events without spamming the console?

**QUESTION 24.1.3**: Are there any Minecraft debug modes for input?

### 24.2 Common Issues

**QUESTION 24.2.1**: What are common mistakes when migrating to the new input system?

**QUESTION 24.2.2**: What are common runtime errors related to input?

**QUESTION 24.2.3**: How do we debug input events not being received?

### 24.3 Testing Tools

**QUESTION 24.3.1**: Are there any tools for testing input handling?

**QUESTION 24.3.2**: Can we unit test input handling code?

**QUESTION 24.3.3**: How do we test modifier key combinations?

---

## 25. MIGRATION CHECKLIST

Once all questions are answered, verify:

### 25.1 Code Changes

- [ ] All imports updated
- [ ] All method signatures updated
- [ ] All super calls updated
- [ ] All event parameter extractions updated
- [ ] All modifier key checks updated
- [ ] All window handle accesses updated
- [ ] All InputConstants calls updated
- [ ] All KeyMapping.Category usages updated

### 25.2 Compilation

- [ ] 0 compilation errors
- [ ] 0 critical warnings
- [ ] All classes compile successfully

### 25.3 Functionality

- [ ] KeybindButton captures keys correctly
- [ ] KeybindButton captures modifiers correctly
- [ ] SaveMacroScreen responds to Enter/Escape
- [ ] MacroListEntry buttons work
- [ ] KeybindConfigEntry works
- [ ] Macros execute with correct keybinds
- [ ] Modifier key combinations work
- [ ] Config screen opens and closes

### 25.4 Testing

- [ ] Tested on Minecraft 1.21.9
- [ ] Tested on Minecraft 1.21.10 (if available)
- [ ] Tested on Minecraft 1.21.11 (if available)
- [ ] No crashes
- [ ] No errors in log
- [ ] All features work as expected

---

## 26. REFERENCE IMPLEMENTATIONS

### 26.1 Vanilla Minecraft Examples

**QUESTION 26.1.1**: Which vanilla Minecraft screens use the new input system?

- Provide examples of screens that handle keyboard input
- Provide examples of screens that handle mouse input

**QUESTION 26.1.2**: Which vanilla Minecraft buttons use the new input system?

- Provide examples we can reference

**QUESTION 26.1.3**: How does vanilla Minecraft handle keybind configuration?

- Can we reference the controls screen implementation?

### 26.2 Mod Examples

**QUESTION 26.2.1**: Which popular mods have updated to 1.21.9?

- Provide GitHub links if available

**QUESTION 26.2.2**: Are there any open-source mods with similar functionality?

- Mods with custom keybind configuration
- Mods with custom GUI buttons
- Mods with input event handling

**QUESTION 26.2.3**: Are there any Fabric example mods for 1.21.9?

---

## 27. ALTERNATIVE SOLUTIONS

### 27.1 Reflection-Based Approach

**QUESTION 27.1.1**: Could we use reflection to support both old and new input systems?

- Pros and cons?
- Performance implications?
- Maintenance burden?

**QUESTION 27.1.2**: Could we detect the input system at runtime and adapt?

### 27.2 Abstraction Layer

**QUESTION 27.2.1**: Should we create our own input abstraction layer?

```java
public interface InputEvent {
    int getKeyCode();
    int getScanCode();
    int getModifiers();
}

public class LegacyInputEvent implements InputEvent { ... }
public class ModernInputEvent implements InputEvent { ... }
```

**QUESTION 27.2.2**: Would this make future migrations easier?

**QUESTION 27.2.3**: What's the overhead of an abstraction layer?

### 27.3 Code Generation

**QUESTION 27.3.1**: Could we use code generation to create version-specific code?

**QUESTION 27.3.2**: Are there any Gradle plugins that help with multi-version support?

**QUESTION 27.3.3**: What's the best practice for maintaining multi-version mods?

---

## 28. CRITICAL PATH QUESTIONS

These are the MOST IMPORTANT questions that MUST be answered to proceed:

### 28.1 CRITICAL QUESTION #1: Input Event Classes

**WHERE ARE THE INPUT EVENT CLASSES?**

Provide the EXACT, COMPLETE answer:

```java
// Package and class for keyboard events:
package ???;
public class ??? { ... }

// Package and class for mouse events:
package ???;
public class ??? { ... }

// Package and class for input with modifiers:
package ???;
public class/interface ??? { ... }
```

### 28.2 CRITICAL QUESTION #2: Method Signatures

**WHAT ARE THE EXACT METHOD SIGNATURES?**

Provide COMPLETE signatures for:

```java
// AbstractButton.onPress():
public abstract void onPress(???);

// AbstractButton.keyPressed():
public boolean keyPressed(???);

// AbstractButton.mouseClicked():
public boolean mouseClicked(???);

// Screen.keyPressed():
public boolean keyPressed(???);

// AbstractConfigListEntry.keyPressed():
public boolean keyPressed(???);

// AbstractConfigListEntry.mouseClicked():
public boolean mouseClicked(???);
```

### 28.3 CRITICAL QUESTION #3: Window Handle Access

**HOW DO WE GET THE GLFW WINDOW HANDLE?**

Provide the EXACT code:

```java
long window = ???;
```

### 28.4 CRITICAL QUESTION #4: KeyMapping.Category

**HOW DO WE CREATE A KEYMAPPING CATEGORY?**

Provide the EXACT code:

```java
private static final ??? CATEGORY = ???;

// And how to use it:
new KeyMapping("key.name", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, ???);
```

### 28.5 CRITICAL QUESTION #5: Extracting Event Data

**HOW DO WE EXTRACT DATA FROM EVENTS?**

Provide EXACT code for:

```java
// From KeyEvent:
int keyCode = ???;
int scanCode = ???;
int modifiers = ???;

// From MouseButtonEvent:
int button = ???;
double mouseX = ???;
double mouseY = ???;

// Checking modifiers:
boolean hasCtrl = ???;
boolean hasShift = ???;
boolean hasAlt = ???;
```

---

## 29. COMPLETE FILE-BY-FILE REQUIREMENTS

### 29.1 KeybindButton.java Requirements

Provide COMPLETE, WORKING code for:

**REQUIREMENT 29.1.1**: All necessary imports

```java
package com.helixcraft.slotclickmacros.gui;

// List ALL imports needed:
import ???;
import ???;
// ... etc
```

**REQUIREMENT 29.1.2**: Complete onPress() method

```java
@Override  // Keep or remove?
public void onPress(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION
}
```

**REQUIREMENT 29.1.3**: Complete keyPressed() method

```java
@Override  // Keep or remove?
public boolean keyPressed(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION including:
    // - Extracting keyCode from event
    // - Checking for Escape/Backspace/Delete
    // - Checking modifier keys
    // - Building keybind string
    // - Calling super when not listening
}
```

**REQUIREMENT 29.1.4**: Complete mouseClicked() method

```java
@Override  // Keep or remove?
public boolean mouseClicked(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION
}
```

**REQUIREMENT 29.1.5**: Complete getKeyName() default case

```java
default -> /* EXACT CODE FOR 1.21.9 */;
```

### 29.2 SaveMacroScreen.java Requirements

**REQUIREMENT 29.2.1**: Complete keyPressed() method

```java
@Override  // Keep or remove?
public boolean keyPressed(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION
}
```

### 29.3 MacroListEntry.java Requirements

**REQUIREMENT 29.3.1**: Complete mouseClicked() method

```java
@Override  // Keep or remove?
public boolean mouseClicked(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION showing how to:
    // - Pass event to keybindButton
    // - Pass event to deleteButton
    // - Call super
}
```

**REQUIREMENT 29.3.2**: Complete keyPressed() method

```java
@Override  // Keep or remove?
public boolean keyPressed(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION
}
```

### 29.4 KeybindConfigEntry.java Requirements

**REQUIREMENT 29.4.1**: Complete mouseClicked() method

```java
@Override  // Keep or remove?
public boolean mouseClicked(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION
}
```

**REQUIREMENT 29.4.2**: Complete keyPressed() method

```java
@Override  // Keep or remove?
public boolean keyPressed(/* EXACT SIGNATURE */) {
    // COMPLETE IMPLEMENTATION
}
```

### 29.5 KeybindManager.java Requirements

**REQUIREMENT 29.5.1**: Complete CATEGORY declaration

```java
private static final ??? CATEGORY = ???;
```

**REQUIREMENT 29.5.2**: Complete isKeybindPressed() method

```java
public boolean isKeybindPressed(String keybindString, KeyMapping keyMapping) {
    // COMPLETE IMPLEMENTATION with correct window access and modifier checks
}
```

**REQUIREMENT 29.5.3**: Complete isKeybindStringPressed() method

```java
public boolean isKeybindStringPressed(String keybindString) {
    // COMPLETE IMPLEMENTATION with correct window access and modifier checks
}
```

---

## 30. FINAL VERIFICATION QUESTIONS

### 30.1 Completeness Check

**QUESTION 30.1.1**: After answering all questions above, will we have:

- [ ] All necessary imports?
- [ ] All correct method signatures?
- [ ] All correct super calls?
- [ ] All correct event data extraction?
- [ ] All correct modifier key checks?
- [ ] All correct window handle access?
- [ ] All correct InputConstants usage?
- [ ] All correct KeyMapping.Category usage?

**QUESTION 30.1.2**: Are there any other files that need changes?

- List any files not covered above

**QUESTION 30.1.3**: Are there any other classes/interfaces we need to know about?

### 30.2 Implementation Confidence

**QUESTION 30.2.1**: After answering all questions, can we implement the changes in ONE PASS?

- Without trial and error?
- Without multiple compilation attempts?
- With confidence that it will work?

**QUESTION 30.2.2**: Are there any remaining unknowns?

**QUESTION 30.2.3**: Are there any assumptions we're making that might be wrong?

### 30.3 Success Criteria

**QUESTION 30.3.1**: What does success look like?

- [ ] ./gradlew build completes with 0 errors
- [ ] Mod loads in Minecraft 1.21.9
- [ ] Config screen opens
- [ ] KeybindButton captures keys
- [ ] Macros execute
- [ ] No crashes
- [ ] No errors in log

**QUESTION 30.3.2**: What are the acceptance criteria for each feature?

**QUESTION 30.3.3**: How do we verify everything works correctly?

---

## 31. SUMMARY OF REQUIRED INFORMATION

To complete this migration, we need EXACT, COMPLETE answers for:

### 31.1 Core API Information

1. **Input Event Classes**: Package paths and class structures
2. **Method Signatures**: Exact signatures for all overridden methods
3. **Window Access**: Exact way to get GLFW window handle
4. **KeyMapping.Category**: Exact way to create and use categories
5. **Event Data Extraction**: Exact methods to get data from events

### 31.2 Implementation Details

6. **Modifier Key Detection**: Best approach for checking Ctrl/Shift/Alt
7. **Event Propagation**: How to pass events between components
8. **Super Calls**: How to call super methods with new signatures
9. **InputConstants**: Correct usage of getKey() and related methods
10. **Cloth Config**: Correct integration with AbstractConfigListEntry

### 31.3 Complete Code Examples

11. **KeybindButton**: Complete working implementation
12. **SaveMacroScreen**: Complete working implementation
13. **MacroListEntry**: Complete working implementation
14. **KeybindConfigEntry**: Complete working implementation
15. **KeybindManager**: Complete working implementation

---

## CONCLUSION

This document contains **300+ specific questions** covering every aspect of the Minecraft 1.21.9 input system migration.

Once ALL questions are answered with EXACT, COMPLETE information, the migration can be completed in a single implementation pass with confidence.

**NEXT STEPS:**

1. Answer ALL questions in this document
2. Provide COMPLETE code examples
3. Verify all information is accurate
4. Implement all changes
5. Test thoroughly

**ESTIMATED LINES OF ANSWERS NEEDED:** 2000+ lines of detailed answers, code examples, and explanations.

---

## APPENDIX A: Current Error Summary

**Total Errors:** 24

- Method override errors: 8
- Incompatible types errors: 10
- Symbol not found errors: 6

**Files Affected:** 5

1. KeybindButton.java
2. SaveMacroScreen.java
3. MacroListEntry.java
4. KeybindConfigEntry.java
5. KeybindManager.java

**Root Causes:**

1. Input event classes not found/imported
2. Method signatures changed
3. Window API changed
4. KeyMapping.Category type changed
5. InputConstants API changed

---

## APPENDIX B: Version Information

- **Minecraft Version:** 1.21.9
- **Fabric Loader:** 0.15.11
- **Fabric API:** 0.134.1+1.21.9
- **Cloth Config:** 20.0.149
- **Mod Menu:** 15.0.0-beta.3
- **Loom Version:** 1.15-SNAPSHOT
- **Java Version:** 21

---

## APPENDIX C: Contact & Resources

If you can provide answers to these questions, please include:

- Source of information (official docs, decompiled code, etc.)
- Confidence level (certain, likely, uncertain)
- Any caveats or known issues
- Links to relevant resources

**Thank you for helping complete this migration!**

---

**END OF DOCUMENT**
**Total Questions: 300+**
**Total Sections: 31**
**Total Lines: 1000+**
