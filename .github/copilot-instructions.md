
# GitHub Copilot Instructions for MC Looper Mod

## Project Overview
This workspace contains the Fabric mod "MC Looper" for Minecraft 1.20.4, built with Java 21 and Gradle. The mod is designed for loop-based automation tasks with advanced chat parsing, auto-clicker logic, and server communication features. The project supports configurable task automation inspired by Tasker for Android.

## Mod Functionality
- **Loop-Based Automation:** Configurable task loops with triggers, conditions, and blocks for automation
- **Chat Message Handling:** Listens for chat messages using Fabric's `ClientReceiveMessageEvents.ALLOW_GAME`. Parses messages for variables and trigger patterns. Handles color codes, commas, and case-insensitive matching.
- **Auto-Clicker:** Detects GUI items (e.g., "Click to Confirm") and automatically clicks them. Logic is restored and improved for reliability.
- **Server Communication:** Sends JSON payloads (including full mob coin balance and username) to a PHP endpoint via HTTP POST. Delta calculations were removed for simplicity.
- **Logging:** Uses SLF4J for concise, essential logging. Verbose or redundant logs have been cleaned up.
- **Configuration:** Designed for clear, extensible configuration and logic separation. Uses idiomatic, modern Fabric/Java code.

## Development Notes
- **Task-Based Architecture:** The mod uses a configurable loop-based system inspired by Tasker for Android, allowing users to create complex automation workflows
- **Build System:** Uses Gradle 8.6 and Fabric API 0.91.2+1.20.4. Java 21 JDK required. Build issues were resolved by matching event listener signatures and dependency versions.
- **Event Handling:** Correct event signatures are crucial. Mismatches caused build failures and runtime errors.
- **Chat Parsing:** Regex was improved to handle color codes, commas, and case. Pattern matching for triggers supports complex chat interactions.
- **Auto-Clicker:** Logic scans GUI and clicks target items reliably. Configurable through block parameters.
- **Logging:** Clean, minimal logging improves maintainability and debugging. Uses McLooper prefix for identification.
- **Server Communication:** HTTP POST blocks allow integration with external services and APIs.
- **Variable System:** Runtime and config variables with Minecraft context variables for dynamic automation.

## Lessons Learned
- **Workspace Duplication:** Effective for parallel stable/experimental development. All files and context are preserved.
- **Event Signatures:** Must match Fabric API exactly to avoid errors.
- **Regex Robustness:** Chat parsing must handle all possible color/formatting codes and number formats.
- **Logging Discipline:** Clean, minimal logging improves maintainability and debugging.
## Lessons Learned
- **Configurable Architecture:** Task-based systems provide much more flexibility than hardcoded automation
- **Event Signatures:** Must match Fabric API exactly to avoid errors.
- **Pattern Matching:** Chat and GUI trigger patterns must handle all possible color/formatting codes
- **Logging Discipline:** Clean, minimal logging improves maintainability and debugging.
- **Block-Based Logic:** Modular block system allows complex automation workflows to be built visually.

## Known Issues & Pitfalls
- **Event Listener Mismatches:** Caused build/runtime errors until corrected.
- **Pattern Complexity:** Regex patterns in triggers require careful tuning for edge cases.
- **Block Dependencies:** Some blocks depend on others being executed first (e.g., variables must be set before use).
- **Performance:** Large loops or complex conditions can impact game performance.

## File Structure Highlights
- `McLooperMod.java`: Main mod logic (loop execution, event handling, keybindings)
- `config/`: Configuration classes for tasks, loops, blocks, conditions, and triggers
- `loop/LoopExecutor.java`: Core loop execution engine with block executors and condition evaluators
- `ui/`: Screen classes for the in-game configuration interface
- `fabric.mod.json`: Mod metadata
- `assets/mclooper/`: Mod icon and resources
- `build.gradle`, `settings.gradle`: Build configuration

## Usage Tips
- Use the in-game UI (Right Shift key) to configure loops and blocks visually
- Always test loops manually before enabling automatic triggers
- Use verbose mode in blocks and conditions to debug variable states
- Keep loops simple and focused on specific tasks for better reliability
- Monitor performance when using interval triggers with short delays

## Documentation Standards
- **MANDATORY:** When explaining mod features or functionality, always provide usage instructions for BOTH interfaces when applicable:
  - **UI Usage:** Step-by-step instructions for the in-game visual interface (Right Shift key)
  - **JSON Config:** Direct configuration file examples with proper syntax and structure
- **Dual Interface Support:** The mod supports both visual configuration and direct JSON editing - always document both approaches
- **Configuration Examples:** Include specific JSON snippets showing exact syntax for blocks, conditions, triggers, and variables
- **UI Navigation:** Provide clear menu navigation paths and button descriptions for visual configuration
- **Validation:** Explain how users can verify configurations work correctly in both interfaces

## Development Workflow & Change Tracking
- **MANDATORY:** After making any code changes, always log them using the changelog script
- **Change Logging Script:** `log-changes.ps1` located in project root
- **Usage:** `powershell -ExecutionPolicy Bypass -File "log-changes.ps1" -Open "n" "Change description 1" "Change description 2"`
- **Auto-open options:** Use `-Open "y"` to auto-open changelog, `-Open "n"` to skip opening, or omit for prompt
- **Interactive mode:** Run script without parameters to be prompted for each change
- **Purpose:** Maintains detailed development history without git overhead for rapid iteration
- **Location:** All changes logged to `CHANGELOG.md` with timestamps

### Change Logging Best Practices
- Log changes immediately after making them (before testing/building)
- Be specific and descriptive in change descriptions
- Group related changes in a single logging session
- Include file names and key functionality affected
- Note any breaking changes or important behavioral modifications

---
This file should be updated as new features, fixes, or lessons are learned during development.
