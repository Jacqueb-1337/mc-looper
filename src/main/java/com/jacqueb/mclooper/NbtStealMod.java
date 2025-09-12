package com.jacqueb.mclooper;

import com.jacqueb.mclooper.config.ConfigManager;
import com.jacqueb.mclooper.loop.LoopExecutor;
import com.jacqueb.mclooper.ui.LoopEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NbtStealMod implements ClientModInitializer {
    public static final String MOD_ID = "nbtsteal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static ConfigManager configManager;
    private static LoopExecutor loopExecutor;
    
    private static KeyBinding openEditorKeyBinding;
    private static KeyBinding pauseLoopsKeyBinding;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("NBTSteal mod initializing...");
        
        // Initialize config manager
        configManager = new ConfigManager();
        configManager.loadConfig();
        
        // Initialize loop executor
        loopExecutor = new LoopExecutor(configManager);
        
        // Register keybinding
        openEditorKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nbtsteal.open_editor",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.nbtsteal.general"
        ));
        
        // Register pause keybinding
        pauseLoopsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.nbtsteal.pause_loops",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.nbtsteal.general"
        ));
        
        // Register event handlers
        registerEventHandlers();
        
        LOGGER.info("NBTSteal mod initialized successfully");
    }
    
    private void registerEventHandlers() {
        // Handle client tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Check for keybinding press
            if (openEditorKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new LoopEditorScreen(Text.literal("Loop Editor")));
                }
            }
            
            // Check for pause keybinding press
            if (pauseLoopsKeyBinding.wasPressed()) {
                loopExecutor.togglePause();
            }
            
            // Update loop executor
            if (client.player != null && client.world != null) {
                loopExecutor.tick();
            }
        });
        
        // Handle connection events for initialization
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            loopExecutor.onWorldJoin();
        });
        
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            loopExecutor.onWorldLeave();
        });
        
        // Handle chat messages for triggers
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, signed) -> {
            String text = message.getString();
            loopExecutor.onChatMessage(text);
            return true; // Allow the message to be displayed
        });
    }
    
    public static ConfigManager getConfigManager() {
        return configManager;
    }
    
    public static LoopExecutor getLoopExecutor() {
        return loopExecutor;
    }
}
