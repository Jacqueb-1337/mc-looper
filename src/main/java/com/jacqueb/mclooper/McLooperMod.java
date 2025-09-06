package com.jacqueb.mclooper;

import com.jacqueb.mclooper.config.ConfigManager;
import com.jacqueb.mclooper.loop.LoopExecutor;
import com.jacqueb.mclooper.ui.LoopEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McLooperMod
implements ClientModInitializer {
    public static final String MOD_ID = "mc-looper";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"mc-looper");
    private static ConfigManager configManager;
    private static LoopExecutor loopExecutor;
    private static KeyBinding openEditorKeyBinding;
    private static KeyBinding pauseLoopsKeyBinding;

    public void onInitializeClient() {
        LOGGER.info("MC Looper mod initializing...");
        configManager = new ConfigManager();
        configManager.loadConfig();
        loopExecutor = new LoopExecutor(configManager);
        openEditorKeyBinding = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.mclooper.open_editor", InputUtil.Type.KEYSYM, 344, "category.mclooper.general"));
        pauseLoopsKeyBinding = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.mclooper.pause_loops", InputUtil.Type.KEYSYM, 80, "category.mclooper.general"));
        this.registerEventHandlers();
        LOGGER.info("MC Looper mod initialized successfully");
    }

    private void registerEventHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openEditorKeyBinding.wasPressed() && client.currentScreen == null) {
                client.setScreen((net.minecraft.client.gui.screen.Screen)new LoopEditorScreen((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop Editor")));
            }
            if (pauseLoopsKeyBinding.wasPressed()) {
                loopExecutor.togglePause();
            }
            if (client.player != null && client.world != null) {
                this.checkKeyPresses(client);
                loopExecutor.tick();
            }
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> loopExecutor.onWorldJoin());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> loopExecutor.onWorldLeave());
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, signed) -> {
            String text = message.getString();
            loopExecutor.onChatMessage(text);
            return true;
        });
    }

    private void checkKeyPresses(net.minecraft.client.MinecraftClient client) {
        if (client.getWindow() != null) {
            String keyName;
            int key;
            long window = client.getWindow().getHandle();
            for (key = 65; key <= 90; ++key) {
                if (GLFW.glfwGetKey((long)window, (int)key) != 1) continue;
                keyName = net.minecraft.client.util.InputUtil.fromKeyCode((int)key, (int)0).getTranslationKey();
                loopExecutor.onKeyPressed(keyName);
            }
            for (key = 290; key <= 301; ++key) {
                if (GLFW.glfwGetKey((long)window, (int)key) != 1) continue;
                keyName = net.minecraft.client.util.InputUtil.fromKeyCode((int)key, (int)0).getTranslationKey();
                loopExecutor.onKeyPressed(keyName);
            }
            for (key = 48; key <= 57; ++key) {
                if (GLFW.glfwGetKey((long)window, (int)key) != 1) continue;
                keyName = net.minecraft.client.util.InputUtil.fromKeyCode((int)key, (int)0).getTranslationKey();
                loopExecutor.onKeyPressed(keyName);
            }
        }
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static LoopExecutor getLoopExecutor() {
        return loopExecutor;
    }

    public static boolean isVerboseLogging() {
        return configManager != null && configManager.getConfig() != null && McLooperMod.configManager.getConfig().verboseLogging;
    }
}