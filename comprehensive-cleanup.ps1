# More comprehensive cleanup of decompiler artifacts

param(
    [string]$SourceDir = "src\main\java\com\jacqueb\mclooper"
)

Write-Host "Performing comprehensive cleanup..." -ForegroundColor Green

$javaFiles = Get-ChildItem -Path $SourceDir -Filter "*.java" -Recurse

foreach ($file in $javaFiles) {
    Write-Host "Processing $($file.Name)..." -ForegroundColor Cyan
    
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    
    # Remove the "Could not load" comment blocks completely
    $content = $content -replace '/\*\s*\*\s*Could not load the following classes:[^*]*(?:\*(?!/)[^*]*)*\*/', ''
    
    # Remove decompiler header comments
    $content = $content -replace '/\*\s*\*\s*Decompiled with CFR[^*]*(?:\*(?!/)[^*]*)*\*/', ''
    
    # Fix ALL occurrences of double net.minecraft paths - both in imports and comments
    $content = $content -replace 'net\.minecraft\.net\.minecraft\.', 'net.minecraft.'
    
    # Clean up multiple blank lines
    $content = $content -replace '\n\s*\n\s*\n+', "`n`n"
    
    # Remove leading/trailing whitespace
    $content = $content.Trim()
    
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        Write-Host "  Fixed $($file.Name)" -ForegroundColor Green
    } else {
        Write-Host "  No changes needed for $($file.Name)" -ForegroundColor Gray
    }
}

Write-Host "`nComprehensive cleanup complete!" -ForegroundColor Green
