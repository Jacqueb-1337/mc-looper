# Fix setCursor method calls to include boolean parameter
param(
    [switch]$Open = $false
)

$files = @(
    "src\main\java\com\jacqueb\mclooper\ui\BlockEditorScreen.java",
    "src\main\java\com\jacqueb\mclooper\ui\LoopEditorScreen.java",
    "src\main\java\com\jacqueb\mclooper\ui\BlockSelectorScreen.java"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "Fixing setCursor calls in $file..."
        
        # Read file content
        $content = Get-Content $file -Raw
        
        # Replace setCursor calls to include the boolean parameter
        $content = $content -replace '\.setCursor\(([^)]+)\);', '.setCursor($1, false);'
        
        # Write back to file using UTF-8 without BOM
        [System.IO.File]::WriteAllText((Resolve-Path $file).Path, $content, [System.Text.UTF8Encoding]::new($false))
        
        Write-Host "Fixed setCursor calls in $file"
    }
}

Write-Host "setCursor fix complete!"
