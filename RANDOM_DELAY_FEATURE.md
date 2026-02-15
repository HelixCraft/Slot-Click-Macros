# Random Delay Feature - Anti-Detection

## Implementierung Abgeschlossen ✓

### Neue Features

#### 1. Random Delay Range Config

**Datei**: `ModConfig.java`

- Neues Feld: `randomDelayRange` (0-100ms)
- Getter/Setter mit Validierung (0-100)
- JSON-Serialisierung

#### 2. GUI Slider

**Datei**: `ModConfigScreen.java`

- Zweiter Slider unter dem Haupt-Delay
- Range: 0-100ms
- Text: "Disabled" bei 0, sonst "0-X ms"
- Tooltip erklärt Anti-Detection-Funktion

#### 3. MacroPlayer Random Logic

**Datei**: `MacroPlayer.java`

- `java.util.Random` Instanz
- `calculateDelay()` Methode
- Addiert random(0, randomDelayRange) zum Base-Delay

## Funktionsweise

### Beispiel-Berechnung:

```
Config:
- Base Delay: 100ms
- Random Range: 50ms

Action 1: 100ms + random(0-50) = 133ms
Action 2: 100ms + random(0-50) = 112ms
Action 3: 100ms + random(0-50) = 147ms
Action 4: 100ms + random(0-50) = 108ms
```

### Code-Flow:

```java
// In playMacro()
for (MacroAction action : actions) {
    executeAction(action, player);

    // Berechne Delay mit Randomness
    int actualDelay = calculateDelay();
    // actualDelay = defaultDelay + random.nextInt(randomDelayRange + 1)

    Thread.sleep(actualDelay);
}

// calculateDelay() Methode
private int calculateDelay() {
    if (randomDelayRange == 0) {
        return defaultDelay; // Kein Random
    }
    int randomAddition = random.nextInt(randomDelayRange + 1);
    return defaultDelay + randomAddition;
}
```

## GUI Darstellung

```
┌─────────────────────────────────────────┐
│ General Settings                        │
├─────────────────────────────────────────┤
│                                         │
│ Default Delay (ms)                      │
│ ├─────────●─────────┤ 100 ms          │
│ 10                            500       │
│                                         │
│ Random Delay Range (ms)                 │
│ ├──────●────────────┤ 0-50 ms         │
│ 0                             100       │
│                                         │
│ ℹ️ Adds random delay (0 to this value) │
│    to each action                       │
│    Helps avoid detection by anti-cheat │
│    Example: Base 100ms + Random 50ms   │
│             = 100-150ms                 │
│    0 = Disabled (not recommended)       │
└─────────────────────────────────────────┘
```

## Empfohlene Einstellungen

### Normale Server (wenig Anti-Cheat):

```
Base Delay: 80-100ms
Random Range: 20-30ms
→ Ergebnis: 80-130ms
```

### Strenge Server (starker Anti-Cheat):

```
Base Delay: 150-200ms
Random Range: 50-100ms
→ Ergebnis: 150-300ms
```

### Schnelle Aktionen (Risiko):

```
Base Delay: 50ms
Random Range: 10ms
→ Ergebnis: 50-60ms
⚠️ Könnte erkannt werden!
```

### Maximale Sicherheit:

```
Base Delay: 200ms
Random Range: 100ms
→ Ergebnis: 200-300ms
✓ Sehr menschlich
```

## Testing

### Test 1: Random Delay aktiviert

```
1. Öffne Config GUI
2. Setze Base Delay: 100ms
3. Setze Random Range: 50ms
4. Zeichne Macro mit 5 Actions auf
5. Spiele ab
6. Prüfe Logs:
   "Action 0 delay: 133ms (base: 100ms, random range: 50ms)"
   "Action 1 delay: 112ms (base: 100ms, random range: 50ms)"
   "Action 2 delay: 147ms (base: 100ms, random range: 50ms)"
```

### Test 2: Random Delay deaktiviert

```
1. Setze Random Range: 0
2. Spiele Macro ab
3. Alle Delays sollten exakt Base Delay sein
```

### Test 3: Maximaler Random Range

```
1. Setze Random Range: 100ms
2. Delays sollten zwischen Base und Base+100 variieren
```

## Logging

**Debug-Ausgabe** (wenn aktiviert):

```
[DEBUG] Action 0 delay: 133ms (base: 100ms, random range: 50ms)
[DEBUG] Action 1 delay: 112ms (base: 100ms, random range: 50ms)
[DEBUG] Detected container change, waiting extra 100ms for sync
[DEBUG] Action 2 delay: 147ms (base: 100ms, random range: 50ms)
```

## Anti-Detection Vorteile

### 1. Variable Timing ✓

- Kein konstanter Delay mehr
- Jede Action hat leicht anderen Delay
- Menschliches Verhalten simuliert

### 2. Unvorhersehbar ✓

- Random-Generator macht Timing unvorhersehbar
- Anti-Cheat kann kein Muster erkennen
- Jede Macro-Ausführung ist anders

### 3. Konfigurierbar ✓

- Benutzer kann Sicherheitslevel wählen
- 0 = Schnell (Risiko)
- 100 = Langsam (Sicher)

### 4. Kombiniert mit Container-Wechsel-Delay ✓

- Base Delay + Random + Container-Wechsel
- Sehr natürliches Verhalten
- Schwer zu erkennen

## Build-Status

✓ Kompiliert erfolgreich
✓ Keine Fehler
✓ Keine Warnungen
✓ Getestet mit `./gradlew build`

## Zusammenfassung

Die Mod hat jetzt einen **Random Delay Range Slider** (0-100ms), der zu jedem Action-Delay einen zufälligen Wert hinzufügt. Dies macht das Timing unvorhersehbar und menschlicher, was die Erkennung durch Anti-Cheat-Systeme erheblich erschwert.

**Empfehlung**: Für maximale Sicherheit `Base Delay: 150ms` und `Random Range: 50ms` verwenden.
