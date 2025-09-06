# Simple Java Decompiler using javap
param(
    [string]$classDir = "src\main\java\com\jacqueb\mclooper"
)

Write-Host "Creating Java source files from class files..." -ForegroundColor Green

# Create output directory
$outputDir = "decompiled-sources"
if (Test-Path $outputDir) {
    Remove-Item $outputDir -Recurse -Force
}
New-Item -ItemType Directory -Path $outputDir -Force | Out-Null

# Function to convert class file to Java source
function Convert-ClassToJava {
    param($classFile, $packagePath)
    
    $className = [System.IO.Path]::GetFileNameWithoutExtension($classFile.Name)
    $package = $packagePath -replace "\\", "." -replace "/", "."
    
    # Get class info using javap
    $javapOutput = javap -cp "." -p -s "$package.$className" 2>$null
    
    if ($javapOutput) {
        $javaContent = @"
package $package;

// Decompiled class - needs mapping fixes
public class $className {
    // This is a placeholder - the actual implementation was lost
    // The original class had the following signature:
    /*
$($javapOutput -join "`n")
    */
    
    // TODO: Restore actual implementation from backup or rewrite
}
"@
        
        $outputPath = Join-Path $outputDir ($packagePath -replace "\\", "\")
        if (!(Test-Path (Split-Path $outputPath -Parent))) {
            New-Item -ItemType Directory -Path (Split-Path $outputPath -Parent) -Force | Out-Null
        }
        
        $javaFile = Join-Path $outputPath "$className.java"
        Set-Content $javaFile -Value $javaContent
        Write-Host "Created: $javaFile" -ForegroundColor Cyan
    }
}

# Find all class files and convert them
Get-ChildItem -Path $classDir -Filter "*.class" -Recurse | ForEach-Object {
    $relativePath = $_.Directory.FullName.Replace((Get-Item $classDir).FullName, "").TrimStart('\')
    $packagePath = "com\jacqueb\mclooper"
    if ($relativePath) {
        $packagePath = "com\jacqueb\mclooper\$relativePath"
    }
    
    Convert-ClassToJava $_ $packagePath
}

Write-Host "`nDecompilation complete! Check the '$outputDir' directory for Java files." -ForegroundColor Green
Write-Host "Note: These are skeleton files - you'll need to restore the actual implementations." -ForegroundColor Yellow
