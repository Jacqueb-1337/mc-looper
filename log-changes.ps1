# MC Looper Change Logger PowerShell Script

param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$Changes = @(),
    
    [Parameter()]
    [ValidateSet("y", "yes", "n", "no", "auto")]
    [string]$Open = "prompt"
)

$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$logFile = "CHANGELOG.md"

# Create log file if it doesn't exist
if (-not (Test-Path $logFile)) {
    @"
# MC Looper Development Changelog

This file tracks all development changes made to the MC Looper mod.

"@ | Out-File -FilePath $logFile -Encoding UTF8
}

# If no changes provided as parameters, prompt for input
if ($Changes.Count -eq 0) {
    Write-Host ""
    Write-Host "========================================"
    Write-Host "    MC LOOPER CHANGE LOGGER"
    Write-Host "========================================"
    Write-Host ""
    Write-Host "Current time: $timestamp"
    Write-Host ""
    Write-Host "Please describe the changes you made:"
    Write-Host "(Enter each change on a new line, press ENTER on empty line to finish)"
    Write-Host ""

    $Changes = @()
    $changeCount = 0
    
    do {
        $change = Read-Host "Change $changeCount"
        if ($change -ne "") {
            $Changes += $change
            $changeCount++
        }
    } while ($change -ne "")
}

# If no changes entered, exit
if ($Changes.Count -eq 0) {
    Write-Host "No changes entered. Exiting."
    Read-Host "Press Enter to exit"
    exit
}

# Write to log file
$logEntry = "`n## $timestamp`n`n"

foreach ($change in $Changes) {
    $logEntry += " - $change`n"
}

$logEntry += "`n"

Add-Content -Path $logFile -Value $logEntry -Encoding UTF8

Write-Host ""
Write-Host "========================================"
Write-Host "Changes logged successfully!"
Write-Host "========================================"
Write-Host ""
Write-Host "Summary:"
Write-Host "- $($Changes.Count) changes recorded"
Write-Host "- Logged to: $logFile"
Write-Host "- Timestamp: $timestamp"
Write-Host ""

# Show recent entries
Write-Host "Recent log entries:"
Write-Host ""
Get-Content $logFile | Select-Object -Last 15

Write-Host ""

# Handle opening the changelog based on the -Open parameter
if ($Open -eq "y" -or $Open -eq "yes" -or $Open -eq "auto") {
    Write-Host "Opening changelog file..."
    notepad $logFile
} elseif ($Open -eq "n" -or $Open -eq "no") {
    Write-Host "Changelog not opened."
} else {
    # Default behavior - prompt user
    $openFile = Read-Host "Would you like to open the changelog file? (y/n)"
    if ($openFile -eq "y" -or $openFile -eq "Y") {
        notepad $logFile
    }
}
