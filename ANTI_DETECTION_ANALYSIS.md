# Anti-Detection Analyse & Implementierung

## Implementierte Features

### 1. Random Delay (✓ Implementiert)

**Config**: `randomDelayRange` (0-100ms)
**GUI**: Zweiter Slider unter dem Haupt-Delay-Slider

**Funktionsweise**:

```
Beispiel: Base Delay = 100ms, Random Range = 50ms
Action 1: 100ms + random(0-50) = 100ms + 33ms = 133ms
Action 2: 100ms + random(0-50) = 100ms + 12ms = 112ms
Action 3: 100ms + random(0-50) = 100ms + 47ms = 147ms
```

**Anti-Detection Vorteil**:

- Menschliches Verhalten ist nie perfekt getimed
- Verhindert konstante Delays, die Bots verraten
- Jede Action hat einen leicht anderen Delay

## Packet-Analyse: Natürlichkeit

### Aktuelle Implementierung

#### 1. Slot Click Packets

**Methode**: `gameMode.handleInventoryMouseClick()`

**Was wird gesendet**:

```java
ServerboundContainerClickPacket {
    containerId: int,      // Aktueller Container (dynamisch!)
    stateId: int,          // Server-managed sequence number
    slotId: int,           // Geklickter Slot
    button: int,           // 0=Links, 1=Rechts
    clickType: ClickType,  // PICKUP, QUICK_MOVE, etc.
    changedSlots: Map,     // Vorhergesagte Slot-Änderungen
    carriedItem: ItemStack // Item auf Cursor
}
```

**✓ Natürlich**: Diese Methode ist die GLEICHE, die Minecraft verwendet, wenn ein echter Spieler klickt!

#### 2. Container-ID Handling

**Problem**: Statische Container-ID würde unnatürlich aussehen
**Lösung**: `player.containerMenu` wird dynamisch abgerufen

```java
// VORHER (❌ Unnatürlich):
int containerId = 1; // Immer gleich!
gameMode.handleInventoryMouseClick(containerId, ...);

// NACHHER (✓ Natürlich):
AbstractContainerMenu currentContainer = player.containerMenu;
int containerId = currentContainer.containerId; // Aktueller Container!
gameMode.handleInventoryMouseClick(containerId, ...);
```

**✓ Natürlich**: Verwendet immer den aktuellen Container, genau wie ein echter Spieler

#### 3. Timing-Verhalten

**Vorher**: Konstanter Delay (z.B. immer 50ms)
**Nachher**: Variabler Delay (z.B. 50-150ms)

```java
// Berechnung mit Randomness
private int calculateDelay() {
    if (randomDelayRange == 0) {
        return defaultDelay;
    }
    int randomAddition = random.nextInt(randomDelayRange + 1);
    return defaultDelay + randomAddition;
}
```

**✓ Natürlich**: Menschliche Reaktionszeiten variieren immer

#### 4. Container-Wechsel-Handling

**Zusätzlicher Delay bei Container-Wechsel**: +100ms

```java
if (currentId != previousContainerId[0]) {
    Thread.sleep(100); // Extra Zeit für Server-Sync
}
```

**✓ Natürlich**: Menschen brauchen Zeit, um neue Menüs zu verarbeiten

## Potenzielle Detection-Vektoren

### 1. Perfekte Slot-Genauigkeit (✓ Gelöst)

**Problem**: Bot klickt immer exakt die gleichen Slots
**Lösung**: Mod zeichnet echte Spieler-Klicks auf, keine vordefinierten Slots

### 2. Konstante Timing (✓ Gelöst)

**Problem**: Immer gleicher Delay zwischen Actions
**Lösung**: Random Delay Range (0-100ms zusätzlich)

### 3. Keine Fehler/Misclicks (⚠️ Noch nicht implementiert)

**Problem**: Menschen machen Fehler, Bots nicht
**Mögliche Lösung**: Optional "Fehlerrate" einbauen (z.B. 1% Chance auf falschen Slot)

### 4. Sofortige Reaktion (✓ Teilweise gelöst)

**Problem**: Bot reagiert sofort auf Container-Öffnung
**Lösung**: Container-Wechsel-Delay von 100ms
**Verbesserung**: Könnte auch randomisiert werden (100-200ms)

### 5. Packet-Reihenfolge (✓ Natürlich)

