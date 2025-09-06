/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package com.jacqueb.mclooper.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jacqueb.mclooper.McLooperMod;
import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskConfig;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.config.TaskTrigger;
import com.jacqueb.mclooper.ui.BlockEditorScreen;
import com.jacqueb.mclooper.ui.BlockSelectorScreen;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public class LoopEditorScreen
extends class_437 {
    private static final int PANEL_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 3;
    private static final int SECTION_SPACING = 20;
    private static final int LIST_ITEM_SPACING = 1;
    private static final int TRIGGER_SECTION_BOTTOM_SPACING = 3;
    private static final int PATTERN_SECTION_BOTTOM_SPACING = 3;
    private static final int STARTUP_DELAY_SECTION_BOTTOM_SPACING = 3;
    private static final int MAX_VISIBLE_LOOPS = 6;
    private static final int MAX_VISIBLE_BLOCKS = 4;
    private TaskLoop selectedLoop;
    private int selectedBlockIndex = -1;
    private int loopScrollOffset = 0;
    private int blockScrollOffset = 0;
    private class_4185 addLoopButton;
    private class_4185 deleteLoopButton;
    private class_4185 saveButton;
    private class_4185 closeButton;
    private class_4185 exportLoopButton;
    private class_4185 importLoopButton;
    private class_4185 addBlockButton;
    private class_4185 editBlockButton;
    private class_4185 deleteBlockButton;
    private class_4185 moveUpButton;
    private class_4185 moveDownButton;
    private class_4185 runLoopButton;
    private class_4185 triggerTypeButton;
    private class_342 triggerParamField;
    private class_342 triggerParam2Field;
    private class_4185 verboseToggleButton;
    private boolean verboseEnabled = false;
    private class_4185 globalVerboseToggleButton;
    private class_342 loopNameField;
    private class_342 startupDelayField;

    public LoopEditorScreen(class_2561 title) {
        super(title);
    }

    protected void method_25426() {
        super.method_25426();
        int leftPanelX = 10;
        int rightPanelX = leftPanelX + 200 + 20;
        int buttonY = 30;
        this.addLoopButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Add Loop"), button -> this.addNewLoop()).method_46434(leftPanelX, buttonY, 200, 20).method_46431();
        this.method_37063((class_364)this.addLoopButton);
        this.deleteLoopButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Delete Loop"), button -> this.deleteSelectedLoop()).method_46434(leftPanelX, buttonY + 20 + 3, 200, 20).method_46431();
        this.method_37063((class_364)this.deleteLoopButton);
        this.runLoopButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Run Loop"), button -> this.runSelectedLoop()).method_46434(leftPanelX, buttonY + 46, 200, 20).method_46431();
        this.method_37063((class_364)this.runLoopButton);
        TaskConfig config = McLooperMod.getConfigManager().getConfig();
        this.globalVerboseToggleButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)("Verbose Logging: " + (config.verboseLogging ? "ON" : "OFF"))), button -> {
            config.verboseLogging = !config.verboseLogging;
            button.method_25355((class_2561)class_2561.method_43470((String)("Verbose Logging: " + (config.verboseLogging ? "ON" : "OFF"))));
            McLooperMod.getConfigManager().saveConfig();
        }).method_46434(this.field_22789 - 150, 10, 140, 20).method_46431();
        this.method_37063((class_364)this.globalVerboseToggleButton);
        this.loopNameField = new class_342(this.field_22793, rightPanelX, buttonY, 200, 20, (class_2561)class_2561.method_43470((String)"Loop Name"));
        this.loopNameField.method_1880(200);
        this.method_37063((class_364)this.loopNameField);
        int startupDelayY = buttonY + 20 + 3 + 15 + 3;
        this.startupDelayField = new class_342(this.field_22793, rightPanelX, startupDelayY, 200, 20, (class_2561)class_2561.method_43470((String)"Startup Delay (ticks)"));
        this.startupDelayField.method_1880(10);
        this.method_37063((class_364)this.startupDelayField);
        int triggerY = startupDelayY + 20 + 3 + 15 + 10;
        this.triggerTypeButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Trigger: on_start"), button -> this.cycleTriggerType()).method_46434(rightPanelX, triggerY, 200, 20).method_46431();
        this.method_37063((class_364)this.triggerTypeButton);
        this.triggerParamField = new class_342(this.field_22793, rightPanelX, triggerY + 20 + 3 + 15, 98, 20, (class_2561)class_2561.method_43470((String)"Interval"));
        this.triggerParamField.method_1880(1000);
        this.method_37063((class_364)this.triggerParamField);
        this.triggerParam2Field = new class_342(this.field_22793, rightPanelX + 100 + 2, triggerY + 20 + 3 + 15, 98, 20, (class_2561)class_2561.method_43470((String)"Unit"));
        this.triggerParam2Field.method_1880(1000);
        this.method_37063((class_364)this.triggerParam2Field);
        this.verboseToggleButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Verbose: OFF"), button -> {
            this.verboseEnabled = !this.verboseEnabled;
            button.method_25355((class_2561)class_2561.method_43470((String)("Verbose: " + (this.verboseEnabled ? "ON" : "OFF"))));
        }).method_46434(rightPanelX, triggerY + 46 + 15, 200, 20).method_46431();
        this.method_37063((class_364)this.verboseToggleButton);
        int blockButtonY = triggerY + 69 + 30 + 3 + 3;
        this.addBlockButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"+"), button -> this.openBlockSelector()).method_46434(rightPanelX, blockButtonY, 25, 20).method_46431();
        this.method_37063((class_364)this.addBlockButton);
        this.editBlockButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u270e"), button -> this.editSelectedBlock()).method_46434(rightPanelX + 100, blockButtonY, 25, 20).method_46431();
        this.method_37063((class_364)this.editBlockButton);
        this.deleteBlockButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"-"), button -> this.deleteSelectedBlock()).method_46434(rightPanelX, blockButtonY + 20 + 3, 25, 20).method_46431();
        this.method_37063((class_364)this.deleteBlockButton);
        this.moveUpButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u2191"), button -> this.moveBlockUp()).method_46434(rightPanelX + 100, blockButtonY + 20 + 3, 20, 20).method_46431();
        this.method_37063((class_364)this.moveUpButton);
        this.moveDownButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u2193"), button -> this.moveBlockDown()).method_46434(rightPanelX + 150, blockButtonY + 20 + 3, 20, 20).method_46431();
        this.method_37063((class_364)this.moveDownButton);
        this.updateBlockButtonPositions();
        this.exportLoopButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Export"), button -> this.exportSelectedLoop()).method_46434(this.field_22789 - 320, this.field_22790 - 30, 70, 20).method_46431();
        this.method_37063((class_364)this.exportLoopButton);
        this.importLoopButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Import"), button -> this.importLoop()).method_46434(this.field_22789 - 240, this.field_22790 - 30, 70, 20).method_46431();
        this.method_37063((class_364)this.importLoopButton);
        this.saveButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Save"), button -> this.saveConfig()).method_46434(this.field_22789 - 160, this.field_22790 - 30, 70, 20).method_46431();
        this.method_37063((class_364)this.saveButton);
        this.closeButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Close"), button -> this.method_25419()).method_46434(this.field_22789 - 80, this.field_22790 - 30, 70, 20).method_46431();
        this.method_37063((class_364)this.closeButton);
        this.autoSelectNestedLoop();
        this.updateButtonStates();
    }

    private void autoSelectNestedLoop() {
        List<TaskLoop> loops = McLooperMod.getConfigManager().getConfig().loops;
        for (TaskLoop loop : loops) {
            if (loop.name == null || !loop.name.startsWith("Nested Blocks (") || loop.enabled) continue;
            this.selectLoop(loop);
            break;
        }
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        this.method_25420(context, mouseX, mouseY, delta);
        this.drawLoopList(context);
        this.drawBlockList(context);
        super.method_25394(context, mouseX, mouseY, delta);
        context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Loops"), 10, 10, 0xFFFFFF, false);
        context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Loop Configuration"), 230, 10, 0xFFFFFF, false);
        if (this.selectedLoop != null) {
            int rightPanelX = 230;
            int buttonY = 30;
            int startupDelayY = buttonY + 20 + 3 + 15 + 3;
            int triggerY = startupDelayY + 20 + 3 + 15 + 10;
            context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Loop Name:"), rightPanelX, buttonY - 15, 0xFFFFFF, false);
            context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Startup Delay (ticks):"), rightPanelX, startupDelayY - 15, 0xFFFFFF, false);
            context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Trigger Type:"), rightPanelX, triggerY - 15, 0xFFFFFF, false);
            if (this.selectedLoop.trigger != null) {
                switch (this.selectedLoop.trigger.type) {
                    case "interval": {
                        context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Interval:"), rightPanelX, triggerY + 20 + 3, 0xFFFFFF, false);
                        context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Unit:"), rightPanelX + 100 + 2, triggerY + 20 + 3, 0xFFFFFF, false);
                        break;
                    }
                    case "on_chat": 
                    case "on_gui_item": {
                        context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Pattern:"), rightPanelX, triggerY + 20 + 3, 0xFFFFFF, false);
                        context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Var Prefix:"), rightPanelX + 100 + 2, triggerY + 20 + 3, 0xFFFFFF, false);
                    }
                }
            }
        }
    }

    private void drawLoopList(class_332 context) {
        int x = 10;
        int y = 100;
        List<TaskLoop> loops = McLooperMod.getConfigManager().getConfig().loops;
        int startIndex = this.loopScrollOffset;
        int endIndex = Math.min(loops.size(), startIndex + 6);
        for (int i = startIndex; i < endIndex; ++i) {
            Object displayName;
            TaskLoop loop = loops.get(i);
            boolean isSelected = loop == this.selectedLoop;
            int color = isSelected ? -2130706433 : 0x40FFFFFF;
            int currentY = y + (i - startIndex) * 21;
            context.method_25294(x, currentY, x + 200, currentY + 20, color);
            int toggleX = x + 200 - 20;
            int toggleY = currentY + 2;
            int toggleSize = 16;
            int toggleBgColor = loop.enabled ? -16733696 : -10066330;
            context.method_25294(toggleX, toggleY, toggleX + toggleSize, toggleY + toggleSize, toggleBgColor);
            context.method_49601(toggleX, toggleY, toggleSize, toggleSize, -16777216);
            if (loop.enabled) {
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"\u2713"), toggleX + 3, toggleY + 4, 0xFFFFFF, false);
            }
            Object object = displayName = loop.name != null ? loop.name : "Unnamed Loop";
            if (((String)displayName).length() > 20) {
                displayName = ((String)displayName).substring(0, 17) + "...";
            }
            int textColor = loop.enabled ? 0xFFFFFF : 0x808080;
            context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)displayName), x + 5, currentY + 6, textColor, false);
        }
        if (loops.size() > 6) {
            int scrollBarX = x + 200 + 5;
            int scrollBarHeight = 126;
            if (this.loopScrollOffset > 0) {
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"\u25b2"), scrollBarX, y - 10, 0xFFFFFF, false);
            }
            if (this.loopScrollOffset < loops.size() - 6) {
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"\u25bc"), scrollBarX, y + scrollBarHeight + 5, 0xFFFFFF, false);
            }
        }
    }

    private void drawBlockList(class_332 context) {
        if (this.selectedLoop == null) {
            return;
        }
        int x = 230;
        int y = 200;
        List<TaskBlock> blocks = this.selectedLoop.blocks;
        context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"Blocks"), x, y - 20, 0xFFFFFF, false);
        int startIndex = this.blockScrollOffset;
        int endIndex = Math.min(blocks.size(), startIndex + 4);
        for (int i = startIndex; i < endIndex; ++i) {
            TaskBlock block = blocks.get(i);
            boolean isSelected = i == this.selectedBlockIndex;
            int currentY = y + (i - startIndex) * 21;
            int backgroundColor = this.getBlockColor(block.type, isSelected);
            context.method_25294(x, currentY, x + 200, currentY + 20, backgroundColor);
            Object displayText = this.getBlockDisplayText(block);
            if (((String)displayText).length() > 25) {
                displayText = ((String)displayText).substring(0, 22) + "...";
            }
            context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)displayText), x + 5, currentY + 6, 0xFFFFFF, false);
        }
        if (blocks.size() > 4) {
            int scrollBarX = x + 200 + 5;
            int scrollBarHeight = 84;
            if (this.blockScrollOffset > 0) {
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"\u25b2"), scrollBarX, y - 10, 0xFFFFFF, false);
            }
            if (this.blockScrollOffset < blocks.size() - 4) {
                context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)"\u25bc"), scrollBarX, y + scrollBarHeight + 5, 0xFFFFFF, false);
            }
        }
    }

    private int getBlockColor(String blockType, boolean isSelected) {
        int baseColor = switch (blockType) {
            case "comment" -> 0x40FFFF00;
            case "if", "exit", "loop", "wait_until" -> 0x4000FFFF;
            case "chat" -> 0x4000FF00;
            case "client_message" -> 1073774847;
            case "steal_items" -> 0x40FF0000;
            case "wait" -> 1090486272;
            default -> 0x40FFFFFF;
        };
        return isSelected ? baseColor | 0x40000000 : baseColor;
    }

    private String getBlockDisplayText(TaskBlock block) {
        switch (block.type) {
            case "comment": {
                String text = (String)block.params.get("text");
                return "// " + (text != null ? text : "Comment");
            }
            case "chat": {
                String message = (String)block.params.get("message");
                if (message != null && message.startsWith("/")) {
                    return "Command: " + message;
                }
                return "Chat: " + (message != null ? message : "");
            }
            case "client_message": {
                String clientMsg = (String)block.params.get("message");
                return "Client Msg: " + (clientMsg != null ? clientMsg : "");
            }
            case "wait": {
                Object time = block.params.get("time");
                String unit = (String)block.params.getOrDefault("unit", "ticks");
                return "Wait " + String.valueOf(time) + " " + unit;
            }
            case "call_loop": {
                String loopName = (String)block.params.get("loop");
                return "Call: " + (loopName != null ? loopName : "");
            }
            case "if": {
                return "If (condition)";
            }
            case "loop": {
                Object count = block.params.get("count");
                return "Loop " + String.valueOf(count) + " times";
            }
            case "wait_until": {
                return "Wait until (condition)";
            }
            case "world_click": {
                String clickType = (String)block.params.getOrDefault("click_type", "right");
                String target = (String)block.params.getOrDefault("target", "crosshair");
                return clickType.substring(0, 1).toUpperCase() + clickType.substring(1) + " click " + target;
            }
        }
        return block.type;
    }

    public boolean method_25402(double mouseX, double mouseY, int button) {
        int clickedIndex;
        if (super.method_25402(mouseX, mouseY, button)) {
            return true;
        }
        if (mouseX >= 10.0 && mouseX <= 210.0 && mouseY >= 100.0) {
            clickedIndex = (int)((mouseY - 100.0) / 21.0) + this.loopScrollOffset;
            List<TaskLoop> loops = McLooperMod.getConfigManager().getConfig().loops;
            int startIndex = this.loopScrollOffset;
            int endIndex = Math.min(loops.size(), startIndex + 6);
            if (clickedIndex >= startIndex && clickedIndex < endIndex && clickedIndex < loops.size()) {
                TaskLoop loop = loops.get(clickedIndex);
                int displayIndex = clickedIndex - this.loopScrollOffset;
                int loopY = 100 + displayIndex * 21;
                int toggleX = 190;
                int toggleY = loopY + 2;
                if (mouseX >= (double)toggleX && mouseX <= (double)(toggleX + 16) && mouseY >= (double)toggleY && mouseY <= (double)(toggleY + 16)) {
                    loop.enabled = !loop.enabled;
                    McLooperMod.getConfigManager().saveConfig();
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Loop '{}' {}", (Object)loop.name, (Object)(loop.enabled ? "enabled" : "disabled"));
                    }
                    return true;
                }
                this.selectLoop(loop);
                return true;
            }
        }
        if (this.selectedLoop != null && mouseX >= 230.0 && mouseX <= 430.0 && mouseY >= 200.0) {
            clickedIndex = (int)((mouseY - 200.0) / 21.0) + this.blockScrollOffset;
            int startIndex = this.blockScrollOffset;
            int endIndex = Math.min(this.selectedLoop.blocks.size(), startIndex + 4);
            if (clickedIndex >= startIndex && clickedIndex < endIndex && clickedIndex < this.selectedLoop.blocks.size()) {
                this.selectedBlockIndex = clickedIndex;
                this.updateButtonStates();
                this.updateBlockButtonPositions();
                return true;
            }
        }
        return false;
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        List<TaskLoop> loops = McLooperMod.getConfigManager().getConfig().loops;
        if (mouseX >= 10.0 && mouseX <= 210.0 && mouseY >= 100.0 && loops.size() > 6) {
            int maxScroll = Math.max(0, loops.size() - 6);
            this.loopScrollOffset = Math.max(0, Math.min(maxScroll, this.loopScrollOffset - (int)(verticalAmount * 3.0)));
            return true;
        }
        if (this.selectedLoop != null && mouseX >= 230.0 && mouseX <= 430.0 && mouseY >= 200.0 && this.selectedLoop.blocks.size() > 4) {
            int maxScroll = Math.max(0, this.selectedLoop.blocks.size() - 4);
            this.blockScrollOffset = Math.max(0, Math.min(maxScroll, this.blockScrollOffset - (int)(verticalAmount * 3.0)));
            this.updateBlockButtonPositions();
            return true;
        }
        return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void selectLoop(TaskLoop loop) {
        this.selectedLoop = loop;
        this.selectedBlockIndex = -1;
        this.blockScrollOffset = 0;
        this.loopNameField.method_1852(loop.name != null ? loop.name : "");
        this.loopNameField.method_1875(this.loopNameField.method_1882().length());
        this.loopNameField.method_1884(this.loopNameField.method_1882().length());
        this.startupDelayField.method_1852(String.valueOf(loop.startupDelay));
        this.startupDelayField.method_1875(this.startupDelayField.method_1882().length());
        this.startupDelayField.method_1884(this.startupDelayField.method_1882().length());
        this.updateButtonStates();
        this.updateBlockButtonPositions();
    }

    private void addNewLoop() {
        TaskLoop newLoop = new TaskLoop("NewLoop_" + System.currentTimeMillis());
        newLoop.trigger = new TaskTrigger("manual");
        TaskBlock commentBlock = new TaskBlock("comment");
        commentBlock.params.put("text", "New loop - add your blocks here");
        newLoop.blocks.add(commentBlock);
        McLooperMod.getConfigManager().getConfig().loops.add(newLoop);
        this.selectLoop(newLoop);
    }

    private void deleteSelectedLoop() {
        if (this.selectedLoop != null) {
            McLooperMod.getConfigManager().getConfig().loops.remove(this.selectedLoop);
            this.selectedLoop = null;
            this.selectedBlockIndex = -1;
            this.loopNameField.method_1852("");
            this.updateButtonStates();
        }
    }

    private void runSelectedLoop() {
        if (this.selectedLoop != null) {
            McLooperMod.getLoopExecutor().executeLoop(this.selectedLoop);
        }
    }

    private void openBlockSelector() {
        if (this.selectedLoop != null) {
            this.field_22787.method_1507((class_437)new BlockSelectorScreen(this, this.selectedLoop, this.selectedBlockIndex));
        }
    }

    private void editSelectedBlock() {
        if (this.selectedLoop != null && this.selectedBlockIndex >= 0 && this.selectedBlockIndex < this.selectedLoop.blocks.size()) {
            TaskBlock block = this.selectedLoop.blocks.get(this.selectedBlockIndex);
            this.field_22787.method_1507((class_437)new BlockEditorScreen(this, block));
        }
    }

    private void deleteSelectedBlock() {
        if (this.selectedLoop != null && this.selectedBlockIndex >= 0 && this.selectedBlockIndex < this.selectedLoop.blocks.size()) {
            if (McLooperMod.isVerboseLogging()) {
                McLooperMod.LOGGER.info("Deleting block at index {}", (Object)this.selectedBlockIndex);
            }
            this.selectedLoop.blocks.remove(this.selectedBlockIndex);
            if (this.selectedBlockIndex >= this.selectedLoop.blocks.size()) {
                this.selectedBlockIndex = this.selectedLoop.blocks.size() - 1;
            }
            if (this.selectedBlockIndex < 0) {
                this.selectedBlockIndex = -1;
            }
            this.updateButtonStates();
            this.updateBlockButtonPositions();
            McLooperMod.getConfigManager().saveConfig();
        }
    }

    private void moveBlockUp() {
        if (this.selectedLoop != null && this.selectedBlockIndex > 0 && this.selectedBlockIndex < this.selectedLoop.blocks.size()) {
            if (McLooperMod.isVerboseLogging()) {
                McLooperMod.LOGGER.info("Moving block up from index {} to {}", (Object)this.selectedBlockIndex, (Object)(this.selectedBlockIndex - 1));
            }
            TaskBlock block = this.selectedLoop.blocks.remove(this.selectedBlockIndex);
            this.selectedLoop.blocks.add(this.selectedBlockIndex - 1, block);
            --this.selectedBlockIndex;
            this.updateButtonStates();
            this.updateBlockButtonPositions();
            McLooperMod.getConfigManager().saveConfig();
        }
    }

    private void moveBlockDown() {
        if (this.selectedLoop != null && this.selectedBlockIndex >= 0 && this.selectedBlockIndex < this.selectedLoop.blocks.size() - 1) {
            if (McLooperMod.isVerboseLogging()) {
                McLooperMod.LOGGER.info("Moving block down from index {} to {}", (Object)this.selectedBlockIndex, (Object)(this.selectedBlockIndex + 1));
            }
            TaskBlock block = this.selectedLoop.blocks.remove(this.selectedBlockIndex);
            this.selectedLoop.blocks.add(this.selectedBlockIndex + 1, block);
            ++this.selectedBlockIndex;
            this.updateButtonStates();
            this.updateBlockButtonPositions();
            McLooperMod.getConfigManager().saveConfig();
        }
    }

    private void updateBlockButtonPositions() {
        int buttonY;
        if (this.selectedLoop == null) {
            return;
        }
        int blocksStartY = 200;
        if (this.selectedBlockIndex >= 0 && this.selectedBlockIndex < this.selectedLoop.blocks.size()) {
            int visualIndex = this.selectedBlockIndex - this.blockScrollOffset;
            buttonY = visualIndex >= 0 && visualIndex < 4 ? blocksStartY + visualIndex * 21 : -100;
        } else {
            int visibleBlocks = Math.min(this.selectedLoop.blocks.size() - this.blockScrollOffset, 4);
            buttonY = blocksStartY + visibleBlocks * 21;
        }
        int buttonX = 433;
        this.addBlockButton.method_48229(buttonX, buttonY);
        this.editBlockButton.method_48229(buttonX + 30, buttonY);
        this.deleteBlockButton.method_48229(buttonX + 60, buttonY);
        this.moveUpButton.method_48229(buttonX + 90, buttonY);
        this.moveDownButton.method_48229(buttonX + 115, buttonY);
    }

    private void saveConfig() {
        if (this.isNestedEditing()) {
            BlockEditorScreen.handleNestedBlockEditReturn();
            return;
        }
        if (this.selectedLoop != null && !this.loopNameField.method_1882().trim().isEmpty()) {
            this.selectedLoop.name = this.loopNameField.method_1882().trim();
            try {
                int startupDelay = Integer.parseInt(this.startupDelayField.method_1882().trim());
                this.selectedLoop.startupDelay = Math.max(0, startupDelay);
            }
            catch (NumberFormatException e) {
                this.selectedLoop.startupDelay = 0;
            }
            if (this.selectedLoop.trigger != null) {
                this.selectedLoop.trigger.params.put("verbose", String.valueOf(this.verboseEnabled));
                switch (this.selectedLoop.trigger.type) {
                    case "interval": {
                        try {
                            int interval = Integer.parseInt(this.triggerParamField.method_1882());
                            this.selectedLoop.trigger.params.put("interval", interval);
                        }
                        catch (NumberFormatException e) {
                            this.selectedLoop.trigger.params.put("interval", 20);
                        }
                        this.selectedLoop.trigger.params.put("unit", this.triggerParam2Field.method_1882().isEmpty() ? "ticks" : this.triggerParam2Field.method_1882());
                        break;
                    }
                    case "on_chat": 
                    case "on_gui_item": {
                        this.selectedLoop.trigger.params.put("pattern", this.triggerParamField.method_1882().isEmpty() ? "(?i).*click.*confirm.*" : this.triggerParamField.method_1882());
                        this.selectedLoop.trigger.params.put("var_prefix", this.triggerParam2Field.method_1882().isEmpty() ? "match" : this.triggerParam2Field.method_1882());
                        break;
                    }
                    case "keybind": {
                        this.selectedLoop.trigger.params.put("key", this.triggerParamField.method_1882().isEmpty() ? "key.keyboard.f" : this.triggerParamField.method_1882());
                    }
                }
            }
        }
        McLooperMod.getConfigManager().saveConfig();
        this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)"Config saved!").method_27692(class_124.field_1060), false);
    }

    private void updateButtonStates() {
        boolean hasSelectedLoop;
        boolean bl = hasSelectedLoop = this.selectedLoop != null;
        if (hasSelectedLoop && this.selectedLoop.blocks.size() > 0 && (this.selectedBlockIndex < 0 || this.selectedBlockIndex >= this.selectedLoop.blocks.size())) {
            int startIndex = this.blockScrollOffset;
            int endIndex = Math.min(this.selectedLoop.blocks.size(), startIndex + 4);
            if (endIndex > startIndex) {
                this.selectedBlockIndex = endIndex - 1;
                this.updateBlockButtonPositions();
            }
        }
        boolean hasSelectedBlock = hasSelectedLoop && this.selectedBlockIndex >= 0 && this.selectedBlockIndex < this.selectedLoop.blocks.size();
        this.deleteLoopButton.field_22763 = hasSelectedLoop;
        this.runLoopButton.field_22763 = hasSelectedLoop;
        this.addBlockButton.field_22763 = hasSelectedLoop;
        this.editBlockButton.field_22763 = hasSelectedBlock;
        this.deleteBlockButton.field_22763 = hasSelectedBlock;
        this.moveUpButton.field_22763 = hasSelectedBlock && this.selectedBlockIndex > 0;
        this.moveDownButton.field_22763 = hasSelectedBlock && this.selectedBlockIndex < this.selectedLoop.blocks.size() - 1;
        this.loopNameField.field_22763 = hasSelectedLoop;
        this.triggerTypeButton.field_22763 = hasSelectedLoop;
        this.triggerParamField.field_22763 = hasSelectedLoop;
        this.triggerParam2Field.field_22763 = hasSelectedLoop;
        this.verboseToggleButton.field_22763 = hasSelectedLoop;
        this.exportLoopButton.field_22763 = hasSelectedLoop;
        this.importLoopButton.field_22763 = true;
        if (hasSelectedLoop) {
            this.loopNameField.method_1852(this.selectedLoop.name != null ? this.selectedLoop.name : "");
            if (this.selectedLoop.name != null && this.selectedLoop.name.length() > 0) {
                this.loopNameField.method_1875(this.loopNameField.method_1882().length());
                this.loopNameField.method_1884(this.loopNameField.method_1882().length());
            }
            this.updateTriggerFields();
        }
    }

    private void cycleTriggerType() {
        if (this.selectedLoop == null) {
            return;
        }
        if (this.selectedLoop.trigger == null) {
            this.selectedLoop.trigger = new TaskTrigger("on_start");
        }
        switch (this.selectedLoop.trigger.type) {
            case "on_start": {
                this.selectedLoop.trigger.type = "interval";
                this.selectedLoop.trigger.params.clear();
                this.selectedLoop.trigger.params.put("interval", 20);
                this.selectedLoop.trigger.params.put("unit", "ticks");
                this.selectedLoop.trigger.params.put("verbose", "false");
                break;
            }
            case "interval": {
                this.selectedLoop.trigger.type = "on_chat";
                this.selectedLoop.trigger.params.clear();
                this.selectedLoop.trigger.params.put("pattern", "(?i).*money.*");
                this.selectedLoop.trigger.params.put("var_prefix", "chat");
                this.selectedLoop.trigger.params.put("verbose", "false");
                break;
            }
            case "on_chat": {
                this.selectedLoop.trigger.type = "on_gui_item";
                this.selectedLoop.trigger.params.clear();
                this.selectedLoop.trigger.params.put("pattern", "(?i).*click.*confirm.*");
                this.selectedLoop.trigger.params.put("var_prefix", "gui");
                this.selectedLoop.trigger.params.put("verbose", "false");
                break;
            }
            case "on_gui_item": {
                this.selectedLoop.trigger.type = "keybind";
                this.selectedLoop.trigger.params.clear();
                this.selectedLoop.trigger.params.put("key", "key.keyboard.f");
                this.selectedLoop.trigger.params.put("verbose", "false");
                break;
            }
            default: {
                this.selectedLoop.trigger.type = "on_start";
                this.selectedLoop.trigger.params.clear();
            }
        }
        this.updateTriggerFields();
        McLooperMod.getConfigManager().saveConfig();
    }

    private void updateTriggerFields() {
        if (this.selectedLoop == null || this.selectedLoop.trigger == null) {
            this.triggerTypeButton.method_25355((class_2561)class_2561.method_43470((String)"Trigger: on_start"));
            this.triggerParamField.method_1852("");
            this.triggerParam2Field.method_1852("");
            this.triggerParamField.method_47404((class_2561)class_2561.method_43470((String)""));
            this.triggerParam2Field.method_47404((class_2561)class_2561.method_43470((String)""));
            this.verboseEnabled = false;
            this.verboseToggleButton.method_25355((class_2561)class_2561.method_43470((String)"Verbose: OFF"));
            return;
        }
        Object verboseObj = this.selectedLoop.trigger.params.get("verbose");
        this.verboseEnabled = verboseObj != null && "true".equals(verboseObj.toString());
        this.verboseToggleButton.method_25355((class_2561)class_2561.method_43470((String)("Verbose: " + (this.verboseEnabled ? "ON" : "OFF"))));
        this.triggerTypeButton.method_25355((class_2561)class_2561.method_43470((String)("Trigger: " + this.selectedLoop.trigger.type)));
        switch (this.selectedLoop.trigger.type) {
            case "interval": {
                Object interval = this.selectedLoop.trigger.params.get("interval");
                Object unit = this.selectedLoop.trigger.params.get("unit");
                this.triggerParamField.method_1852(interval != null ? interval.toString() : "20");
                this.triggerParamField.method_1875(this.triggerParamField.method_1882().length());
                this.triggerParamField.method_1884(this.triggerParamField.method_1882().length());
                this.triggerParam2Field.method_1852(unit != null ? unit.toString() : "ticks");
                this.triggerParam2Field.method_1875(this.triggerParam2Field.method_1882().length());
                this.triggerParam2Field.method_1884(this.triggerParam2Field.method_1882().length());
                this.triggerParamField.method_47404((class_2561)class_2561.method_43470((String)"Interval"));
                this.triggerParam2Field.method_47404((class_2561)class_2561.method_43470((String)"Unit (ticks/seconds)"));
                break;
            }
            case "on_chat": {
                String varPrefixStr;
                Object pattern = this.selectedLoop.trigger.params.get("pattern");
                Object varPrefix = this.selectedLoop.trigger.params.get("var_prefix");
                String patternStr = pattern != null ? pattern.toString() : "(?i).*money.*";
                String string = varPrefixStr = varPrefix != null ? varPrefix.toString() : "chat";
                if (McLooperMod.isVerboseLogging() && patternStr.length() > 30) {
                    McLooperMod.LOGGER.info("Setting pattern: '{}' (length: {})", (Object)patternStr, (Object)patternStr.length());
                }
                this.triggerParamField.method_1852(patternStr);
                if (McLooperMod.isVerboseLogging() && patternStr.length() > 30) {
                    McLooperMod.LOGGER.info("Text field after setText: '{}' (length: {})", (Object)this.triggerParamField.method_1882(), (Object)this.triggerParamField.method_1882().length());
                }
                this.triggerParamField.method_1875(this.triggerParamField.method_1882().length());
                this.triggerParamField.method_1884(this.triggerParamField.method_1882().length());
                this.triggerParam2Field.method_1852(varPrefixStr);
                this.triggerParam2Field.method_1875(this.triggerParam2Field.method_1882().length());
                this.triggerParam2Field.method_1884(this.triggerParam2Field.method_1882().length());
                this.triggerParamField.method_47404((class_2561)class_2561.method_43470((String)"Regex Pattern"));
                this.triggerParam2Field.method_47404((class_2561)class_2561.method_43470((String)"Variable Prefix"));
                break;
            }
            case "on_gui_item": {
                Object guiPattern = this.selectedLoop.trigger.params.get("pattern");
                Object guiVarPrefix = this.selectedLoop.trigger.params.get("var_prefix");
                this.triggerParamField.method_1852(guiPattern != null ? guiPattern.toString() : "(?i).*click.*confirm.*");
                this.triggerParamField.method_1875(this.triggerParamField.method_1882().length());
                this.triggerParamField.method_1884(this.triggerParamField.method_1882().length());
                this.triggerParam2Field.method_1852(guiVarPrefix != null ? guiVarPrefix.toString() : "gui");
                this.triggerParam2Field.method_1875(this.triggerParam2Field.method_1882().length());
                this.triggerParam2Field.method_1884(this.triggerParam2Field.method_1882().length());
                this.triggerParamField.method_47404((class_2561)class_2561.method_43470((String)"Regex Pattern"));
                this.triggerParam2Field.method_47404((class_2561)class_2561.method_43470((String)"Variable Prefix"));
                break;
            }
            case "keybind": {
                Object key = this.selectedLoop.trigger.params.get("key");
                this.triggerParamField.method_1852(key != null ? key.toString() : "key.keyboard.f");
                this.triggerParamField.method_1875(this.triggerParamField.method_1882().length());
                this.triggerParamField.method_1884(this.triggerParamField.method_1882().length());
                this.triggerParam2Field.method_1852("");
                this.triggerParam2Field.method_1875(this.triggerParam2Field.method_1882().length());
                this.triggerParam2Field.method_1884(this.triggerParam2Field.method_1882().length());
                this.triggerParamField.method_47404((class_2561)class_2561.method_43470((String)"Key (e.g., key.keyboard.f)"));
                this.triggerParam2Field.method_47404((class_2561)class_2561.method_43470((String)""));
                break;
            }
            default: {
                this.triggerParamField.method_1852("");
                this.triggerParamField.method_1875(this.triggerParamField.method_1882().length());
                this.triggerParamField.method_1884(this.triggerParamField.method_1882().length());
                this.triggerParam2Field.method_1852("");
                this.triggerParam2Field.method_1875(this.triggerParam2Field.method_1882().length());
                this.triggerParam2Field.method_1884(this.triggerParam2Field.method_1882().length());
                this.triggerParamField.method_47404((class_2561)class_2561.method_43470((String)"No parameters"));
                this.triggerParam2Field.method_47404((class_2561)class_2561.method_43470((String)""));
            }
        }
    }

    public boolean method_25421() {
        return false;
    }

    private void exportSelectedLoop() {
        if (this.selectedLoop == null) {
            if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)"No loop selected for export").method_27692(class_124.field_1061), false);
            }
            return;
        }
        try {
            TaskLoop exportLoop = new TaskLoop();
            exportLoop.name = this.selectedLoop.name;
            exportLoop.enabled = false;
            exportLoop.trigger = this.selectedLoop.trigger;
            exportLoop.blocks = this.selectedLoop.blocks;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson((Object)exportLoop);
            StringSelection stringSelection = new StringSelection(json);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)"Loop '").method_10852((class_2561)class_2561.method_43470((String)this.selectedLoop.name).method_27692(class_124.field_1054)).method_10852((class_2561)class_2561.method_43470((String)"' exported to clipboard!").method_27692(class_124.field_1060)), false);
            }
            McLooperMod.LOGGER.info("Exported loop '{}' to clipboard ({} characters)", (Object)this.selectedLoop.name, (Object)json.length());
        }
        catch (Exception e) {
            if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)("Failed to export loop: " + e.getMessage())).method_27692(class_124.field_1061), false);
            }
            McLooperMod.LOGGER.error("Failed to export loop", (Throwable)e);
        }
    }

    private void importLoop() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String clipboardText = (String)clipboard.getData(DataFlavor.stringFlavor);
            if (clipboardText == null || clipboardText.trim().isEmpty()) {
                if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                    this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)"Clipboard is empty").method_27692(class_124.field_1061), false);
                }
                return;
            }
            Gson gson = new Gson();
            TaskLoop importedLoop = (TaskLoop)gson.fromJson(clipboardText.trim(), TaskLoop.class);
            if (importedLoop == null) {
                if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                    this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)"Invalid loop data in clipboard").method_27692(class_124.field_1061), false);
                }
                return;
            }
            if (importedLoop.name == null || importedLoop.name.trim().isEmpty()) {
                importedLoop.name = "Imported Loop";
            }
            if (importedLoop.trigger == null) {
                importedLoop.trigger = new TaskTrigger();
                importedLoop.trigger.type = "interval";
                importedLoop.trigger.params.put("interval", 20);
                importedLoop.trigger.params.put("unit", "ticks");
            }
            if (importedLoop.blocks == null) {
                importedLoop.blocks = new ArrayList<TaskBlock>();
            }
            List<TaskLoop> existingLoops = McLooperMod.getConfigManager().getConfig().loops;
            String originalName = importedLoop.name;
            int counter = 1;
            while (existingLoops.stream().anyMatch(loop -> loop.name.equals(importedLoop.name))) {
                importedLoop.name = originalName + " (" + counter + ")";
                ++counter;
            }
            importedLoop.enabled = false;
            existingLoops.add(importedLoop);
            McLooperMod.getConfigManager().saveConfig();
            this.selectLoop(importedLoop);
            if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)"Loop '").method_10852((class_2561)class_2561.method_43470((String)importedLoop.name).method_27692(class_124.field_1054)).method_10852((class_2561)class_2561.method_43470((String)"' imported successfully!").method_27692(class_124.field_1060)), false);
            }
            McLooperMod.LOGGER.info("Imported loop '{}' with {} blocks", (Object)importedLoop.name, (Object)importedLoop.blocks.size());
        }
        catch (JsonSyntaxException e) {
            if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)"Invalid JSON format in clipboard").method_27692(class_124.field_1061), false);
            }
            McLooperMod.LOGGER.error("Failed to parse loop JSON", (Throwable)e);
        }
        catch (Exception e) {
            if (this.field_22787 != null && this.field_22787.field_1724 != null) {
                this.field_22787.field_1724.method_7353((class_2561)class_2561.method_43470((String)("Failed to import loop: " + e.getMessage())).method_27692(class_124.field_1061), false);
            }
            McLooperMod.LOGGER.error("Failed to import loop", (Throwable)e);
        }
    }

    public void method_25419() {
        BlockEditorScreen.handleNestedBlockEditReturn();
        if (!this.isNestedEditing()) {
            super.method_25419();
        }
    }

    private boolean isNestedEditing() {
        List<TaskLoop> loops = McLooperMod.getConfigManager().getConfig().loops;
        for (TaskLoop loop : loops) {
            if (loop.name == null || !loop.name.startsWith("Nested Blocks (") || loop.enabled) continue;
            return true;
        }
        return false;
    }
}
