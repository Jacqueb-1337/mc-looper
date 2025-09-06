package com.jacqueb.mclooper.ui;

import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.ui.BlockEditorScreen;
import com.jacqueb.mclooper.ui.LoopEditorScreen;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;

public class BlockSelectorScreen
extends net.minecraft.client.gui.screen.Screen {
    private final LoopEditorScreen parent;
    private final TaskLoop targetLoop;
    private final int insertionIndex;
    private double scrollY = 0.0;
    private static final double SCROLL_SPEED = 15.0;
    private static final String[] BLOCK_TYPES = new String[]{"comment", "chat", "client_message", "wait", "call_loop", "if", "exit", "loop", "wait_until", "click_gui_item", "world_click", "post_request", "regex_replace", "prevent_execution"};
    private static final String[] BLOCK_DESCRIPTIONS = new String[]{"Add a comment for documentation", "Send a message to chat or run a command", "Show a message only to the client", "Wait for a specified time", "Call another loop", "Conditional execution", "Stop execution of current loop or parent block", "Repeat blocks multiple times", "Wait until a condition is met", "Auto-click items in GUI by name pattern", "Left click (break/attack) or right click (place/use) in world", "Send HTTP POST request with timeout", "Replace text in variables using regex patterns", "Prevent loop execution for specified time"};

    public BlockSelectorScreen(LoopEditorScreen parent, TaskLoop targetLoop, int insertionIndex) {
        super((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Select Block Type"));
        this.parent = parent;
        this.targetLoop = targetLoop;
        this.insertionIndex = insertionIndex;
    }

    protected void init() {
        super.init();
        this.clearChildren();
        this.repositionWidgets();
    }

    private void repositionWidgets() {
        this.clearChildren();
        int startY = 50;
        int buttonWidth = 250;
        int buttonHeight = 20;
        int spacing = 35;
        int leftMargin = 160;
        for (int i = 0; i < BLOCK_TYPES.length; ++i) {
            String blockType = BLOCK_TYPES[i];
            String description = BLOCK_DESCRIPTIONS[i];
            int buttonY = (int)((double)(startY + i * spacing) - this.scrollY);
            if (buttonY <= -buttonHeight || buttonY >= this.height + buttonHeight) continue;
            net.minecraft.client.gui.widget.ButtonWidget button = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)description), btn -> this.selectBlockType(blockType)).dimensions(leftMargin, buttonY, buttonWidth, buttonHeight).build();
            this.addDrawableChild((ClickableWidget)button);
        }
        net.minecraft.client.gui.widget.ButtonWidget cancelButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Cancel"), btn -> this.close()).dimensions(this.width / 2 - 50, this.height - 40, 100, 20).build();
        this.addDrawableChild((ClickableWidget)cancelButton);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int spacing = 35;
        int totalContentHeight = BLOCK_TYPES.length * spacing + 100;
        int maxScroll = Math.max(0, totalContentHeight - this.height + 100);
        this.scrollY = Math.max(0.0, Math.min((double)maxScroll, this.scrollY - verticalAmount * 15.0));
        this.repositionWidgets();
        return true;
    }

    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        int startY = 50;
        int spacing = 35;
        int leftMargin = 50;
        for (int i = 0; i < BLOCK_TYPES.length; ++i) {
            String blockType = BLOCK_TYPES[i];
            int labelY = (int)((double)(startY + i * spacing + 6) - this.scrollY);
            if (labelY <= -20 || labelY >= this.height) continue;
            context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)blockType), leftMargin, labelY, 0xFFFFFF);
        }
        int totalContentHeight = BLOCK_TYPES.length * spacing + 100;
        if (totalContentHeight > this.height - 100) {
            int scrollBarHeight = Math.max(20, (this.height - 100) * (this.height - 100) / totalContentHeight);
            int scrollBarY = (int)(40.0 + (double)(this.height - 140 - scrollBarHeight) * this.scrollY / (double)Math.max(1, totalContentHeight - this.height + 100));
            context.fill(this.width - 10, scrollBarY, this.width - 5, scrollBarY + scrollBarHeight, -2130706433);
        }
    }

    private void selectBlockType(String blockType) {
        TaskBlock newBlock = new TaskBlock(blockType);
        switch (blockType) {
            case "comment": {
                newBlock.params.put("text", "New comment");
                break;
            }
            case "chat": {
                newBlock.params.put("message", "Hello world!");
                break;
            }
            case "wait": {
                newBlock.params.put("time", 20);
                newBlock.params.put("unit", "ticks");
                break;
            }
            case "call_loop": {
                newBlock.params.put("loop", "");
                break;
            }
            case "loop": {
                newBlock.params.put("count", 1);
                break;
            }
            case "post_request": {
                newBlock.params.put("url", "https://example.com/api");
                newBlock.params.put("body", "{}");
                newBlock.params.put("timeout", 5000);
                newBlock.params.put("response_var", "response");
                break;
            }
            case "world_click": {
                newBlock.params.put("click_type", "right");
                newBlock.params.put("target", "crosshair");
            }
        }
        if (this.insertionIndex >= 0 && this.insertionIndex < this.targetLoop.blocks.size()) {
            this.targetLoop.blocks.add(this.insertionIndex + 1, newBlock);
        } else {
            this.targetLoop.blocks.add(newBlock);
        }
        this.client.setScreen((net.minecraft.client.gui.screen.Screen)new BlockEditorScreen(this.parent, newBlock));
    }

    public void close() {
        this.client.setScreen((net.minecraft.client.gui.screen.Screen)this.parent);
    }

    public boolean shouldPause() {
        return false;
    }
}
