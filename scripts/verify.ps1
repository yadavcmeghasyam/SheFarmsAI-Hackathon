param([switch]$Connected)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location -LiteralPath $projectRoot
try {
    if (-not $env:JAVA_HOME) {
        $studioJava = Join-Path $env:ProgramFiles 'Android\Android Studio\jbr'
        if (Test-Path -LiteralPath "$studioJava\bin\java.exe") {
            $env:JAVA_HOME = $studioJava
        } else {
            throw 'Set JAVA_HOME to a JDK 25 installation, or install Android Studio with its bundled JDK.'
        }
    }
    if (-not $env:ANDROID_HOME) {
        $env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
    }
    if (-not (Test-Path -LiteralPath $env:ANDROID_HOME)) {
        throw 'Install the Android SDK or set ANDROID_HOME to its directory.'
    }
    $env:DEBUG = ''
    $tasks = @(':app:assembleDebug', ':app:testDebugUnitTest', ':app:assembleDebugAndroidTest')
    if ($Connected) { $tasks += ':app:connectedDebugAndroidTest' }
    & .\gradlew.bat @tasks --console=plain --no-daemon
    if ($LASTEXITCODE -ne 0) { throw "Gradle verification failed (exit $LASTEXITCODE)." }
    # Release compiler/dex memory before starting the lint analyzer.
    & .\gradlew.bat :app:lintDebug --console=plain --no-daemon
    if ($LASTEXITCODE -ne 0) { throw "Android lint failed (exit $LASTEXITCODE)." }
} finally {
    Pop-Location
}
