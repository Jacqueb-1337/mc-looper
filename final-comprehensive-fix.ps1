# Final comprehensive fix for all remaining compilation errors
Write-Host "Applying final comprehensive fixes..."

# Fix LoopExecutor variable declaration and scope issues
$loopExecutorPath = "src\main\java\com\jacqueb\mclooper\loop\LoopExecutor.java"
if (Test-Path $loopExecutorPath) {
    Write-Host "Fixing LoopExecutor variable scope issues..."
    $content = Get-Content $loopExecutorPath -Raw -Encoding UTF8
    
    # Fix enum case
    $content = $content -replace 'case ENTER:', 'case BLOCK:'
    
    # Fix variable declarations - add missing declarations at proper scope
    $content = $content -replace '(?m)^(\s+)for \(net\.minecraft\.screen\.slot\.Slot slot : screen\.getScreenHandler\(\)\.slots\) \{', '$1if (screen instanceof HandledScreen) {$1    HandledScreen handledScreen = (HandledScreen) screen;$1    for (net.minecraft.screen.slot.Slot slot : handledScreen.getScreenHandler().slots) {'
    
    # Fix screen casting issue
    $content = $content -replace 'screen\.getScreenHandler\(\)', 'handledScreen.getScreenHandler()'
    
    # Add missing variable declarations at method scope for inventory condition
    $inventoryConditionFix = @'
                String searchMode = (String)condition.params.getOrDefault("search_mode", "registry");
                String searchTerm = null;
                searchTerm = "regex".equals(searchMode) != false ? (String)condition.params.get("regex_pattern") : (String)condition.params.get("item_name");
                if (searchTerm == null || searchTerm.trim().isEmpty()) {
                    return false;
                }
                searchTerm = "regex".equals(searchMode) != false ? this.normalizeRegexPattern(searchTerm) : this.replaceVariables(searchTerm);
                MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
                if (client.player != null && client.player.getInventory() != null) {
                    for (int i = 0; i < client.player.getInventory().size(); ++i) {
                        ItemStack stack = client.player.getInventory().getStack(i);
                        if (stack.isEmpty()) continue;
                        boolean matches = false;
                        if ("regex".equals(searchMode)) {
                            try {
                                String displayName = stack.getName().getString();
                                if (displayName.matches(searchTerm)) {
                                    matches = true;
                                }
                                if (matches || !stack.hasNbt() || !stack.getNbt().contains("display")) {
                                    // Skip lore check
                                } else {
                                    NbtCompound displayTag = stack.getNbt().getCompound("display");
                                    if (displayTag.contains("Lore")) {
                                        NbtList loreList = displayTag.getList("Lore", 8);
                                        for (int j = 0; j < loreList.size(); ++j) {
                                            String loreText = loreList.getString(j);
                                            if (!loreText.matches(searchTerm)) continue;
                                            matches = true;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // Invalid regex, treat as literal
                                String displayName = stack.getName().getString();
                                matches = displayName.toLowerCase().contains(searchTerm.toLowerCase());
                            }
                        } else {
                            String stackName = stack.getItem().toString();
                            String displayName = stack.getName().getString();
                            matches = stackName.contains(searchTerm) || displayName.toLowerCase().contains(searchTerm.toLowerCase());
                        }
                        if (!matches) continue;
                        String varName = (String)condition.params.get("var_name");
                        boolean verbose = Boolean.parseBoolean(condition.params.getOrDefault("verbose", "false").toString());
                        if (varName != null && !varName.trim().isEmpty()) {
                            this.setVariable(varName, stack.getName().getString());
                            this.setVariable(varName + "_registry", stack.getItem().toString());
                            this.setVariable(varName + "_count", stack.getCount());
                            this.setVariable(varName + "_slot", i);
                            this.showVerboseVariables("Inventory Condition", verbose);
                        }
                        return true;
                    }
                }
                return false;
'@
    
    # Replace the problematic inventory condition section
    $content = $content -replace '(?s)searchMode = \(String\)condition\.params\.getOrDefault.*?return false;', $inventoryConditionFix
    
    # Fix InputUtil references  
    $content = $content -replace 'net\.minecraft\.client\.util\.InputUtil\.Type\.KEYBOARD', 'InputUtil.Type.KEYBOARD'
    
    # Add InputUtil import if not present
    if ($content -notmatch 'import.*InputUtil') {
        $content = $content -replace '(import net\.minecraft\.client\.MinecraftClient;)', '$1`nimport net.minecraft.client.util.InputUtil;'
    }
    
    # Write back
    $utf8NoBom = [System.Text.UTF8Encoding]::new($false)
    [System.IO.File]::WriteAllText($loopExecutorPath, $content, $utf8NoBom)
    Write-Host "Fixed LoopExecutor"
}

# Fix McLooperMod InputUtil issues
$mcLooperPath = "src\main\java\com\jacqueb\mclooper\McLooperMod.java"
if (Test-Path $mcLooperPath) {
    Write-Host "Fixing McLooperMod InputUtil references..."
    $content = Get-Content $mcLooperPath -Raw -Encoding UTF8
    
    # Fix InputUtil references
    $content = $content -replace 'net\.minecraft\.client\.util\.InputUtil\.Type\.KEYBOARD', 'InputUtil.Type.KEYBOARD'
    
    # Add InputUtil import if not present
    if ($content -notmatch 'import.*InputUtil') {
        $content = $content -replace '(import net\.minecraft\.client\.MinecraftClient;)', '$1`nimport net.minecraft.client.util.InputUtil;'
    }
    
    # Write back
    $utf8NoBom = [System.Text.UTF8Encoding]::new($false)
    [System.IO.File]::WriteAllText($mcLooperPath, $content, $utf8NoBom)
    Write-Host "Fixed McLooperMod"
}

# Fix setCursor calls in UI files
$uiFiles = @(
    "src\main\java\com\jacqueb\mclooper\ui\BlockEditorScreen.java",
    "src\main\java\com\jacqueb\mclooper\ui\LoopEditorScreen.java"
)

foreach ($file in $uiFiles) {
    if (Test-Path $file) {
        Write-Host "Fixing setCursor calls in $file..."
        $content = Get-Content $file -Raw -Encoding UTF8
        
        # Fix setCursor calls - add false parameter
        $content = $content -replace '\.setCursor\(([^)]+)\.length\(\)\)', '.setCursor($1.length(), false)'
        
        # Write back
        $utf8NoBom = [System.Text.UTF8Encoding]::new($false)
        [System.IO.File]::WriteAllText($file, $content, $utf8NoBom)
        Write-Host "Fixed setCursor calls in $file"
    }
}

Write-Host "All comprehensive fixes applied"
