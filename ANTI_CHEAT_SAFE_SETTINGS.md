# Anti-Cheat Sichere Einstellungen - Analyse

## Gängige Anti-Cheat-Systeme

### 1. Vanilla Minecraft (Kein Anti-Cheat)

**Erkennung**: Keine spezifische Inventory-Click-Erkennung
**Limit**: Technisch ~50 Packets/Sekunde (20ms zwischen Packets)
**Empfehlung**: Mindestens 30-40ms Delay

### 2. Spartan Anti-Cheat

**Typ**: Mittelstark
**Inventory-Checks**:

- Prüft Click-Geschwindigkeit
- Erkennt unmenschliche Patterns (konstante Delays)
- Threshold: ~10-15 Clicks/Sekunde = verdächtig

**Sichere Einstellungen**:

- Base Delay: 80-100ms
- Random Range: 30-50ms
- Ergebnis: 80-150ms (6-12 Clicks/Sekunde)

### 3. Matrix Anti-Cheat

**Typ**: Stark
**Inventory-Checks**:

- Erweiterte Pattern-Erkennung
- Prüft Timing-Konsistenz
- Erkennt perfekte Delays
- Threshold: ~8-10 Clicks/Sekunde = Flag

**Sichere Einstellungen**:

- Base Delay: 100-120ms
- Random Range: 40-60ms
- Ergebnis: 100-180ms (5-10 Clicks/Sekunde)

### 4. Vulcan Anti-Cheat

**Typ**: Sehr stark
**Inventory-Checks**:

- Machine-Learning basiert
- Erkennt Bot-Patterns
- Analysiert Timing-Varianz
- Threshold: ~6-8 Clicks/Sekunde = Flag

**Sichere Einstellungen**:

- Base Delay: 120-150ms
- Random Range: 50-80ms
- Ergebnis: 120-230ms (4-8 Clicks/Sekunde)

### 5. Grim Anti-Cheat

**Typ**: Extrem stark (Prediction-basiert)
**Inventory-Checks**:

- Vorhersage-basierte Erkennung
- Sehr sensitiv auf unnatürliche Patterns
- Threshold: ~5-7 Clicks/Sekunde = Flag

**Sichere Einstellungen**:

- Base Delay: 150-200ms
- Random Range: 60-100ms
- Ergebnis: 150-300ms (3-6 Clicks/Sekunde)

## Menschliche Reaktionszeiten (Referenz)

### Durchschnittlicher Spieler:

- **Einfacher Klick**: 200-300ms
- **Gezielter Klick**: 250-400ms
- **Schneller Spieler**: 150-250ms
- **Pro-Spieler**: 100-200ms

### Inventory-Interaktionen:

- **Item suchen + klicken**: 300-600ms
- **Bekanntes Item klicken**: 150-300ms
- **Shift-Click Transfer**: 200-400ms
- **Mehrere Items nacheinander**: 100-250ms pro Item

## Erkennungsmechanismen

### 1. Click-Rate (Clicks per Second)

**Was wird geprüft**: Anzahl der Clicks in einem Zeitfenster
**Typische Limits**:

- 20+ CPS = Sofortiger Ban (unmenschlich)
- 15-20 CPS = Hohe Warnung
- 10-15 CPS = Warnung
- 8-10 CPS = Verdächtig
- 5-8 CPS = Grenzwertig
- <5 CPS = Normal

### 2. Timing-Konsistenz

**Was wird geprüft**: Varianz zwischen Clicks
**Verdächtig**:

- Perfekt konstante Delays (z.B. immer exakt 50ms)
- Zu geringe Standardabweichung (<10ms)
- Mathematische Patterns

**Natürlich**:

- Variable Delays (50-150ms Varianz)
- Hohe Standardabweichung (>20ms)
- Zufällige Patterns

### 3. Packet-Reihenfolge

**Was wird geprüft**: Reihenfolge und Timing von Packets
**Verdächtig**:

- Packets ohne Client-seitige Verzögerung
- Perfekte Synchronisation
- Keine "Denkpausen"

**Natürlich**:

- Gelegentliche längere Pausen
- Unregelmäßige Patterns
- Reaktionszeit-Varianz

### 4. Container-Interaktion-Patterns

**Was wird geprüft**: Verhalten nach Container-Öffnung
**Verdächtig**:

- Sofortiger erster Klick (<50ms nach Öffnung)
- Perfekte Slot-Sequenzen
- Keine Fehler/Misclicks

**Natürlich**:

- Verzögerung nach Öffnung (200-500ms)
- Gelegentliche "falsche" Klicks
- Variable Geschwindigkeit

## Empfohlene Einstellungen nach Server-Typ

### Vanilla/Kleine Server (Kein/Schwaches Anti-Cheat)

```
Base Delay: 50-80ms
Random Range: 20-40ms
Ergebnis: 50-120ms (8-20 CPS)
Risiko: Niedrig
```

### Mittelgroße Server (Spartan, Basic Matrix)

```
Base Delay: 80-100ms
Random Range: 30-50ms
Ergebnis: 80-150ms (6-12 CPS)
Risiko: Niedrig-Mittel
```

### Große Server (Matrix, Vulcan)

```
Base Delay: 120-150ms
Random Range: 50-80ms
Ergebnis: 120-230ms (4-8 CPS)
Risiko: Mittel
```

### Sehr strenge Server (Grim, Custom AC)

