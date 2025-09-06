package com.jacqueb.mclooper.ui;

import com.jacqueb.mclooper.McLooperMod;
import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskCondition;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.config.TaskTrigger;
import com.jacqueb.mclooper.ui.LoopEditorScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;

public class BlockEditorScreen
extends net.minecraft.client.gui.screen.Screen {
    private final LoopEditorScreen parent;
    private final TaskBlock block;
    private double scrollY = 0.0;
    private static final double SCROLL_SPEED = 15.0;
    private net.minecraft.client.gui.widget.TextFieldWidget messageField;
    private net.minecraft.client.gui.widget.TextFieldWidget timeField;
    private net.minecraft.client.gui.widget.TextFieldWidget unitField;
    private net.minecraft.client.gui.widget.TextFieldWidget loopNameField;
    private net.minecraft.client.gui.widget.TextFieldWidget countField;
    private net.minecraft.client.gui.widget.TextFieldWidget commentField;
    private net.minecraft.client.gui.widget.ButtonWidget selectedConditionButton = null;
    private int selectedConditionButtonX = 0;
    private int selectedConditionButtonY = 0;
    private int selectedConditionButtonWidth = 0;
    private int selectedConditionButtonHeight = 0;
    private static NestedBlockEditingContext nestedBlockEditingContext = null;

    public BlockEditorScreen(LoopEditorScreen parent, TaskBlock block) {
        super((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Edit Block: " + block.type)));
        this.parent = parent;
        this.block = block;
    }

    protected void init() {
        super.init();
        this.clearChildren();
        if (nestedBlockEditingContext != null && BlockEditorScreen.nestedBlockEditingContext.originalScreen == this) {
            BlockEditorScreen.handleNestedBlockEditReturn();
            return;
        }
        this.repositionWidgets();
    }

    private void repositionWidgets() {
        this.clearChildren();
        int fieldWidth = 200;
        int fieldHeight = 20;
        int x = this.width / 2 - fieldWidth / 2;
        int y = (int)(60.0 - this.scrollY);
        int spacing = 40;
        switch (this.block.type) {
            case "comment": {
                this.commentField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Comment"));
                String commentText = (String)this.block.params.getOrDefault("text", "");
                if (McLooperMod.isVerboseLogging() && commentText.length() > 30) {
                    McLooperMod.LOGGER.info("Loading comment into field: '{}' (length: {})", (Object)commentText, (Object)commentText.length());
                }
                this.commentField.setMaxLength(2000);
                this.commentField.setText(commentText);
                if (McLooperMod.isVerboseLogging() && commentText.length() > 30) {
                    McLooperMod.LOGGER.info("Comment field after setText: '{}' (length: {})", (Object)this.commentField.getText(), (Object)this.commentField.getText().length());
                }
                this.commentField.setCursor(this.commentField.getText().length(), false);
                this.commentField.setSelectionStart(this.commentField.getText().length());
                this.addDrawableChild((ClickableWidget)this.commentField);
                break;
            }
            case "exit": {
                break;
            }
            case "chat": {
                this.messageField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Message/Command"));
                this.messageField.setMaxLength(2000);
                this.messageField.setText((String)this.block.params.getOrDefault("message", ""));
                this.messageField.setCursor(this.messageField.getText().length(), false);
                this.messageField.setSelectionStart(this.messageField.getText().length());
                this.addDrawableChild((ClickableWidget)this.messageField);
                break;
            }
            case "client_message": {
                this.messageField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Client Message"));
                this.messageField.setMaxLength(2000);
                this.messageField.setText((String)this.block.params.getOrDefault("message", ""));
                this.messageField.setCursor(this.messageField.getText().length(), false);
                this.messageField.setSelectionStart(this.messageField.getText().length());
                this.addDrawableChild((ClickableWidget)this.messageField);
                break;
            }
            case "wait": {
                this.timeField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth / 2 - 5, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Time"));
                this.timeField.setMaxLength(50);
                this.timeField.setText(String.valueOf(this.block.params.getOrDefault("time", 20)));
                this.timeField.setCursor(this.timeField.getText().length(), false);
                this.timeField.setSelectionStart(this.timeField.getText().length());
                this.addDrawableChild((ClickableWidget)this.timeField);
                this.unitField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x + fieldWidth / 2 + 5, y, fieldWidth / 2 - 5, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Unit"));
                this.unitField.setMaxLength(50);
                this.unitField.setText((String)this.block.params.getOrDefault("unit", "ticks"));
                this.unitField.setCursor(this.unitField.getText().length(), false);
                this.unitField.setSelectionStart(this.unitField.getText().length());
                this.addDrawableChild((ClickableWidget)this.unitField);
                break;
            }
            case "call_loop": {
                this.loopNameField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop Name"));
                this.loopNameField.setMaxLength(200);
                this.loopNameField.setText((String)this.block.params.getOrDefault("loop", ""));
                this.loopNameField.setCursor(this.loopNameField.getText().length(), false);
                this.loopNameField.setSelectionStart(this.loopNameField.getText().length());
                this.addDrawableChild((ClickableWidget)this.loopNameField);
                List<TaskLoop> loops = McLooperMod.getConfigManager().getConfig().loops;
                int buttonY = y += spacing;
                for (TaskLoop loop : loops) {
                    if (loop.name == null) continue;
                    net.minecraft.client.gui.widget.ButtonWidget loopButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)loop.name), btn -> {
                        this.loopNameField.setText(loop.name);
                        this.loopNameField.setCursor(this.loopNameField.getText().length(), false);
                        this.loopNameField.setSelectionStart(this.loopNameField.getText().length());
                    }).dimensions(x, buttonY, fieldWidth, fieldHeight).build();
                    this.addDrawableChild((ClickableWidget)loopButton);
                    buttonY += 25;
                }
                break;
            }
            case "click_gui_item": {
                this.messageField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Name Pattern (regex)"));
                this.messageField.setMaxLength(1000);
                this.messageField.setText((String)this.block.params.getOrDefault("name_pattern", "Click to Confirm"));
                this.messageField.setCursor(this.messageField.getText().length(), false);
                this.messageField.setSelectionStart(this.messageField.getText().length());
                this.addDrawableChild((ClickableWidget)this.messageField);
                this.commentField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Variable Name (optional)"));
                this.commentField.setMaxLength(100);
                this.commentField.setText((String)this.block.params.getOrDefault("var_name", ""));
                this.commentField.setCursor(this.commentField.getText().length(), false);
                this.commentField.setSelectionStart(this.commentField.getText().length());
                this.addDrawableChild((ClickableWidget)this.commentField);
                break;
            }
            case "loop": {
                this.countField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Count"));
                this.countField.setMaxLength(50);
                this.countField.setText(String.valueOf(this.block.params.getOrDefault("count", 1)));
                this.countField.setCursor(this.countField.getText().length(), false);
                this.countField.setSelectionStart(this.countField.getText().length());
                this.addDrawableChild((ClickableWidget)this.countField);
                break;
            }
            case "post_request": {
                this.messageField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"URL"));
                String urlText = (String)this.block.params.getOrDefault("url", "https://example.com/api");
                if (McLooperMod.isVerboseLogging() && urlText.length() > 30) {
                    McLooperMod.LOGGER.info("Loading POST URL into field: '{}' (length: {})", (Object)urlText, (Object)urlText.length());
                }
                this.messageField.setMaxLength(2000);
                this.messageField.setText(urlText);
                if (McLooperMod.isVerboseLogging() && urlText.length() > 30) {
                    McLooperMod.LOGGER.info("URL field after setText: '{}' (length: {})", (Object)this.messageField.getText(), (Object)this.messageField.getText().length());
                }
                this.messageField.setCursor(this.messageField.getText().length(), false);
                this.messageField.setSelectionStart(this.messageField.getText().length());
                this.addDrawableChild((ClickableWidget)this.messageField);
                this.commentField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth, fieldHeight * 2, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Request Body (JSON)"));
                String bodyText = (String)this.block.params.getOrDefault("body", "{}");
                if (McLooperMod.isVerboseLogging() && bodyText.length() > 30) {
                    McLooperMod.LOGGER.info("Loading POST body into field: '{}' (length: {})", bodyText.length() > 100 ? bodyText.substring(0, 100) + "..." : bodyText, (Object)bodyText.length());
                }
                this.commentField.setMaxLength(50000);
                this.commentField.setText(bodyText);
                if (McLooperMod.isVerboseLogging() && bodyText.length() > 30) {
                    McLooperMod.LOGGER.info("POST body field after setText: '{}' (length: {})", this.commentField.getText().length() > 100 ? this.commentField.getText().substring(0, 100) + "..." : this.commentField.getText(), (Object)this.commentField.getText().length());
                }
                this.commentField.setCursor(this.commentField.getText().length(), false);
                this.commentField.setSelectionStart(this.commentField.getText().length());
                this.addDrawableChild((ClickableWidget)this.commentField);
                this.addDrawableChild((ClickableWidget)this.commentField);
                y += fieldHeight;
                this.timeField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth / 2 - 5, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Timeout (ms)"));
                this.timeField.setMaxLength(50);
                this.timeField.setText(String.valueOf(this.block.params.getOrDefault("timeout", 5000)));
                this.timeField.setCursor(this.timeField.getText().length(), false);
                this.timeField.setSelectionStart(this.timeField.getText().length());
                this.addDrawableChild((ClickableWidget)this.timeField);
                this.unitField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x + fieldWidth / 2 + 5, y, fieldWidth / 2 - 5, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Response Variable"));
                this.unitField.setMaxLength(100);
                this.unitField.setText((String)this.block.params.getOrDefault("response_var", "response"));
                this.unitField.setCursor(this.unitField.getText().length(), false);
                this.unitField.setSelectionStart(this.unitField.getText().length());
                this.addDrawableChild((ClickableWidget)this.unitField);
                break;
            }
            case "if": 
            case "wait_until": {
                String defaultText;
                String currentConditionType = this.block.condition != null ? this.block.condition.type : "variable_equals";
                boolean isRegexMode = false;
                if (("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) && this.block.condition != null) {
                    isRegexMode = "regex".equals(this.block.condition.params.getOrDefault("search_mode", "registry"));
                }
                String firstFieldLabel = "gui_item_exists".equals(currentConditionType) ? "Name Pattern (regex)" : ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType) ? (isRegexMode ? "Regex Pattern (name/lore)" : "Registry ID (minecraft:item)") : "Variable Name");
                String secondFieldLabel = "gui_item_exists".equals(currentConditionType) ? "Variable Name (optional)" : ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType) ? "Variable Name" : "Expected Value");
                this.commentField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)firstFieldLabel));
                if (this.block.condition != null) {
                    if ("gui_item_exists".equals(currentConditionType)) {
                        this.commentField.setText((String)this.block.condition.params.getOrDefault("name_pattern", "Click to Confirm"));
                        this.commentField.setCursor(this.commentField.getText().length(), false);
                        this.commentField.setSelectionStart(this.commentField.getText().length());
                    } else if ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) {
                        if (isRegexMode) {
                            this.commentField.setText((String)this.block.condition.params.getOrDefault("regex_pattern", "(?i).*diamond.*"));
                            this.commentField.setCursor(this.commentField.getText().length(), false);
                            this.commentField.setSelectionStart(this.commentField.getText().length());
                        } else {
                            this.commentField.setText((String)this.block.condition.params.getOrDefault("item_name", "minecraft:cobblestone"));
                            this.commentField.setCursor(this.commentField.getText().length(), false);
                            this.commentField.setSelectionStart(this.commentField.getText().length());
                        }
                    } else {
                        this.commentField.setText((String)this.block.condition.params.getOrDefault("variable", "example"));
                        this.commentField.setCursor(this.commentField.getText().length(), false);
                        this.commentField.setSelectionStart(this.commentField.getText().length());
                    }
                } else {
                    defaultText = "gui_item_exists".equals(currentConditionType) ? "(?i).*click.*confirm.*" : ("inventory_contains".equals(currentConditionType) ? "minecraft:cobblestone" : ("gui_contains".equals(currentConditionType) ? "minecraft:diamond" : "example"));
                    this.commentField.setText(defaultText);
                    this.commentField.setCursor(this.commentField.getText().length(), false);
                    this.commentField.setSelectionStart(this.commentField.getText().length());
                }
                this.addDrawableChild((ClickableWidget)this.commentField);
                this.messageField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)secondFieldLabel));
                if (this.block.condition != null) {
                    if ("gui_item_exists".equals(currentConditionType)) {
                        this.messageField.setText((String)this.block.condition.params.getOrDefault("var_name", "confirm_var"));
                        this.messageField.setCursor(this.messageField.getText().length(), false);
                        this.messageField.setSelectionStart(this.messageField.getText().length());
                    } else if ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) {
                        String defaultVar = "inventory_contains".equals(currentConditionType) ? "item_var" : "gui_item_var";
                        this.messageField.setText((String)this.block.condition.params.getOrDefault("var_name", defaultVar));
                        this.messageField.setCursor(this.messageField.getText().length(), false);
                        this.messageField.setSelectionStart(this.messageField.getText().length());
                    } else {
                        Object value = this.block.condition.params.get("value");
                        this.messageField.setText(value != null ? value.toString() : "true");
                        this.messageField.setCursor(this.messageField.getText().length(), false);
                        this.messageField.setSelectionStart(this.messageField.getText().length());
                    }
                } else {
                    defaultText = "gui_item_exists".equals(currentConditionType) ? "confirm_var" : ("inventory_contains".equals(currentConditionType) ? "item_var" : ("gui_contains".equals(currentConditionType) ? "gui_item_var" : "true"));
                    this.messageField.setText(defaultText);
                    this.messageField.setCursor(this.messageField.getText().length(), false);
                    this.messageField.setSelectionStart(this.messageField.getText().length());
                }
                this.addDrawableChild((ClickableWidget)this.messageField);
                if ("inventory_contains".equals(currentConditionType) || "gui_contains".equals(currentConditionType)) {
                    String searchMode = this.block.condition != null ? (String)this.block.condition.params.getOrDefault("search_mode", "registry") : "registry";
                    String toggleText = "registry".equals(searchMode) ? "Mode: Registry ID" : "Mode: Regex Pattern";
                    net.minecraft.client.gui.widget.ButtonWidget searchModeButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)toggleText), btn -> {
                        String currentMode;
                        if (this.block.condition == null) {
                            this.block.condition = new TaskCondition(currentConditionType);
                        }
                        String newMode = "registry".equals(currentMode = (String)this.block.condition.params.getOrDefault("search_mode", "registry")) ? "regex" : "registry";
                        this.block.condition.params.put("search_mode", newMode);
                        String newText = "registry".equals(newMode) ? "Mode: Registry ID" : "Mode: Regex Pattern";
                        btn.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)newText));
                        if ("registry".equals(newMode)) {
                            this.commentField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"minecraft:cobblestone"));
                            if (this.commentField.getText().isEmpty()) {
                                this.commentField.setText("minecraft:cobblestone");
                            }
                        } else {
                            this.commentField.setPlaceholder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"(?i).*diamond.*"));
                            if (this.commentField.getText().isEmpty()) {
                                this.commentField.setText("(?i).*diamond.*");
                            }
                        }
                    }).dimensions(x, y += spacing, fieldWidth, fieldHeight).build();
                    this.addDrawableChild((ClickableWidget)searchModeButton);
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
                    net.minecraft.client.gui.widget.ButtonWidget conditionButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)label), btn -> {
                        if (this.block.condition == null) {
                            this.block.condition = new TaskCondition(conditionType);
                        } else {
                            this.block.condition.type = conditionType;
                        }
                        this.client.setScreen((net.minecraft.client.gui.screen.Screen)new BlockEditorScreen(this.parent, this.block));
                    }).dimensions(buttonX, y, buttonWidth - 2, fieldHeight).build();
                    this.addDrawableChild((ClickableWidget)conditionButton);
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
                this.commentField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Source Variable"));
                this.commentField.setText((String)this.block.params.getOrDefault("source_var", "variable_name"));
                this.commentField.setMaxLength(100);
                this.addDrawableChild((ClickableWidget)this.commentField);
                this.messageField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Target Variable"));
                this.messageField.setText((String)this.block.params.getOrDefault("target_var", "cleaned_variable"));
                this.messageField.setMaxLength(100);
                this.addDrawableChild((ClickableWidget)this.messageField);
                this.timeField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Regex Pattern"));
                this.timeField.setText((String)this.block.params.getOrDefault("pattern", ","));
                this.timeField.setMaxLength(1000);
                this.addDrawableChild((ClickableWidget)this.timeField);
                this.unitField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Replacement"));
                this.unitField.setText((String)this.block.params.getOrDefault("replacement", ""));
                this.unitField.setMaxLength(1000);
                this.addDrawableChild((ClickableWidget)this.unitField);
                break;
            }
            case "prevent_execution": {
                this.timeField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y, fieldWidth / 2 - 5, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Time"));
                this.timeField.setText(String.valueOf(this.block.params.getOrDefault("time", 5)));
                this.addDrawableChild((ClickableWidget)this.timeField);
                this.unitField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x + fieldWidth / 2 + 5, y, fieldWidth / 2 - 5, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Unit"));
                this.unitField.setText((String)this.block.params.getOrDefault("unit", "seconds"));
                this.addDrawableChild((ClickableWidget)this.unitField);
                break;
            }
            case "world_click": {
                String clickType = (String)this.block.params.getOrDefault("click_type", "right");
                net.minecraft.client.gui.widget.ButtonWidget clickTypeButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Click: " + clickType)), btn -> {
                    String current = (String)this.block.params.get("click_type");
                    String newType = "left".equals(current) ? "right" : "left";
                    this.block.params.put("click_type", newType);
                    btn.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Click: " + newType)));
                }).dimensions(x, y, fieldWidth, fieldHeight).build();
                this.addDrawableChild((ClickableWidget)clickTypeButton);
                this.messageField = new net.minecraft.client.gui.widget.TextFieldWidget(this.textRenderer, x, y += spacing, fieldWidth, fieldHeight, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Target (crosshair or x,y,z)"));
                this.messageField.setText((String)this.block.params.getOrDefault("target", "crosshair"));
                this.messageField.setMaxLength(200);
                this.addDrawableChild((ClickableWidget)this.messageField);
                boolean pauseOnGui = Boolean.parseBoolean(String.valueOf(this.block.params.getOrDefault("pause_on_gui", "false")));
                net.minecraft.client.gui.widget.ButtonWidget pauseOnGuiButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Pause on GUI: " + (pauseOnGui ? "ON" : "OFF"))), btn -> {
                    boolean current = Boolean.parseBoolean(String.valueOf(this.block.params.getOrDefault("pause_on_gui", "false")));
                    boolean newValue = !current;
                    this.block.params.put("pause_on_gui", String.valueOf(newValue));
                    btn.setMessage((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)("Pause on GUI: " + (newValue ? "ON" : "OFF"))));
                }).dimensions(x, y += spacing, fieldWidth, fieldHeight).build();
                this.addDrawableChild((ClickableWidget)pauseOnGuiButton);
            }
        }
        if (this.supportsNestedBlocks(this.block.type)) {
            int nestedCount = this.block.blocks != null ? this.block.blocks.size() : 0;
            String nestedButtonText = "Manage Nested Blocks (" + nestedCount + ")";
            net.minecraft.client.gui.widget.ButtonWidget nestedBlocksButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)nestedButtonText), btn -> this.openNestedBlocksEditor()).dimensions(x, y += spacing + 10, fieldWidth, fieldHeight).build();
            this.addDrawableChild((ClickableWidget)nestedBlocksButton);
            y += spacing;
        }
        net.minecraft.client.gui.widget.ButtonWidget saveButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Save"), btn -> this.saveBlock()).dimensions(this.width / 2 - 110, this.height - 40, 100, 20).build();
        this.addDrawableChild((ClickableWidget)saveButton);
        net.minecraft.client.gui.widget.ButtonWidget cancelButton = net.minecraft.client.gui.widget.ButtonWidget.builder((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Cancel"), btn -> this.close()).dimensions(this.width / 2 + 10, this.height - 40, 100, 20).build();
        this.addDrawableChild((ClickableWidget)cancelButton);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
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
        int maxScroll = Math.max(0, estimatedContentHeight - this.height + 100);
        this.scrollY = Math.max(0.0, Math.min((double)maxScroll, this.scrollY - verticalAmount * 15.0));
        this.repositionWidgets();
        return true;
    }

    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        int fieldWidth = 200;
        int x = this.width / 2 - fieldWidth / 2;
        int y = (int)(60.0 - this.scrollY);
        int spacing = 40;
        switch (this.block.type) {
            case "comment": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Comment Text:"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "exit": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Exit block - stops current execution"), x, y - 15, 0xFFFF00);
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"No parameters required"), x, y + 5, 0x888888);
                break;
            }
            case "chat": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Message/Command (use / for commands):"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "client_message": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Client Message:"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "wait": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Wait Time:"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "call_loop": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop to Call:"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "click_gui_item": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Name Pattern / Variable Name:"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "loop": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Loop Count:"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "post_request": {
                if (y - 15 > -20 && y - 15 < this.height) {
                    context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"HTTP POST Request:"), x, y - 15, 0xFFFFFF);
                }
                if (y + spacing - 15 > -20 && y + spacing - 15 < this.height) {
                    context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Request Body:"), x, y + spacing - 15, 0xFFFFFF);
                }
                if (y + 2 * spacing - 15 <= -20 || y + 2 * spacing - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Timeout / Response Variable:"), x, y + 2 * spacing - 15, 0xFFFFFF);
                break;
            }
            case "regex_replace": {
                if (y - 15 > -20 && y - 15 < this.height) {
                    context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Source Variable:"), x, y - 15, 0xFFFFFF);
                }
                if (y + spacing - 15 > -20 && y + spacing - 15 < this.height) {
                    context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Target Variable:"), x, y + spacing - 15, 0xFFFFFF);
                }
                if (y + 2 * spacing - 15 > -20 && y + 2 * spacing - 15 < this.height) {
                    context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Regex Pattern:"), x, y + 2 * spacing - 15, 0xFFFFFF);
                }
                if (y + 3 * spacing - 15 <= -20 || y + 3 * spacing - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Replacement:"), x, y + 3 * spacing - 15, 0xFFFFFF);
                break;
            }
            case "prevent_execution": {
                if (y - 15 <= -20 || y - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Prevention Time:"), x, y - 15, 0xFFFFFF);
                break;
            }
            case "world_click": {
                if (y - 15 > -20 && y - 15 < this.height) {
                    context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Click Type:"), x, y - 15, 0xFFFFFF);
                }
                if (y + spacing - 15 > -20 && y + spacing - 15 < this.height) {
                    context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Target:"), x, y + spacing - 15, 0xFFFFFF);
                }
                if (y + spacing * 2 - 15 <= -20 || y + spacing * 2 - 15 >= this.height) break;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Pause on GUI:"), x, y + spacing * 2 - 15, 0xFFFFFF);
                break;
            }
            case "if": 
            case "wait_until": {
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Condition - Variable / Value:"), x, y - 15, 0xFFFFFF);
                int conditionTypeY = y + 2 * spacing;
                context.drawTextWithShadow(this.textRenderer, (net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Condition Type:"), x, conditionTypeY - 15, 0xFFFFFF);
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
        if (estimatedContentHeight > this.height - 100) {
            int scrollBarHeight = Math.max(20, (this.height - 100) * (this.height - 100) / estimatedContentHeight);
            int maxScroll = Math.max(1, estimatedContentHeight - this.height + 100);
            int scrollBarY = (int)(40.0 + (double)(this.height - 140 - scrollBarHeight) * this.scrollY / (double)maxScroll);
            context.fill(this.width - 10, scrollBarY, this.width - 5, scrollBarY + scrollBarHeight, -2130706433);
        }
        if (this.selectedConditionButton != null) {
            int outlineX = this.selectedConditionButtonX - 1;
            int outlineY = (int)((double)this.selectedConditionButtonY - this.scrollY) - 1;
            int outlineWidth = this.selectedConditionButtonWidth + 2;
            int outlineHeight = this.selectedConditionButtonHeight + 2;
            if (outlineY > -outlineHeight && outlineY < this.height) {
                context.fill(outlineX, outlineY, outlineX + outlineWidth, outlineY + 2, -1);
                context.fill(outlineX, outlineY + outlineHeight - 2, outlineX + outlineWidth, outlineY + outlineHeight, -1);
                context.fill(outlineX, outlineY, outlineX + 2, outlineY + outlineHeight, -1);
                context.fill(outlineX + outlineWidth - 2, outlineY, outlineX + outlineWidth, outlineY + outlineHeight, -1);
            }
        }
    }

    private void saveBlock() {
        switch (this.block.type) {
            case "comment": {
                if (this.commentField == null) break;
                String commentText = this.commentField.getText();
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
                String chatText = this.messageField.getText();
                if (McLooperMod.isVerboseLogging() && chatText.length() > 30) {
                    McLooperMod.LOGGER.info("Saving chat message: '{}' (length: {})", (Object)chatText, (Object)chatText.length());
                }
                this.block.params.put("message", chatText);
                break;
            }
            case "client_message": {
                if (this.messageField == null) break;
                this.block.params.put("message", this.messageField.getText());
                break;
            }
            case "wait": {
                if (this.timeField == null || this.unitField == null) break;
                String timeText = this.timeField.getText();
                if (timeText.matches("\\[\\d+-\\d+\\]")) {
                    this.block.params.put("time", timeText);
                    this.block.params.put("unit", this.unitField.getText());
                    break;
                }
                try {
                    int time = Integer.parseInt(timeText);
                    this.block.params.put("time", time);
                    this.block.params.put("unit", this.unitField.getText());
                }
                catch (NumberFormatException time) {}
                break;
            }
            case "call_loop": {
                if (this.loopNameField == null) break;
                this.block.params.put("loop", this.loopNameField.getText());
                break;
            }
            case "click_gui_item": {
                if (this.messageField != null) {
                    this.block.params.put("name_pattern", this.messageField.getText());
                }
                if (this.commentField == null) break;
                this.block.params.put("var_name", this.commentField.getText());
                break;
            }
            case "loop": {
                if (this.countField == null) break;
                try {
                    int count = Integer.parseInt(this.countField.getText());
                    this.block.params.put("count", count);
                }
                catch (NumberFormatException count) {}
                break;
            }
            case "post_request": {
                if (this.messageField != null) {
                    String url = this.messageField.getText();
                    this.block.params.put("url", url);
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Saving POST URL: {} characters", (Object)url.length());
                    }
                }
                if (this.commentField != null) {
                    String body = this.commentField.getText();
                    this.block.params.put("body", body);
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Saving POST body: {} characters - '{}'", (Object)body.length(), body.length() > 100 ? body.substring(0, 100) + "..." : body);
                    }
                }
                if (this.timeField != null) {
                    try {
                        int timeout = Integer.parseInt(this.timeField.getText());
                        this.block.params.put("timeout", timeout);
                    }
                    catch (NumberFormatException e) {
                        this.block.params.put("timeout", 5000);
                    }
                }
                if (this.unitField == null) break;
                this.block.params.put("response_var", this.unitField.getText());
                break;
            }
            case "if": 
            case "wait_until": {
                if (this.commentField == null || this.messageField == null) break;
                if (this.block.condition == null) {
                    this.block.condition = new TaskCondition("variable_equals");
                }
                if ("gui_item_exists".equals(this.block.condition.type)) {
                    this.block.condition.params.put("name_pattern", this.commentField.getText());
                    this.block.condition.params.put("var_name", this.messageField.getText());
                    break;
                }
                if ("inventory_contains".equals(this.block.condition.type) || "gui_contains".equals(this.block.condition.type)) {
                    String searchMode = (String)this.block.condition.params.getOrDefault("search_mode", "registry");
                    if ("regex".equals(searchMode)) {
                        String regexText = this.commentField.getText();
                        if (McLooperMod.isVerboseLogging() && regexText.length() > 30) {
                            McLooperMod.LOGGER.info("Saving condition regex: '{}' (length: {})", (Object)regexText, (Object)regexText.length());
                        }
                        this.block.condition.params.put("regex_pattern", regexText);
                    } else {
                        String itemText = this.commentField.getText();
                        if (McLooperMod.isVerboseLogging() && itemText.length() > 30) {
                            McLooperMod.LOGGER.info("Saving condition item name: '{}' (length: {})", (Object)itemText, (Object)itemText.length());
                        }
                        this.block.condition.params.put("item_name", itemText);
                    }
                    String varText = this.messageField.getText();
                    if (McLooperMod.isVerboseLogging() && varText.length() > 30) {
                        McLooperMod.LOGGER.info("Saving condition var_name: '{}' (length: {})", (Object)varText, (Object)varText.length());
                    }
                    this.block.condition.params.put("var_name", varText);
                    this.block.condition.params.put("search_mode", searchMode);
                    break;
                }
                this.block.condition.params.put("variable", this.commentField.getText());
                this.block.condition.params.put("value", this.messageField.getText());
                break;
            }
            case "regex_replace": {
                if (this.commentField != null) {
                    this.block.params.put("source_var", this.commentField.getText());
                }
                if (this.messageField != null) {
                    this.block.params.put("target_var", this.messageField.getText());
                }
                if (this.timeField != null) {
                    this.block.params.put("pattern", this.timeField.getText());
                }
                if (this.unitField == null) break;
                this.block.params.put("replacement", this.unitField.getText());
                break;
            }
            case "prevent_execution": {
                if (this.timeField == null || this.unitField == null) break;
                try {
                    int time = Integer.parseInt(this.timeField.getText());
                    this.block.params.put("time", time);
                    this.block.params.put("unit", this.unitField.getText());
                }
                catch (NumberFormatException numberFormatException) {}
                break;
            }
            case "world_click": {
                if (this.messageField == null) break;
                this.block.params.put("target", this.messageField.getText());
            }
        }
        this.close();
    }

    public void close() {
        this.client.setScreen((net.minecraft.client.gui.screen.Screen)this.parent);
    }

    public boolean shouldPause() {
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
        this.client.setScreen((net.minecraft.client.gui.screen.Screen)new LoopEditorScreen((net.minecraft.text.Text)net.minecraft.text.Text.literal((String)"Edit Nested Blocks")));
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
                net.minecraft.client.MinecraftClient.getInstance().setScreen((net.minecraft.client.gui.screen.Screen)context.originalScreen);
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

