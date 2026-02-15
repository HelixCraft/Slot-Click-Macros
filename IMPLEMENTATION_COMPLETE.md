# Server Menu Click Implementation - Complete

## Änderungen Zusammenfassung

### Problem

Die Mod konnte keine einfachen Klicks auf Items in Server-Menüs (z.B. Shop-GUIs, Teleport-Menüs) abspielen. Klicks wurden aufgezeichnet, aber beim Abspielen nicht korrekt an den Server gesendet.

### Root Cause

1. **Fehlender Button-Parameter**: Die ursprüngliche `MacroAction` speicherte nur `slotId` und `clickType`, aber nicht den `button`-Parameter (0=Linksklick, 1=Rechtsklick)
2. **Client-seitige Validierung**: `handleInventoryMouseClick()` führt Client-seitige Item-Validierung durch, die bei Server-Menüs fehlschlagen kann

## Implementierte Lösung

### 1. MacroAction erweitert (✓ Kompiliert)

**Datei**: `src/main/java/com/helixcraft/slotclickmacros/data/MacroAction.java`

```java
public record MacroAction(int slotId, MacroClickType clickType, int button)
```

**Änderungen**:

- Neuer Parameter `button` hinzugefügt
- Serialisierung: `slotId,clickType,button` (z.B. `12,PICKUP,0`)
- Rückwärtskompatibilität: Legacy-Format `slotId,clickType` wird unterstützt (Standard: button=0)

### 2. MacroRecorder aktualisiert (✓ Kompiliert)

**Datei**: `src/main/java/com/helixcraft/slotclickmacros/recording/MacroRecorder.java`

**Änderungen**:

- `recordAction()` speichert jetzt den `button`-Parameter
- `recordDragAction()` speichert ebenfalls den `button`-Parameter

### 3. MacroPlayer verbessert (✓ Kompiliert)

**Datei**: `src/main/java/com/helixcraft/slotclickmacros/playback/MacroPlayer.java`

**Änderungen**:

- `executeAction()` verwendet jetzt `action.button()` statt `getButtonData()`
- Korrekte Behandlung von:
  - **Linksklick**: button=0, ClickType.PICKUP
  - **Rechtsklick**: button=1, ClickType.PICKUP
  - **Shift-Klick**: button=0, ClickType.QUICK_MOVE
  - **Hotbar-Swaps**: button=0-8, ClickType.SWAP

## Wie es funktioniert

### Recording-Flow:

```
1. Spieler klickt auf Slot in Container
   ↓
2. AbstractContainerMenuMixin.onSlotClick() fängt ab
   - slotId: z.B. 12
   - clickType: z.B. PICKUP
   - button: z.B. 0 (Linksklick)
   ↓
3. MacroRecorder.recordAction(12, PICKUP, 0)
   ↓
4. MacroAction(12, PICKUP, 0) wird gespeichert
   ↓
5. Datei: "12,PICKUP,0"
```

### Playback-Flow:

```
1. MacroPlayer lädt Macro-Datei
   ↓
2. Parst "12,PICKUP,0" → MacroAction(12, PICKUP, 0)
   ↓
3. executeAction() wird aufgerufen
   ↓
4. gameMode.handleInventoryMouseClick(
      containerId,  // z.B. 1
      12,          // slotId
      0,           // button (Linksklick!)
      PICKUP,      // clickType
      player
   )
   ↓
5. Minecraft sendet ServerboundContainerClickPacket an Server
   ↓
6. Server verarbeitet Klick (öffnet Menü, kauft Item, etc.)
```

## Click-Type Matrix

| Aktion            | ClickType  | Button | Beschreibung                  |
| ----------------- | ---------- | ------ | ----------------------------- |
| Linksklick        | PICKUP     | 0      | Normaler Linksklick auf Item  |
| Rechtsklick       | PICKUP     | 1      | Normaler Rechtsklick auf Item |
| Shift+Linksklick  | QUICK_MOVE | 0      | Quick-Transfer                |
| Shift+Rechtsklick | QUICK_MOVE | 1      | Quick-Transfer (selten)       |
| Taste 1-9         | SWAP       | 0-8    | Hotbar-Swap                   |
| Q-Taste           | THROW      | 0      | Item werfen                   |
| Strg+Q            | THROW      | 1      | Stack werfen                  |
| Doppelklick       | PICKUP_ALL | 0      | Alle gleichen Items sammeln   |

## Testing-Anleitung

### 1. Einfacher Linksklick (Server-Menü)

```
1. Starte Minecraft mit der Mod
2. Verbinde mit Server mit GUI-Menü (z.B. Shop)
3. Drücke R (Record-Taste)
4. Öffne Server-Menü
5. Klicke EINMAL mit LINKS auf ein Item
6. Drücke R erneut (Stop Recording)
7. Speichere Macro als "test_left_click"
8. Schließe Menü
9. Führe Macro aus: /slotmacro play test_left_click
10. Öffne Menü erneut
11. ✓ Macro sollte den Linksklick korrekt ausführen
```

### 2. Rechtsklick-Test

```
1. Wiederhole obige Schritte, aber mit RECHTSKLICK
2. Macro-Datei sollte "X,PICKUP,1" enthalten (button=1)
```

### 3. Shift-Klick-Test

```
1. Teste mit Shift+Linksklick
2. Macro-Datei sollte "X,QUICK_MOVE,0" enthalten
```

## Bekannte Einschränkungen

1. **Drag-Actions**: Komplexe Drag-Operationen sind vereinfacht implementiert
2. **Timing**: Kein Timing zwischen Klicks gespeichert (verwendet festes Delay)
3. **Container-Validierung**: Keine Prüfung, ob der Container-Typ beim Playback übereinstimmt

## Rückwärtskompatibilität

Alte Macro-Dateien (Format: `slotId,clickType`) funktionieren weiterhin:

- Werden als `button=0` (Linksklick) interpretiert
- Funktionieren für die meisten Anwendungsfälle
- Sollten für volle Funktionalität neu aufgezeichnet werden

## Build-Status

✓ Kompiliert erfolgreich mit Gradle
✓ Keine Compiler-Fehler
✓ Keine Warnungen in kritischen Dateien
✓ Fabric Loom 1.15.3
✓ Minecraft 1.21.4
✓ Mojang Mappings

## Nächste Schritte für Benutzer

1. Build erstellen: `./gradlew build`
2. JAR-Datei finden: `build/libs/slot-click-macros-*.jar`
3. In `.minecraft/mods/` kopieren
4. Minecraft starten und testen
5. Bei Problemen: Logs prüfen (`logs/latest.log`)