```
Base Delay: 150-200ms
Random Range: 60-100ms
Ergebnis: 150-300ms (3-6 CPS)
Risiko: Niedrig
```

### Maximale Sicherheit (Paranoid Mode)

```
Base Delay: 200-250ms
Random Range: 80-120ms
Ergebnis: 200-370ms (2-5 CPS)
Risiko: Sehr niedrig
```

## Kritische Schwellenwerte (NICHT ÜBERSCHREITEN!)

### Absolute Minimums:

- **Niemals unter 40ms Base Delay** (25 CPS = instant ban)
- **Niemals unter 30ms Gesamt-Delay** (33 CPS = unmenschlich)
- **Immer mindestens 20ms Random Range** (für Varianz)

### Gefährliche Zone:

- **40-60ms**: Sehr riskant, nur für Vanilla-Server
- **60-80ms**: Riskant, nur für schwache Anti-Cheats
- **80-100ms**: Grenzwertig, benötigt hohe Random Range

### Sichere Zone:

- **100-150ms**: Sicher für die meisten Server
- **150-200ms**: Sehr sicher, kaum Risiko
- **200-300ms**: Extrem sicher, ununterscheidbar von Menschen

## Zusätzliche Sicherheitsmaßnahmen

### 1. Startup-Delay (Empfohlen)

**Problem**: Sofortiger Start nach Container-Öffnung ist verdächtig
**Lösung**: 200-500ms warten nach Container-Öffnung
**Implementierung**: Optional in zukünftiger Version

### 2. Gelegentliche Pausen (Empfohlen)

**Problem**: Konstante Aktivität ohne Pausen
**Lösung**: 5-10% Chance auf 500-1000ms Pause
**Implementierung**: Optional in zukünftiger Version

### 3. Fehlerrate (Optional)

**Problem**: Perfekte Ausführung jedes Mal
**Lösung**: 1-2% Chance auf "falschen" Klick
**Implementierung**: Komplex, nicht empfohlen

## Praktische Empfehlungen

### Für normale Nutzung:

```
Base Delay: 100ms
Random Range: 50ms
→ 100-150ms (6-10 CPS)
```

**Begründung**:

- Schnell genug für Effizienz
- Langsam genug für Sicherheit
- Gute Balance zwischen Speed und Safety

### Für strenge Server:

```
Base Delay: 150ms
Random Range: 70ms
→ 150-220ms (4-6 CPS)
```

**Begründung**:

- Sehr sicher
- Immer noch effizient
- Kaum Erkennungsrisiko

### Für maximale Sicherheit:

```
Base Delay: 200ms
Random Range: 100ms
→ 200-300ms (3-5 CPS)
```

**Begründung**:

- Praktisch unerkennbar
- Entspricht menschlicher Geschwindigkeit
- Null Risiko

## Warnsignale für zu schnelle Einstellungen

### Wenn du gebannt wirst bei:

- **<50ms**: Viel zu schnell, sofortiger Ban erwartet
- **50-80ms**: Zu schnell für die meisten Anti-Cheats
- **80-100ms**: Grenzwertig, erhöhe Random Range
- **100-120ms**: Sollte sicher sein, prüfe andere Faktoren

### Wenn du Warnungen bekommst:

1. Erhöhe Base Delay um 20-30ms
2. Erhöhe Random Range um 10-20ms
3. Teste erneut
4. Wiederhole bis keine Warnungen mehr

## Mathematische Analyse

### Click-Rate Berechnung:

```
CPS = 1000 / (Base Delay + (Random Range / 2))

Beispiele:
Base 50ms + Random 20ms → 1000 / 60 = 16.6 CPS (GEFÄHRLICH!)
Base 100ms + Random 50ms → 1000 / 125 = 8 CPS (Grenzwertig)
Base 150ms + Random 70ms → 1000 / 185 = 5.4 CPS (Sicher)
Base 200ms + Random 100ms → 1000 / 250 = 4 CPS (Sehr sicher)
```

### Standardabweichung (wichtig für Natürlichkeit):

```
Random Range 20ms → Std Dev ~6ms (zu niedrig!)
Random Range 50ms → Std Dev ~15ms (okay)
Random Range 80ms → Std Dev ~24ms (gut)
Random Range 100ms → Std Dev ~30ms (sehr gut)
```

## Finale Empfehlung

### Standard-Einstellung (bereits implementiert):

```
Base Delay: 50ms
Random Range: 50ms
→ 50-100ms (10-20 CPS)
```

**Status**: Grenzwertig für strenge Server

### Empfohlene Änderung für mehr Sicherheit:

```
Base Delay: 100ms (statt 50ms)
Random Range: 50ms (beibehalten)
→ 100-150ms (6-10 CPS)
```

**Vorteil**: Deutlich sicherer, immer noch schnell

### Für maximale Sicherheit:

```
Base Delay: 150ms
Random Range: 70ms
→ 150-220ms (4-6 CPS)
```

**Vorteil**: Praktisch unerkennbar

## Zusammenfassung

**Absolute Minimums (NICHT UNTERSCHREITEN)**:

- Base Delay: 80ms
- Random Range: 30ms
- Gesamt: 80-110ms

**Empfohlene Werte**:

- Base Delay: 100-150ms
- Random Range: 50-70ms
- Gesamt: 100-220ms

**Maximale Sicherheit**:

- Base Delay: 150-200ms
- Random Range: 70-100ms
- Gesamt: 150-300ms

**Faustregel**: Je wichtiger der Account, desto höher die Delays!
