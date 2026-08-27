$ProjectPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$CheckIntervalSeconds = 10

Set-Location $ProjectPath

Write-Host "========================================"
Write-Host " Auto GitHub Push"
Write-Host "========================================"
Write-Host "Klasor: $ProjectPath"
Write-Host "Kontrol: $CheckIntervalSeconds saniye"
Write-Host ""

while ($true) {
    try {
        Set-Location $ProjectPath

        $changes = git status --porcelain 2>$null

        if ($changes) {
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Degisiklik algilandi."

            git add .

            $commitMessage = "Auto update $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"

            git commit -m $commitMessage

            if ($LASTEXITCODE -eq 0) {
                git push

                if ($LASTEXITCODE -eq 0) {
                    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] GitHub guncellendi."
                }
                else {
                    Write-Host "GitHub push basarisiz."
                }
            }
        }
    }
    catch {
        Write-Host "Hata: $($_.Exception.Message)"
    }

    Start-Sleep -Seconds $CheckIntervalSeconds
}