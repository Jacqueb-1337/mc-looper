# Fix the specific NbtList declaration issues
Write-Host "Fixing NbtList declaration issues..."

$filePath = "src\main\java\com\jacqueb\mclooper\loop\LoopExecutor.java"
$content = Get-Content $filePath -Raw -Encoding UTF8

# Fix the malformed if-NbtList declarations
$content = $content -replace 'if \(!displayTag\.contains\("Lore"\)\)\s*\r?\n\s*NbtList loreList', 'if (!displayTag.contains("Lore")) {`r`n                continue;`r`n            }`r`n            NbtList loreList'

# Write back with UTF8 no BOM
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)

Write-Host "NbtList declaration issues fixed"
