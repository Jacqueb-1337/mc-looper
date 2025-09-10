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
import net.minecraft.util.Formatting;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;

public class LoopEditorScreen
extends net.minecraft.client.gui.screen.Screen {
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
    private net.minecraft.client.gui.widget.ButtonWidget addLoopButton;
    private net.minecraft.client.gui.widget.ButtonWidget deleteLoopButton;
    private net.minecraft.client.gui.widget.ButtonWidget saveButton;
    private net.minecraft.client.gui.widget.ButtonWidget closeButton;
    private net.minecraft.client.gui.widget.ButtonWidget exportLoopButton;
    private net.minecraft.client.gui.widget.ButtonWidget importLoopButton;
    private net.minecraft.client.gui.widget.ButtonWidget addBlockButton;
    private net.minecraft.client.gui.widget.ButtonWidget editBlockButton;
    private net.minecraft.client.gui.widget.ButtonWidget deleteBlockButton;
    private net.minecraft.client.gui.widget.ButtonWidget moveUpButton;
    private net.minecraft.client.gui.widget.ButtonWidget moveDownButton;
    private net.minecraft.client.gui.widget.ButtonWidget runLoopButton;
    private net.minecraft.client.gui.widget.ButtonWidget triggerTypeButton;
    private net.minecraft.client.gui.widget.TextFieldWidget triggerParamField;
    private net.minecraft.client.gui.widget.TextFieldWidget triggerParam2Field;
    private net.minecraft.client.gui.widget.ButtonWidget verboseToggleButton;
    private boolean verboseEnabled = false;
    private net.minecraft.client.gui.widget.ButtonWidget globalVerboseToggleButton;
    private net.minecraft.client.gui.widget.TextFieldWidget loopNameField;
    private net.minecraft.client.gui.widget.TextFieldWidget startupDelayField;

    public LoopEditorScreen(net.minecraft.text.Text title) {
        super(title);
    }

    protected void init() {
        super.init();
        int leftPanelX = 10;
        int rightPanelX = leftPanelX + 200 + 20;
        int buttonY = 30;
        this.addLoopButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Add Loop"), button -> this.addNewLoop()).dimensions(leftPanelX, buttonY, 200, 20).build();
        this.addDrawableChild((ClickableWidget)this.addLoopButton);
        this.deleteLoopButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Delete Loop"), button -> this.deleteSelectedLoop()).dimensions(leftPanelX, buttonY + 20 + 3, 200, 20).build();
        this.addDrawableChild((ClickableWidget)this.deleteLoopButton);
        this.runLoopButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Run Loop"), button -> this.runSelectedLoop()).dimensions(leftPanelX, buttonY + 46, 200, 20).build();
        this.addDrawableChild((ClickableWidget)this.runLoopButton);
        TaskConfig config = McLooperMod.getConfigManager().getConfig();
        this.globalVerboseToggleButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Verbose Logging: " + (config.verboseLogging ? "ON" : "OFF"))), button -> {
            config.verboseLogging = !config.verboseLogging;
            button.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Verbose Logging: " + (config.verboseLogging ? "ON" : "OFF"))));
            McLooperMod.getConfigManager().saveConfig();
        }).dimensions(this.width - 150, 10, 140, 20).build();
        this.addDrawableChild((ClickableWidget)this.globalVerboseToggleButton);
        this.loopNameField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, rightPanelX, buttonY, 200, 20, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop Name"));
        this.loopNameField.setMaxLength(200);
        this.addDrawableChild((ClickableWidget)this.loopNameField);
        int startupDelayY = buttonY + 20 + 3 + 15 + 3;
        this.startupDelayField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, rightPanelX, startupDelayY, 200, 20, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Startup Delay (ticks)"));
        this.startupDelayField.setMaxLength(10);
        this.addDrawableChild((ClickableWidget)this.startupDelayField);
        int triggerY = startupDelayY + 20 + 3 + 15 + 10;
        this.triggerTypeButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Trigger: on_start"), button -> this.cycleTriggerType()).dimensions(rightPanelX, triggerY, 200, 20).build();
        this.addDrawableChild((ClickableWidget)this.triggerTypeButton);
        this.triggerParamField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, rightPanelX, triggerY + 20 + 3 + 15, 98, 20, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Interval"));
        this.triggerParamField.setMaxLength(1000);
        this.addDrawableChild((ClickableWidget)this.triggerParamField);
        this.triggerParam2Field = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, rightPanelX + 100 + 2, triggerY + 20 + 3 + 15, 98, 20, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Unit"));
        this.triggerParam2Field.setMaxLength(1000);
        this.addDrawableChild((ClickableWidget)this.triggerParam2Field);
        this.verboseToggleButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Verbose: OFF"), button -> {
            this.verboseEnabled = !this.verboseEnabled;
            button.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Verbose: " + (this.verboseEnabled ? "ON" : "OFF"))));
        }).dimensions(rightPanelX, triggerY + 46 + 15, 200, 20).build();
        this.addDrawableChild((ClickableWidget)this.verboseToggleButton);
        int blockButtonY = triggerY + 69 + 30 + 3 + 3;
        this.addBlockButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"+"), button -> this.openBlockSelector()).dimensions(rightPanelX, blockButtonY, 25, 20).build();
        this.addDrawableChild((ClickableWidget)this.addBlockButton);
        this.editBlockButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u270e"), button -> this.editSelectedBlock()).dimensions(rightPanelX + 100, blockButtonY, 25, 20).build();
        this.addDrawableChild((ClickableWidget)this.editBlockButton);
        this.deleteBlockButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"-"), button -> this.deleteSelectedBlock()).dimensions(rightPanelX, blockButtonY + 20 + 3, 25, 20).build();
        this.addDrawableChild((ClickableWidget)this.deleteBlockButton);
        this.moveUpButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u2191"), button -> this.moveBlockUp()).dimensions(rightPanelX + 100, blockButtonY + 20 + 3, 20, 20).build();
        this.addDrawableChild((ClickableWidget)this.moveUpButton);
        this.moveDownButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u2193"), button -> this.moveBlockDown()).dimensions(rightPanelX + 150, blockButtonY + 20 + 3, 20, 20).build();
        this.addDrawableChild((ClickableWidget)this.moveDownButton);
        this.updateBlockButtonPositions();
        this.exportLoopButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Export"), button -> this.exportSelectedLoop()).dimensions(this.width - 320, this.height - 30, 70, 20).build();
        this.addDrawableChild((ClickableWidget)this.exportLoopButton);
        this.importLoopButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Import"), button -> this.importLoop()).dimensions(this.width - 240, this.height - 30, 70, 20).build();
        this.addDrawableChild((ClickableWidget)this.importLoopButton);
        this.saveButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Save"), button -> this.saveConfig()).dimensions(this.width - 160, this.height - 30, 70, 20).build();
        this.addDrawableChild((ClickableWidget)this.saveButton);
        this.closeButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Close"), button -> this.close()).dimensions(this.width - 80, this.height - 30, 70, 20).build();
        this.addDrawableChild((ClickableWidget)this.closeButton);
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

    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        this.drawLoopList(context);
        this.drawBlockList(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loops"), 10, 10, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop Configuration"), 230, 10, 0xFFFFFF);
        if (this.selectedLoop != null) {
            int rightPanelX = 230;
            int buttonY = 30;
            int startupDelayY = buttonY + 20 + 3 + 15 + 3;
            int triggerY = startupDelayY + 20 + 3 + 15 + 10;
            context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop Name:"), rightPanelX, buttonY - 15, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Startup Delay (ticks):"), rightPanelX, startupDelayY - 15, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Trigger Type:"), rightPanelX, triggerY - 15, 0xFFFFFF);
            if (this.selectedLoop.trigger != null) {
                switch (this.selectedLoop.trigger.type) {
                    case "interval": {
                        context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Interval:"), rightPanelX, triggerY + 20 + 3, 0xFFFFFF);
                        context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Unit:"), rightPanelX + 100 + 2, triggerY + 20 + 3, 0xFFFFFF);
                        break;
                    }
                    case "on_chat": 
                    case "on_gui_item": {
                        context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Pattern:"), rightPanelX, triggerY + 20 + 3, 0xFFFFFF);
                        context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Var Prefix:"), rightPanelX + 100 + 2, triggerY + 20 + 3, 0xFFFFFF);
                    }
                }
            }
        }
    }

    private void drawLoopList(net.minecraft.client.gui.DrawContext context) {
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
            context.fill(x, currentY, x + 200, currentY + 20, color);
            int toggleX = x + 200 - 20;
            int toggleY = currentY + 2;
            int toggleSize = 16;
            int toggleBgColor = loop.enabled ? -16733696 : -10066330;
            context.fill(toggleX, toggleY, toggleX + toggleSize, toggleY + toggleSize, toggleBgColor);
            context.drawBorder(toggleX, toggleY, toggleSize, toggleSize, -16777216);
            if (loop.enabled) {
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u2713"), toggleX + 3, toggleY + 4, 0xFFFFFF);
            }
            Object object = displayName = loop.name != null ? loop.name : "Unnamed Loop";
            if (((String)displayName).length() > 20) {
                displayName = ((String)displayName).substring(0, 17) + "...";
            }
            int textColor = loop.enabled ? 0xFFFFFF : 0x808080;
            context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)displayName), x + 5, currentY + 6, textColor);
        }
        if (loops.size() > 6) {
            int scrollBarX = x + 200 + 5;
            int scrollBarHeight = 126;
            if (this.loopScrollOffset > 0) {
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u25b2"), scrollBarX, y - 10, 0xFFFFFF);
            }
            if (this.loopScrollOffset < loops.size() - 6) {
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u25bc"), scrollBarX, y + scrollBarHeight + 5, 0xFFFFFF);
            }
        }
    }

    private void drawBlockList(net.minecraft.client.gui.DrawContext context) {
        if (this.selectedLoop == null) {
            return;
        }
        int x = 230;
        int y = 200;
        List<TaskBlock> blocks = this.selectedLoop.blocks;
        context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Blocks"), x, y - 20, 0xFFFFFF);
        int startIndex = this.blockScrollOffset;
        int endIndex = Math.min(blocks.size(), startIndex + 4);
        for (int i = startIndex; i < endIndex; ++i) {
            TaskBlock block = blocks.get(i);
            boolean isSelected = i == this.selectedBlockIndex;
            int currentY = y + (i - startIndex) * 21;
            int backgroundColor = this.getBlockColor(block.type, isSelected);
            context.fill(x, currentY, x + 200, currentY + 20, backgroundColor);
            Object displayText = this.getBlockDisplayText(block);
            if (((String)displayText).length() > 25) {
                displayText = ((String)displayText).substring(0, 22) + "...";
            }
            context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)displayText), x + 5, currentY + 6, 0xFFFFFF);
        }
        if (blocks.size() > 4) {
            int scrollBarX = x + 200 + 5;
            int scrollBarHeight = 84;
            if (this.blockScrollOffset > 0) {
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u25b2"), scrollBarX, y - 10, 0xFFFFFF);
            }
            if (this.blockScrollOffset < blocks.size() - 4) {
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"\u25bc"), scrollBarX, y + scrollBarHeight + 5, 0xFFFFFF);
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

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int clickedIndex;
        if (super.mouseClicked(mouseX, mouseY, button)) {
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

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
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
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void selectLoop(TaskLoop loop) {
        this.selectedLoop = loop;
        this.selectedBlockIndex = -1;
        this.blockScrollOffset = 0;
        this.loopNameField.setText(loop.name != null ? loop.name : "");
        this.loopNameField.setCursor(this.loopNameField.getText().length(), false);
        this.loopNameField.setSelectionStart(this.loopNameField.getText().length());
        this.startupDelayField.setText(String.valueOf(loop.startupDelay));
        this.startupDelayField.setCursor(this.startupDelayField.getText().length(), false);
        this.startupDelayField.setSelectionStart(this.startupDelayField.getText().length());
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
            this.loopNameField.setText("");
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
            this.client.setScreen((net.minecraft.client.gui.screen.Screen)new BlockSelectorScreen(this, this.selectedLoop, this.selectedBlockIndex));
        }
    }

    private void editSelectedBlock() {
        if (this.selectedLoop != null && this.selectedBlockIndex >= 0 && this.selectedBlockIndex < this.selectedLoop.blocks.size()) {
            TaskBlock block = this.selectedLoop.blocks.get(this.selectedBlockIndex);
            this.client.setScreen((net.minecraft.client.gui.screen.Screen)new BlockEditorScreen(this, block));
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
        this.addBlockButton.setPosition(buttonX, buttonY);
        this.editBlockButton.setPosition(buttonX + 30, buttonY);
        this.deleteBlockButton.setPosition(buttonX + 60, buttonY);
        this.moveUpButton.setPosition(buttonX + 90, buttonY);
        this.moveDownButton.setPosition(buttonX + 115, buttonY);
    }

    private void saveConfig() {
        if (this.isNestedEditing()) {
            BlockEditorScreen.handleNestedBlockEditReturn();
            return;
        }
        if (this.selectedLoop != null && !this.loopNameField.getText().trim().isEmpty()) {
            this.selectedLoop.name = this.loopNameField.getText().trim();
            try {
                int startupDelay = Integer.parseInt(this.startupDelayField.getText().trim());
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
                            int interval = Integer.parseInt(this.triggerParamField.getText());
                            this.selectedLoop.trigger.params.put("interval", interval);
                        }
                        catch (NumberFormatException e) {
                            this.selectedLoop.trigger.params.put("interval", 20);
                        }
                        this.selectedLoop.trigger.params.put("unit", this.triggerParam2Field.getText().isEmpty() ? "ticks" : this.triggerParam2Field.getText());
                        break;
                    }
                    case "on_chat": 
                    case "on_gui_item": {
                        this.selectedLoop.trigger.params.put("pattern", this.triggerParamField.getText().isEmpty() ? "(?i).*click.*confirm.*" : this.triggerParamField.getText());
                        this.selectedLoop.trigger.params.put("var_prefix", this.triggerParam2Field.getText().isEmpty() ? "match" : this.triggerParam2Field.getText());
                        break;
                    }
                    case "keybind": {
                        this.selectedLoop.trigger.params.put("key", this.triggerParamField.getText().isEmpty() ? "key.keyboard.f" : this.triggerParamField.getText());
                    }
                }
            }
        }
        McLooperMod.getConfigManager().saveConfig();
        this.client.player.sendMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Config saved!").formatted(Formatting.GREEN), false);
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
        this.deleteLoopButton.active = hasSelectedLoop;
        this.runLoopButton.active = hasSelectedLoop;
        this.addBlockButton.active = hasSelectedLoop;
        this.editBlockButton.active = hasSelectedBlock;
        this.deleteBlockButton.active = hasSelectedBlock;
        this.moveUpButton.active = hasSelectedBlock && this.selectedBlockIndex > 0;
        this.moveDownButton.active = hasSelectedBlock && this.selectedBlockIndex < this.selectedLoop.blocks.size() - 1;
        this.loopNameField.active = hasSelectedLoop;
        this.triggerTypeButton.active = hasSelectedLoop;
        this.triggerParamField.active = hasSelectedLoop;
        this.triggerParam2Field.active = hasSelectedLoop;
        this.verboseToggleButton.active = hasSelectedLoop;
        this.exportLoopButton.active = hasSelectedLoop;
        this.importLoopButton.active = true;
        if (hasSelectedLoop) {
            this.loopNameField.setText(this.selectedLoop.name != null ? this.selectedLoop.name : "");
            if (this.selectedLoop.name != null && this.selectedLoop.name.length() > 0) {
                this.loopNameField.setCursor(this.loopNameField.getText().length(), false);
                this.loopNameField.setSelectionStart(this.loopNameField.getText().length());
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
            this.triggerTypeButton.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Trigger: on_start"));
            this.triggerParamField.setText("");
            this.triggerParam2Field.setText("");
            this.triggerParamField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)""));
            this.triggerParam2Field.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)""));
            this.verboseEnabled = false;
            this.verboseToggleButton.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Verbose: OFF"));
            return;
        }
        Object verboseObj = this.selectedLoop.trigger.params.get("verbose");
        this.verboseEnabled = verboseObj != null && "true".equals(verboseObj.toString());
        this.verboseToggleButton.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Verbose: " + (this.verboseEnabled ? "ON" : "OFF"))));
        this.triggerTypeButton.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Trigger: " + this.selectedLoop.trigger.type)));
        switch (this.selectedLoop.trigger.type) {
            case "interval": {
                Object interval = this.selectedLoop.trigger.params.get("interval");
                Object unit = this.selectedLoop.trigger.params.get("unit");
                this.triggerParamField.setText(interval != null ? interval.toString() : "20");
                this.triggerParamField.setCursor(this.triggerParamField.getText().length(), false);
                this.triggerParamField.setSelectionStart(this.triggerParamField.getText().length());
                this.triggerParam2Field.setText(unit != null ? unit.toString() : "ticks");
                this.triggerParam2Field.setCursor(this.triggerParam2Field.getText().length(), false);
                this.triggerParam2Field.setSelectionStart(this.triggerParam2Field.getText().length());
                this.triggerParamField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Interval"));
                this.triggerParam2Field.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Unit (ticks/seconds)"));
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
                this.triggerParamField.setText(patternStr);
                if (McLooperMod.isVerboseLogging() && patternStr.length() > 30) {
                    McLooperMod.LOGGER.info("Text field after setText: '{}' (length: {})", (Object)this.triggerParamField.getText(), (Object)this.triggerParamField.getText().length());
                }
                this.triggerParamField.setCursor(this.triggerParamField.getText().length(), false);
                this.triggerParamField.setSelectionStart(this.triggerParamField.getText().length());
                this.triggerParam2Field.setText(varPrefixStr);
                this.triggerParam2Field.setCursor(this.triggerParam2Field.getText().length(), false);
                this.triggerParam2Field.setSelectionStart(this.triggerParam2Field.getText().length());
                this.triggerParamField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Regex Pattern"));
                this.triggerParam2Field.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Variable Prefix"));
                break;
            }
            case "on_gui_item": {
                Object guiPattern = this.selectedLoop.trigger.params.get("pattern");
                Object guiVarPrefix = this.selectedLoop.trigger.params.get("var_prefix");
                this.triggerParamField.setText(guiPattern != null ? guiPattern.toString() : "(?i).*click.*confirm.*");
                this.triggerParamField.setCursor(this.triggerParamField.getText().length(), false);
                this.triggerParamField.setSelectionStart(this.triggerParamField.getText().length());
                this.triggerParam2Field.setText(guiVarPrefix != null ? guiVarPrefix.toString() : "gui");
                this.triggerParam2Field.setCursor(this.triggerParam2Field.getText().length(), false);
                this.triggerParam2Field.setSelectionStart(this.triggerParam2Field.getText().length());
                this.triggerParamField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Regex Pattern"));
                this.triggerParam2Field.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Variable Prefix"));
                break;
            }
            case "keybind": {
                Object key = this.selectedLoop.trigger.params.get("key");
                this.triggerParamField.setText(key != null ? key.toString() : "key.keyboard.f");
                this.triggerParamField.setCursor(this.triggerParamField.getText().length(), false);
                this.triggerParamField.setSelectionStart(this.triggerParamField.getText().length());
                this.triggerParam2Field.setText("");
                this.triggerParam2Field.setCursor(this.triggerParam2Field.getText().length(), false);
                this.triggerParam2Field.setSelectionStart(this.triggerParam2Field.getText().length());
                this.triggerParamField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Key (e.g., key.keyboard.f)"));
                this.triggerParam2Field.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)""));
                break;
            }
            default: {
                this.triggerParamField.setText("");
                this.triggerParamField.setCursor(this.triggerParamField.getText().length(), false);
                this.triggerParamField.setSelectionStart(this.triggerParamField.getText().length());
                this.triggerParam2Field.setText("");
                this.triggerParam2Field.setCursor(this.triggerParam2Field.getText().length(), false);
                this.triggerParam2Field.setSelectionStart(this.triggerParam2Field.getText().length());
                this.triggerParamField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"No parameters"));
                this.triggerParam2Field.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)""));
            }
        }
    }

    public boolean shouldPause() {
        return false;
    }

    private void exportSelectedLoop() {
        if (this.selectedLoop == null) {
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"No loop selected for export").formatted(Formatting.RED), false);
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
            try {
                // Try system clipboard first (may fail in headless or restricted environments)
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(Text.literal("Loop '").append(Text.literal(this.selectedLoop.name).formatted(Formatting.YELLOW)).append(Text.literal("' exported to clipboard!").formatted(Formatting.GREEN)), false);
                }
                McLooperMod.LOGGER.info("Exported loop '{}' to clipboard ({} characters)", (Object)this.selectedLoop.name, (Object)json.length());
            }
            catch (java.awt.HeadlessException | SecurityException | IllegalStateException clipboardEx) {
                // Fallback: write export to config directory so user can retrieve it
                try {
                    java.nio.file.Path cfgDir = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir();
                    java.nio.file.Files.createDirectories(cfgDir);
                    String safeName = this.selectedLoop.name == null ? "exported-loop" : this.selectedLoop.name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
                    java.nio.file.Path out = cfgDir.resolve(safeName + "-" + System.currentTimeMillis() + ".json");
                    java.nio.file.Files.writeString(out, json);
                    if (this.client != null && this.client.player != null) {
                        // Create a clickable text that opens the file when clicked in supported clients
                        Text pathText = Text.literal(out.toString()).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, out.toString())).withColor(Formatting.YELLOW));
                        Text msg = Text.literal("Loop exported to file: ").formatted(Formatting.GREEN).append(pathText);
                        this.client.player.sendMessage(msg, false);
                    }
                    McLooperMod.LOGGER.info("Exported loop '{}' to file {}", (Object)this.selectedLoop.name, (Object)out.toString());
                }
                catch (Exception fileEx) {
                    if (this.client != null && this.client.player != null) {
                        String msg = fileEx.getMessage() == null ? "unknown error" : fileEx.getMessage();
                        this.client.player.sendMessage(Text.literal((String)("Failed to export loop: " + msg)).formatted(Formatting.RED), false);
                    }
                    McLooperMod.LOGGER.error("Failed to export loop", (Throwable)fileEx);
                }
            }
        }
        catch (Exception e) {
            if (this.client != null && this.client.player != null) {
                String msg = e.getMessage();
                if (msg == null || msg.trim().isEmpty()) {
                    msg = e.getClass().getSimpleName() + " (see log for details)";
                }
                this.client.player.sendMessage(Text.literal((String)("Failed to export loop: " + msg)).formatted(Formatting.RED), false);
            }
            McLooperMod.LOGGER.error("Failed to export loop", (Throwable)e);
        }
    }

    private void importLoop() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String clipboardText = (String)clipboard.getData(DataFlavor.stringFlavor);
            if (clipboardText == null || clipboardText.trim().isEmpty()) {
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Clipboard is empty").formatted(Formatting.RED), false);
                }
                return;
            }
            Gson gson = new Gson();
            TaskLoop importedLoop = (TaskLoop)gson.fromJson(clipboardText.trim(), TaskLoop.class);
            if (importedLoop == null) {
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Invalid loop data in clipboard").formatted(Formatting.RED), false);
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
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop '").append((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)importedLoop.name).formatted(Formatting.YELLOW)).append((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"' imported successfully!").formatted(Formatting.GREEN)), false);
            }
            McLooperMod.LOGGER.info("Imported loop '{}' with {} blocks", (Object)importedLoop.name, (Object)importedLoop.blocks.size());
        }
        catch (JsonSyntaxException e) {
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Invalid JSON format in clipboard").formatted(Formatting.RED), false);
            }
            McLooperMod.LOGGER.error("Failed to parse loop JSON", (Throwable)e);
        }
        catch (Exception e) {
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Failed to import loop: " + e.getMessage())).formatted(Formatting.RED), false);
            }
            McLooperMod.LOGGER.error("Failed to import loop", (Throwable)e);
        }
    }

    public void close() {
        BlockEditorScreen.handleNestedBlockEditReturn();
        if (!this.isNestedEditing()) {
            super.close();
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

