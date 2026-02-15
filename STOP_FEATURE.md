# Stop Macro Feature - Implementierung

## Übersicht

Neue Funktionalität zum Stoppen von laufenden und gequeueten Macros über Command oder Keybind.

## Implementierte Features

### 1. `/slotmacro stop` Command ✓

**Datei**: `SlotMacroCommand.java`

**Funktionalität**:

- Stoppt alle laufenden Macros sofort
- Löscht alle gequeueten Macros
- Gibt Feedback über gestoppte Aktivität

**Verwendung**:

```
/slotmacro stop
```

**Ausgaben**:

- `§aStopped all macro playback and cleared queue` - Wenn etwas gestoppt wurde
- `§7No macros were running or queued` - Wenn nichts lief

### 2. Stop-Keybind in Config GUI ✓

**Datei**: `ModConfigScreen.java`

**GUI-Position**: General Settings Tab
**Typ**: KeybindConfigEntry (gleich wie Record Keybind)
**Label**: "Stop Macros Keybind"
**Beschreibung**: "Press this key to stop all running/queued macros"

**Config-Speicherung**:

- Feld: `stopKey` (String)
- Default: "" (leer = nicht gesetzt)
- Speicherung in `slot-click-macros.json`

### 3. MacroPlayer.stopAll() Methode ✓

**Datei**: `MacroPlayer.java`

**Funktionalität**:

```java
public boolean stopAll() {
    boolean hadActivity = !playbackQueue.isEmpty() || isPlaying;

    // Clear queue
    playbackQueue.clear();

    // Stop current playback
    if (isPlaying) {
        isPlaying = false;
    }

    return hadActivity; // true wenn etwas gestoppt wurde
}
```

**Rückgabewert**:

- `true` - Wenn Macros gestoppt wurden (Queue oder Playback aktiv)
- `false` - Wenn nichts lief

### 4. Keybind-Handler im Client ✓

**Datei**: `SlotClickMacrosClient.java`

**Funktionalität**:

- Prüft Stop-Keybind in jedem Client-Tick
- Debouncing (nur einmal pro Tastendruck)
- Zeigt Action-Bar-Nachricht an Spieler
- Loggt Stop-Aktion

**Code-Flow**:

```java
String stopKeybind = config.getStopKey();
if (!stopKeybind.isEmpty() && keybindManager.isKeybindStringPressed(stopKeybind)) {
    if (!stopKeyWasPressed) {
        stopKeyWasPressed = true;

        if (MacroPlayer.getInstance().stopAll()) {
            // Zeige "Stopped all playback"
        } else {
            // Zeige "No macros running"
        }
    }
} else {
    stopKeyWasPressed = false;
}
```

## Verwendungsszenarien

### Szenario 1: Macro läuft gerade

```
1. Macro wird abgespielt (Actions werden ausgeführt)
2. Spieler drückt Stop-Keybind (z.B. "X")
3. Macro stoppt sofort
4. Nachricht: "§c[Macro] Stopped all playback"
```

### Szenario 2: Macro ist gequeued

```
1. Spieler führt /slotmacro play test aus
2. Nachricht: "Queued 'test' - open a container to play"
3. Spieler ändert Meinung
4. Spieler führt /slotmacro stop aus
5. Queue wird geleert
6. Nachricht: "§aStopped all macro playback and cleared queue"
```

### Szenario 3: Nichts läuft

```
1. Keine Macros aktiv
2. Spieler drückt Stop-Keybind
3. Nachricht: "§7[Macro] No macros running"
```

### Szenario 4: Mehrere Macros gequeued

```
1. /slotmacro play macro1
2. /slotmacro play macro2
3. /slotmacro play macro3
4. Queue: [macro1, macro2, macro3]
5. /slotmacro stop
6. Queue: [] (alle gelöscht)
```

## Config-Struktur

### slot-click-macros.json

```json
{
  "defaultDelay": 50,
  "randomDelayRange": 50,
  "recordKey": "R",
  "stopKey": "X",
  "macroKeybinds": {
    "test_macro": "Ctrl+1"
  }
}
```

**Felder**:

