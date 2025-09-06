# Comprehensive Minecraft 1.20.4 Fabric Mappings Fix Tool
param(
    [string]$targetDir = "src\main\java\com\jacqueb\mclooper",
    [switch]$verbose = $false
)

Write-Host "Starting comprehensive mappings fix for MC Looper..." -ForegroundColor Green

# Complete mapping dictionary for Minecraft 1.20.4 Fabric
$mappings = @{
    # Core Minecraft Classes
    "class_310" = "net.minecraft.client.MinecraftClient"
    "class_437" = "net.minecraft.client.gui.screen.Screen" 
    "class_332" = "net.minecraft.client.gui.DrawContext"
    "class_342" = "net.minecraft.client.gui.widget.TextFieldWidget"
    "class_4185" = "net.minecraft.client.gui.widget.ButtonWidget"
    "class_2561" = "net.minecraft.text.Text"
    "class_1792" = "net.minecraft.item.Item"
    "class_1799" = "net.minecraft.item.ItemStack"
    "class_3532" = "net.minecraft.client.option.KeyBinding"
    "class_3675" = "net.minecraft.client.util.InputUtil"
    "class_307" = "Type"
    "class_3417" = "net.minecraft.client.gui.screen.ingame.HandledScreen"
    "class_490" = "net.minecraft.client.gui.screen.ingame.InventoryScreen"
    "class_1735" = "net.minecraft.screen.slot.Slot"
    "class_1713" = "net.minecraft.screen.ClickType"
    "class_239" = "net.minecraft.util.hit.HitResult"
    "class_3965" = "net.minecraft.util.hit.BlockHitResult"
    "class_3966" = "net.minecraft.util.hit.EntityHitResult"
    "class_1268" = "net.minecraft.util.Hand"
    "class_2338" = "net.minecraft.util.math.BlockPos"
    "class_2350" = "net.minecraft.util.math.Direction"
    "class_243" = "net.minecraft.util.math.Vec3d"
    
    # Methods - ButtonWidget
    "method_46430" = "builder"
    "method_46434" = "dimensions"
    "method_46431" = "build"
    "method_25355" = "setMessage"
    "method_25419" = "close"
    
    # Methods - MinecraftClient
    "method_22683" = "getWindow"
    "method_4490" = "getHandle"
    
    # Methods - InputUtil
    "method_15985" = "fromKeyCode"
    "method_1441" = "getTranslationKey"
    
    # Methods - Screen/Widget
    "method_25394" = "render"
    "method_17577" = "getScreenHandler"
    "method_7677" = "getStack"
    "method_1743" = "getHud"
    "method_1812" = "getChatHud"
    "method_1743().method_1812" = "getHud().getChatHud"
    
    # Fields
    "field_1668" = "KEYBOARD"
    "field_22790" = "height"
    "field_7761" = "slots"
    "field_7790" = "PICKUP"
    "field_1705" = "inGameHud"
    
    # Package fixes
    "net.minecraft.HandledScreen" = "net.minecraft.client.gui.screen.ingame.HandledScreen"
    "net.minecraft.InventoryScreen" = "net.minecraft.client.gui.screen.ingame.InventoryScreen"
    "net.minecraft.Slot" = "net.minecraft.screen.slot.Slot"
    "net.minecraft.ClickType" = "net.minecraft.screen.ClickType"
    "net.minecraft.ItemStack" = "net.minecraft.item.ItemStack"
    "net.minecraft.Item" = "net.minecraft.item.Item"
    "net.minecraft.Text" = "net.minecraft.text.Text"
    "net.minecraft.MinecraftClient" = "net.minecraft.client.MinecraftClient"
    "net.minecraft.BlockHitResult" = "net.minecraft.util.hit.BlockHitResult"
    "net.minecraft.EntityHitResult" = "net.minecraft.util.hit.EntityHitResult"
    "net.minecraft.DrawContext" = "net.minecraft.client.gui.DrawContext"
    "net.minecraft.TextFieldWidget" = "net.minecraft.client.gui.widget.TextFieldWidget"
    "net.minecraft.ButtonWidget" = "net.minecraft.client.gui.widget.ButtonWidget"
    "net.minecraft.Screen" = "net.minecraft.client.gui.screen.Screen"
    "net.minecraft.KeyBinding" = "net.minecraft.client.option.KeyBinding"
    "net.minecraft.InputUtil" = "net.minecraft.client.util.InputUtil"
}

# Get all Java files in the target directory
$javaFiles = Get-ChildItem -Path $targetDir -Filter "*.java" -Recurse

$totalFiles = $javaFiles.Count
$processedFiles = 0

foreach ($file in $javaFiles) {
    $processedFiles++
    Write-Progress -Activity "Fixing mappings" -Status "Processing $($file.Name)" -PercentComplete (($processedFiles / $totalFiles) * 100)
    
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    $changesCount = 0
    
    # Apply all mappings
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
        Write-Host "Fixed $changesCount mappings in: $($file.Name)" -ForegroundColor Cyan
    } else {
        Write-Host "No mappings needed in: $($file.Name)" -ForegroundColor Gray
    }
}

Write-Host "`nMapping fix complete!" -ForegroundColor Green
Write-Host "Processed $totalFiles files" -ForegroundColor Green
