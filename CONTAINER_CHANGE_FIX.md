# Container-Wechsel Fix - Server-Menü Navigation

## Problem

Beim Abspielen von Macros, die mehrere Container durchlaufen (z.B. Server-Menüs, die nach einem Klick ein neues Menü öffnen), funktionierte nur der erste Klick. Nachfolgende Klicks wurden zwar visuell angezeigt (roter Slot), aber nicht an den Server gesendet.

## Root Cause

1. **Statisches Container-Menu**: Das `containerMenu` wurde beim Start des Macros gespeichert und für alle Actions verwendet
2. **Veraltete Container-ID**: Wenn der Server einen neuen Container öffnet, hat dieser eine neue `containerId`
3. **Ungültige Packets**: Klicks mit der alten `containerId` wurden vom Server ignoriert
4. **Timing-Problem**: Der Client braucht Zeit, um den neuen Container vom Server zu empfangen

## Implementierte Lösung

### 1. Dynamisches Container-Menu (✓)

**Änderung in `MacroPlayer.executeAction()`**:

```java
// VORHER: Container-Menu als Parameter
private void executeAction(MacroAction action, AbstractContainerMenu containerMenu, LocalPlayer player)

// NACHHER: Container-Menu dynamisch abrufen
private void executeAction(MacroAction action, LocalPlayer player) {
    AbstractContainerMenu containerMenu = player.containerMenu; // Aktuelles Menu!
    // ...
}
```

**Vorteil**: Jede Action verwendet das AKTUELLE Container-Menu, nicht das vom Start

### 2. Container-Wechsel-Erkennung (✓)

**Änderung in `MacroPlayer.playMacro()`**:

```java
final int[] previousContainerId = {containerMenu.containerId};

for (MacroAction action : actions) {
    // Führe Action aus
    executeAction(action, player);

    // Prüfe, ob Container gewechselt hat
    int currentId = player.containerMenu.containerId;
    if (currentId != previousContainerId[0]) {
        // Container hat gewechselt!
        SlotClickMacros.LOGGER.info("Container changed: {} -> {}",
            previousContainerId[0], currentId);

        // Extra Wartezeit für Server-Synchronisation
        Thread.sleep(100); // 100ms extra

        previousContainerId[0] = currentId;
    }
}
```

**Vorteil**:

- Erkennt Container-Wechsel automatisch
- Wartet extra Zeit für Server-Sync
- Loggt Container-Änderungen für Debugging

### 3. Timing-Verbesserungen (✓)

- **Standard-Delay**: 50ms zwischen Actions (konfigurierbar)
- **Container-Wechsel-Delay**: +100ms extra wenn Container wechselt
- **Gesamt**: 150ms pro Container-Wechsel

## Wie es jetzt funktioniert

### Beispiel: Server-Shop mit 3 Menüs

```
1. Spieler öffnet Shop-Hauptmenü (containerId=1)
   ↓
2. Macro startet, Action 1: Klick auf "Waffen" (Slot 12)
   - Verwendet containerId=1 ✓
   - Server öffnet Waffen-Menü (containerId=2)
   ↓
3. Warte 50ms (Standard-Delay)
   ↓
4. Container-Wechsel erkannt! (1 → 2)
   - Logge: "Container changed: 1 -> 2"
   - Warte extra 100ms für Sync
   ↓
5. Action 2: Klick auf "Schwert" (Slot 5)
   - Verwendet containerId=2 ✓ (dynamisch abgerufen!)
   - Server öffnet Schwert-Details (containerId=3)
   ↓
6. Warte 50ms + Container-Wechsel erkannt (2 → 3)
   - Warte extra 100ms
   ↓
7. Action 3: Klick auf "Kaufen" (Slot 10)
   - Verwendet containerId=3 ✓
   - Server führt Kauf aus
```

## Vorher vs. Nachher

### Vorher (❌ Fehlerhaft)

```
Action 1: containerId=1, slot=12 ✓ Funktioniert
  → Server öffnet Container 2
Action 2: containerId=1, slot=5  ❌ Falsche Container-ID!
  → Server ignoriert Packet
Action 3: containerId=1, slot=10 ❌ Falsche Container-ID!
  → Server ignoriert Packet
```

### Nachher (✓ Funktioniert)

```
Action 1: containerId=1, slot=12 ✓ Funktioniert
  → Server öffnet Container 2
  → Warte 150ms (50ms + 100ms extra)
Action 2: containerId=2, slot=5  ✓ Korrekte Container-ID!
  → Server öffnet Container 3
  → Warte 150ms
Action 3: containerId=3, slot=10 ✓ Korrekte Container-ID!
  → Server führt Action aus
```

## Testing

### Test 1: Einfacher Container-Wechsel

```
1. Starte Minecraft mit Mod
2. Verbinde mit Server mit verschachtelten Menüs
3. Zeichne Macro auf:
   - Öffne Hauptmenü
   - Klicke auf Item → Öffnet Untermenü
   - Klicke auf Item im Untermenü
4. Spiele Macro ab
5. ✓ Alle Klicks sollten funktionieren
6. Prüfe Logs: "Container changed: X -> Y" sollte erscheinen
```

### Test 2: Mehrfache Container-Wechsel

```
1. Zeichne Macro mit 3+ Container-Wechseln auf
2. Spiele ab
3. ✓ Alle Klicks sollten in den richtigen Containern ausgeführt werden
```

### Test 3: Delay-Anpassung

```
1. Setze Delay auf 200ms (Config)
2. Teste mit langsamen Servern
3. ✓ Sollte auch bei hoher Latenz funktionieren
```

## Debugging

### Log-Ausgaben prüfen

```bash
# In logs/latest.log suchen nach:
grep "Container changed" logs/latest.log
grep "Executed action" logs/latest.log
```

**Erwartete Ausgabe**:

```
[INFO] Container changed: 1 -> 2 (action 1)
[DEBUG] Executed action: containerId=2, slot=5, button=0, type=PICKUP
[INFO] Container changed: 2 -> 3 (action 2)
[DEBUG] Executed action: containerId=3, slot=10, button=0, type=PICKUP
```

## Bekannte Einschränkungen

1. **Timing-Abhängig**: Bei sehr langsamen Servern (>200ms Latenz) könnte 100ms extra nicht ausreichen
2. **Keine Retry-Logik**: Wenn ein Packet verloren geht, wird es nicht wiederholt
3. **Container-Validierung**: Keine Prüfung, ob der neue Container die erwarteten Slots hat

## Mögliche Verbesserungen

1. **Konfigurierbares Container-Wechsel-Delay**: Benutzer könnte extra Delay einstellen
2. **Intelligentes Warten**: Warte bis Container tatsächlich empfangen wurde (nicht nur Zeit-basiert)
3. **Retry-Mechanismus**: Wiederhole fehlgeschlagene Klicks automatisch

## Build-Status

✓ Kompiliert erfolgreich
✓ Keine Compiler-Fehler
✓ Keine Warnungen
✓ Getestet mit Gradle build

## Zusammenfassung

Die Mod verwendet jetzt das **aktuelle** Container-Menu für jede Action und erkennt Container-Wechsel automatisch. Dies ermöglicht das korrekte Abspielen von Macros über mehrere Server-Menüs hinweg.
