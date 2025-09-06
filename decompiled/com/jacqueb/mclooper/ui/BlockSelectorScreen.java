/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package com.jacqueb.mclooper.ui;

import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.ui.BlockEditorScreen;
import com.jacqueb.mclooper.ui.LoopEditorScreen;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public class BlockSelectorScreen
extends class_437 {
    private final LoopEditorScreen parent;
    private final TaskLoop targetLoop;
    private final int insertionIndex;
    private double scrollY = 0.0;
    private static final double SCROLL_SPEED = 15.0;
    private static final String[] BLOCK_TYPES = new String[]{"comment", "chat", "client_message", "wait", "call_loop", "if", "exit", "loop", "wait_until", "click_gui_item", "world_click", "post_request", "regex_replace", "prevent_execution"};
    private static final String[] BLOCK_DESCRIPTIONS = new String[]{"Add a comment for documentation", "Send a message to chat or run a command", "Show a message only to the client", "Wait for a specified time", "Call another loop", "Conditional execution", "Stop execution of current loop or parent block", "Repeat blocks multiple times", "Wait until a condition is met", "Auto-click items in GUI by name pattern", "Left click (break/attack) or right click (place/use) in world", "Send HTTP POST request with timeout", "Replace text in variables using regex patterns", "Prevent loop execution for specified time"};

    public BlockSelectorScreen(LoopEditorScreen parent, TaskLoop targetLoop, int insertionIndex) {
        super((class_2561)class_2561.method_43470((String)"Select Block Type"));
        this.parent = parent;
        this.targetLoop = targetLoop;
        this.insertionIndex = insertionIndex;
    }

    protected void method_25426() {
        super.method_25426();
        this.method_37067();
        this.repositionWidgets();
    }

    private void repositionWidgets() {
        this.method_37067();
        int startY = 50;
        int buttonWidth = 250;
        int buttonHeight = 20;
        int spacing = 35;
        int leftMargin = 160;
        for (int i = 0; i < BLOCK_TYPES.length; ++i) {
            String blockType = BLOCK_TYPES[i];
            String description = BLOCK_DESCRIPTIONS[i];
            int buttonY = (int)((double)(startY + i * spacing) - this.scrollY);
            if (buttonY <= -buttonHeight || buttonY >= this.field_22790 + buttonHeight) continue;
            class_4185 button = class_4185.method_46430((class_2561)class_2561.method_43470((String)description), btn -> this.selectBlockType(blockType)).method_46434(leftMargin, buttonY, buttonWidth, buttonHeight).method_46431();
            this.method_37063((class_364)button);
        }
        class_4185 cancelButton = class_4185.method_46430((class_2561)class_2561.method_43470((String)"Cancel"), btn -> this.method_25419()).method_46434(this.field_22789 / 2 - 50, this.field_22790 - 40, 100, 20).method_46431();
        this.method_37063((class_364)cancelButton);
    }

    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int spacing = 35;
        int totalContentHeight = BLOCK_TYPES.length * spacing + 100;
        int maxScroll = Math.max(0, totalContentHeight - this.field_22790 + 100);
        this.scrollY = Math.max(0.0, Math.min((double)maxScroll, this.scrollY - verticalAmount * 15.0));
        this.repositionWidgets();
        return true;
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        this.method_25420(context, mouseX, mouseY, delta);
        super.method_25394(context, mouseX, mouseY, delta);
        context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, 0xFFFFFF);
        int startY = 50;
        int spacing = 35;
        int leftMargin = 50;
        for (int i = 0; i < BLOCK_TYPES.length; ++i) {
            String blockType = BLOCK_TYPES[i];
            int labelY = (int)((double)(startY + i * spacing + 6) - this.scrollY);
            if (labelY <= -20 || labelY >= this.field_22790) continue;
            context.method_51439(this.field_22793, (class_2561)class_2561.method_43470((String)blockType), leftMargin, labelY, 0xFFFFFF, false);
        }
        int totalContentHeight = BLOCK_TYPES.length * spacing + 100;
        if (totalContentHeight > this.field_22790 - 100) {
            int scrollBarHeight = Math.max(20, (this.field_22790 - 100) * (this.field_22790 - 100) / totalContentHeight);
            int scrollBarY = (int)(40.0 + (double)(this.field_22790 - 140 - scrollBarHeight) * this.scrollY / (double)Math.max(1, totalContentHeight - this.field_22790 + 100));
            context.method_25294(this.field_22789 - 10, scrollBarY, this.field_22789 - 5, scrollBarY + scrollBarHeight, -2130706433);
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
        this.field_22787.method_1507((class_437)new BlockEditorScreen(this.parent, newBlock));
    }

    public void method_25419() {
        this.field_22787.method_1507((class_437)this.parent);
    }

    public boolean method_25421() {
        return false;
    }
}
