# Precise Minecraft 1.20.4 Fabric Mappings Fix Tool
param(
    [string]$targetDir = "src\main\java\com\jacqueb\mclooper",
    [switch]$verbose = $false,
    [switch]$reextract = $false,
    [string]$backupJar = "mc-looper-1.0.0 - Copy.jar"
)

if ($reextract) {
    Write-Host "Re-extracting files from backup JAR..." -ForegroundColor Yellow
    
    # Clean existing files
    if (Test-Path $targetDir) {
        Remove-Item $targetDir -Recurse -Force
        Write-Host "Cleaned existing source directory" -ForegroundColor Green
    }
    
    if (Test-Path "recovered-sources-backup") {
        Remove-Item "recovered-sources-backup" -Recurse -Force
        Write-Host "Cleaned existing backup directory" -ForegroundColor Green
    }
    
    # Re-extract from JAR
    if (Test-Path $backupJar) {
        Add-Type -AssemblyName System.IO.Compression.FileSystem
        [System.IO.Compression.ZipFile]::ExtractToDirectory($backupJar, "temp-extract")
        
        # Copy Java files to backup location
        $javaFiles = Get-ChildItem -Path "temp-extract" -Filter "*.java" -Recurse
        New-Item -ItemType Directory -Path "recovered-sources-backup" -Force | Out-Null
        
        foreach ($file in $javaFiles) {
            $relativePath = $file.FullName.Replace((Get-Item "temp-extract").FullName, "").TrimStart('\')
            $destPath = Join-Path "recovered-sources-backup" $relativePath
            $destDir = Split-Path $destPath -Parent
            
            if (!(Test-Path $destDir)) {
                New-Item -ItemType Directory -Path $destDir -Force | Out-Null
            }
            
            Copy-Item $file.FullName $destPath
        }
        
        # Copy to main source location
        Copy-Item "recovered-sources-backup\*" $targetDir -Recurse -Force
        
        # Cleanup
        Remove-Item "temp-extract" -Recurse -Force
        
        Write-Host "Successfully re-extracted files from $backupJar" -ForegroundColor Green
        Write-Host "You can now run the script again without -reextract to apply fixes" -ForegroundColor Cyan
        return
    } else {
        Write-Host "Backup JAR not found: $backupJar" -ForegroundColor Red
        return
    }
}

Write-Host "Starting precise mappings fix for MC Looper..." -ForegroundColor Green

# ONLY fix import statements - be very precise
$importFixes = @{
    # Exact import statement replacements only
    "^import net\.minecraft\.ItemStack;$" = "import net.minecraft.item.ItemStack;"
    "^import net\.minecraft\.Item;$" = "import net.minecraft.item.Item;"
    "^import net\.minecraft\.Text;$" = "import net.minecraft.text.Text;"
    "^import net\.minecraft\.MinecraftClient;$" = "import net.minecraft.client.MinecraftClient;"
    "^import net\.minecraft\.BlockHitResult;$" = "import net.minecraft.util.hit.BlockHitResult;"
    "^import net\.minecraft\.EntityHitResult;$" = "import net.minecraft.util.hit.EntityHitResult;"
    "^import net\.minecraft\.DrawContext;$" = "import net.minecraft.client.gui.DrawContext;"
    "^import net\.minecraft\.TextFieldWidget;$" = "import net.minecraft.client.gui.widget.TextFieldWidget;"
    "^import net\.minecraft\.ButtonWidget;$" = "import net.minecraft.client.gui.widget.ButtonWidget;"
    "^import net\.minecraft\.Screen;$" = "import net.minecraft.client.gui.screen.Screen;"
    "^import net\.minecraft\.KeyBinding;$" = "import net.minecraft.client.option.KeyBinding;"
    "^import net\.minecraft\.InputUtil;$" = "import net.minecraft.client.util.InputUtil;"
}

# ONLY fix obfuscated class/method/field names - very specific patterns
$obfuscatedFixes = @{
    # Class mappings (only obfuscated references)
    "\bclass_310\b" = "net.minecraft.client.MinecraftClient"
    "\bclass_437\b" = "net.minecraft.client.gui.screen.Screen"
    "\bclass_332\b" = "net.minecraft.client.gui.DrawContext"
    "\bclass_342\b" = "net.minecraft.client.gui.widget.TextFieldWidget"
    "\bclass_4185\b" = "net.minecraft.client.gui.widget.ButtonWidget"
    "\bclass_2561\b" = "net.minecraft.text.Text"
    "\bclass_1792\b" = "net.minecraft.item.Item"
    "\bclass_1799\b" = "net.minecraft.item.ItemStack"
    "\bclass_3532\b" = "net.minecraft.client.option.KeyBinding"
    "\bclass_3675\b" = "net.minecraft.client.util.InputUtil"
    "\bclass_307\b" = "Type"
    "\bclass_3417\b" = "net.minecraft.client.gui.screen.ingame.HandledScreen"
    "\bclass_490\b" = "net.minecraft.client.gui.screen.ingame.InventoryScreen"
    "\bclass_1735\b" = "net.minecraft.screen.slot.Slot"
    "\bclass_1713\b" = "net.minecraft.screen.ClickType"
    
    # Method mappings (only obfuscated)
    "\bmethod_46430\b" = "builder"
    "\bmethod_46434\b" = "dimensions"
    "\bmethod_46431\b" = "build"
    "\bmethod_25355\b" = "setMessage"
    "\bmethod_25419\b" = "close"
    "\bmethod_22683\b" = "getWindow"
    "\bmethod_4490\b" = "getHandle"
    "\bmethod_15985\b" = "fromKeyCode"
    "\bmethod_1441\b" = "getTranslationKey"
    "\bmethod_25394\b" = "render"
    "\bmethod_17577\b" = "getScreenHandler"
    "\bmethod_7677\b" = "getStack"
    "\bmethod_1743\b" = "getHud"
    "\bmethod_1812\b" = "getChatHud"
    
    # Field mappings (only obfuscated)
    "\bfield_1668\b" = "KEYBOARD"
    "\bfield_22790\b" = "height"
    "\bfield_7761\b" = "slots"
    "\bfield_7790\b" = "PICKUP"
    "\bfield_1705\b" = "inGameHud"
}

# Fix specific fully qualified names that are wrong
$fullyQualifiedFixes = @{
    "net\.minecraft\.HandledScreen" = "net.minecraft.client.gui.screen.ingame.HandledScreen"
    "net\.minecraft\.InventoryScreen" = "net.minecraft.client.gui.screen.ingame.InventoryScreen"
    "net\.minecraft\.Slot" = "net.minecraft.screen.slot.Slot"
    "net\.minecraft\.ClickType" = "net.minecraft.screen.ClickType"
}

# Get all Java files in the target directory
$javaFiles = Get-ChildItem -Path $targetDir -Filter "*.java" -Recurse

$totalFiles = $javaFiles.Count
$processedFiles = 0

foreach ($file in $javaFiles) {
    $processedFiles++
    Write-Progress -Activity "Precise mappings fix" -Status "Processing $($file.Name)" -PercentComplete (($processedFiles / $totalFiles) * 100)
    
    $lines = Get-Content $file.FullName
    $originalContent = $lines -join "`r`n"
    $modified = $false
    
    # Process line by line for imports
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        
        # Fix imports (only import lines)
        if ($line -match "^import ") {
            foreach ($fix in $importFixes.GetEnumerator()) {
                if ($line -match $fix.Key) {
                    $newLine = $line -replace $fix.Key, $fix.Value
                    if ($newLine -ne $line) {
                        $lines[$i] = $newLine
                        $modified = $true
                        if ($verbose) {
                            Write-Host "  Fixed import in $($file.Name): $line -> $newLine" -ForegroundColor Yellow
                        }
                    }
                }
            }
        }
    }
    
    # Now fix obfuscated references in the entire content
    $content = $lines -join "`r`n"
    $changesCount = 0
    
    foreach ($fix in $obfuscatedFixes.GetEnumerator()) {
        $pattern = $fix.Key
        $replacement = $fix.Value
        
        $matches = [regex]::Matches($content, $pattern)
        if ($matches.Count -gt 0) {
            $content = $content -replace $pattern, $replacement
            $changesCount += $matches.Count
            $modified = $true
            if ($verbose) {
                Write-Host "  Fixed $($matches.Count) obfuscated references: $pattern -> $replacement" -ForegroundColor Yellow
            }
        }
    }
    
    # Fix fully qualified names
    foreach ($fix in $fullyQualifiedFixes.GetEnumerator()) {
        $pattern = $fix.Key
        $replacement = $fix.Value
        
        $matches = [regex]::Matches($content, $pattern)
        if ($matches.Count -gt 0) {
            $content = $content -replace $pattern, $replacement
            $changesCount += $matches.Count
            $modified = $true
            if ($verbose) {
                Write-Host "  Fixed $($matches.Count) fully qualified names: $pattern -> $replacement" -ForegroundColor Yellow
            }
        }
    }
    
    # Only write if changes were made
    if ($modified) {
        Set-Content $file.FullName -Value $content -NoNewline
        Write-Host "Fixed mappings in: $($file.Name)" -ForegroundColor Cyan
    } else {
        Write-Host "No fixes needed in: $($file.Name)" -ForegroundColor Gray
    }
}

Write-Host "`nPrecise mappings fix complete!" -ForegroundColor Green
Write-Host "Processed $totalFiles files" -ForegroundColor Green
Write-Host "`nUsage examples:" -ForegroundColor Cyan
Write-Host "  To re-extract from backup: .\precise-fix.ps1 -reextract" -ForegroundColor White
Write-Host "  To apply fixes with verbose output: .\precise-fix.ps1 -verbose" -ForegroundColor White
