package com.jacqueb.mclooper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jacqueb.mclooper.McLooperMod;
import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskConfig;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.config.TaskTrigger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigManager {
    private static final String CONFIG_FILE = "mclooper-tasks.json";
    private final Path configDir = FabricLoader.getInstance().getConfigDir();
    private final Path configFile = this.configDir.resolve("mclooper-tasks.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private TaskConfig config = new TaskConfig();

    public void loadConfig() {
        try {
            if (Files.exists(this.configFile, new LinkOption[0])) {
                String json = Files.readString(this.configFile);
                TaskConfig loadedConfig = (TaskConfig)this.gson.fromJson(json, TaskConfig.class);
                if (loadedConfig != null) {
                    this.config = loadedConfig;
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Config loaded successfully");
                    }
                } else {
                    McLooperMod.LOGGER.warn("Config file is empty or invalid, creating default config");
                    this.createDefaultConfig();
                    this.saveConfig();
                }
            } else {
                this.createDefaultConfig();
                this.saveConfig();
                if (McLooperMod.isVerboseLogging()) {
                    McLooperMod.LOGGER.info("Created default config");
                }
            }
        }
        catch (Exception e) {
            McLooperMod.LOGGER.error("Failed to load config: {}", (Object)e.getMessage(), (Object)e);
            this.createDefaultConfig();
            try {
                this.saveConfig();
            }
            catch (Exception saveException) {
                McLooperMod.LOGGER.error("Failed to save default config: {}", (Object)saveException.getMessage());
            }
        }
        if (this.config == null) {
            McLooperMod.LOGGER.warn("Config is still null after loading, creating emergency default");
            this.createDefaultConfig();
        }
    }

    public void saveConfig() {
        try {
            Files.createDirectories(this.configDir, new FileAttribute[0]);
            String json = this.gson.toJson((Object)this.config);
            Files.writeString(this.configFile, (CharSequence)json, new OpenOption[0]);
            if (McLooperMod.isVerboseLogging()) {
                McLooperMod.LOGGER.info("Config saved successfully");
            }
        }
        catch (IOException e) {
            McLooperMod.LOGGER.error("Failed to save config", (Throwable)e);
        }
    }

    private void createDefaultConfig() {
        block4: {
            try {
                this.config = new TaskConfig();
                this.config.variables = new HashMap<String, Object>();
                this.config.variables.put("mob_coins", 0);
                this.config.variables.put("player", "${minecraft.username}");
                TaskLoop exampleLoop = new TaskLoop("ExampleLoop_" + System.currentTimeMillis());
                exampleLoop.trigger = new TaskTrigger("manual");
                TaskBlock commentBlock = new TaskBlock("comment");
                commentBlock.params.put("text", "Example showing different message types");
                TaskBlock clientMsgBlock = new TaskBlock("client_message");
                clientMsgBlock.params.put("message", "This message only shows to you!");
                TaskBlock chatBlock = new TaskBlock("chat");
                chatBlock.params.put("message", "This goes to server chat");
                TaskBlock commandBlock = new TaskBlock("chat");
                commandBlock.params.put("message", "/time query daytime");
                exampleLoop.blocks.add(commentBlock);
                exampleLoop.blocks.add(clientMsgBlock);
                exampleLoop.blocks.add(chatBlock);
                exampleLoop.blocks.add(commandBlock);
                this.config.loops.add(exampleLoop);
                if (McLooperMod.isVerboseLogging()) {
                    McLooperMod.LOGGER.info("Default config created with {} loops", (Object)this.config.loops.size());
                }
            }
            catch (Exception e) {
                McLooperMod.LOGGER.error("Failed to create default config: {}", (Object)e.getMessage(), (Object)e);
                this.config = new TaskConfig();
                if (this.config.variables == null) {
                    this.config.variables = new HashMap<String, Object>();
                }
                if (this.config.loops != null) break block4;
                this.config.loops = new ArrayList<TaskLoop>();
            }
        }
    }

    public TaskConfig getConfig() {
        if (this.config == null) {
            McLooperMod.LOGGER.warn("Config is null, creating emergency default");
            this.createDefaultConfig();
        }
        return this.config;
    }

    public void setConfig(TaskConfig config) {
        this.config = config;
    }
}