**Status**: Verwendet native Minecraft-Methoden
**Ergebnis**: Packets werden in der gleichen Reihenfolge gesendet wie bei echten Spielern

### 6. StateId Synchronisation (✓ Natürlich)

**Status**: `containerMenu.getStateId()` wird automatisch verwendet
**Ergebnis**: StateId ist immer korrekt synchronisiert mit Server

## Vergleich: Bot vs. Echter Spieler

### Echter Spieler:

```
Container öffnen
  ↓ 150-300ms (Menü anschauen)
Klick auf Slot 12
  ↓ 80-200ms (nächstes Item suchen)
Klick auf Slot 5
  ↓ 100-250ms (überlegen)
Klick auf Slot 10
```

### Unsere Mod (mit Random Delay):

```
Container öffnen
  ↓ 0ms (sofort, da Macro gequeued ist)
Klick auf Slot 12
  ↓ 100-150ms (Base 100ms + Random 0-50ms)
Container wechselt
  ↓ +100ms (Container-Sync)
Klick auf Slot 5
  ↓ 100-150ms
Klick auf Slot 10
```

**Unterschied**: Mod startet sofort, echter Spieler braucht Zeit zum Öffnen

## Empfohlene Einstellungen für maximale Natürlichkeit

### Für normale Server:

```
Base Delay: 100-150ms
Random Range: 30-50ms
Ergebnis: 100-200ms pro Action
```

### Für strenge Anti-Cheat Server:

```
Base Delay: 150-200ms
Random Range: 50-100ms
Ergebnis: 150-300ms pro Action
```

### Für schnelle Aktionen (Risiko!):

```
Base Delay: 50ms
Random Range: 20ms
Ergebnis: 50-70ms pro Action
⚠️ Könnte als Bot erkannt werden!
```

## Weitere Anti-Detection Maßnahmen (Optional)

### 1. Startup-Delay

**Idee**: Warte 200-500ms nach Container-Öffnung, bevor Macro startet
**Vorteil**: Simuliert "Menü anschauen"

### 2. Fehlerrate

**Idee**: 1-2% Chance, falschen Slot zu klicken (dann korrigieren)
**Vorteil**: Menschen machen Fehler

### 3. Maus-Bewegung (Schwierig!)

**Idee**: Simuliere Maus-Bewegung zu Slots
**Problem**: Sehr komplex, könnte unnatürlich aussehen

### 4. Pause-Chance

**Idee**: 5% Chance auf extra lange Pause (500-1000ms)
**Vorteil**: Simuliert "Nachdenken" oder Ablenkung

## Packet-Struktur Validierung

### ServerboundContainerClickPacket Felder:

1. **containerId** ✓
   - Dynamisch vom aktuellen Container
   - Ändert sich bei Container-Wechsel
   - Identisch zu echtem Spieler

2. **stateId** ✓
   - Automatisch von `containerMenu.getStateId()`
   - Server-managed, immer korrekt
   - Identisch zu echtem Spieler

3. **slotId** ✓
   - Aus aufgezeichnetem Macro
   - Echte Spieler-Klicks
   - Identisch zu echtem Spieler

4. **button** ✓
   - 0=Links, 1=Rechts
   - Korrekt aufgezeichnet
   - Identisch zu echtem Spieler

5. **clickType** ✓
   - PICKUP, QUICK_MOVE, etc.
   - Korrekt konvertiert
   - Identisch zu echtem Spieler

6. **changedSlots** ✓
   - Von `handleInventoryMouseClick()` berechnet
   - Native Minecraft-Logik
   - Identisch zu echtem Spieler

7. **carriedItem** ✓
   - Aktueller Cursor-Item
   - Von `containerMenu.getCarried()`
   - Identisch zu echtem Spieler

## Fazit

### ✓ Natürlich:

- Verwendet native Minecraft-Methoden
- Dynamische Container-IDs
- Variable Delays mit Randomness
- Korrekte StateId-Synchronisation
- Echte Spieler-Klicks (aufgezeichnet)

### ⚠️ Potenzielle Schwachstellen:

- Sofortiger Start nach Container-Öffnung
- Keine Fehler/Misclicks
- Perfekte Ausführung jedes Mal

### 🎯 Empfehlung:

Mit `Base Delay: 150ms` und `Random Range: 50ms` ist die Mod sehr schwer von echten Spielern zu unterscheiden, besonders bei komplexen Macros mit vielen Container-Wechseln.
