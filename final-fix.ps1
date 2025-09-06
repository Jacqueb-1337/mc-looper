# Final comprehensive import and mapping fix
param(
    [string]$targetDir = "src\main\java\com\jacqueb\mclooper",
    [switch]$verbose = $false
)

Write-Host "Starting final comprehensive fix for all mappings and imports..." -ForegroundColor Green

# Import statement fixes first
$importFixes = @{
    "import net.minecraft.ItemStack;" = "import net.minecraft.item.ItemStack;"
    "import net.minecraft.Item;" = "import net.minecraft.item.Item;"
    "import net.minecraft.Text;" = "import net.minecraft.text.Text;"
    "import net.minecraft.MinecraftClient;" = "import net.minecraft.client.MinecraftClient;"
    "import net.minecraft.BlockHitResult;" = "import net.minecraft.util.hit.BlockHitResult;"
    "import net.minecraft.EntityHitResult;" = "import net.minecraft.util.hit.EntityHitResult;"
    "import net.minecraft.DrawContext;" = "import net.minecraft.client.gui.DrawContext;"
    "import net.minecraft.TextFieldWidget;" = "import net.minecraft.client.gui.widget.TextFieldWidget;"
    "import net.minecraft.ButtonWidget;" = "import net.minecraft.client.gui.widget.ButtonWidget;"
    "import net.minecraft.Screen;" = "import net.minecraft.client.gui.screen.Screen;"
    "import net.minecraft.KeyBinding;" = "import net.minecraft.client.option.KeyBinding;"
    "import net.minecraft.InputUtil;" = "import net.minecraft.client.util.InputUtil;"
}

# Complete mapping dictionary
$mappings = @{
    # Core class references in code (not imports)
    "MinecraftClient" = "net.minecraft.client.MinecraftClient"
    "Screen" = "net.minecraft.client.gui.screen.Screen"
    "DrawContext" = "net.minecraft.client.gui.DrawContext" 
    "TextFieldWidget" = "net.minecraft.client.gui.widget.TextFieldWidget"
    "ButtonWidget" = "net.minecraft.client.gui.widget.ButtonWidget"
    "Text" = "net.minecraft.text.Text"
    "ItemStack" = "net.minecraft.item.ItemStack"
    "Item" = "net.minecraft.item.Item"
    "KeyBinding" = "net.minecraft.client.option.KeyBinding"
    "InputUtil" = "net.minecraft.client.util.InputUtil"
    
    # Special cases for fully qualified names that got partially fixed
    "net.minecraft.HandledScreen" = "net.minecraft.client.gui.screen.ingame.HandledScreen"
    "net.minecraft.InventoryScreen" = "net.minecraft.client.gui.screen.ingame.InventoryScreen"
    "net.minecraft.Slot" = "net.minecraft.screen.slot.Slot"
    "net.minecraft.ClickType" = "net.minecraft.screen.ClickType"
    
    # Method mappings
    "builder" = "builder"
    "dimensions" = "dimensions" 
    "build" = "build"
    "setMessage" = "setMessage"
    "close" = "close"
    "getWindow" = "getWindow"
    "getHandle" = "getHandle"
    "fromKeyCode" = "fromKeyCode"
    "getTranslationKey" = "getTranslationKey"
    "render" = "render"
    "getScreenHandler" = "getScreenHandler"
    "getStack" = "getStack"
    "getHud" = "getHud"
    "getChatHud" = "getChatHud"
    
    # Field mappings
    "KEYBOARD" = "KEYBOARD"
    "height" = "height"
    "slots" = "slots"
    "PICKUP" = "PICKUP"
    "inGameHud" = "inGameHud"
    
    # InputUtil.Type fix
    "InputUtil.Type" = "net.minecraft.client.util.InputUtil.Type"
}

# Get all Java files in the target directory
$javaFiles = Get-ChildItem -Path $targetDir -Filter "*.java" -Recurse

$totalFiles = $javaFiles.Count
$processedFiles = 0

foreach ($file in $javaFiles) {
    $processedFiles++
    Write-Progress -Activity "Final comprehensive fix" -Status "Processing $($file.Name)" -PercentComplete (($processedFiles / $totalFiles) * 100)
    
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    $changesCount = 0
    
    # First fix imports
    foreach ($fix in $importFixes.GetEnumerator()) {
        $oldImport = $fix.Key
        $newImport = $fix.Value
        
        if ($content -match [regex]::Escape($oldImport)) {
            $content = $content -replace [regex]::Escape($oldImport), $newImport
            $changesCount++
            
            if ($verbose) {
                Write-Host "  Fixed import: $oldImport -> $newImport" -ForegroundColor Yellow
            }
        }
    }
    
    # Then fix other mappings  
    foreach ($mapping in $mappings.GetEnumerator()) {
        $oldValue = $mapping.Key
        $newValue = $mapping.Value
        
        # Count occurrences before replacement
        $beforeCount = ($content -split [regex]::Escape($oldValue), 0, "SimpleMatch").Count - 1
        
        if ($beforeCount -gt 0) {
            $content = $content -replace [regex]::Escape($oldValue), $newValue
            $changesCount += $beforeCount
            
            if ($verbose) {
                Write-Host "  Replaced $beforeCount occurrences of '$oldValue' with '$newValue'" -ForegroundColor Yellow
            }
        }
    }
    
    # Only write if changes were made
    if ($content -ne $originalContent) {
        Set-Content $file.FullName -Value $content -NoNewline
        Write-Host "Fixed $changesCount mappings/imports in: $($file.Name)" -ForegroundColor Cyan
    } else {
        Write-Host "No fixes needed in: $($file.Name)" -ForegroundColor Gray
    }
}

Write-Host "`nFinal fix complete!" -ForegroundColor Green
Write-Host "Processed $totalFiles files" -ForegroundColor Green
