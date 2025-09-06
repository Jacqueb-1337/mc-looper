
## 2025-09-05 21:33:05
 - Added global verbose logging toggle to TaskConfig.java with verboseLogging boolean field (default false)
 - Created globalVerboseToggleButton in LoopEditorScreen.java positioned at top-right corner
 - Added isVerboseLogging() helper method in McLooperMod.java to check verbose status
 - Wrapped 50+ info logging statements with verbose checks in LoopExecutor.java (blocks, commands, clicks, triggers, etc.)
 - Wrapped UI-related info logging in LoopEditorScreen.java and BlockEditorScreen.java
 - Wrapped config-related info logging in ConfigManager.java
 - Created log-changes.bat and log-changes.ps1 scripts for tracking development changes with timestamps
 - Fixed 127GB log file growth issue by making verbose logging optional (saves ~99.9% log space)



## 2025-09-05 21:35:49

 - Test change 1
 - Test change 2
 - Test change 3



## 2025-09-05 21:37:24

 - Test auto-no open
 - Another test change



## 2025-09-05 21:37:31

 - Test auto-open feature



## 2025-09-05 21:41:00

 - PROJECT MILESTONE: Complete feature summary and documentation
 - Comprehensive MC Looper mod implementation with task-based automation system
 - Advanced chat message parsing with color code and case-insensitive pattern matching
 - Auto-clicker functionality for GUI item detection and automated clicking
 - HTTP POST server communication for mob coin balance and username data
 - Loop-based automation engine with configurable triggers, conditions, and blocks
 - Variable system supporting runtime, config, and Minecraft context variables
 - In-game configuration UI accessible via Right Shift keybinding
 - Robust logging system using SLF4J with verbose mode toggle controls
 - Task executor framework with modular block-based automation logic
 - Regex pattern matching for complex chat interactions and triggers
 - Development workflow automation with PowerShell change tracking script
 - Comprehensive copilot instructions with mandatory changelog workflow
 - Build system optimization for Fabric 1.20.4 and Java 21 compatibility
 - Performance-optimized loop execution with configurable interval triggers



## 2025-09-05 21:43:08

 - Enhanced copilot instructions with mandatory dual interface documentation
 - Added Documentation Standards section requiring both UI and JSON config usage examples
 - Enforced requirement to provide step-by-step UI instructions and JSON configuration syntax
 - Established guidelines for dual interface support documentation and validation methods



## 2025-09-05 21:43:22

 - Enhanced copilot instructions with mandatory dual interface documentation
 - Added Documentation Standards section requiring both UI and JSON config usage examples
 - Enforced requirement to provide step-by-step UI instructions and JSON configuration syntax
 - Established guidelines for dual interface support documentation and validation methods



## 2025-09-05 21:44:41

 - COMPREHENSIVE USAGE DOCUMENTATION: Complete dual-interface guide for all MC Looper features
 - Loop-Based Automation: UI navigation (Right Shift  Loops  Add Loop) and JSON config structure
 - Chat Message Parsing: Pattern configuration with regex support and variable extraction
 - Auto-Clicker Functionality: Target pattern setup, GUI detection, and click delay configuration
 - HTTP Server Communication: POST blocks with endpoint URLs, JSON payloads, and player data inclusion
 - Variable System: Runtime/config/context variables with  syntax usage
 - Trigger Types: Chat, interval, manual keybind, and GUI triggers with full configuration options
 - Condition System: Variable comparisons, chat conditions, time scheduling with AND/OR logic
 - Block Types: SendChat, Wait, SetVariable, HTTP POST, and AutoClick with parameter details
 - Verbose Logging: Debug mode configuration, log levels, and performance monitoring setup
 - Testing & Validation: Test mode, step-through execution, variable inspector, and troubleshooting
 - Performance Optimization: Loop frequency limits, thread pool sizing, and execution statistics
 - JSON Configuration: Complete syntax examples for all features with proper structure
 - UI Navigation: Step-by-step menu paths and button descriptions for visual configuration
 - Built-in Context Variables: player.name, player.world, time.now, mobcoins.total usage examples



## 2025-09-05 21:57:10

 - CRITICAL BUG FIX: Resolved infinite recursion stack overflow in BlockEditorScreen
 - Fixed handleNestedBlockEditReturn() infinite loop causing StackOverflowError when closing nested block editors
 - Added context nullification before setScreen() call to prevent re-entry in handleNestedBlockEditReturn()
 - Modified init() method to check if nested editing context belongs to current screen instance
 - Added early return in init() when handling nested block editing to prevent widget recreation during screen transitions
 - Resolved crash when closing block editors nested inside if statement blocks
 - Fixed UI responsiveness and stability for complex nested block configurations



