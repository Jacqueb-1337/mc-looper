# MC Looper - Loop-Based Automation System

This is MC Looper, a configurable loop-based task automation system for Minecraft inspired by Tasker for Android.

## Key Features

### No Default Automation
- When installed, the mod does nothing automatically
- All actions must be configured through the loop system
- Users have complete control over what runs and when

### Visual Loop Editor
- Press **Right Shift** (configurable) to open the loop editor
- Create, edit, delete, and manage loops through an intuitive GUI
- Real-time preview of loop structure and blocks

### Block-Based Programming
- **Comment Blocks**: Add documentation (displayed in yellow)
- **Chat Blocks**: Send messages to game chat
- **Wait Blocks**: Pause execution for specified time
- **Call Loop Blocks**: Execute another loop inline
- **If Blocks**: Conditional execution based on conditions
- **Loop Blocks**: Repeat nested blocks N times
- **Wait Until Blocks**: Wait for a condition to become true
- **Steal Items Blocks**: (Placeholder for NBT-based item stealing)

### Flexible Triggers
- **Manual**: Run via UI button or command
- **On Start**: Run when joining a world
- **Interval**: Run every X seconds/ticks
- **On Chat**: Run when chat matches a pattern (planned)
- **On Key**: Run when a key is pressed (planned)

### Variable System
- Runtime variables: Set and modified during execution
- Config variables: Persistent across sessions
- Minecraft variables: `${minecraft.username}`, etc.
- Variable replacement in messages and parameters

## Example Usage

### Basic Chat Loop
```json
{
  "name": "SayHello",
  "enabled": true,
  "trigger": { "type": "manual" },
  "blocks": [
    {
      "type": "comment",
      "params": { "text": "Simple hello message" }
    },
    {
      "type": "chat",
      "params": { "message": "Hello world!" }
    }
  ]
}
```

### Conditional Loop
```json
{
  "name": "StealIfRich",
  "enabled": true,
  "trigger": { "type": "interval", "params": { "interval": 30, "unit": "seconds" } },
  "blocks": [
    {
      "type": "if",
      "condition": {
        "type": "variable_greater_than",
        "params": { "variable": "mob_coins", "value": 1000 }
      },
      "blocks": [
        {
          "type": "steal_items",
          "params": { "nbt_match": {} }
        }
      ]
    }
  ]
}
```

### Nested Loop with Calls
```json
{
  "name": "ComplexTask",
  "enabled": true,
  "trigger": { "type": "manual" },
  "blocks": [
    {
      "type": "loop",
      "params": { "count": 3 },
      "blocks": [
        {
          "type": "call_loop",
          "params": { "loop": "SayHello" }
        },
        {
          "type": "wait",
          "params": { "time": 1, "unit": "seconds" }
        }
      ]
    }
  ]
}
```

## File Structure

```
src/main/java/com/jacqueb/mclooper/
├── McLooperMod.java           # Main mod entry point
├── config/
│   ├── TaskConfig.java        # Root config structure
│   ├── TaskLoop.java          # Loop definition
│   ├── TaskBlock.java         # Block definition
│   ├── TaskTrigger.java       # Trigger definition
│   ├── TaskCondition.java     # Condition definition
│   └── ConfigManager.java     # Config loading/saving
├── loop/
│   └── LoopExecutor.java      # Loop execution engine
└── ui/
    ├── LoopEditorScreen.java  # Main editor UI
    ├── BlockSelectorScreen.java # Block type selection
    └── BlockEditorScreen.java # Block parameter editor
```

## Configuration

The config file is automatically created at `config/mclooper-tasks.json`. It can be edited manually or through the in-game UI.

See `example-config.json` for a comprehensive example with various block types and triggers.

## Keybinds

- **Right Shift**: Open Loop Editor (configurable in Minecraft controls)

## Planned Features

- Chat message parsing triggers
- Inventory condition checking
- NBT-based item stealing implementation
- Server communication blocks
- More condition types
- Loop scheduling and timing controls
- Import/export of loop configurations
- Shared loop libraries

## Migration from Original NBTSteal

The original NBTSteal automated everything by default. This experimental version requires manual configuration but offers much more flexibility and control. To replicate the original behavior:

1. Create loops for each desired automation
2. Set appropriate triggers (interval, chat, etc.)
3. Configure the same NBT matching and server communication
4. Enable only the loops you want

This approach gives you complete control while maintaining all the original functionality.
