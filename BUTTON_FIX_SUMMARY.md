# Button Data Fix - Server Menu Interaction

## Problem

Die Mod konnte keine einfachen Klicks auf Items in Server-Menüs (z.B. Shop-GUIs) abspielen. Das Problem war, dass die `MacroAction` nur `slotId` und `clickType` gespeichert hat, aber nicht den `button`-Parameter.

## Warum das wichtig ist

Bei einem `PICKUP` ClickType unterscheidet der `button`-Parameter zwischen:

- `button=0` → Linksklick
- `button=1` → Rechtsklick

Server-Menüs reagieren oft nur auf einen spezifischen Klick-Typ. Ohne den `button`-Parameter wurde immer `button=0` verwendet (aus `getButtonData()`), was bei Rechtsklick-Aktionen fehlschlug.

## Änderungen

### 1. MacroAction.java

- **Neues Format**: `MacroAction(int slotId, MacroClickType clickType, int button)`
- **Serialisierung**: `slotId,clickType,button` (vorher: `slotId,clickType`)
- **Rückwärtskompatibilität**: Legacy-Format wird unterstützt (verwendet `button=0` als Standard)

### 2. MacroRecorder.java

- Speichert jetzt den `button`-Parameter beim Aufzeichnen
- Beide Methoden (`recordAction` und `recordDragAction`) übergeben `button` an `MacroAction`

### 3. MacroPlayer.java

- Verwendet jetzt `action.button()` statt `action.clickType().getButtonData()`
- Spezielle Behandlung für Hotbar-Swaps (überschreibt mit `getButtonData()`)

## Beispiel Macro-Datei

**Vorher (funktionierte nicht für Server-Menüs):**

```
12,PICKUP
15,QUICK_MOVE
```

**Nachher (funktioniert mit Links- und Rechtsklick):**

```
12,PICKUP,0
15,QUICK_MOVE,0
20,PICKUP,1
```

## Testen

1. Starte Minecraft und verbinde dich mit einem Server mit GUI-Menüs
2. Drücke die Record-Taste (Standard: R)
3. Klicke auf Items im Server-Menü (z.B. Shop-Items)
4. Stoppe die Aufnahme
5. Spiele das Macro ab - die Klicks sollten jetzt korrekt funktionieren

## Rückwärtskompatibilität

Alte Macro-Dateien (ohne `button`-Parameter) funktionieren weiterhin, verwenden aber standardmäßig `button=0` (Linksklick).
