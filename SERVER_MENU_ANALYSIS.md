# Server Menu Click Analysis

## Problem

Einfache Klicks auf Items in Server-Menüs (z.B. Shop-GUIs) werden nicht korrekt abgespielt, obwohl sie aufgezeichnet werden.

## Root Cause Analysis

### Was passiert bei einem Server-Menü-Klick:

1. Spieler klickt auf ein Item in einem Server-GUI (z.B. Shop-Item)
2. Client sendet `ServerboundContainerClickPacket` an Server
3. Server verarbeitet den Klick und führt eine Aktion aus (z.B. öffnet ein neues Menü, kauft ein Item)
4. **Wichtig**: Bei Server-Menüs wird oft KEIN Item tatsächlich verschoben!

### Aktuelles Verhalten der Mod:

1. **Recording**: `AbstractContainerMenuMixin.onSlotClick()` fängt den Klick ab ✓
2. **Speicherung**: `MacroAction(slotId, clickType, button)` wird gespeichert ✓
3. **Playback**: `gameMode.handleInventoryMouseClick()` wird aufgerufen
4. **Problem**: Diese Methode könnte Client-seitige Validierung durchführen, die bei Server-Menüs fehlschlägt

### Warum `handleInventoryMouseClick` möglicherweise nicht funktioniert:

- Die Methode macht Client-seitige Item-Berechnungen
- Bei Server-Menüs gibt es oft keine "echten" Items zum Verschieben
- Der Client könnte den Klick als "ungültig" betrachten und das Packet nicht senden

## Lösung: Direktes Packet-Sending

Wir müssen das `ServerboundContainerClickPacket` direkt erstellen und senden, ohne durch `handleInventoryMouseClick` zu gehen.

### ServerboundContainerClickPacket Struktur (1.21.4):

```
- containerId: int (Window ID)
- stateId: int (Server-managed sequence number)
- slotId: int (Clicked slot)
- button: int (0=left, 1=right, etc.)
- clickType: ClickType enum
- changedSlots: Map<Integer, ItemStack> (Predicted slot changes)
- carriedItem: ItemStack (Item on cursor)
```

### Wichtige Erkenntnisse:

1. `stateId` muss vom Container kommen (`containerMenu.getStateId()`)
2. `changedSlots` kann leer sein - Server validiert selbst
3. `carriedItem` sollte der aktuelle Cursor-Item sein

## Nächste Schritte:

1. Packet-Konstruktor-Signatur für 1.21.4 verifizieren
2. Direktes Packet-Sending implementieren
3. Testen mit verschiedenen Server-Menü-Typen
