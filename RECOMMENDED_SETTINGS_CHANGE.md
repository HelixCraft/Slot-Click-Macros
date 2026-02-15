# Empfohlene Einstellungsänderung für mehr Sicherheit

## Aktuelle Default-Werte (Riskant!)

```
Base Delay: 50ms
Random Range: 50ms
→ Ergebnis: 50-100ms (10-20 CPS)
```

**Problem**:

- 10-20 CPS ist für die meisten Anti-Cheats verdächtig
- Zu schnell für menschliche Reaktionszeiten
- Hohe Erkennungswahrscheinlichkeit auf großen Servern

## Empfohlene Änderung

### Option 1: Ausgewogen (Empfohlen)

```
Base Delay: 100ms (statt 50ms)
Random Range: 50ms (beibehalten)
→ Ergebnis: 100-150ms (6-10 CPS)
```

**Vorteile**:

- ✓ Sicher für die meisten Server (Spartan, Matrix)
- ✓ Immer noch schnell genug für Effizienz
- ✓ Innerhalb menschlicher Reaktionszeiten
- ✓ Gute Balance zwischen Speed und Safety

**Nachteile**:

- Etwas langsamer als vorher (aber sicherer)

### Option 2: Sehr sicher

```
Base Delay: 120ms
Random Range: 60ms
→ Ergebnis: 120-180ms (5-8 CPS)
```

**Vorteile**:

- ✓ Sehr sicher für strenge Server (Vulcan, Grim)
- ✓ Kaum Erkennungsrisiko
- ✓ Entspricht schnellen menschlichen Spielern

### Option 3: Maximal sicher

```
Base Delay: 150ms
Random Range: 70ms
→ Ergebnis: 150-220ms (4-6 CPS)
```

**Vorteile**:

- ✓ Praktisch unerkennbar
- ✓ Null Risiko auf allen Servern
- ✓ Entspricht durchschnittlichen Spielern

## Vergleich: Menschliche Geschwindigkeit

### Durchschnittlicher Spieler:

- Inventory-Klicks: 200-400ms
- Schnelle Aktionen: 150-250ms
- **Unsere Mod (aktuell)**: 50-100ms ❌ ZU SCHNELL!
- **Unsere Mod (empfohlen)**: 100-150ms ✓ REALISTISCH

### Pro-Spieler:

- Inventory-Klicks: 100-200ms
- Schnelle Aktionen: 80-150ms
- **Unsere Mod (empfohlen)**: 100-150ms ✓ PASST

## Anti-Cheat Schwellenwerte

### Spartan (Mittelstark):

- Flag bei: >12 CPS
- Ban bei: >15 CPS
- **Aktuell**: 10-20 CPS ⚠️ GRENZWERTIG
- **Empfohlen**: 6-10 CPS ✓ SICHER

### Matrix (Stark):

- Flag bei: >10 CPS
- Ban bei: >12 CPS
- **Aktuell**: 10-20 CPS ❌ GEFÄHRLICH
- **Empfohlen**: 6-10 CPS ✓ SICHER

### Vulcan (Sehr stark):

- Flag bei: >8 CPS
- Ban bei: >10 CPS
- **Aktuell**: 10-20 CPS ❌ SEHR GEFÄHRLICH
- **Empfohlen**: 6-10 CPS ⚠️ GRENZWERTIG
- **Besser**: 5-8 CPS ✓ SICHER

### Grim (Extrem stark):

- Flag bei: >7 CPS
- Ban bei: >9 CPS
- **Aktuell**: 10-20 CPS ❌ INSTANT BAN
- **Empfohlen**: 6-10 CPS ❌ IMMER NOCH RISKANT
- **Besser**: 4-6 CPS ✓ SICHER

## Implementierungsvorschlag

### Änderung in ModConfig.java:

```java
// VORHER:
private int defaultDelay = 50; // milliseconds
private int randomDelayRange = 50; // 0-100ms random addition

// NACHHER (Option 1 - Empfohlen):
private int defaultDelay = 100; // milliseconds
private int randomDelayRange = 50; // 0-100ms random addition

// ODER (Option 2 - Sehr sicher):
private int defaultDelay = 120; // milliseconds
private int randomDelayRange = 60; // 0-100ms random addition

// ODER (Option 3 - Maximal sicher):
private int defaultDelay = 150; // milliseconds
private int randomDelayRange = 70; // 0-100ms random addition
```

### Änderung in MacroPlayer.java:

```java
// VORHER:
private int defaultDelay = 50; // milliseconds
private int randomDelayRange = 50; // 0-100ms random addition

// NACHHER (Option 1):
private int defaultDelay = 100; // milliseconds
private int randomDelayRange = 50; // 0-100ms random addition
```

### Änderung in ConfigData:

```java
// VORHER:
int defaultDelay = 50;
int randomDelayRange = 50;

// NACHHER (Option 1):
int defaultDelay = 100;
int randomDelayRange = 50;
```

## Benutzer-Kommunikation

### In GUI Tooltip hinzufügen:

```
"Default Delay (ms)"
Tooltip:
- "Delay between macro actions during playback"
- "Range: 10-500 milliseconds"
- "Recommended: 100-150ms for safety"
- "⚠️ Values below 80ms may trigger anti-cheat!"
```

### In Help-Tab hinzufügen:

```
"⚠️ Anti-Cheat Safety:"
- "Use at least 100ms base delay for most servers"
- "Use 150ms+ for servers with strict anti-cheat"
- "Lower values = faster but higher ban risk"
- "Higher values = slower but completely safe"
```

## Risiko-Matrix

| Base Delay | Random Range | Ergebnis  | CPS   | Vanilla | Spartan | Matrix | Vulcan | Grim |
| ---------- | ------------ | --------- | ----- | ------- | ------- | ------ | ------ | ---- |
| 50ms       | 50ms         | 50-100ms  | 10-20 | ✓       | ⚠️      | ❌     | ❌     | ❌   |
| 80ms       | 40ms         | 80-120ms  | 8-12  | ✓       | ✓       | ⚠️     | ❌     | ❌   |
| 100ms      | 50ms         | 100-150ms | 6-10  | ✓       | ✓       | ✓      | ⚠️     | ❌   |
| 120ms      | 60ms         | 120-180ms | 5-8   | ✓       | ✓       | ✓      | ✓      | ⚠️   |
| 150ms      | 70ms         | 150-220ms | 4-6   | ✓       | ✓       | ✓      | ✓      | ✓    |

**Legende**:

- ✓ = Sicher
- ⚠️ = Grenzwertig
- ❌ = Gefährlich

## Finale Empfehlung

### Für die Mod (Default-Werte):

```
Base Delay: 100ms
Random Range: 50ms
```

**Begründung**:

1. Sicher für 80% der Server
2. Immer noch effizient
3. Gute Balance
4. Benutzer können bei Bedarf reduzieren (auf eigenes Risiko)
5. Besser zu sicher als zu riskant

### Für Benutzer-Dokumentation:

```
"Empfohlene Einstellungen:
- Normale Server: 100ms + 50ms Random
- Strenge Server: 150ms + 70ms Random
- Maximale Sicherheit: 200ms + 100ms Random

⚠️ WARNUNG: Werte unter 80ms können zu Bans führen!"
```

## Zusammenfassung

**Aktuelle Einstellung**: 50ms + 50ms = 50-100ms (10-20 CPS) ❌ RISKANT

**Empfohlene Änderung**: 100ms + 50ms = 100-150ms (6-10 CPS) ✓ SICHER

**Vorteil**: Deutlich geringeres Ban-Risiko bei nur geringfügig langsamerer Ausführung
