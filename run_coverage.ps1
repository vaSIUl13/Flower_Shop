# Auto-build script, test runner, and code coverage generator (JaCoCo)

$ErrorActionPreference = "Stop"

# Define paths to JDK and libraries
$javaPath = "C:\Users\744C~1\.jdks\openjdk-25.0.1\bin\java.exe"
$javacPath = "C:\Users\744C~1\.jdks\openjdk-25.0.1\bin\javac.exe"
$javafxLib = "D:\libs\javafx-sdk-26.0.1\lib"

Write-Host "=== 1. Creating Build Directories ===" -ForegroundColor Cyan
New-Item -ItemType Directory -Force -Path "out/production/Flower_Shop" | Out-Null
New-Item -ItemType Directory -Force -Path "out/test/Flower_Shop" | Out-Null
New-Item -ItemType Directory -Force -Path "coverage" | Out-Null

Write-Host "=== 2. Compiling Main Sources (src/) ===" -ForegroundColor Cyan
$sourceFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
& $javacPath -d "out/production/Flower_Shop" --release 21 --module-path $javafxLib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.web -classpath "lib/log4j-api-2.20.0.jar;lib/log4j-core-2.20.0.jar;lib/javax.mail-1.6.2.jar;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar" $sourceFiles

Write-Host "=== 3. Compiling Test Sources (test/) ===" -ForegroundColor Cyan
$testFiles = Get-ChildItem -Path "test" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
& $javacPath -d "out/test/Flower_Shop" --release 21 --module-path $javafxLib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.web -classpath "out/production/Flower_Shop;lib/log4j-api-2.20.0.jar;lib/log4j-core-2.20.0.jar;lib/javax.mail-1.6.2.jar;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/test/junit-jupiter-api-5.10.2.jar;lib/test/junit-jupiter-engine-5.10.2.jar;lib/test/junit-platform-commons-1.10.2.jar;lib/test/junit-platform-engine-1.10.2.jar;lib/test/testfx-core-4.0.18.jar;lib/test/testfx-junit5-4.0.18.jar;lib/test/hamcrest-2.2.jar" $testFiles

Write-Host "=== 4. Running JUnit 5 Tests with JaCoCo ===" -ForegroundColor Cyan
& $javaPath -ea "-javaagent:lib/test/jacocoagent.jar=destfile=coverage/Flower_Shop.exec,append=false" --module-path $javafxLib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.web -classpath "out/production/Flower_Shop;out/test/Flower_Shop;lib/log4j-api-2.20.0.jar;lib/log4j-core-2.20.0.jar;lib/javax.mail-1.6.2.jar;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-1.7.36.jar;lib/test/junit-platform-console-standalone-1.10.2.jar;lib/test/testfx-core-4.0.18.jar;lib/test/testfx-junit5-4.0.18.jar;lib/test/hamcrest-2.2.jar" org.junit.platform.console.ConsoleLauncher --scan-classpath --details=summary

Write-Host "=== 5. Generating JaCoCo Reports ===" -ForegroundColor Cyan
& $javaPath -jar "lib/test/jacococli.jar" report "coverage/Flower_Shop.exec" --classfiles "out/production/Flower_Shop" --sourcefiles "src" --html "coverage/html-report" --csv "coverage/coverage.csv" | Out-Null
Write-Host "HTML Report successfully generated: coverage/html-report/" -ForegroundColor Green

Write-Host "=== 6. CODE COVERAGE SUMMARY ===" -ForegroundColor Yellow
if (Test-Path "coverage/coverage.csv") {
    $csv = Import-Csv -Path "coverage/coverage.csv"
    $packages = $csv | Group-Object -Property PACKAGE
    
    $totalCovered = 0
    $totalMissed = 0
    
    foreach ($pkg in $packages) {
        $covered = 0
        $missed = 0
        foreach ($row in $pkg.Group) {
            $covered += [int]$row.LINE_COVERED
            $missed += [int]$row.LINE_MISSED
        }
        $totalCovered += $covered
        $totalMissed += $missed
        
        $percent = 0
        if (($covered + $missed) -gt 0) {
            $percent = [math]::Round(($covered / ($covered + $missed)) * 100, 2)
        }
        
        $pkgName = $pkg.Name
        if ($pkgName -eq "") { $pkgName = "default" }
        
        $msg = "Package: " + $pkgName + " | Covered Lines: " + $covered + " | Missed Lines: " + $missed + " | Coverage: " + $percent + "%"
        if ($percent -ge 90) {
            Write-Host $msg -ForegroundColor Green
        } elseif ($percent -ge 75) {
            Write-Host $msg -ForegroundColor Yellow
        } else {
            Write-Host $msg -ForegroundColor Red
        }
    }
    
    Write-Host "---------------------------------------------------------"
    $totalPercent = [math]::Round(($totalCovered / ($totalCovered + $totalMissed)) * 100, 2)
    $totalMsg = "TOTAL CODE COVERAGE: " + $totalPercent + "% (Covered: " + $totalCovered + ", Missed: " + $totalMissed + ")"
    Write-Host $totalMsg -ForegroundColor Yellow
} else {
    Write-Host "Error: Could not read CSV coverage file." -ForegroundColor Red
}
