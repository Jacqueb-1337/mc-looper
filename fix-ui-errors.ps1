# Fix UI method signature errors
Write-Host "Fixing UI method signature errors..."

# Fix drawTextWithShadow calls (remove the false boolean parameter)
$files = @(
    "src\main\java\com\jacqueb\mclooper\ui\BlockEditorScreen.java",
    "src\main\java\com\jacqueb\mclooper\ui\LoopEditorScreen.java", 
    "src\main\java\com\jacqueb\mclooper\ui\BlockSelectorScreen.java"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "Fixing $file..."
        $content = Get-Content $file -Raw -Encoding UTF8
        
        # Fix drawTextWithShadow - remove the false boolean parameter
        $content = $content -replace 'context\.drawTextWithShadow\(([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*false\)', 'context.drawTextWithShadow($1, $2, $3, $4, $5)'
        
        # Fix setCursor calls - add false boolean parameter
        $content = $content -replace '\.setCursor\(([^)]+)\.length\(\)\)', '.setCursor($1.length(), false)'
        
        # Write back with UTF8 no BOM
        $utf8NoBom = [System.Text.UTF8Encoding]::new($false)
        [System.IO.File]::WriteAllText($file, $content, $utf8NoBom)
        Write-Host "Fixed $file"
    }
}

Write-Host "UI method signature errors fixed"
