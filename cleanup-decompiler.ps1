# Clean up decompiler artifacts and fix import statements

param(
    [string]$SourceDir = "src\main\java\com\jacqueb\mclooper"
)

Write-Host "Cleaning up decompiler artifacts and fixing imports..." -ForegroundColor Green

$javaFiles = Get-ChildItem -Path $SourceDir -Filter "*.java" -Recurse

foreach ($file in $javaFiles) {
    Write-Host "Processing $($file.Name)..." -ForegroundColor Cyan
    
    $content = Get-Content $file.FullName -Raw
    $originalContent = $content
    
    # Remove decompiler comment blocks
    $content = $content -replace '/\*\s*\*\s*Decompiled with CFR.*?\*/', ''
    $content = $content -replace '/\*\s*\*\s*Could not load the following classes:.*?\*/', ''
    
    # Fix common incorrect import statements
    $importFixes = @{
        'net\.minecraft\.net\.minecraft\.' = 'net.minecraft.'
        'import net\.minecraft\.Hand;' = 'import net.minecraft.util.Hand;'
        'import net\.minecraft\.PlayerEntity;' = 'import net.minecraft.entity.player.PlayerEntity;'
        'import net\.minecraft\.BlockPos;' = 'import net.minecraft.util.math.BlockPos;'
        'import net\.minecraft\.Direction;' = 'import net.minecraft.util.math.Direction;'
        'import net\.minecraft\.Vec3d;' = 'import net.minecraft.util.math.Vec3d;'
        'import net\.minecraft\.BlockHitResult;' = 'import net.minecraft.util.hit.BlockHitResult;'
        'import net\.minecraft\.EntityHitResult;' = 'import net.minecraft.util.hit.EntityHitResult;'
        'import net\.minecraft\.HandledScreen;' = 'import net.minecraft.client.gui.screen.ingame.HandledScreen;'
        'import net\.minecraft\.KeyBinding;' = 'import net.minecraft.client.option.KeyBinding;'
    }
    
    foreach ($pattern in $importFixes.Keys) {
        $replacement = $importFixes[$pattern]
        $content = $content -replace $pattern, $replacement
    }
    
    # Clean up multiple empty lines
    $content = $content -replace '\n\s*\n\s*\n', "`n`n"
    
    # Add proper header comment for main files
    if ($file.Name -eq "McLooperMod.java") {
        $content = $content -replace '^/\*.*?\*/', "/*`n * MC Looper Mod - Main Mod Class`n * Provides loop execution functionality for Minecraft`n */"
    } elseif ($file.Name -eq "LoopExecutor.java") {
        # Already fixed this one
    } elseif ($file.Name.EndsWith("Screen.java")) {
        $screenType = $file.Name -replace "Screen\.java", ""
        $content = $content -replace '^/\*.*?\*/', "/*`n * MC Looper Mod - $screenType Screen`n * User interface for managing loops and blocks`n */"
    } elseif ($file.Directory.Name -eq "config") {
        $configType = $file.Name -replace "\.java", ""
        $content = $content -replace '^/\*.*?\*/', "/*`n * MC Looper Mod - $configType`n * Configuration data structure`n */"
    }
    
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        Write-Host "  Cleaned up $($file.Name)" -ForegroundColor Green
    } else {
        Write-Host "  No changes needed for $($file.Name)" -ForegroundColor Gray
    }
}

Write-Host "`nCleanup complete!" -ForegroundColor Green
