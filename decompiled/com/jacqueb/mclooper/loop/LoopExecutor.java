/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_3966
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_490
 */
package com.jacqueb.mclooper.loop;

import com.jacqueb.mclooper.McLooperMod;
import com.jacqueb.mclooper.config.ConfigManager;
import com.jacqueb.mclooper.config.TaskBlock;
import com.jacqueb.mclooper.config.TaskCondition;
import com.jacqueb.mclooper.config.TaskLoop;
import com.jacqueb.mclooper.config.TaskTrigger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_490;

public class LoopExecutor {
    private final ConfigManager configManager;
    private final Map<String, BlockExecutor> blockExecutors;
    private final Map<String, ConditionEvaluator> conditionEvaluators;
    private final Map<String, TriggerHandler> triggerHandlers;
    private final Map<String, Object> runtimeVariables;
    private final Map<String, Long> loopTimers;
    private final Random random;
    private boolean worldJoined = false;
    private long tickCounter = 0L;
    private long worldJoinTick = 0L;
    private Map<String, ExecutionContext> runningLoops = new ConcurrentHashMap<String, ExecutionContext>();
    private boolean isPaused = false;
    private String lastChatMessage = "";
    private long lastChatMessageTime = 0L;
    private StringBuilder recentChatMessages = new StringBuilder();
    private long lastChatLineTime = 0L;
    private Set<String> processedChatMessages = ConcurrentHashMap.newKeySet();
    private String lastGuiItems = "";
    private long lastGuiItemsTime = 0L;
    private Set<String> keyPressedThisTick = new HashSet<String>();

    public LoopExecutor(ConfigManager configManager) {
        this.configManager = configManager;
        this.blockExecutors = new HashMap<String, BlockExecutor>();
        this.conditionEvaluators = new HashMap<String, ConditionEvaluator>();
        this.triggerHandlers = new HashMap<String, TriggerHandler>();
        this.runtimeVariables = new ConcurrentHashMap<String, Object>();
        this.loopTimers = new HashMap<String, Long>();
        this.random = new Random();
        this.initializeExecutors();
        this.initializeConditionEvaluators();
        this.initializeTriggerHandlers();
    }

