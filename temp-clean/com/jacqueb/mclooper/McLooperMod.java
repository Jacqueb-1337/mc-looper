/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
 *  net.minecraft.class_2561
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.jacqueb.mclooper;

import com.jacqueb.mclooper.config.ConfigManager;
import com.jacqueb.mclooper.loop.LoopExecutor;
import com.jacqueb.mclooper.ui.LoopEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McLooperMod
implements ClientModInitializer {
    public static final String MOD_ID = "mc-looper";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"mc-looper");
    private static ConfigManager configManager;
    private static LoopExecutor loopExecutor;
    private static class_304 openEditorKeyBinding;
    private static class_304 pauseLoopsKeyBinding;

    public void onInitializeClient() {
        LOGGER.info("MC Looper mod initializing...");
        configManager = new ConfigManager();
        configManager.loadConfig();
        loopExecutor = new LoopExecutor(configManager);
        openEditorKeyBinding = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.mclooper.open_editor", class_3675.class_307.field_1668, 344, "category.mclooper.general"));
        pauseLoopsKeyBinding = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.mclooper.pause_loops", class_3675.class_307.field_1668, 80, "category.mclooper.general"));
        this.registerEventHandlers();
        LOGGER.info("MC Looper mod initialized successfully");
    }

    private void registerEventHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openEditorKeyBinding.method_1436() && client.field_1755 == null) {
                client.method_1507((class_437)new LoopEditorScreen((class_2561)class_2561.method_43470((String)"Loop Editor")));
            }
            if (pauseLoopsKeyBinding.method_1436()) {
                loopExecutor.togglePause();
            }
            if (client.field_1724 != null && client.field_1687 != null) {
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

    private void checkKeyPresses(class_310 client) {
        if (client.method_22683() != null) {
            String keyName;
            int key;
            long window = client.method_22683().method_4490();
            for (key = 65; key <= 90; ++key) {
                if (GLFW.glfwGetKey((long)window, (int)key) != 1) continue;
                keyName = class_3675.method_15985((int)key, (int)0).method_1441();
                loopExecutor.onKeyPressed(keyName);
            }
            for (key = 290; key <= 301; ++key) {
                if (GLFW.glfwGetKey((long)window, (int)key) != 1) continue;
                keyName = class_3675.method_15985((int)key, (int)0).method_1441();
                loopExecutor.onKeyPressed(keyName);
            }
            for (key = 48; key <= 57; ++key) {
                if (GLFW.glfwGetKey((long)window, (int)key) != 1) continue;
                keyName = class_3675.method_15985((int)key, (int)0).method_1441();
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

