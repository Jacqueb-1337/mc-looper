/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package com.jacqueb.mclooper.ui;

import com.jacqueb.mclooper.McLooperMod;
import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskCondition;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.config.TaskTrigger;
import com.jacqueb.mclooper.ui.LoopEditorScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public class BlockEditorScreen
extends class_437 {
    private final LoopEditorScreen parent;
    private final TaskBlock block;
    private double scrollY = 0.0;
    private static final double SCROLL_SPEED = 15.0;
    private class_342 messageField;
    private class_342 timeField;
    private class_342 unitField;
    private class_342 loopNameField;
    private class_342 countField;
    private class_342 commentField;
    private class_4185 selectedConditionButton = null;
    private int selectedConditionButtonX = 0;
    private int selectedConditionButtonY = 0;
    private int selectedConditionButtonWidth = 0;
    private int selectedConditionButtonHeight = 0;
    private static NestedBlockEditingContext nestedBlockEditingContext = null;

    public BlockEditorScreen(LoopEditorScreen parent, TaskBlock block) {
        super((class_2561)class_2561.method_43470((String)("Edit Block: " + block.type)));
        this.parent = parent;
        this.block = block;
    }

    protected void method_25426() {
        super.method_25426();
        this.method_37067();
        if (nestedBlockEditingContext != null && BlockEditorScreen.nestedBlockEditingContext.originalScreen == this) {
            BlockEditorScreen.handleNestedBlockEditReturn();
            return;
        }
        this.repositionWidgets();
    }

    private void repositionWidgets() {
        this.method_37067();
        int fieldWidth = 200;
        int fieldHeight = 20;
        int x = this.field_22789 / 2 - fieldWidth / 2;
        int y = (int)(60.0 - this.scrollY);
        int spacing = 40;
        switch (this.block.type) {
            case "comment": {
                this.commentField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Comment"));
                String commentText = (String)this.block.params.getOrDefault("text", "");
                if (McLooperMod.isVerboseLogging() && commentText.length() > 30) {
                    McLooperMod.LOGGER.info("Loading comment into field: '{}' (length: {})", (Object)commentText, (Object)commentText.length());
                }
                this.commentField.method_1880(2000);
                this.commentField.method_1852(commentText);
                if (McLooperMod.isVerboseLogging() && commentText.length() > 30) {
                    McLooperMod.LOGGER.info("Comment field after setText: '{}' (length: {})", (Object)this.commentField.method_1882(), (Object)this.commentField.method_1882().length());
                }
                this.commentField.method_1875(this.commentField.method_1882().length());
                this.commentField.method_1884(this.commentField.method_1882().length());
                this.method_37063((class_364)this.commentField);
                break;
            }
            case "exit": {
                break;
            }
            case "chat": {
                this.messageField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Message/Command"));
                this.messageField.method_1880(2000);
                this.messageField.method_1852((String)this.block.params.getOrDefault("message", ""));
                this.messageField.method_1875(this.messageField.method_1882().length());
                this.messageField.method_1884(this.messageField.method_1882().length());
                this.method_37063((class_364)this.messageField);
                break;
            }
            case "client_message": {
                this.messageField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Client Message"));
                this.messageField.method_1880(2000);
                this.messageField.method_1852((String)this.block.params.getOrDefault("message", ""));
                this.messageField.method_1875(this.messageField.method_1882().length());
                this.messageField.method_1884(this.messageField.method_1882().length());
                this.method_37063((class_364)this.messageField);
                break;
            }
            case "wait": {
                this.timeField = new class_342(this.field_22793, x, y, fieldWidth / 2 - 5, fieldHeight, (class_2561)class_2561.method_43470((String)"Time"));
                this.timeField.method_1880(50);
                this.timeField.method_1852(String.valueOf(this.block.params.getOrDefault("time", 20)));
                this.timeField.method_1875(this.timeField.method_1882().length());
                this.timeField.method_1884(this.timeField.method_1882().length());
                this.method_37063((class_364)this.timeField);
                this.unitField = new class_342(this.field_22793, x + fieldWidth / 2 + 5, y, fieldWidth / 2 - 5, fieldHeight, (class_2561)class_2561.method_43470((String)"Unit"));
                this.unitField.method_1880(50);
                this.unitField.method_1852((String)this.block.params.getOrDefault("unit", "ticks"));
                this.unitField.method_1875(this.unitField.method_1882().length());
                this.unitField.method_1884(this.unitField.method_1882().length());
                this.method_37063((class_364)this.unitField);
                break;
            }
            case "call_loop": {
                this.loopNameField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Loop Name"));
                this.loopNameField.method_1880(200);
                this.loopNameField.method_1852((String)this.block.params.getOrDefault("loop", ""));
                this.loopNameField.method_1875(this.loopNameField.method_1882().length());
                this.loopNameField.method_1884(this.loopNameField.method_1882().length());
                this.method_37063((class_364)this.loopNameField);
                List<TaskLoop> loops = McLooperMod.getConfigManager().getConfig().loops;
                int buttonY = y += spacing;
                for (TaskLoop loop : loops) {
                    if (loop.name == null) continue;
                    class_4185 loopButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)loop.name), btn -> {
                        this.loopNameField.method_1852(loop.name);
                        this.loopNameField.method_1875(this.loopNameField.method_1882().length());
                        this.loopNameField.method_1884(this.loopNameField.method_1882().length());
                    }).method_46434(x, buttonY, fieldWidth, fieldHeight).method_46431();
                    this.method_37063((class_364)loopButton);
                    buttonY += 25;
                }
                break;
            }
            case "click_gui_item": {
                this.messageField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Name Pattern (regex)"));
                this.messageField.method_1880(1000);
                this.messageField.method_1852((String)this.block.params.getOrDefault("name_pattern", "Click to Confirm"));
                this.messageField.method_1875(this.messageField.method_1882().length());
                this.messageField.method_1884(this.messageField.method_1882().length());
                this.method_37063((class_364)this.messageField);
                this.commentField = new class_342(this.field_22793, x, y += spacing, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Variable Name (optional)"));
                this.commentField.method_1880(100);
                this.commentField.method_1852((String)this.block.params.getOrDefault("var_name", ""));
                this.commentField.method_1875(this.commentField.method_1882().length());
                this.commentField.method_1884(this.commentField.method_1882().length());
                this.method_37063((class_364)this.commentField);
                break;
            }
            case "loop": {
                this.countField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Count"));
                this.countField.method_1880(50);
                this.countField.method_1852(String.valueOf(this.block.params.getOrDefault("count", 1)));
                this.countField.method_1875(this.countField.method_1882().length());
                this.countField.method_1884(this.countField.method_1882().length());
                this.method_37063((class_364)this.countField);
                break;
            }
            case "post_request": {
                this.messageField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"URL"));
                String urlText = (String)this.block.params.getOrDefault("url", "https://example.com/api");
                if (McLooperMod.isVerboseLogging() && urlText.length() > 30) {
                    McLooperMod.LOGGER.info("Loading POST URL into field: '{}' (length: {})", (Object)urlText, (Object)urlText.length());
                }
                this.messageField.method_1880(2000);
                this.messageField.method_1852(urlText);
                if (McLooperMod.isVerboseLogging() && urlText.length() > 30) {
                    McLooperMod.LOGGER.info("URL field after setText: '{}' (length: {})", (Object)this.messageField.method_1882(), (Object)this.messageField.method_1882().length());
                }
                this.messageField.method_1875(this.messageField.method_1882().length());
                this.messageField.method_1884(this.messageField.method_1882().length());
                this.method_37063((class_364)this.messageField);
                this.commentField = new class_342(this.field_22793, x, y += spacing, fieldWidth, fieldHeight * 2, (class_2561)class_2561.method_43470((String)"Request Body (JSON)"));
                String bodyText = (String)this.block.params.getOrDefault("body", "{}");
                if (McLooperMod.isVerboseLogging() && bodyText.length() > 30) {
                    McLooperMod.LOGGER.info("Loading POST body into field: '{}' (length: {})", bodyText.length() > 100 ? bodyText.substring(0, 100) + "..." : bodyText, (Object)bodyText.length());
                }
                this.commentField.method_1880(50000);
                this.commentField.method_1852(bodyText);
                if (McLooperMod.isVerboseLogging() && bodyText.length() > 30) {
                    McLooperMod.LOGGER.info("POST body field after setText: '{}' (length: {})", this.commentField.method_1882().length() > 100 ? this.commentField.method_1882().substring(0, 100) + "..." : this.commentField.method_1882(), (Object)this.commentField.method_1882().length());
                }
                this.commentField.method_1875(this.commentField.method_1882().length());
                this.commentField.method_1884(this.commentField.method_1882().length());
                this.method_37063((class_364)this.commentField);
                this.method_37063((class_364)this.commentField);
                y += fieldHeight;
                this.timeField = new class_342(this.field_22793, x, y += spacing, fieldWidth / 2 - 5, fieldHeight, (class_2561)class_2561.method_43470((String)"Timeout (ms)"));
                this.timeField.method_1880(50);
                this.timeField.method_1852(String.valueOf(this.block.params.getOrDefault("timeout", 5000)));
                this.timeField.method_1875(this.timeField.method_1882().length());
                this.timeField.method_1884(this.timeField.method_1882().length());
                this.method_37063((class_364)this.timeField);
                this.unitField = new class_342(this.field_22793, x + fieldWidth / 2 + 5, y, fieldWidth / 2 - 5, fieldHeight, (class_2561)class_2561.method_43470((String)"Response Variable"));
                this.unitField.method_1880(100);
                this.unitField.method_1852((String)this.block.params.getOrDefault("response_var", "response"));
                this.unitField.method_1875(this.unitField.method_1882().length());
                this.unitField.method_1884(this.unitField.method_1882().length());
                this.method_37063((class_364)this.unitField);
                break;
            }
            case "if": 
            case "wait_until": {
                String defaultText;
                String firstFieldLabel;
                String currentConditionType = this.block.condition != null ? this.block.condition.type : "variable_equals";
                boolean isRegexMode = false;
                if (("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) && this.block.condition != null) {
                    isRegexMode = "regex".equals(this.block.condition.params.getOrDefault("search_mode", "registry"));
                }
                String string = "gui_item_exists".equals(currentConditionType) ? "Name Pattern (regex)" : ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType) ? (isRegexMode ? "Regex Pattern (name/lore)" : "Registry ID (minecraft:item)") : (firstFieldLabel = "Variable Name"));
                String secondFieldLabel = "gui_item_exists".equals(currentConditionType) ? "Variable Name (optional)" : ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType) ? "Variable Name" : "Expected Value");
                this.commentField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)firstFieldLabel));
                if (this.block.condition != null) {
                    if ("gui_item_exists".equals(currentConditionType)) {
                        this.commentField.method_1852((String)this.block.condition.params.getOrDefault("name_pattern", "Click to Confirm"));
                        this.commentField.method_1875(this.commentField.method_1882().length());
                        this.commentField.method_1884(this.commentField.method_1882().length());
                    } else if ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) {
                        if (isRegexMode) {
                            this.commentField.method_1852((String)this.block.condition.params.getOrDefault("regex_pattern", "(?i).*diamond.*"));
                            this.commentField.method_1875(this.commentField.method_1882().length());
                            this.commentField.method_1884(this.commentField.method_1882().length());
                        } else {
                            this.commentField.method_1852((String)this.block.condition.params.getOrDefault("item_name", "minecraft:cobblestone"));
                            this.commentField.method_1875(this.commentField.method_1882().length());
                            this.commentField.method_1884(this.commentField.method_1882().length());
                        }
                    } else {
                        this.commentField.method_1852((String)this.block.condition.params.getOrDefault("variable", "example"));
                        this.commentField.method_1875(this.commentField.method_1882().length());
                        this.commentField.method_1884(this.commentField.method_1882().length());
                    }
                } else {
                    defaultText = "gui_item_exists".equals(currentConditionType) ? "(?i).*click.*confirm.*" : ("inventory_contains".equals(currentConditionType) ? "minecraft:cobblestone" : ("gui_contains".equals(currentConditionType) ? "minecraft:diamond" : "example"));
                    this.commentField.method_1852(defaultText);
                    this.commentField.method_1875(this.commentField.method_1882().length());
                    this.commentField.method_1884(this.commentField.method_1882().length());
                }
                this.method_37063((class_364)this.commentField);
                this.messageField = new class_342(this.field_22793, x, y += spacing, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)secondFieldLabel));
                if (this.block.condition != null) {
                    if ("gui_item_exists".equals(currentConditionType)) {
                        this.messageField.method_1852((String)this.block.condition.params.getOrDefault("var_name", "confirm_var"));
                        this.messageField.method_1875(this.messageField.method_1882().length());
                        this.messageField.method_1884(this.messageField.method_1882().length());
                    } else if ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) {
                        String defaultVar = "inventory_contains".equals(currentConditionType) ? "item_var" : "gui_item_var";
                        this.messageField.method_1852((String)this.block.condition.params.getOrDefault("var_name", defaultVar));
                        this.messageField.method_1875(this.messageField.method_1882().length());
                        this.messageField.method_1884(this.messageField.method_1882().length());
                    } else {
                        Object value = this.block.condition.params.get("value");
                        this.messageField.method_1852(value != null ? value.toString() : "true");
                        this.messageField.method_1875(this.messageField.method_1882().length());
                        this.messageField.method_1884(this.messageField.method_1882().length());
                    }
                } else {
                    defaultText = "gui_item_exists".equals(currentConditionType) ? "confirm_var" : ("inventory_contains".equals(currentConditionType) ? "item_var" : ("gui_contains".equals(currentConditionType) ? "gui_item_var" : "true"));
                    this.messageField.method_1852(defaultText);
                    this.messageField.method_1875(this.messageField.method_1882().length());
                    this.messageField.method_1884(this.messageField.method_1882().length());
                }
                this.method_37063((class_364)this.messageField);
                if ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) {
                    String searchMode = this.block.condition != null ? (String)this.block.condition.params.getOrDefault("search_mode", "registry") : "registry";
                    String toggleText = "registry".equals(searchMode) ? "Mode: Registry ID" : "Mode: Regex Pattern";
                    class_4185 searchModeButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)toggleText), btn -> {
                        String currentMode;
                        if (this.block.condition == null) {
                            this.block.condition = new TaskCondition(currentConditionType);
                        }
                        String newMode = "registry".equals(currentMode = (String)this.block.condition.params.getOrDefault("search_mode", "registry")) ? "regex" : "registry";
                        this.block.condition.params.put("search_mode", newMode);
                        String newText = "registry".equals(newMode) ? "Mode: Registry ID" : "Mode: Regex Pattern";
                        btn.method_25355((class_2561)class_2561.method_43470((String)newText));
                        if ("registry".equals(newMode)) {
                            this.commentField.method_47404((class_2561)class_2561.method_43470((String)"minecraft:cobblestone"));
                            if (this.commentField.method_1882().isEmpty()) {
                                this.commentField.method_1852("minecraft:cobblestone");
                            }
                        } else {
                            this.commentField.method_47404((class_2561)class_2561.method_43470((String)"(?i).*diamond.*"));
                            if (this.commentField.method_1882().isEmpty()) {
                                this.commentField.method_1852("(?i).*diamond.*");
                            }
                        }
                    }).method_46434(x, y += spacing, fieldWidth, fieldHeight).method_46431();
                    this.method_37063((class_364)searchModeButton);
                }
                y += spacing;
                String[] conditionTypes = new String[]{"variable_equals", "variable_greater_than", "variable_less_than", "inventory_contains", "gui_contains", "gui_item_exists"};
                String[] conditionLabels = new String[]{"Variable =", "Variable >", "Variable <", "Inventory Contains", "GUI Contains", "GUI Item Exists"};
                int buttonWidth = fieldWidth / 3;
                int buttonX = x;
                for (int i = 0; i < conditionTypes.length; ++i) {
                    String conditionType = conditionTypes[i];
                    String label = conditionLabels[i];
                    boolean isSelected = this.block.condition != null && conditionType.equals(this.block.condition.type);
                    class_4185 conditionButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)label), btn -> {
                        if (this.block.condition == null) {
                            this.block.condition = new TaskCondition(conditionType);
                        } else {
                            this.block.condition.type = conditionType;
                        }
                        this.field_22787.method_1507((class_437)new BlockEditorScreen(this.parent, this.block));
                    }).method_46434(buttonX, y, buttonWidth - 2, fieldHeight).method_46431();
                    this.method_37063((class_364)conditionButton);
                    if (isSelected) {
                        this.selectedConditionButton = conditionButton;
                        this.selectedConditionButtonX = buttonX;
                        this.selectedConditionButtonY = y;
                        this.selectedConditionButtonWidth = buttonWidth - 2;
                        this.selectedConditionButtonHeight = fieldHeight;
                    }
                    buttonX += buttonWidth;
                    if (i != 2) continue;
                    buttonX = x;
                    y += fieldHeight + 5;
                }
                break;
            }
            case "regex_replace": {
                this.commentField = new class_342(this.field_22793, x, y, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Source Variable"));
                this.commentField.method_1852((String)this.block.params.getOrDefault("source_var", "variable_name"));
                this.commentField.method_1880(100);
                this.method_37063((class_364)this.commentField);
                this.messageField = new class_342(this.field_22793, x, y += spacing, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Target Variable"));
                this.messageField.method_1852((String)this.block.params.getOrDefault("target_var", "cleaned_variable"));
                this.messageField.method_1880(100);
                this.method_37063((class_364)this.messageField);
                this.timeField = new class_342(this.field_22793, x, y += spacing, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Regex Pattern"));
                this.timeField.method_1852((String)this.block.params.getOrDefault("pattern", ","));
                this.timeField.method_1880(1000);
                this.method_37063((class_364)this.timeField);
                this.unitField = new class_342(this.field_22793, x, y += spacing, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Replacement"));
                this.unitField.method_1852((String)this.block.params.getOrDefault("replacement", ""));
                this.unitField.method_1880(1000);
                this.method_37063((class_364)this.unitField);
                break;
            }
            case "prevent_execution": {
                this.timeField = new class_342(this.field_22793, x, y, fieldWidth / 2 - 5, fieldHeight, (class_2561)class_2561.method_43470((String)"Time"));
                this.timeField.method_1852(String.valueOf(this.block.params.getOrDefault("time", 5)));
                this.method_37063((class_364)this.timeField);
                this.unitField = new class_342(this.field_22793, x + fieldWidth / 2 + 5, y, fieldWidth / 2 - 5, fieldHeight, (class_2561)class_2561.method_43470((String)"Unit"));
                this.unitField.method_1852((String)this.block.params.getOrDefault("unit", "seconds"));
                this.method_37063((class_364)this.unitField);
                break;
            }
            case "world_click": {
                String clickType = (String)this.block.params.getOrDefault("click_type", "right");
                class_4185 clickTypeButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)("Click: " + clickType)), btn -> {
                    String current = (String)this.block.params.get("click_type");
                    String newType = "left".equals(current) ? "right" : "left";
                    this.block.params.put("click_type", newType);
                    btn.method_25355((class_2561)class_2561.method_43470((String)("Click: " + newType)));
                }).method_46434(x, y, fieldWidth, fieldHeight).method_46431();
                this.method_37063((class_364)clickTypeButton);
                this.messageField = new class_342(this.field_22793, x, y += spacing, fieldWidth, fieldHeight, (class_2561)class_2561.method_43470((String)"Target (crosshair or x,y,z)"));
                this.messageField.method_1852((String)this.block.params.getOrDefault("target", "crosshair"));
                this.messageField.method_1880(200);
                this.method_37063((class_364)this.messageField);
                boolean pauseOnGui = Boolean.parseBoolean(String.valueOf(this.block.params.getOrDefault("pause_on_gui", "false")));
                class_4185 pauseOnGuiButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)("Pause on GUI: " + (pauseOnGui ? "ON" : "OFF"))), btn -> {
                    boolean current = Boolean.parseBoolean(String.valueOf(this.block.params.getOrDefault("pause_on_gui", "false")));
                    boolean newValue = !current;
                    this.block.params.put("pause_on_gui", String.valueOf(newValue));
                    btn.method_25355((class_2561)class_2561.method_43470((String)("Pause on GUI: " + (newValue ? "ON" : "OFF"))));
                }).method_46434(x, y += spacing, fieldWidth, fieldHeight).method_46431();
                this.method_37063((class_364)pauseOnGuiButton);
            }
        }
        if (this.supportsNestedBlocks(this.block.type)) {
            int nestedCount = this.block.blocks != null ? this.block.blocks.size() : 0;
            String nestedButtonText = "Manage Nested Blocks (" + nestedCount + ")";
            class_4185 nestedBlocksButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)nestedButtonText), btn -> this.openNestedBlocksEditor()).method_46434(x, y += spacing + 10, fieldWidth, fieldHeight).method_46431();
            this.method_37063((class_364)nestedBlocksButton);
            y += spacing;
        }
        class_4185 saveButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Save"), btn -> this.saveBlock()).method_46434(this.field_22789 / 2 - 110, this.field_22790 - 40, 100, 20).method_46431();
        this.method_37063((class_364)saveButton);
        class_4185 cancelButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Cancel"), btn -> this.method_25419()).method_46434(this.field_22789 / 2 + 10, this.field_22790 - 40, 100, 20).method_46431();
        this.method_37063((class_364)cancelButton);
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int estimatedContentHeight = 400;
        switch (this.block.type) {
            case "regex_replace": 
            case "prevent_execution": 
            case "post_request": {
                estimatedContentHeight = 500;
                break;
            }
            case "if": 
            case "wait_until": {
                estimatedContentHeight = 600;
            }
        }
        int maxScroll = Math.max(0, estimatedContentHeight - this.field_22790 + 100);
        this.scrollY = Math.max(0.0, Math.min((double)maxScroll, this.scrollY - verticalAmount * 15.0));
        this.repositionWidgets();
        return true;
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        this.method_25420(context, mouseX, mouseY, delta);
        super.method_25394(context, mouseX, mouseY, delta);
        context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, 0xFFFFFF);
        int fieldWidth = 200;
        int x = this.field_22789 / 2 - fieldWidth / 2;
        int y = (int)(60.0 - this.scrollY);
        int spacing = 40;
        switch (this.block.type) {
            case "comment": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Comment Text:"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "exit": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Exit block - stops current execution"), x, y - 15, 0xFFFF00, false);
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"No parameters required"), x, y + 5, 0x888888, false);
                break;
            }
            case "chat": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Message/Command (use / for commands):"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "client_message": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Client Message:"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "wait": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Wait Time:"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "call_loop": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Loop to Call:"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "click_gui_item": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Name Pattern / Variable Name:"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "loop": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Loop Count:"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "post_request": {
                if (y - 15 > -20 && y - 15 < this.field_22790) {
                    context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"HTTP POST Request:"), x, y - 15, 0xFFFFFF, false);
                }
                if (y + spacing - 15 > -20 && y + spacing - 15 < this.field_22790) {
                    context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Request Body:"), x, y + spacing - 15, 0xFFFFFF, false);
                }
                if (y + 2 * spacing - 15 <= -20 || y + 2 * spacing - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Timeout / Response Variable:"), x, y + 2 * spacing - 15, 0xFFFFFF, false);
                break;
            }
            case "regex_replace": {
                if (y - 15 > -20 && y - 15 < this.field_22790) {
                    context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Source Variable:"), x, y - 15, 0xFFFFFF, false);
                }
                if (y + spacing - 15 > -20 && y + spacing - 15 < this.field_22790) {
                    context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Target Variable:"), x, y + spacing - 15, 0xFFFFFF, false);
                }
                if (y + 2 * spacing - 15 > -20 && y + 2 * spacing - 15 < this.field_22790) {
                    context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Regex Pattern:"), x, y + 2 * spacing - 15, 0xFFFFFF, false);
                }
                if (y + 3 * spacing - 15 <= -20 || y + 3 * spacing - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Replacement:"), x, y + 3 * spacing - 15, 0xFFFFFF, false);
                break;
            }
            case "prevent_execution": {
                if (y - 15 <= -20 || y - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Prevention Time:"), x, y - 15, 0xFFFFFF, false);
                break;
            }
            case "world_click": {
                if (y - 15 > -20 && y - 15 < this.field_22790) {
                    context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Click Type:"), x, y - 15, 0xFFFFFF, false);
                }
                if (y + spacing - 15 > -20 && y + spacing - 15 < this.field_22790) {
                    context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Target:"), x, y + spacing - 15, 0xFFFFFF, false);
                }
                if (y + spacing * 2 - 15 <= -20 || y + spacing * 2 - 15 >= this.field_22790) break;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Pause on GUI:"), x, y + spacing * 2 - 15, 0xFFFFFF, false);
                break;
            }
            case "if": 
            case "wait_until": {
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Condition - Variable / Value:"), x, y - 15, 0xFFFFFF, false);
                int conditionTypeY = y + 2 * spacing;
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Condition Type:"), x, conditionTypeY - 15, 0xFFFFFF, false);
            }
        }
        int estimatedContentHeight = 400;
        switch (this.block.type) {
            case "regex_replace": 
            case "prevent_execution": 
            case "post_request": {
                estimatedContentHeight = 500;
                break;
            }
            case "if": 
            case "wait_until": {
                estimatedContentHeight = 600;
            }
        }
        if (estimatedContentHeight > this.field_22790 - 100) {
            int scrollBarHeight = Math.max(20, (this.field_22790 - 100) * (this.field_22790 - 100) / estimatedContentHeight);
            int maxScroll = Math.max(1, estimatedContentHeight - this.field_22790 + 100);
            int scrollBarY = (int)(40.0 + (double)(this.field_22790 - 140 - scrollBarHeight) * this.scrollY / (double)maxScroll);
            context.method_25294(this.field_22789 - 10, scrollBarY, this.field_22789 - 5, scrollBarY + scrollBarHeight, -2130706433);
        }
        if (this.selectedConditionButton != null) {
            int outlineX = this.selectedConditionButtonX - 1;
            int outlineY = (int)((double)this.selectedConditionButtonY - this.scrollY) - 1;
            int outlineWidth = this.selectedConditionButtonWidth + 2;
            int outlineHeight = this.selectedConditionButtonHeight + 2;
            if (outlineY > -outlineHeight && outlineY < this.field_22790) {
                context.method_25294(outlineX, outlineY, outlineX + outlineWidth, outlineY + 2, -1);
                context.method_25294(outlineX, outlineY + outlineHeight - 2, outlineX + outlineWidth, outlineY + outlineHeight, -1);
                context.method_25294(outlineX, outlineY, outlineX + 2, outlineY + outlineHeight, -1);
                context.method_25294(outlineX + outlineWidth - 2, outlineY, outlineX + outlineWidth, outlineY + outlineHeight, -1);
            }
        }
    }

    private void saveBlock() {
        switch (this.block.type) {
            case "comment": {
                if (this.commentField == null) break;
                String commentText = this.commentField.method_1882();
                if (McLooperMod.isVerboseLogging() && commentText.length() > 30) {
                    McLooperMod.LOGGER.info("Saving comment text: '{}' (length: {})", (Object)commentText, (Object)commentText.length());
                }
                this.block.params.put("text", commentText);
                break;
            }
            case "exit": {
                break;
            }
            case "chat": {
                if (this.messageField == null) break;
                String chatText = this.messageField.method_1882();
                if (McLooperMod.isVerboseLogging() && chatText.length() > 30) {
                    McLooperMod.LOGGER.info("Saving chat message: '{}' (length: {})", (Object)chatText, (Object)chatText.length());
                }
                this.block.params.put("message", chatText);
                break;
            }
            case "client_message": {
                if (this.messageField == null) break;
                this.block.params.put("message", this.messageField.method_1882());
                break;
            }
            case "wait": {
                if (this.timeField == null || this.unitField == null) break;
                String timeText = this.timeField.method_1882();
                if (timeText.matches("\\[\\d+-\\d+\\]")) {
                    this.block.params.put("time", timeText);
                    this.block.params.put("unit", this.unitField.method_1882());
                    break;
                }
                try {
                    int time = Integer.parseInt(timeText);
                    this.block.params.put("time", time);
                    this.block.params.put("unit", this.unitField.method_1882());
                }
                catch (NumberFormatException time) {}
                break;
            }
            case "call_loop": {
                if (this.loopNameField == null) break;
                this.block.params.put("loop", this.loopNameField.method_1882());
                break;
            }
            case "click_gui_item": {
                if (this.messageField != null) {
                    this.block.params.put("name_pattern", this.messageField.method_1882());
                }
                if (this.commentField == null) break;
                this.block.params.put("var_name", this.commentField.method_1882());
                break;
            }
            case "loop": {
                if (this.countField == null) break;
                try {
                    int count = Integer.parseInt(this.countField.method_1882());
                    this.block.params.put("count", count);
                }
                catch (NumberFormatException count) {}
                break;
            }
            case "post_request": {
                if (this.messageField != null) {
                    String url = this.messageField.method_1882();
                    this.block.params.put("url", url);
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Saving POST URL: {} characters", (Object)url.length());
                    }
                }
                if (this.commentField != null) {
                    String body = this.commentField.method_1882();
                    this.block.params.put("body", body);
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Saving POST body: {} characters - '{}'", (Object)body.length(), body.length() > 100 ? body.substring(0, 100) + "..." : body);
                    }
                }
                if (this.timeField != null) {
                    try {
                        int timeout = Integer.parseInt(this.timeField.method_1882());
                        this.block.params.put("timeout", timeout);
                    }
                    catch (NumberFormatException e) {
                        this.block.params.put("timeout", 5000);
                    }
                }
                if (this.unitField == null) break;
                this.block.params.put("response_var", this.unitField.method_1882());
                break;
            }
            case "if": 
            case "wait_until": {
                if (this.commentField == null || this.messageField == null) break;
                if (this.block.condition == null) {
                    this.block.condition = new TaskCondition("variable_equals");
                }
                if ("gui_item_exists".equals(this.block.condition.type)) {
                    this.block.condition.params.put("name_pattern", this.commentField.method_1882());
                    this.block.condition.params.put("var_name", this.messageField.method_1882());
                    break;
                }
                if ("inventory_contains".equals(this.block.condition.type) || "gui_contains".equals(this.block.condition.type)) {
                    String searchMode = (String)this.block.condition.params.getOrDefault("search_mode", "registry");
                    if ("regex".equals(searchMode)) {
                        String regexText = this.commentField.method_1882();
                        if (McLooperMod.isVerboseLogging() && regexText.length() > 30) {
                            McLooperMod.LOGGER.info("Saving condition regex: '{}' (length: {})", (Object)regexText, (Object)regexText.length());
                        }
                        this.block.condition.params.put("regex_pattern", regexText);
                    } else {
                        String itemText = this.commentField.method_1882();
                        if (McLooperMod.isVerboseLogging() && itemText.length() > 30) {
                            McLooperMod.LOGGER.info("Saving condition item name: '{}' (length: {})", (Object)itemText, (Object)itemText.length());
                        }
                        this.block.condition.params.put("item_name", itemText);
                    }
                    String varText = this.messageField.method_1882();
                    if (McLooperMod.isVerboseLogging() && varText.length() > 30) {
                        McLooperMod.LOGGER.info("Saving condition var_name: '{}' (length: {})", (Object)varText, (Object)varText.length());
                    }
                    this.block.condition.params.put("var_name", varText);
                    this.block.condition.params.put("search_mode", searchMode);
                    break;
                }
                this.block.condition.params.put("variable", this.commentField.method_1882());
                this.block.condition.params.put("value", this.messageField.method_1882());
                break;
            }
            case "regex_replace": {
                if (this.commentField != null) {
                    this.block.params.put("source_var", this.commentField.method_1882());
                }
                if (this.messageField != null) {
                    this.block.params.put("target_var", this.messageField.method_1882());
                }
                if (this.timeField != null) {
                    this.block.params.put("pattern", this.timeField.method_1882());
                }
                if (this.unitField == null) break;
                this.block.params.put("replacement", this.unitField.method_1882());
                break;
            }
            case "prevent_execution": {
                if (this.timeField == null || this.unitField == null) break;
                try {
                    int time = Integer.parseInt(this.timeField.method_1882());
                    this.block.params.put("time", time);
                    this.block.params.put("unit", this.unitField.method_1882());
                }
                catch (NumberFormatException numberFormatException) {}
                break;
            }
            case "world_click": {
                if (this.messageField == null) break;
                this.block.params.put("target", this.messageField.method_1882());
            }
        }
        this.method_25419();
    }

    public void method_25419() {
        this.field_22787.method_1507((class_437)this.parent);
    }

    public boolean method_25421() {
        return false;
    }

    private boolean supportsNestedBlocks(String blockType) {
        return "if".equals(blockType) || "loop".equals(blockType);
    }

    private void openNestedBlocksEditor() {
        if (this.block.blocks == null) {
            this.block.blocks = new ArrayList<TaskBlock>();
        }
        nestedBlockEditingContext = new NestedBlockEditingContext(this.block, this);
        TaskLoop tempLoop = new TaskLoop();
        tempLoop.name = "Nested Blocks (" + this.block.type + ")";
        tempLoop.blocks = new ArrayList<TaskBlock>(this.block.blocks);
        tempLoop.enabled = false;
        tempLoop.trigger = new TaskTrigger();
        tempLoop.trigger.type = "manual";
        McLooperMod.getConfigManager().getConfig().loops.add(tempLoop);
        BlockEditorScreen.nestedBlockEditingContext.tempLoop = tempLoop;
        this.field_22787.method_1507((class_437)new LoopEditorScreen((class_2561)class_2561.method_43470((String)"Edit Nested Blocks")));
    }

    public static void handleNestedBlockEditReturn() {
        if (nestedBlockEditingContext != null) {
            NestedBlockEditingContext context = nestedBlockEditingContext;
            nestedBlockEditingContext = null;
            if (context.tempLoop != null) {
                context.originalBlock.blocks = new ArrayList<TaskBlock>(context.tempLoop.blocks);
            }
            if (context.tempLoop != null) {
                McLooperMod.getConfigManager().getConfig().loops.remove(context.tempLoop);
            }
            McLooperMod.getConfigManager().saveConfig();
            if (context.originalScreen != null) {
                class_310.method_1551().method_1507((class_437)context.originalScreen);
            }
        }
    }

    private static class NestedBlockEditingContext {
        TaskBlock originalBlock;
        BlockEditorScreen originalScreen;
        TaskLoop tempLoop;

        NestedBlockEditingContext(TaskBlock block, BlockEditorScreen screen) {
            this.originalBlock = block;
            this.originalScreen = screen;
        }
    }
}

