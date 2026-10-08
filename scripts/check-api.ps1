param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"
$failed = 0
$badFile = Join-Path $env:TEMP ("ecoscan-bad-file-{0}.txt" -f [guid]::NewGuid())

function Invoke-ApiRequest {
    param(
        [string]$Path,
        [string[]]$Arguments = @()
    )

    $output = (& curl.exe --silent --show-error --output - --write-out "`n%{http_code}" @Arguments "$BaseUrl$Path") -join "`n"
    if ($LASTEXITCODE -ne 0) {
        throw "curl.exe failed for $Path"
    }
    if ($output -notmatch "(?s)^(.*)\r?\n(\d{3})$") {
        throw "Could not read HTTP response for $Path"
    }
    return @{
        Body = $Matches[1]
        Status = [int]$Matches[2]
    }
}

function Write-Check {
    param([string]$Name, [bool]$Passed)
    if ($Passed) {
        Write-Output "PASS $Name"
    } else {
        Write-Output "FAIL $Name"
        $script:failed++
    }
}

try {
    [System.IO.File]::WriteAllText($badFile, "not an image")

    $status = Invoke-ApiRequest -Path "/api/status"
    $statusJson = $status.Body | ConvertFrom-Json
    Write-Check "GET /api/status" ($status.Status -eq 200 -and $statusJson.PSObject.Properties.Name -contains "aiEnabled")

    $demo = Invoke-ApiRequest -Path "/api/demo"
    $demoJson = $demo.Body | ConvertFrom-Json
    Write-Check "GET /api/demo returns 9 items" ($demo.Status -eq 200 -and $demoJson.Count -eq 9)

    $item = Invoke-ApiRequest -Path "/api/demo/plastic-bottle"
    $itemJson = $item.Body | ConvertFrom-Json
    Write-Check "GET /api/demo/plastic-bottle" ($item.Status -eq 200 -and $itemJson.itemName -eq "Plastic Bottle")

    $unknown = Invoke-ApiRequest -Path "/api/demo/unknown"
    $unknownJson = $unknown.Body | ConvertFrom-Json
    Write-Check "GET /api/demo/unknown returns 404 message" ($unknown.Status -eq 404 -and $unknownJson.message)

    $bad = Invoke-ApiRequest -Path "/api/scan" -Arguments @("-F", "image=@$badFile;type=text/plain")
    $badJson = $bad.Body | ConvertFrom-Json
    Write-Check "POST /api/scan rejects bad file" ($bad.Status -eq 400 -and $badJson.message)
} catch {
    Write-Output "FAIL $($_.Exception.Message)"
    $failed++
} finally {
    if (Test-Path -LiteralPath $badFile) {
        Remove-Item -LiteralPath $badFile
    }
}

if ($failed -gt 0) {
    exit 1
}
exit 0
