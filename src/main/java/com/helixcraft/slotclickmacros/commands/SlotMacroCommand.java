package com.helixcraft.slotclickmacros.commands;

import com.helixcraft.slotclickmacros.io.MacroFileManager;
import com.helixcraft.slotclickmacros.playback.MacroPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Registers and handles the /slotmacro command.
 */
public class SlotMacroCommand {
    
    private final MacroFileManager fileManager;
    private final MacroPlayer player;
    
    public SlotMacroCommand(MacroFileManager fileManager, MacroPlayer player) {
        this.fileManager = fileManager;
        this.player = player;
    }
    
    /**
     * Registers the command.
     */
    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerCommand(dispatcher);
        });
    }
    
    private void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("slotmacro")
            .then(ClientCommandManager.literal("play")
                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                    .suggests(this::suggestMacroNames)
                    .executes(this::playMacro)
                )
            )
            .then(ClientCommandManager.literal("stop")
                .executes(this::stopMacros)
            )
            .then(ClientCommandManager.literal("list")
                .executes(this::listMacros)
            )
            .then(ClientCommandManager.literal("delete")
                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                    .suggests(this::suggestMacroNames)
                    .executes(this::deleteMacro)
                )
            )
            .executes(this::showHelp)
        );
    }
    
    /**
     * Suggests macro names for command completion.
     */
    private CompletableFuture<Suggestions> suggestMacroNames(
            CommandContext<FabricClientCommandSource> context, 
            SuggestionsBuilder builder) {
        List<String> macros = fileManager.listMacros();
        return SharedSuggestionProvider.suggest(macros, builder);
    }
    
    private int playMacro(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        
        if (!fileManager.macroExists(name)) {
            context.getSource().sendError(Component.literal("§cMacro '" + name + "' does not exist"));
            return 0;
        }
        
        if (player.queueMacro(name)) {
            context.getSource().sendFeedback(Component.literal("§aQueued macro '" + name + "' - open a container to play"));
            return 1;
        } else {
            context.getSource().sendError(Component.literal("§cFailed to load macro '" + name + "'"));
            return 0;
        }
    }
    
    private int listMacros(CommandContext<FabricClientCommandSource> context) {
        List<String> macros = fileManager.listMacros();
        
        if (macros.isEmpty()) {
            context.getSource().sendFeedback(Component.literal("§eNo macros found"));
            return 0;
        }
        
        context.getSource().sendFeedback(Component.literal("§6Available macros (" + macros.size() + "):"));
        for (String macro : macros) {
            context.getSource().sendFeedback(Component.literal("§7- §f" + macro));
        }
        
        return macros.size();
    }
    
    private int deleteMacro(CommandContext<FabricClientCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        
        if (fileManager.deleteMacro(name)) {
            context.getSource().sendFeedback(Component.literal("§aDeleted macro '" + name + "'"));
            return 1;
        } else {
            context.getSource().sendError(Component.literal("§cMacro '" + name + "' does not exist or could not be deleted"));
            return 0;
        }
    }
    
    private int stopMacros(CommandContext<FabricClientCommandSource> context) {
        if (player.stopAll()) {
            context.getSource().sendFeedback(Component.literal("§aStopped all macro playback and cleared queue"));
            return 1;
        } else {
            context.getSource().sendFeedback(Component.literal("§7No macros were running or queued"));
            return 0;
        }
    }
    
    private int showHelp(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Component.literal("§6Slot Click Macros Commands:"));
        context.getSource().sendFeedback(Component.literal("§7/slotmacro play <name> §f- Queue a macro for playback"));
        context.getSource().sendFeedback(Component.literal("§7/slotmacro stop §f- Stop all macro playback"));
        context.getSource().sendFeedback(Component.literal("§7/slotmacro list §f- List all saved macros"));
        context.getSource().sendFeedback(Component.literal("§7/slotmacro delete <name> §f- Delete a macro"));
        return 1;
    }
}
