# Fix all remaining setCursor calls by manually finding and replacing them
param()

# List all files that have setCursor errors
$filesToFix = @(
    @{
        File = "src\main\java\com\jacqueb\mclooper\ui\BlockEditorScreen.java"
        Lines = @(85, 94, 103, 109, 118, 127, 139, 145, 154, 170, 183, 191, 197, 217, 222, 226, 231, 237, 245, 250, 255, 261)
    },
    @{
        File = "src\main\java\com\jacqueb\mclooper\ui\LoopEditorScreen.java"
        Lines = @(368, 371, 556, 631, 634, 653, 656, 666, 669, 678, 681, 689, 692)
    }
)

foreach ($fileInfo in $filesToFix) {
    $file = $fileInfo.File
    if (Test-Path $file) {
        Write-Host "Fixing setCursor calls in $file..."
        
        # Read all lines
        $lines = Get-Content $file
        
        # Apply fixes
        $modified = $false
        for ($i = 0; $i -lt $lines.Length; $i++) {
            $lineNumber = $i + 1
            if ($fileInfo.Lines -contains $lineNumber) {
                if ($lines[$i] -match '\.setCursor\([^,)]+\);') {
                    $lines[$i] = $lines[$i] -replace '\.setCursor\(([^,)]+)\);', '.setCursor($1, false);'
                    Write-Host "  Line ${lineNumber}: Fixed setCursor call"
                    $modified = $true
                }
            }
        }
        
        if ($modified) {
            # Write back to file
            $content = $lines -join "`r`n"
            [System.IO.File]::WriteAllText((Resolve-Path $file).Path, $content, [System.Text.UTF8Encoding]::new($false))
            Write-Host "Updated $file"
        }
    }
}

Write-Host "All setCursor fixes applied!"
