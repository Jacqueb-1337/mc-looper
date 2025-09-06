# Fix all setCursor method calls by adding boolean parameter
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
        
        # Read file as string array to preserve line endings
        $lines = Get-Content $file
        
        $modified = $false
        for ($i = 0; $i -lt $lines.Length; $i++) {
            if ($lines[$i] -match '\.setCursor\(([^,)]+)\);') {
                $lines[$i] = $lines[$i] -replace '\.setCursor\(([^,)]+)\);', '.setCursor($1, false);'
                Write-Host "  Fixed line $($i+1): $($lines[$i].Trim())"
                $modified = $true
            }
        }
        
        if ($modified) {
            # Write back to file using UTF-8 without BOM
            $content = $lines -join "`r`n"
            [System.IO.File]::WriteAllText((Resolve-Path $file).Path, $content, [System.Text.UTF8Encoding]::new($false))
            Write-Host "Updated $file"
        } else {
            Write-Host "No setCursor calls found in $file"
        }
    }
}

Write-Host "setCursor fix complete!"
