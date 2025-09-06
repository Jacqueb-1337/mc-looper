# Fix CFR obfuscated class references
Write-Host "Fixing CFR obfuscated class references..."

$filePath = "src\main\java\com\jacqueb\mclooper\loop\LoopExecutor.java"
$content = Get-Content $filePath -Raw -Encoding UTF8

# Map CFR class names to proper Minecraft classes
$mappings = @{
    'class_310' = 'MinecraftClient'
    'class_2561' = 'Text'
    'class_437' = 'Screen'
    'class_490' = 'InventoryScreen'
    'class_1713' = 'SlotActionType'
    'class_1735' = 'Slot'
    'class_1799' = 'ItemStack'
}

# Map field and method references
$fieldMappings = @{
    'field_1705' = 'inGameHud'
    'field_7761' = 'slots'
    'method_1743' = 'getChatHud'
    'method_1812' = 'addMessage'
    'method_17577' = 'getScreenHandler'
    'method_7677' = 'getStack'
}

# Apply class mappings
foreach ($obfuscated in $mappings.Keys) {
    $mapped = $mappings[$obfuscated]
    $content = $content -replace $obfuscated, $mapped
    Write-Host "Mapped $obfuscated -> $mapped"
}

# Apply field/method mappings  
foreach ($obfuscated in $fieldMappings.Keys) {
    $mapped = $fieldMappings[$obfuscated]
    $content = $content -replace $obfuscated, $mapped
    Write-Host "Mapped $obfuscated -> $mapped"
}

# Fix specific issues
$content = $content -replace 'Object throwable', 'Throwable throwable'

# Add missing variable declarations at proper scopes
$content = $content -replace '(?m)^(\s+)searchMode = ', '$1String searchMode = '
$content = $content -replace '(?m)^(\s+)searchTerm = null;', '$1String searchTerm = null;'
$content = $content -replace '(?m)^(\s+)client = MinecraftClient', '$1MinecraftClient client = MinecraftClient'
$content = $content -replace '(?m)^(\s+)patt0\$temp = client', '$1Screen patt0$$temp = client'
$content = $content -replace '(?m)^(\s+)screen = \(HandledScreen\)', '$1HandledScreen screen = (HandledScreen)'
$content = $content -replace '(?m)^(\s+)stack = slot\.getStack', '$1ItemStack stack = slot.getStack'
$content = $content -replace '(?m)^(\s+)matches = false', '$1boolean matches = false'
$content = $content -replace '(?m)^(\s+)displayName = stack', '$1String displayName = stack'
$content = $content -replace '(?m)^(\s+)loreList = displayTag', '$1NbtList loreList = displayTag'
$content = $content -replace '(?m)^(\s+)for \(j = 0', '$1for (int j = 0'
$content = $content -replace '(?m)^(\s+)loreText = loreList', '$1String loreText = loreList'
$content = $content -replace '(?m)^(\s+)stackName = stack', '$1String stackName = stack'
$content = $content -replace '(?m)^(\s+)v0 = matches', '$1boolean v0; v0 = matches'

# Fix class_4372 variable (strange CFR artifact)
$content = $content -replace 'Screen class_4372', 'Screen screen'
$content = $content -replace 'class_4372', 'screen'

# Write back with UTF8 no BOM
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)

Write-Host "CFR obfuscated references fixed"