- `stopKey`: Keybind zum Stoppen (z.B. "X", "Ctrl+S", "Shift+Q")
- Leer ("") = nicht gesetzt

## GUI-Darstellung

```
┌─────────────────────────────────────────┐
│ General Settings                        │
├─────────────────────────────────────────┤
│                                         │
│ Default Delay (ms)                      │
│ ├─────────●─────────┤ 50 ms           │
│                                         │
│ Random Delay Range (ms)                 │
│ ├──────●────────────┤ 0-50 ms         │
│                                         │
│ Record Keybind                          │
│ [ R ]  ← Click to change               │
│ Press this key to start/stop recording  │
│                                         │
│ Stop Macros Keybind                     │
│ [ X ]  ← Click to change               │
│ Press this key to stop all running/     │
│ queued macros                           │
└─────────────────────────────────────────┘
```

## Command-Hilfe

```
/slotmacro
→ Slot Click Macros Commands:
  /slotmacro play <name> - Queue a macro for playback
  /slotmacro stop - Stop all macro playback
  /slotmacro list - List all saved macros
  /slotmacro delete <name> - Delete a macro
```

## Testing

### Test 1: Command während Playback

```
1. Starte Minecraft
2. Zeichne Macro mit 10 Actions auf (langsam, z.B. 500ms Delay)
3. Spiele Macro ab
4. Während es läuft: /slotmacro stop
5. ✓ Macro sollte sofort stoppen
6. ✓ Nachricht: "Stopped all macro playback and cleared queue"
```

### Test 2: Keybind während Playback

```
1. Setze Stop-Keybind auf "X" in Config GUI
2. Spiele Macro ab
3. Drücke "X" während es läuft
4. ✓ Macro sollte sofort stoppen
5. ✓ Action-Bar: "§c[Macro] Stopped all playback"
```

### Test 3: Queue löschen

```
1. /slotmacro play macro1
2. /slotmacro play macro2
3. Noch keinen Container geöffnet (Macros in Queue)
4. /slotmacro stop
5. ✓ Queue sollte leer sein
6. Container öffnen → Keine Macros starten
```

### Test 4: Nichts läuft

```
1. Keine Macros aktiv
2. /slotmacro stop
3. ✓ Nachricht: "No macros were running or queued"
```

### Test 5: Keybind-Debouncing

```
1. Halte Stop-Keybind gedrückt
2. ✓ Sollte nur einmal triggern (nicht mehrfach)
3. Loslassen und erneut drücken
4. ✓ Sollte wieder triggern
```

## Logging

**Debug-Ausgaben**:

```
[INFO] Stop keybind pressed - stopped all macros
[INFO] Stopped all macro activity (queue cleared, playback stopped)
[INFO] Set stop keybind to: X
```

## Technische Details

### Thread-Safety

- `isPlaying` Flag wird auf `false` gesetzt
- Playback-Thread prüft `isPlaying` und beendet sich
- Queue wird mit `clear()` geleert (thread-safe ConcurrentLinkedQueue)

### Timing

- Stop ist **sofort** wirksam
- Aktuell laufende Action wird noch beendet
- Nächste Action wird nicht mehr ausgeführt

### State-Management

```
Vor Stop:
- isPlaying = true
- playbackQueue = [macro1, macro2]

Nach Stop:
- isPlaying = false
- playbackQueue = []
```

## Vorteile

1. **Notfall-Stop**: Schnelles Stoppen bei Problemen
2. **Queue-Management**: Löschen versehentlich gequeueter Macros
3. **Flexibilität**: Command UND Keybind verfügbar
4. **Feedback**: Klare Rückmeldung über Erfolg
5. **Einfach**: Keine Parameter nötig

## Build-Status

✓ Kompiliert erfolgreich
✓ Keine Fehler
✓ Nur harmlose Import-Warnungen
✓ Getestet mit `./gradlew build`

## Zusammenfassung

Die Mod hat jetzt zwei Wege, um Macros zu stoppen:

1. **Command**: `/slotmacro stop`
2. **Keybind**: Konfigurierbar in General Settings

Beide Methoden stoppen sofort alle laufenden Macros und löschen die Queue.
