# Fix compilation errors in LoopExecutor.java
$filePath = "src\main\java\com\jacqueb\mclooper\loop\LoopExecutor.java"

Write-Host "Fixing compilation errors in LoopExecutor.java..."

# Read content
$content = Get-Content $filePath -Raw -Encoding UTF8

# Fix method calls
$content = $content -replace 'client\.inGameHud\.getHud\(\)\.getChatHud\((.*?)\)', 'client.inGameHud.getChatHud().addMessage($1)'
$content = $content -replace 'net\.minecraft\.screen\.ClickType', 'net.minecraft.screen.slot.SlotActionType'
$content = $content -replace 'throwable\.getMessage\(\)', '((Throwable)throwable).getMessage()'
$content = $content -replace 'case field_1332:', 'case ENTER:'
$content = $content -replace 'client\.getSession\(\)\.getName\(\)', 'client.getSession().getUsername()'
$content = $content -replace 'client\.getSession\(\)\.getUuid\(\)', 'client.getSession().getUuidOrNull()'
$content = $content -replace '\.allowsFlying', '.flying'

# Fix variable declarations at method scope
$content = $content -replace '(?ms)^(\s+)(searchMode = )', '$1String $2'
$content = $content -replace '(?ms)^(\s+)(searchTerm = null;)', '$1String $2'
$content = $content -replace '(?ms)^(\s+)(searchTerm = "regex")', '$1searchTerm = "regex"'
$content = $content -replace '(?ms)^(\s+)(client = net\.minecraft\.client\.MinecraftClient)', '$1MinecraftClient $2'
$content = $content -replace '(?ms)^(\s+)(patt0\$temp = client)', '$1Screen $2'
$content = $content -replace '(?ms)^(\s+)(screen = \(HandledScreen\)patt0\$temp)', '$1HandledScreen $2'
$content = $content -replace '(?ms)^(\s+)(stack = slot\.getStack)', '$1ItemStack $2'
$content = $content -replace '(?ms)^(\s+)(matches = false)', '$1boolean $2'
$content = $content -replace '(?ms)^(\s+)(displayName = stack)', '$1String $2'
$content = $content -replace '(?ms)^(\s+)(displayTag = stack)', '$1NbtCompound $2'
$content = $content -replace '(?ms)^(\s+)(loreList = displayTag)', '$1NbtList $2'
$content = $content -replace '(?ms)^(\s+)(for \(j = 0)', '$1for (int j = 0'
$content = $content -replace '(?ms)^(\s+)(loreText = loreList)', '$1String $2'
$content = $content -replace '(?ms)^(\s+)(stackName = stack)', '$1String $2'
$content = $content -replace '(?ms)^(\s+)(varName = \(String\))', '$1String $2'
$content = $content -replace '(?ms)^(\s+)(verbose = Boolean)', '$1boolean $2'
$content = $content -replace '(?ms)^(\s+)(for \(i = 0)', '$1for (int i = 0'

# Fix duplicate variable declaration
$content = $content -replace 'HandledScreen screen = \(HandledScreen\)screen;', '// Removed duplicate declaration'

# Fix client.player references
$content = $content -replace 'client\.player\.getInventory\(\)', 'client.player.getInventory()'

# Add missing imports at the top
$importSection = @'
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.SlotActionType;
'@

# Find the package line and add imports after it
$content = $content -replace '(package com\.jacqueb\.mclooper\.loop;)', "$1`n`n$importSection"

# Write back with UTF8 no BOM
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)

Write-Host "Compilation errors fixed in LoopExecutor.java"
