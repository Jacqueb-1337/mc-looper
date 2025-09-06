# Fix malformed line breaks in LoopExecutor
Write-Host "Fixing malformed line breaks..."

$filePath = "src\main\java\com\jacqueb\mclooper\loop\LoopExecutor.java"
$content = Get-Content $filePath -Raw -Encoding UTF8

# Fix the malformed `r`n sequences
$content = $content -replace '\{\`r\`n\s*continue;\`r\`n\s*\}\`r\`n\s*NbtList', '{
                continue;
            }
            NbtList'

# Write back with UTF8 no BOM
$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)

Write-Host "Malformed line breaks fixed"