    private void initializeExecutors() {
        this.blockExecutors.put("chat", (block, context) -> {
            String message = (String)block.params.get("message");
            if (message != null && !message.trim().isEmpty()) {
                message = this.replaceVariables(message);
                class_310 client = class_310.method_1551();
                if (client.field_1724 != null) {
                    if (message.startsWith("/")) {
                        client.field_1724.field_3944.method_45731(message.substring(1));
                        if (McLooperMod.isVerboseLogging()) {
                            McLooperMod.LOGGER.info("Sent command: {}", (Object)message);
                        }
                    } else {
                        client.field_1724.field_3944.method_45729(message);
                        if (McLooperMod.isVerboseLogging()) {
                            McLooperMod.LOGGER.info("Sent chat message: {}", (Object)message);
                        }
                    }
                }
            }
        });
        this.blockExecutors.put("client_message", (block, context) -> {
            String message = (String)block.params.get("message");
            if (message != null && !message.trim().isEmpty()) {
                message = this.replaceVariables(message);
                class_310 client = class_310.method_1551();
                if (client.field_1724 != null) {
                    client.field_1705.method_1743().method_1812((class_2561)class_2561.method_43470((String)message));
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Client message displayed: {}", (Object)message);
                    }
                }
            }
        });
        this.blockExecutors.put("comment", (block, context) -> {});
        this.blockExecutors.put("wait", (block, context) -> {
            long waitTime;
            Number num;
            Object timeObj = block.params.get("time");
            String timeStr = timeObj instanceof Number ? ((num = (Number)timeObj) instanceof Double || num instanceof Float ? String.valueOf(num.longValue()) : num.toString()) : (timeObj instanceof String ? (String)timeObj : "20");
            timeStr = this.replaceVariables(timeStr);
            try {
                waitTime = timeStr.contains(".") ? (long)Double.parseDouble(timeStr) : Long.parseLong(timeStr);
            }
            catch (NumberFormatException e) {
                waitTime = 20L;
                McLooperMod.LOGGER.warn("Invalid time format {}, using default", (Object)timeStr);
            }
            if (waitTime > 0L) {
                String unit = (String)block.params.getOrDefault("unit", "ticks");
                if ("seconds".equals(unit)) {
                    waitTime *= 20L;
                }
                context.waitTicks = waitTime;
                if (McLooperMod.isVerboseLogging()) {
                    McLooperMod.LOGGER.info("Wait block: {} {} (converted to {} ticks)", new Object[]{timeObj, unit, waitTime});
                }
            } else {
                McLooperMod.LOGGER.warn("Wait block: invalid time parameter {}", timeObj);
            }
        });
        this.blockExecutors.put("call_loop", (block, context) -> {
            TaskLoop targetLoop;
            String loopName = (String)block.params.get("loop");
            if (loopName != null && (targetLoop = this.findLoopByName(loopName)) != null) {
                this.executeBlocks(targetLoop.blocks, context);
            }
        });
        this.blockExecutors.put("if", (block, context) -> {
            boolean verbose = Boolean.parseBoolean(block.params.getOrDefault("verbose", "false").toString());
            context.clearScopeVariables();
            if (block.condition != null && this.evaluateCondition(block.condition)) {
                if (block.blocks != null && !block.blocks.isEmpty()) {
                    this.executeBlocks(block.blocks, context);
                }
                this.showScopedVerboseVariables("If Block", verbose, context);
            } else {
                if (McLooperMod.isVerboseLogging()) {
                    McLooperMod.LOGGER.info("If condition failed, skipping nested blocks");
                }
                this.showScopedVerboseVariables("If Block (Failed)", verbose, context);
            }
        });
        this.blockExecutors.put("exit", (block, context) -> {
            context.shouldStop = true;
            if (McLooperMod.isVerboseLogging()) {
                McLooperMod.LOGGER.info("Exit block executed, stopping current execution context");
            }
        });
        this.blockExecutors.put("loop", (block, context) -> {
            Object countObj = block.params.get("count");
            if (countObj instanceof Number) {
                int count = ((Number)countObj).intValue();
                for (int i = 0; i < count && !context.shouldStop; ++i) {
                    this.executeBlocks(block.blocks, context);
                }
            }
        });
        this.blockExecutors.put("wait_until", (block, context) -> {
            if (block.condition != null) {
                context.waitCondition = block.condition;
            }
        });
        this.blockExecutors.put("click_gui_item", (block, context) -> {
            class_310 client = class_310.method_1551();
            class_437 patt0$temp = client.field_1755;
            if (patt0$temp instanceof class_465) {
                class_465 screen = (class_465)patt0$temp;
                if (client.field_1755 instanceof class_490) {
                    McLooperMod.LOGGER.info("Skipping click_gui_item for inventory screen - use inventory-specific blocks instead");
                    return;
                }
                String namePattern = (String)block.params.getOrDefault("name_pattern", "Click to Confirm");
                namePattern = this.normalizeRegexPattern(namePattern);
                String varName = (String)block.params.get("var_name");
                for (class_1735 slot : screen.method_17577().field_7761) {
                    class_1799 stack;
                    if (slot.field_7871 == client.field_1724.method_31548() || (stack = slot.method_7677()).method_7960()) continue;
                    String displayName = stack.method_7964().getString();
                    try {
                        Pattern regex = Pattern.compile(namePattern);
                        Matcher matcher = regex.matcher(displayName);
                        if (!matcher.find()) continue;
                        if (varName != null && !varName.trim().isEmpty()) {
                            this.setVariable(varName, matcher.group(0));
                            for (int i = 1; i <= matcher.groupCount(); ++i) {
                                String groupValue = matcher.group(i);
                                if (groupValue == null) continue;
                                this.setVariable(varName + "_" + i, groupValue);
                            }
                        }
                        client.field_1761.method_2906(screen.method_17577().field_7763, slot.method_34266(), 0, class_1713.field_7790, (class_1657)client.field_1724);
                        if (McLooperMod.isVerboseLogging()) {
                            McLooperMod.LOGGER.info("Clicked GUI item '{}' in slot {}", (Object)displayName, (Object)slot.method_34266());
                        }
                        return;
                    }
                    catch (Exception e) {
                        McLooperMod.LOGGER.warn("Invalid regex pattern in click_gui_item: {}", (Object)namePattern, (Object)e);
                        if (!displayName.contains(namePattern)) continue;
                        client.field_1761.method_2906(screen.method_17577().field_7763, slot.method_34266(), 0, class_1713.field_7790, (class_1657)client.field_1724);
                        if (McLooperMod.isVerboseLogging()) {
                            McLooperMod.LOGGER.info("Clicked GUI item '{}' in slot {}", (Object)displayName, (Object)slot.method_34266());
                        }
                        return;
                    }
                }
                if (McLooperMod.isVerboseLogging()) {
                    McLooperMod.LOGGER.info("No GUI item found matching pattern: {}", (Object)namePattern);
                }
            } else if (McLooperMod.isVerboseLogging()) {
                McLooperMod.LOGGER.info("No GUI screen open for click_gui_item block");
            }
        });
        this.blockExecutors.put("post_request", (block, context) -> {
            String url = (String)block.params.get("url");
            String body = (String)block.params.getOrDefault("body", "");
            Number timeoutNumber = (Number)block.params.getOrDefault("timeout", 5000);
            int timeoutMs = timeoutNumber.intValue();
            String responseVar = (String)block.params.get("response_var");
            if (url == null || url.trim().isEmpty()) {
                McLooperMod.LOGGER.warn("POST request block missing URL");
                return;
            }
            url = this.replaceVariables(url);
            body = this.replaceVariables(body);
            String finalUrl = url;
            String finalBody = body;
            String finalResponseVar = responseVar;
            try {
                HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeoutMs)).build();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(finalUrl)).timeout(Duration.ofMillis(timeoutMs)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(finalBody)).build();
                CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                ((CompletableFuture)responseFuture.thenAccept(response -> {
                    if (finalResponseVar != null && !finalResponseVar.trim().isEmpty()) {
                        this.setVariable(finalResponseVar + "_code", response.statusCode());
                        this.setVariable(finalResponseVar + "_body", response.body());
                        this.setVariable(finalResponseVar + "_success", response.statusCode() >= 200 && response.statusCode() < 300);
                    }
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("POST request to {} completed with status {}", (Object)finalUrl, (Object)response.statusCode());
                    }
                })).exceptionally(throwable -> {
                    if (finalResponseVar != null && !finalResponseVar.trim().isEmpty()) {
                        this.setVariable(finalResponseVar + "_code", -1);
                        this.setVariable(finalResponseVar + "_body", "");
                        this.setVariable(finalResponseVar + "_success", false);
                        this.setVariable(finalResponseVar + "_error", throwable.getMessage());
                    }
                    McLooperMod.LOGGER.warn("POST request to {} failed: {}", (Object)finalUrl, (Object)throwable.getMessage());
                    return null;
                });
            }
            catch (Exception e) {
                if (finalResponseVar != null && !finalResponseVar.trim().isEmpty()) {
                    this.setVariable(finalResponseVar + "_code", -1);
                    this.setVariable(finalResponseVar + "_body", "");
                    this.setVariable(finalResponseVar + "_success", false);
                    this.setVariable(finalResponseVar + "_error", e.getMessage());
                }
                McLooperMod.LOGGER.warn("POST request to {} failed: {}", (Object)finalUrl, (Object)e.getMessage());
            }
        });
        this.blockExecutors.put("regex_replace", (block, context) -> {
            Object sourceValue;
            String sourceVar = (String)block.params.get("source_var");
            String targetVar = (String)block.params.get("target_var");
            String pattern = (String)block.params.get("pattern");
            String replacement = (String)block.params.getOrDefault("replacement", "");
            if (sourceVar == null || targetVar == null || pattern == null) {
                McLooperMod.LOGGER.warn("Regex replace block missing required parameters");
                return;
            }
            String resolvedSourceVar = this.replaceVariables(sourceVar);
            if (sourceVar.startsWith("${") && sourceVar.endsWith("}")) {
                sourceValue = resolvedSourceVar;
            } else {
                sourceValue = this.runtimeVariables.get(resolvedSourceVar);
                if (sourceValue == null) {
                    McLooperMod.LOGGER.warn("Regex replace source variable '{}' not found", (Object)resolvedSourceVar);
                    return;
                }
            }
            try {
                String sourceText = sourceValue.toString();
                String resolvedPattern = this.normalizeRegexPattern(pattern);
                String resolvedReplacement = this.replaceVariables(replacement);
                String result = sourceText.replaceAll(resolvedPattern, resolvedReplacement);
                boolean verbose = Boolean.parseBoolean(block.params.getOrDefault("verbose", "false").toString());
                this.setVariable(targetVar, result);
                context.recordVariableCreated(targetVar, result);
                if (McLooperMod.isVerboseLogging()) {
                    McLooperMod.LOGGER.info("Regex replace: '{}' -> '{}' using pattern '{}'", new Object[]{sourceText, result, pattern});
                }
                this.showScopedVerboseVariables("Regex Replace", verbose, context);
            }
            catch (Exception e) {
                McLooperMod.LOGGER.warn("Invalid regex pattern in regex_replace: {}", (Object)pattern, (Object)e);
                this.setVariable(targetVar, sourceValue.toString());
                context.recordVariableCreated(targetVar, sourceValue.toString());
            }
        });
        this.blockExecutors.put("prevent_execution", (block, context) -> {
            Object timeObj = block.params.get("time");
            String unit = (String)block.params.getOrDefault("unit", "ticks");
            if (timeObj instanceof Number) {
                String currentLoopName;
                long preventTime = ((Number)timeObj).longValue();
                if ("seconds".equals(unit)) {
                    preventTime *= 20L;
                }
                if ((currentLoopName = context.getCurrentLoopName()) != null) {
                    String preventKey = "prevent_" + currentLoopName;
                    this.loopTimers.put(preventKey, this.tickCounter + preventTime);
                    if (McLooperMod.isVerboseLogging()) {
                        McLooperMod.LOGGER.info("Preventing loop '{}' from executing for {} {} ({} ticks)", new Object[]{currentLoopName, timeObj, unit, preventTime});
                    }
                } else {
                    McLooperMod.LOGGER.warn("Prevent execution block: could not determine current loop name");
                }
            } else {
                McLooperMod.LOGGER.warn("Prevent execution block: invalid time parameter {}", timeObj);
            }
        });
        this.blockExecutors.put("world_click", (block, context) -> {
            String clickType = (String)block.params.getOrDefault("click_type", "right");
            String target = (String)block.params.getOrDefault("target", "crosshair");
            boolean pauseOnGui = Boolean.parseBoolean(String.valueOf(block.params.getOrDefault("pause_on_gui", "false")));
            target = this.replaceVariables(target);
            class_310 client = class_310.method_1551();
            if (client.field_1724 == null || client.field_1687 == null || client.field_1761 == null) {
                McLooperMod.LOGGER.warn("World click block: player, world, or interaction manager is null");
                return;
            }
            if (pauseOnGui && client.field_1755 != null) {
                if (!McLooperMod.isVerboseLogging()) return;
                McLooperMod.LOGGER.info("World click block skipped: GUI is open ({})", (Object)client.field_1755.getClass().getSimpleName());
                return;
            }
            if ("crosshair".equals(target)) {
                if ("left".equals(clickType)) {
                    if (client.field_1765 == null) return;
                    switch (client.field_1765.method_17783()) {
                        case field_1332: {
                            class_3965 blockHitResult = (class_3965)client.field_1765;
                            client.field_1761.method_2910(blockHitResult.method_17777(), blockHitResult.method_17780());
                            if (!McLooperMod.isVerboseLogging()) return;
                            McLooperMod.LOGGER.info("Left clicked block at {}", (Object)blockHitResult.method_17777());
                            return;
                        }
                        case field_1331: {
                            class_3966 entityHitResult = (class_3966)client.field_1765;
                            client.field_1761.method_2918((class_1657)client.field_1724, entityHitResult.method_17782());
                            if (!McLooperMod.isVerboseLogging()) return;
                            McLooperMod.LOGGER.info("Left clicked entity: {}", (Object)entityHitResult.method_17782().method_5864());
                            return;
                        }
                        default: {
                            if (!McLooperMod.isVerboseLogging()) return;
                            McLooperMod.LOGGER.info("Left click - no valid target");
                            return;
                        }
                    }
                } else {
                    client.field_1761.method_2919((class_1657)client.field_1724, class_1268.field_5808);
                    if (!McLooperMod.isVerboseLogging()) return;
                    McLooperMod.LOGGER.info("Right clicked (used item/interacted)");
                }
                return;
            }
            try {
                String[] coords = target.split(",");
                if (coords.length == 3) {
                    int x = Integer.parseInt(coords[0].trim());
                    int y = Integer.parseInt(coords[1].trim());
                    int z = Integer.parseInt(coords[2].trim());
                    class_2338 blockPos = new class_2338(x, y, z);
                    if ("left".equals(clickType)) {
                        client.field_1761.method_2910(blockPos, class_2350.field_11036);
                        if (!McLooperMod.isVerboseLogging()) return;
                        McLooperMod.LOGGER.info("Left clicked block at coordinates {},{},{}", new Object[]{x, y, z});
                        return;
                    } else {
                        class_3965 hitResult = new class_3965(new class_243((double)x + 0.5, (double)y + 0.5, (double)z + 0.5), class_2350.field_11036, blockPos, false);
                        client.field_1761.method_2896(client.field_1724, class_1268.field_5808, hitResult);
                        if (!McLooperMod.isVerboseLogging()) return;
                        McLooperMod.LOGGER.info("Right clicked block at coordinates {},{},{}", new Object[]{x, y, z});
                    }
                    return;
                } else {
                    McLooperMod.LOGGER.warn("World click block: invalid coordinate format '{}', expected 'x,y,z'", (Object)target);
                }
                return;
            }
            catch (NumberFormatException e) {
                McLooperMod.LOGGER.warn("World click block: invalid coordinate values in '{}'", (Object)target);
            }
        });
    }

    private void initializeConditionEvaluators() {
        this.conditionEvaluators.put("variable_equals", condition -> {
            String variable = (String)condition.params.get("variable");
            Object expectedValue = condition.params.get("value");
            if (variable != null && expectedValue != null) {
                Object actualValue = this.parseConditionValue(variable);
                Object expected = this.parseConditionValue(expectedValue.toString());
                String actualStr = actualValue != null ? actualValue.toString() : "";
                String expectedStr = expected != null ? expected.toString() : "";
                boolean result = actualStr.equals(expectedStr);
                boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                if (verbose) {
                    McLooperMod.LOGGER.info("Variable equals: '{}' ('{}') == '{}' ('{}') ? {}", new Object[]{variable, actualStr, expectedValue, expectedStr, result});
                }
                return result;
            }
            return false;
        });
        this.conditionEvaluators.put("variable_greater_than", condition -> {
            String variable = (String)condition.params.get("variable");
            Object expectedValue = condition.params.get("value");
            if (variable != null && expectedValue != null) {
                Object actualValue = this.parseConditionValue(variable);
                Object expected = this.parseConditionValue(expectedValue.toString());
                try {
                    double actual = Double.parseDouble(actualValue != null ? actualValue.toString() : "0");
                    double expectedNum = Double.parseDouble(expected != null ? expected.toString() : "0");
                    boolean result = actual > expectedNum;
                    boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                    if (verbose) {
                        McLooperMod.LOGGER.info("Variable greater than: '{}' ({}) > '{}' ({}) ? {}", new Object[]{variable, actual, expectedValue, expectedNum, result});
                    }
                    return result;
                }
                catch (NumberFormatException e) {
                    boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                    if (verbose) {
                        McLooperMod.LOGGER.warn("Variable greater than: Cannot compare non-numeric values '{}' and '{}'", actualValue, expected);
                    }
                    return false;
                }
            }
            return false;
        });
        this.conditionEvaluators.put("variable_less_than", condition -> {
            String variable = (String)condition.params.get("variable");
            Object expectedValue = condition.params.get("value");
            if (variable != null && expectedValue != null) {
                Object actualValue = this.parseConditionValue(variable);
                Object expected = this.parseConditionValue(expectedValue.toString());
                try {
                    double actual = Double.parseDouble(actualValue != null ? actualValue.toString() : "0");
                    double expectedNum = Double.parseDouble(expected != null ? expected.toString() : "0");
                    boolean result = actual < expectedNum;
                    boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                    if (verbose) {
                        McLooperMod.LOGGER.info("Variable less than: '{}' ({}) < '{}' ({}) ? {}", new Object[]{variable, actual, expectedValue, expectedNum, result});
                    }
                    return result;
                }
                catch (NumberFormatException e) {
                    boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                    if (verbose) {
                        McLooperMod.LOGGER.warn("Variable less than: Cannot compare non-numeric values '{}' and '{}'", actualValue, expected);
                    }
                    return false;
                }
            }
            return false;
        });
        this.conditionEvaluators.put("inventory_contains", this::lambda$initializeConditionEvaluators$19);
        this.conditionEvaluators.put("gui_contains", this::lambda$initializeConditionEvaluators$20);
        this.conditionEvaluators.put("gui_item_exists", condition -> {
            class_310 client = class_310.method_1551();
            class_437 patt0$temp = client.field_1755;
            if (patt0$temp instanceof class_465) {
                class_465 screen = (class_465)patt0$temp;
                String namePattern = (String)condition.params.getOrDefault("name_pattern", "");
                namePattern = this.replaceVariables(namePattern);
                String varName = (String)condition.params.get("var_name");
                if (namePattern.isEmpty()) {
                    return false;
                }
                for (class_1735 slot : screen.method_17577().field_7761) {
                    class_1799 stack = slot.method_7677();
                    if (stack.method_7960()) continue;
                    String displayName = stack.method_7964().getString();
                    try {
                        Pattern regex = Pattern.compile(namePattern);
                        Matcher matcher = regex.matcher(displayName);
                        if (!matcher.find()) continue;
                        boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                        if (varName != null && !varName.trim().isEmpty()) {
                            this.setVariable(varName, matcher.group(0));
                            for (int i = 1; i <= matcher.groupCount(); ++i) {
                                String groupValue = matcher.group(i);
                                if (groupValue == null) continue;
                                this.setVariable(varName + "_" + i, groupValue);
                            }
                            this.showVerboseVariables("GUI Item Condition", verbose);
                        }
                        return true;
                    }
                    catch (Exception e) {
                        McLooperMod.LOGGER.warn("Invalid regex pattern in gui_item_exists: {}", (Object)namePattern, (Object)e);
                        if (!displayName.contains(namePattern)) continue;
                        boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                        if (varName != null && !varName.trim().isEmpty()) {
                            this.setVariable(varName, displayName);
                            this.showVerboseVariables("GUI Item Condition", verbose);
                        }
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void initializeTriggerHandlers() {
        this.triggerHandlers.put("manual", (trigger, loop) -> false);
        this.triggerHandlers.put("on_start", (trigger, loop) -> this.worldJoined);
        this.triggerHandlers.put("interval", (trigger, loop) -> {
            long interval;
            Number num;
            Object intervalObj = trigger.params.get("interval");
            String intervalStr = intervalObj instanceof Number ? ((num = (Number)intervalObj) instanceof Double || num instanceof Float ? String.valueOf(num.longValue()) : num.toString()) : (intervalObj instanceof String ? (String)intervalObj : "20");
            intervalStr = this.replaceVariables(intervalStr);
            try {
                interval = intervalStr.contains(".") ? (long)Double.parseDouble(intervalStr) : Long.parseLong(intervalStr);
            }
            catch (NumberFormatException e) {
                interval = 20L;
                McLooperMod.LOGGER.warn("Invalid interval format {}, using default", (Object)intervalStr);
            }
            if (interval > 0L) {
                String key;
                Long lastTrigger;
                String unit = (String)trigger.params.getOrDefault("unit", "ticks");
                if ("seconds".equals(unit)) {
                    interval *= 20L;
                }
                if ((lastTrigger = this.loopTimers.get(key = "trigger_" + loop.name)) == null || this.tickCounter - lastTrigger >= interval) {
                    this.loopTimers.put(key, this.tickCounter);
                    return true;
                }
            }
            return false;
        });
        this.triggerHandlers.put("on_chat", (trigger, loop) -> {
            String pattern = (String)trigger.params.get("pattern");
            if (pattern != null) {
                try {
                    Matcher matcher;
                    Matcher matcher2;
                    String normalizedPattern = this.normalizeRegexPattern(pattern);
                    Pattern regex = Pattern.compile(normalizedPattern);
                    if (!this.lastChatMessage.isEmpty() && !this.processedChatMessages.contains(this.lastChatMessage) && (matcher2 = regex.matcher(this.lastChatMessage)).find()) {
                        this.processedChatMessages.add(this.lastChatMessage);
                        this.saveChatVariables(trigger, matcher2);
                        return true;
                    }
                    String multiLineChat = this.recentChatMessages.toString();
                    if (!multiLineChat.isEmpty() && !this.processedChatMessages.contains(multiLineChat) && (matcher = regex.matcher(multiLineChat)).find()) {
                        this.processedChatMessages.add(multiLineChat);
                        this.saveChatVariables(trigger, matcher);
                        return true;
                    }
                }
                catch (Exception e) {
                    McLooperMod.LOGGER.warn("Invalid regex pattern in chat trigger: {}", (Object)pattern, (Object)e);
                }
            }
            return false;
        });
        this.triggerHandlers.put("on_gui_item", (trigger, loop) -> {
            String pattern = (String)trigger.params.get("pattern");
            if (pattern != null && !this.lastGuiItems.isEmpty()) {
                try {
                    String normalizedPattern = this.normalizeRegexPattern(pattern);
                    Pattern regex = Pattern.compile(normalizedPattern);
                    Matcher matcher = regex.matcher(this.lastGuiItems);
                    if (matcher.find()) {
                        String varPrefix = (String)trigger.params.get("var_prefix");
                        boolean verbose = Boolean.parseBoolean(trigger.params.getOrDefault("verbose", "false").toString());
                        if (varPrefix != null && !varPrefix.trim().isEmpty()) {
                            for (int i = 0; i <= matcher.groupCount(); ++i) {
                                String groupValue = matcher.group(i);
                                if (groupValue == null) continue;
                                this.setVariable(varPrefix + "_" + i, groupValue);
                            }
                            this.showVerboseVariables("GUI Trigger", verbose);
                        }
                        return true;
                    }
                }
                catch (Exception e) {
                    McLooperMod.LOGGER.warn("Invalid regex pattern in GUI trigger: {}", (Object)pattern, (Object)e);
                }
            }
            return false;
        });
        this.triggerHandlers.put("keybind", (trigger, loop) -> {
            String keyName = (String)trigger.params.get("key");
            if (keyName != null && this.keyPressedThisTick.contains(keyName)) {
                boolean verbose = Boolean.parseBoolean(trigger.params.getOrDefault("verbose", "false").toString());
                if (verbose && McLooperMod.isVerboseLogging()) {
                    McLooperMod.LOGGER.info("Keybind trigger activated: {}", (Object)keyName);
                }
                return true;
            }
            return false;
        });
    }

    private void saveChatVariables(TaskTrigger trigger, Matcher matcher) {
        String varPrefix = (String)trigger.params.get("var_prefix");
        boolean verbose = Boolean.parseBoolean(trigger.params.getOrDefault("verbose", "false").toString());
        if (varPrefix != null && !varPrefix.trim().isEmpty()) {
            for (int i = 0; i <= matcher.groupCount(); ++i) {
                String groupValue = matcher.group(i);
                if (groupValue == null) continue;
                this.setVariable(varPrefix + "_" + i, groupValue);
            }
            this.showVerboseVariables("Chat Trigger", verbose);
        }
    }

    public void tick() {
        ++this.tickCounter;
        if (this.configManager.getConfig() == null || this.isPaused) {
            return;
        }
        this.updateGuiItems();
        for (TaskLoop taskLoop : this.configManager.getConfig().loops) {
            TriggerHandler handler;
            String preventKey;
            Long preventUntil;
            if (!taskLoop.enabled || taskLoop.trigger == null || (preventUntil = this.loopTimers.get(preventKey = "prevent_" + taskLoop.name)) != null && this.tickCounter < preventUntil || (handler = this.triggerHandlers.get(taskLoop.trigger.type)) == null || !handler.shouldTrigger(taskLoop.trigger, taskLoop)) continue;
            if (this.isStartupDelayPassed(taskLoop)) {
                this.executeLoop(taskLoop);
                continue;
            }
            if (!McLooperMod.isVerboseLogging()) continue;
            McLooperMod.LOGGER.info("Loop '{}' trigger fired but startup delay not yet passed", (Object)taskLoop.name);
        }
        for (Map.Entry entry2 : this.runningLoops.entrySet()) {
            TaskLoop loop = this.findLoopByName((String)entry2.getKey());
            if (loop == null) continue;
            boolean loopVerbose = false;
            if (loop.trigger != null && loop.trigger.params != null) {
                loopVerbose = Boolean.parseBoolean(loop.trigger.params.getOrDefault("verbose", "false").toString());
            }
            this.continueLoopExecution(loop, (ExecutionContext)entry2.getValue(), loopVerbose);
        }
        if (this.worldJoined) {
            this.worldJoined = false;
        }
        if (this.tickCounter - this.lastChatMessageTime > 20L) {
            this.lastChatMessage = "";
        }
        if (this.tickCounter - this.lastChatLineTime > 60L) {
            this.recentChatMessages.setLength(0);
            this.processedChatMessages.clear();
        }
        if (this.tickCounter - this.lastGuiItemsTime > 20L) {
            this.lastGuiItems = "";
        }
        this.loopTimers.entrySet().removeIf(entry -> ((String)entry.getKey()).startsWith("prevent_") && this.tickCounter >= (Long)entry.getValue());
        this.keyPressedThisTick.clear();
    }

    public void executeLoop(TaskLoop loop) {
        ExecutionContext context = this.runningLoops.get(loop.name);
        if (context == null) {
            context = new ExecutionContext();
            context.setCurrentLoopName(loop.name);
            this.runningLoops.put(loop.name, context);
        }
        boolean loopVerbose = false;
        if (loop.trigger != null && loop.trigger.params != null) {
            loopVerbose = Boolean.parseBoolean(loop.trigger.params.getOrDefault("verbose", "false").toString());
        }
        if (loopVerbose) {
            context.clearScopeVariables();
        }
        this.continueLoopExecution(loop, context, loopVerbose);
    }

    private void continueLoopExecution(TaskLoop loop, ExecutionContext context, boolean loopVerbose) {
        this.executeBlocksFromIndex(loop.blocks, context, context.currentBlockIndex);
        if (context.currentBlockIndex >= loop.blocks.size() || context.shouldStop) {
            if (loopVerbose) {
                this.showScopedVerboseVariables("Loop '" + loop.name + "'", true, context);
            }
            this.runningLoops.remove(loop.name);
        }
    }

    public void executeLoopManually(String loopName) {
        TaskLoop loop = this.findLoopByName(loopName);
        if (loop != null) {
            this.executeLoop(loop);
        }
    }

    private void executeBlocks(List<TaskBlock> blocks, ExecutionContext context) {
        this.executeBlocksFromIndex(blocks, context, 0);
    }

    private void executeBlocksFromIndex(List<TaskBlock> blocks, ExecutionContext context, int startIndex) {
        for (int i = startIndex; i < blocks.size(); ++i) {
            BlockExecutor executor;
            TaskBlock block = blocks.get(i);
            if (context.shouldStop) break;
            if (context.waitTicks > 0L) {
                --context.waitTicks;
                context.currentBlockIndex = i;
                return;
            }
            if (context.waitCondition != null) {
                if (!this.evaluateCondition(context.waitCondition)) {
                    context.currentBlockIndex = i;
                    return;
                }
                context.waitCondition = null;
            }
            if ((executor = this.blockExecutors.get(block.type)) != null) {
                try {
                    executor.execute(block, context);
                    if (context.waitTicks > 0L || context.waitCondition != null) {
                        context.currentBlockIndex = i + 1;
                        return;
                    }
                }
                catch (Exception e) {
                    McLooperMod.LOGGER.error("Error executing block of type: {}", (Object)block.type, (Object)e);
                }
                continue;
            }
            McLooperMod.LOGGER.warn("Unknown block type: {}", (Object)block.type);
        }
        context.currentBlockIndex = blocks.size();
    }

    private boolean evaluateCondition(TaskCondition condition) {
        if (condition == null || condition.type == null) {
            return false;
        }
        ConditionEvaluator evaluator = this.conditionEvaluators.get(condition.type);
        if (evaluator != null) {
            try {
                return evaluator.evaluate(condition);
            }
            catch (Exception e) {
                McLooperMod.LOGGER.error("Error evaluating condition '{}': {}", new Object[]{condition.type, e.getMessage(), e});
                return false;
            }
        }
        McLooperMod.LOGGER.warn("Unknown condition type: {}", (Object)condition.type);
        return false;
    }

    private TaskLoop findLoopByName(String name) {
        if (this.configManager.getConfig() != null) {
            for (TaskLoop loop : this.configManager.getConfig().loops) {
                if (!loop.name.equals(name)) continue;
                return loop;
            }
        }
        return null;
    }

    private String replaceVariables(String text) {
        class_310 client;
        String placeholder;
        if (text == null) {
            return null;
        }
        String result = text;
        for (Map.Entry<String, Object> entry : this.runtimeVariables.entrySet()) {
            placeholder = "${" + entry.getKey() + "}";
            if (!result.contains(placeholder)) continue;
            result = result.replace(placeholder, entry.getValue().toString());
        }
        if (this.configManager.getConfig() != null && this.configManager.getConfig().variables != null) {
            for (Map.Entry<String, Object> entry : this.configManager.getConfig().variables.entrySet()) {
                placeholder = "${" + entry.getKey() + "}";
                if (!result.contains(placeholder)) continue;
                result = result.replace(placeholder, entry.getValue().toString());
            }
        }
        if ((client = class_310.method_1551()).method_1548() != null) {
            result = result.replace("${minecraft.username}", client.method_1548().method_1676());
            result = result.replace("${minecraft.uuid}", client.method_1548().method_44717() != null ? client.method_1548().method_44717().toString() : "unknown");
        }
        if (client.field_1724 != null) {
            result = result.replace("${minecraft.health}", String.valueOf(client.field_1724.method_6032()));
            result = result.replace("${minecraft.hunger}", String.valueOf(client.field_1724.method_7344().method_7586()));
            result = result.replace("${minecraft.level}", String.valueOf(client.field_1724.field_7520));
            result = result.replace("${minecraft.xp}", String.valueOf(client.field_1724.field_7495));
            result = result.replace("${minecraft.posX}", String.valueOf(Math.round(client.field_1724.method_23317())));
            result = result.replace("${minecraft.posY}", String.valueOf(Math.round(client.field_1724.method_23318())));
            result = result.replace("${minecraft.posZ}", String.valueOf(Math.round(client.field_1724.method_23321())));
            result = result.replace("${minecraft.gameMode}", client.field_1724.method_31549().field_7477 ? "Creative" : "Survival");
        }
        if (client.field_1687 != null) {
            result = result.replace("${minecraft.worldTime}", String.valueOf(client.field_1687.method_8532()));
            result = result.replace("${minecraft.dayTime}", String.valueOf(client.field_1687.method_8532() % 24000L));
            if (client.field_1687.method_8597() != null) {
                result = result.replace("${minecraft.dimension}", client.field_1687.method_27983().method_29177().toString());
            }
        }
        if (client.method_1558() != null) {
            result = result.replace("${minecraft.serverAddress}", client.method_1558().field_3761);
            result = result.replace("${minecraft.serverName}", client.method_1558().field_3752);
        }
        result = result.replace("${timestamp}", Instant.now().toString());
        Pattern randomizerPattern = Pattern.compile("\\[(\\d+)-(\\d+)\\]");
        Matcher matcher = randomizerPattern.matcher(result);
        while (matcher.find()) {
            int min = Integer.parseInt(matcher.group(1));
            int max = Integer.parseInt(matcher.group(2));
            int randomValue = this.random.nextInt(max - min + 1) + min;
            result = result.replace(matcher.group(0), String.valueOf(randomValue));
            matcher = randomizerPattern.matcher(result);
        }
        return result;
    }

    public void onWorldJoin() {
        this.worldJoined = true;
        this.worldJoinTick = this.tickCounter;
        if (McLooperMod.isVerboseLogging()) {
            McLooperMod.LOGGER.info("Loop executor: World joined at tick {}", (Object)this.worldJoinTick);
        }
    }

    public void onWorldLeave() {
        this.worldJoined = false;
        this.worldJoinTick = 0L;
        this.runtimeVariables.clear();
        this.loopTimers.clear();
        this.runningLoops.clear();
        this.processedChatMessages.clear();
        this.recentChatMessages.setLength(0);
        if (McLooperMod.isVerboseLogging()) {
            McLooperMod.LOGGER.info("Loop executor: World left, cleared runtime state");
        }
    }

    private boolean isStartupDelayPassed(TaskLoop loop) {
        boolean delayPassed;
        if (loop.startupDelay <= 0) {
            return true;
        }
        long ticksSinceJoin = this.tickCounter - this.worldJoinTick;
        boolean bl = delayPassed = ticksSinceJoin >= (long)loop.startupDelay;
        if (McLooperMod.isVerboseLogging() && !delayPassed) {
            McLooperMod.LOGGER.info("Loop '{}' startup delay: {} ticks remaining", (Object)loop.name, (Object)((long)loop.startupDelay - ticksSinceJoin));
        }
        return delayPassed;
    }

    public void setVariable(String name, Object value) {
        this.runtimeVariables.put(name, value);
    }

    public Object getVariable(String name) {
        return this.runtimeVariables.get(name);
    }

    public void togglePause() {
        boolean bl = this.isPaused = !this.isPaused;
        if (McLooperMod.isVerboseLogging()) {
            McLooperMod.LOGGER.info("Loop execution {}", (Object)(this.isPaused ? "PAUSED" : "RESUMED"));
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 != null) {
            String message = this.isPaused ? "\u00a7c[MC Looper] Loop execution PAUSED" : "\u00a7a[MC Looper] Loop execution RESUMED";
            client.field_1705.method_1743().method_1812((class_2561)class_2561.method_43470((String)message));
        }
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void setPaused(boolean paused) {
        if (this.isPaused != paused) {
            this.togglePause();
        }
    }

    private void showVerboseVariables(String context, boolean verbose) {
        if (!verbose || this.runtimeVariables.isEmpty()) {
            return;
        }
        StringBuilder varList = new StringBuilder();
        varList.append("\u00a7e[").append(context).append("] Variables: ");
        boolean first = true;
        for (Map.Entry<String, Object> entry : this.runtimeVariables.entrySet()) {
            if (!first) {
                varList.append("\u00a77, ");
            }
            varList.append("\u00a7a").append(entry.getKey()).append("\u00a77=\u00a7f").append(entry.getValue());
            first = false;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 != null) {
            client.field_1705.method_1743().method_1812((class_2561)class_2561.method_43470((String)varList.toString()));
        }
    }

    private void showScopedVerboseVariables(String context, boolean verbose, ExecutionContext executionContext) {
        if (!verbose || executionContext.scopeCreatedVariables.isEmpty()) {
            return;
        }
        StringBuilder varList = new StringBuilder();
        varList.append("\u00a7e[").append(context).append("] Scope Variables: ");
        boolean first = true;
        for (String varName : executionContext.scopeCreatedVariables) {
            if (!first) {
                varList.append("\u00a77, ");
            }
            Object value = executionContext.scopeVariableValues.get(varName);
            varList.append("\u00a7a").append(varName).append("\u00a77=\u00a7f").append(value != null ? value : "null");
            first = false;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 != null) {
            client.field_1705.method_1743().method_1812((class_2561)class_2561.method_43470((String)varList.toString()));
        }
    }

    public void onChatMessage(String message) {
        this.lastChatMessage = message;
        this.lastChatMessageTime = this.tickCounter;
        if (this.tickCounter - this.lastChatLineTime > 60L) {
            this.recentChatMessages.setLength(0);
        }
        if (this.recentChatMessages.length() > 0) {
            this.recentChatMessages.append("\n");
        }
        this.recentChatMessages.append(message);
        this.lastChatLineTime = this.tickCounter;
        String[] lines = this.recentChatMessages.toString().split("\n");
        if (lines.length > 10) {
            this.recentChatMessages.setLength(0);
            for (int i = lines.length - 10; i < lines.length; ++i) {
                if (this.recentChatMessages.length() > 0) {
                    this.recentChatMessages.append("\n");
                }
                this.recentChatMessages.append(lines[i]);
            }
        }
    }

    private void updateGuiItems() {
        class_310 client = class_310.method_1551();
        class_437 class_4372 = client.field_1755;
        if (class_4372 instanceof class_465) {
            class_465 screen = (class_465)class_4372;
            StringBuilder items = new StringBuilder();
            for (class_1735 slot : screen.method_17577().field_7761) {
                class_1799 stack = slot.method_7677();
                if (stack.method_7960()) continue;
                if (items.length() > 0) {
                    items.append("|");
                }
                items.append(stack.method_7964().getString());
            }
            String newGuiItems = items.toString();
            if (!newGuiItems.equals(this.lastGuiItems)) {
                this.lastGuiItems = newGuiItems;
                this.lastGuiItemsTime = this.tickCounter;
            }
        } else if (!this.lastGuiItems.isEmpty()) {
            this.lastGuiItems = "";
            this.lastGuiItemsTime = this.tickCounter;
        }
    }

    public void onKeyPressed(String keyName) {
        if (keyName != null) {
            this.keyPressedThisTick.add(keyName);
        }
    }

    private Object parseConditionValue(String input) {
        if (input == null) {
            return null;
        }
        if ((input = input.trim()).startsWith("\"") && input.endsWith("\"") || input.startsWith("'") && input.endsWith("'")) {
            return input.substring(1, input.length() - 1);
        }
        if (input.startsWith("${") && input.endsWith("}")) {
            return this.replaceVariables(input);
        }
        try {
            if (input.contains(".")) {
                return Double.parseDouble(input);
            }
            return Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
            Object value = this.runtimeVariables.get(input);
            return value != null ? value : input;
        }
    }

    private String normalizeRegexPattern(String pattern) {
        if (pattern == null) {
            return null;
        }
        pattern = this.replaceVariables(pattern);
        return pattern;
    }

    /*
     * Unable to fully structure code
     */
    private /* synthetic */ boolean lambda$initializeConditionEvaluators$20(TaskCondition condition) {
        searchMode = (String)condition.params.getOrDefault("search_mode", "registry");
        searchTerm = null;
        searchTerm = "regex".equals(searchMode) != false ? (String)condition.params.get("regex_pattern") : (String)condition.params.get("item_name");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }
        searchTerm = this.replaceVariables(searchTerm);
        client = class_310.method_1551();
        patt0$temp = client.field_1755;
        if (patt0$temp instanceof class_465) {
            screen = (class_465)patt0$temp;
            for (class_1735 slot : screen.method_17577().field_7761) {
                stack = slot.method_7677();
                if (stack.method_7960()) continue;
                matches = false;
                if ("regex".equals(searchMode)) {
                    try {
                        displayName = stack.method_7964().getString();
                        if (displayName.matches(searchTerm)) {
                            matches = true;
                        }
                        if (matches || !stack.method_7985() || !stack.method_7969().method_10545("display") || !(displayTag = stack.method_7969().method_10562("display")).method_10545("Lore")) ** GOTO lbl35
                        loreList = displayTag.method_10554("Lore", 8);
                        for (j = 0; j < loreList.size(); ++j) {
                            loreText = loreList.method_10608(j);
                            if (!loreText.matches(searchTerm)) continue;
                            matches = true;
                        }
                    }
                    catch (Exception e) {
                        displayName = stack.method_7964().getString();
                        matches = displayName.toLowerCase().contains(searchTerm.toLowerCase());
                    }
                } else {
                    stackName = stack.method_7909().toString();
                    displayName = stack.method_7964().getString();
                    v0 = matches = stackName.contains(searchTerm) != false || displayName.toLowerCase().contains(searchTerm.toLowerCase()) != false;
                }
lbl35:
                // 5 sources

                if (!matches) continue;
                varName = (String)condition.params.get("var_name");
                verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                if (varName != null && !varName.trim().isEmpty()) {
                    this.setVariable(varName, stack.method_7964().getString());
                    this.setVariable(varName + "_registry", stack.method_7909().toString());
                    this.setVariable(varName + "_count", stack.method_7947());
                    this.setVariable(varName + "_slot", slot.method_34266());
                    this.showVerboseVariables("GUI Condition", verbose);
                }
                return true;
            }
        }
        return false;
    }

    /*
     * Unable to fully structure code
     */
    private /* synthetic */ boolean lambda$initializeConditionEvaluators$19(TaskCondition condition) {
        searchMode = (String)condition.params.getOrDefault("search_mode", "registry");
        searchTerm = null;
        searchTerm = "regex".equals(searchMode) != false ? (String)condition.params.get("regex_pattern") : (String)condition.params.get("item_name");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }
        searchTerm = "regex".equals(searchMode) != false ? this.normalizeRegexPattern(searchTerm) : this.replaceVariables(searchTerm);
        client = class_310.method_1551();
        if (client.field_1724 != null && client.field_1724.method_31548() != null) {
            for (i = 0; i < client.field_1724.method_31548().method_5439(); ++i) {
                stack = client.field_1724.method_31548().method_5438(i);
                if (stack.method_7960()) continue;
                matches = false;
                if ("regex".equals(searchMode)) {
                    try {
                        displayName = stack.method_7964().getString();
                        if (displayName.matches(searchTerm)) {
                            matches = true;
                        }
                        if (matches || !stack.method_7985() || !stack.method_7969().method_10545("display") || !(displayTag = stack.method_7969().method_10562("display")).method_10545("Lore")) ** GOTO lbl33
                        loreList = displayTag.method_10554("Lore", 8);
                        for (j = 0; j < loreList.size(); ++j) {
                            loreText = loreList.method_10608(j);
                            if (!loreText.matches(searchTerm)) continue;
                            matches = true;
                        }
                    }
                    catch (Exception e) {
                        displayName = stack.method_7964().getString();
                        matches = displayName.toLowerCase().contains(searchTerm.toLowerCase());
                    }
                } else {
                    stackName = stack.method_7909().toString();
                    displayName = stack.method_7964().getString();
                    v0 = matches = stackName.contains(searchTerm) != false || displayName.toLowerCase().contains(searchTerm.toLowerCase()) != false;
                }
lbl33:
                // 5 sources

                if (!matches) continue;
                varName = (String)condition.params.get("var_name");
                verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                if (varName != null && !varName.trim().isEmpty()) {
                    this.setVariable(varName, stack.method_7964().getString());
                    this.setVariable(varName + "_registry", stack.method_7909().toString());
                    this.setVariable(varName + "_count", stack.method_7947());
                    this.showVerboseVariables("Inventory Condition", verbose);
                }
                return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    public static interface BlockExecutor {
        public void execute(TaskBlock var1, ExecutionContext var2);
    }

    @FunctionalInterface
    public static interface ConditionEvaluator {
        public boolean evaluate(TaskCondition var1);
    }

    @FunctionalInterface
    public static interface TriggerHandler {
        public boolean shouldTrigger(TaskTrigger var1, TaskLoop var2);
    }

    public static class ExecutionContext {
        public boolean shouldStop = false;
        public long waitTicks = 0L;
        public TaskCondition waitCondition = null;
        public int currentBlockIndex = 0;
        public String currentLoopName = null;
        public Set<String> scopeCreatedVariables = new HashSet<String>();
        public Map<String, Object> scopeVariableValues = new HashMap<String, Object>();

        public String getCurrentLoopName() {
            return this.currentLoopName;
        }

        public void setCurrentLoopName(String loopName) {
            this.currentLoopName = loopName;
        }

        public void recordVariableCreated(String variableName, Object value) {
            this.scopeCreatedVariables.add(variableName);
            this.scopeVariableValues.put(variableName, value);
        }

        public void clearScopeVariables() {
            this.scopeCreatedVariables.clear();
            this.scopeVariableValues.clear();
        }
    }
}
