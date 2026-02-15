package com.helixcraft.slotclickmacros.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.helixcraft.slotclickmacros.SlotClickMacros;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages mod configuration persistence.
 */
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILENAME = "slot-click-macros.json";
    
    private final Path configFile;
    
    // Configuration values
    private int defaultDelay = 100; // milliseconds (changed from 50ms for anti-cheat safety)
    private int randomDelayRange = 50; // 0-100ms random addition (default: 50ms)
    private String recordKey = "R";
    private String stopKey = ""; // Stop macro playback keybind (empty = not set)
    private Map<String, String> macroKeybinds = new HashMap<>();
    
    public ModConfig(Path gameDir) {
        this.configFile = gameDir.resolve("config").resolve(CONFIG_FILENAME);
        load();
    }
    
    /**
     * Loads configuration from file.
     */
    public void load() {
        if (!Files.exists(configFile)) {
            SlotClickMacros.LOGGER.info("Config file not found, using defaults");
            save(); // Create default config
            return;
        }
        
        try {
            String json = Files.readString(configFile);
            ConfigData data = GSON.fromJson(json, ConfigData.class);
            
            if (data != null) {
                this.defaultDelay = data.defaultDelay;
                this.randomDelayRange = data.randomDelayRange;
                this.recordKey = data.recordKey;
                this.stopKey = data.stopKey != null ? data.stopKey : "";
                this.macroKeybinds = data.macroKeybinds != null ? data.macroKeybinds : new HashMap<>();
            }
            
            SlotClickMacros.LOGGER.info("Loaded config: delay={}, randomRange={}, recordKey={}, stopKey={}", 
                defaultDelay, randomDelayRange, recordKey, stopKey);
        } catch (IOException e) {
            SlotClickMacros.LOGGER.error("Failed to load config", e);
        }
    }
    
    /**
     * Saves configuration to file.
     */
    public void save() {
        try {
            ConfigData data = new ConfigData();
            data.defaultDelay = this.defaultDelay;
            data.randomDelayRange = this.randomDelayRange;
            data.recordKey = this.recordKey;
            data.stopKey = this.stopKey;
            data.macroKeybinds = this.macroKeybinds;
            
            String json = GSON.toJson(data);
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, json);
            
            SlotClickMacros.LOGGER.info("Saved config");
        } catch (IOException e) {
            SlotClickMacros.LOGGER.error("Failed to save config", e);
        }
    }
    
    // Getters and setters
    public int getDefaultDelay() {
        return defaultDelay;
    }
    
    public void setDefaultDelay(int defaultDelay) {
        this.defaultDelay = Math.max(0, Math.min(1000, defaultDelay));
    }
    
    public int getRandomDelayRange() {
        return randomDelayRange;
    }
    
    public void setRandomDelayRange(int randomDelayRange) {
        this.randomDelayRange = Math.max(0, Math.min(100, randomDelayRange));
    }
    
    public String getRecordKey() {
        return recordKey;
    }
    
    public void setRecordKey(String recordKey) {
        this.recordKey = recordKey;
    }
    
    public String getStopKey() {
        return stopKey;
    }
    
    public void setStopKey(String stopKey) {
        this.stopKey = stopKey != null ? stopKey : "";
    }
    
    public Map<String, String> getMacroKeybinds() {
        return new HashMap<>(macroKeybinds);
    }
    
    public void setMacroKeybind(String macroName, String keybind) {
        macroKeybinds.put(macroName, keybind);
        save();
    }
    
    public String getMacroKeybind(String macroName) {
        return macroKeybinds.getOrDefault(macroName, "");
    }
    
    public void removeMacroKeybind(String macroName) {
        macroKeybinds.remove(macroName);
        save();
    }
    
    /**
     * Inner class for JSON serialization.
     */
    private static class ConfigData {
        int defaultDelay = 100;
        int randomDelayRange = 50;
        String recordKey = "R";
        String stopKey = "";
        Map<String, String> macroKeybinds = new HashMap<>();
    }
}
