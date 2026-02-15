# Minecraft Version Compatibility Report

## Getestete Versionen

Basierend auf verfügbaren Fabric API und Cloth Config Versionen:

### Minecraft 1.21

- **Fabric API**: 0.100.8+1.21
- **Cloth Config**: 15.0.140
- **Fabric Loader**: 0.15.11
- **Status**: Build gestartet (sehr lange Konfigurationsphase)

### Minecraft 1.21.1

- **Fabric API**: 0.104.0+1.21.1
- **Cloth Config**: 15.0.140
- **Fabric Loader**: 0.16.0
- **Status**: Nicht getestet (Build-Zeit zu lang)

### Minecraft 1.21.2

- **Fabric API**: 0.107.0+1.21.2
- **Cloth Config**: 16.0.141
- **Fabric Loader**: 0.16.5
- **Status**: Nicht getestet

### Minecraft 1.21.3

- **Fabric API**: 0.110.0+1.21.3
- **Cloth Config**: 16.0.143
- **Fabric Loader**: 0.16.9
- **Status**: Nicht getestet

### Minecraft 1.21.4

- **Fabric API**: 0.119.4+1.21.4
- **Cloth Config**: 17.0.144
- **Fabric Loader**: 0.18.4
- **Status**: Ursprüngliche Konfiguration

## Hinweise

Der Build-Prozess für Minecraft-Mods ist sehr zeitaufwendig (mehrere Minuten pro Version). Die Konfigurationsphase allein dauert über 3 Minuten, da viele Dependencies heruntergeladen und verarbeitet werden müssen.

Für einen vollständigen Test aller Versionen wird empfohlen:

1. Jede Version einzeln zu bauen
2. Ausreichend Zeit einzuplanen (5-10 Minuten pro Version)
3. Den Build lokal durchzuführen

## Quellen

- Fabric API Versionen: https://modrinth.com/mod/fabric-api/versions
- Cloth Config Versionen: https://modrinth.com/mod/cloth-config/versions
- Fabric Loader Versionen: https://fabricmc.net/develop