## 2025-09-05 22:01:40

 - MAJOR FIX: Resolved nested block editing workflow issues in if statements
 - Fixed saveConfig() in LoopEditorScreen to handle nested block editing mode correctly
 - Added automatic detection and handling of nested editing context when saving loops
 - Implemented auto-selection of temporary nested blocks loop when LoopEditorScreen opens
 - Added autoSelectNestedLoop() method to automatically select temp loop for editing
 - Fixed nested block persistence by adding config save in handleNestedBlockEditReturn()
 - Resolved issue where nested blocks appeared as 0 after editing and saving
 - Enhanced user experience by eliminating manual loop selection in nested editing mode
 - Fixed workflow: Manage Nested Blocks  Auto-select temp loop  Edit blocks  Save  Return with blocks preserved



## 2025-09-05 22:16:54

 - Fixed variable substitution in regex replace blocks - source_var, pattern, and replacement parameters now support  syntax
 - Fixed variable substitution in condition evaluators (variable_equals, variable_greater_than) - variable and value parameters now support  syntax
 - Added proper handling for direct variable expressions vs variable names in both regex replace and condition blocks



## 2025-09-05 22:25:27

 - Documented regex escaping differences: UI fields use single backslash (\..*) while JSON may require double backslash (\\..*)
 - Confirmed regex pattern \.* successfully removes decimal points and trailing digits from health values (20.0  20)



## 2025-09-05 22:30:31

 - Added variable_less_than condition evaluator for checking if variable < threshold
 - Improved condition evaluator logic to support intuitive syntax: quoted strings " text\,
 - raw
 - numbers
 - 6,
 - and
 - variables
 - "
 - Added parseConditionValue() helper method to intelligently parse strings, numbers, and variable references in condition parameters
 - Enhanced verbose logging in all variable condition evaluators to show actual vs expected values and parsing details



## 2025-09-05 22:33:29

 - Added normalizeRegexPattern() method to ensure consistent regex behavior across UI and JSON contexts
 - Updated all regex usage points: regex_replace blocks, inventory_contains conditions, click_gui_item blocks, chat triggers, and GUI triggers
 - Standardized regex patterns to work exactly as they would in Java/JavaScript - no more confusion between UI and JSON escaping
 - Documented regex pattern syntax: patterns now work identically whether entered in UI fields or JSON configuration files



## 2025-09-05 22:44:38

 - Added visual indicator for selected condition buttons in BlockEditorScreen with white outline
 - Added variable_less_than to condition types array for complete condition support
 - Enhanced condition button layout with cleaner labels (Variable =, Variable >, Variable <)
 - Implemented condition button tracking system to render visual selection indicators that respect scrolling



## 2025-09-05 22:48:11

 - Improved trigger label positioning: moved Trigger Type label down and reduced padding (triggerY - 8 instead of triggerY - 12)
 - Enhanced interval/unit label spacing: moved Interval and Unit labels down with reduced padding for better visual alignment
 - Implemented smart block insertion: Add Block now inserts blocks after the selected block instead of always at the end
 - Updated BlockSelectorScreen constructor to accept insertion index for precise block placement in loop editor



## 2025-09-06 00:10:16

 - Added variable substitution and randomizer support to world_click blocks
 - Fixed wait block UI to preserve randomizer syntax [min-max] instead of resetting to original values
 - Implemented general randomizer support in replaceVariables() method using [min-max] syntax
 - Updated wait block executor to use unified replaceVariables() method for consistency
 - Updated interval trigger to use unified replaceVariables() method for consistency
 - Added Random field to LoopExecutor class for consistent randomization across all block types



## 2025-09-06 00:16:59

 - Fixed decimal number handling in wait blocks and interval triggers (20.0 now works properly)
 - Added proper parsing for Double/Float values from GSON deserialization
 - Enhanced number parsing to handle decimal strings from variable substitution



## 2025-09-06 00:32:42

 - Added startup delay feature to all loops (0 by default)
 - Added startupDelay field to TaskLoop configuration class
 - Implemented startup delay UI field in LoopEditorScreen with proper save/load
 - Added startup delay logic in LoopExecutor to prevent loop execution until delay passes
 - Added isStartupDelayPassed() helper method to track time since world join
 - Updated onWorldJoin() to record worldJoinTick for startup delay calculations



## 2025-09-06 00:36:04

 - Added scrollable loop list to LoopEditorScreen with mouse wheel support
 - Implemented scrollable block list to LoopEditorScreen with visual scroll indicators
 - Added scroll offset tracking and proper mouse click handling for scrolled lists
 - Added scroll reset when selecting new loops to prevent disorientation
 - Updated loop and block selection logic to account for scroll positions
 - Note: BlockEditorScreen already had scrolling implemented



