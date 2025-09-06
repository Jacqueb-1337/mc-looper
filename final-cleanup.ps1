# Final cleanup for remaining obfuscated references

param(
    [string]$SourceDir = "src\main\java\com\jacqueb\mclooper"
)

$finalMappings = @(
    @{ Old = "class_4372"; New = "screen" },
    @{ Old = "class_239"; New = "HitResult" },
    @{ Old = "class_240"; New = "Type" }
)

Write-Host "Final cleanup of remaining obfuscated references..." -ForegroundColor Green

$totalReplacements = 0

foreach ($mapping in $finalMappings) {
    $oldRef = $mapping.Old
    $newRef = $mapping.New
    
    Write-Host "Mapping: $oldRef -> $newRef" -ForegroundColor Cyan
    
    $javaFiles = Get-ChildItem -Path $SourceDir -Filter "*.java" -Recurse
    
    foreach ($file in $javaFiles) {
        $content = Get-Content $file.FullName -Raw
        $originalContent = $content
        
        $content = $content -replace [regex]::Escape($oldRef), $newRef
        
        if ($content -ne $originalContent) {
            Set-Content -Path $file.FullName -Value $content -NoNewline
            $replacementCount = ([regex]::Matches($originalContent, [regex]::Escape($oldRef))).Count
            $totalReplacements += $replacementCount
            Write-Host "  Updated $($file.Name): $replacementCount replacements" -ForegroundColor Green
        }
    }
}

Write-Host "`nFinal cleanup complete!" -ForegroundColor Green
Write-Host "Total replacements made: $totalReplacements" -ForegroundColor Yellow
