# Clean up CFR decompiler artifacts
Write-Host "Cleaning up CFR decompiler artifacts..."

$filePath = "src\main\java\com\jacqueb\mclooper\loop\LoopExecutor.java"
$content = Get-Content $filePath -Raw -Encoding UTF8

# Remove GOTO statements and fix syntax
$content = $content -replace '\*\* GOTO lbl\d+', ''
$content = $content -replace '\)\) \{', ')) {'

# Fix the specific issue with displayTag assignment in if conditions
# Replace the problematic if condition with proper variable declaration and assignment
$pattern = 'if \(matches \|\| !stack\.hasNbt\(\) \|\| !stack\.getNbt\(\)\.contains\("display"\) \|\| !\(displayTag = stack\.getNbt\(\)\.getCompound\("display"\)\)\.contains\("Lore"\)\)'
$replacement = @'
if (matches || !stack.hasNbt() || !stack.getNbt().contains("display")) {
                                continue;
                            }
                            NbtCompound displayTag = stack.getNbt().getCompound("display");
                            if (!displayTag.contains("Lore"))
'@
$content = $content -replace $pattern, $replacement

# Fix any remaining line formatting issues
$content = $content -replace '\r?\n\s*\r?\n\s*\r?\n', "`r`n`r`n"

# Write back with UTF8 no BOM
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)

Write-Host "CFR decompiler artifacts cleaned up"