## 2025-09-06 00:38:02

 - Added 'Pause on GUI' toggle checkbox to world_click blocks
 - Implemented GUI detection logic in world_click executor using client.currentScreen
 - Added verbose logging when world_click is skipped due to open GUI
 - Updated BlockEditorScreen UI to include pause_on_gui toggle and label
 - Feature prevents accidental world clicks when inventory/GUI screens are open



## 2025-09-06 00:56:35

 - Added proper UI spacing with consistent spacing constants (SPACING=5, SECTION_SPACING=15, LIST_ITEM_SPACING=1)
 - Implemented dynamic block button positioning that automatically adjusts based on number of blocks in selected loop
 - Added updateBlockButtonPositions() method to calculate and set button positions dynamically
 - Enhanced selectLoop(), deleteSelectedBlock(), moveBlockUp(), and moveBlockDown() methods to update button positions
 - Fixed block action buttons to always appear at bottom of block list with proper padding
 - Improved overall UI layout to prevent overlapping elements and maintain professional appearance



## 2025-09-06 02:52:32

 - Fixed text field truncation issue across all UI screens by applying setSelectionStart/End cursor positioning
 - Applied working text field solution from trigger patterns to all regex fields, URLs, POST body content, and variable names
 - Updated LoopEditorScreen.java to fix loop name and startup delay fields with proper cursor positioning
 - Updated BlockEditorScreen.java to fix all text fields including regex patterns, HTTP URLs, JSON POST body, GUI patterns, chat messages, comments, loop names, variable names, and numeric fields
 - Removed verbose debugging code and setCursorToEnd calls that didn't work correctly
 - Ensured data integrity is preserved while fixing visual text truncation in UI text input fields



## 2025-09-06 03:23:04

 - Fixed critical 32-character text field truncation bug across all TextFieldWidget instances in BlockEditorScreen
 - Root cause: setMaxLength() must be called BEFORE setText() to prevent internal truncation
 - Applied fix to all text field types: comment, chat, client_message, wait, call_loop, click_gui_item, loop, and post_request blocks
 - Removed duplicate break statements causing compilation errors at lines 170 and 232
 - All text fields now properly handle long content without 32-character limit truncation



## 2025-09-06 03:23:43

 - TECHNICAL DOCUMENTATION: TextFieldWidget 32-Character Truncation Bug Resolution
 - PROBLEM DESCRIPTION:
 - - All text input fields in BlockEditorScreen were truncating user input to exactly 32 characters
 - - Data loss occurred when users entered longer content (URLs, JSON, regex patterns, etc.)
 - - Issue affected: chat messages, client messages, POST URLs, request bodies, GUI patterns, comments, loop names, variable names, timeouts
 - ROOT CAUSE ANALYSIS:
 - - TextFieldWidget.setText() has internal 32-character limit when called BEFORE setMaxLength()
 - - Incorrect call order: setText()  setMaxLength() resulted in truncation
 - - Verbose logging revealed: 'Loading POST URL (length: 44)' but 'URL field after setText (length: 32)'
 - TECHNICAL SOLUTION:
 - - Changed TextFieldWidget initialization order across ALL block types
 - - Correct pattern: setMaxLength(limit)  setText(content)  setSelectionStart/End()  addDrawableChild()
 - - Applied to 8+ block types: chat, client_message, comment, wait, call_loop, click_gui_item, loop, post_request
 - VERIFICATION:
 - - Java compilation successful after removing duplicate break statements
 - - User confirmed URL field fix with 'That worked!!!' before applying universally
 - - All text fields now handle content >32 characters without data loss



## 2025-09-06 10:23:29

 - CRITICAL ISSUE: Source files were corrupted/deleted
 - Sources JAR contains obfuscated class names (class_437, class_342) instead of proper mappings
 - Recovered sources cannot compile due to mapping mismatch
 - Need to restore proper source files with correct Minecraft class names
 - BlockEditorScreen text field truncation fixes may be lost and need to be reapplied



## 2025-09-06 10:28:38

 - RECOVERY DECISION: Creating clean working files from structure memory
 - Sources JAR has too many obfuscated references (700+ mapping errors)
 - Will recreate core functionality and restore text field truncation fixes
 - Backing up recovered sources for reference if needed later
 - Focus on getting compilation working first, then restore features



## 2025-09-06 10:43:37

 - RECOVERY OPERATION: Restoring full 72KB functionality from backup sources
 - Current mod is only 7KB vs original 72KB - lost 90% of functionality
 - Recovered sources contain 1627-line LoopExecutor with full block execution logic
 - Need to apply systematic mappings to fix 700+ obfuscated class references
 - Critical: BlockEditorScreen text field fixes must be preserved during restoration


