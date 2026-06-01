$ErrorActionPreference = 'Stop'

# Create directories
New-Item -ItemType Directory -Force -Path "test\unit\com\flowershop\model"
New-Item -ItemType Directory -Force -Path "test\unit\com\flowershop\repository"
New-Item -ItemType Directory -Force -Path "test\unit\com\flowershop\service"
New-Item -ItemType Directory -Force -Path "test\unit\com\flowershop\controller"
New-Item -ItemType Directory -Force -Path "test\automation\com\flowershop\controller"

# Move model tests
Get-ChildItem -Path "test\unit\flowers" -Filter "*.java" | ForEach-Object {
    Move-Item $_.FullName "test\unit\com\flowershop\model\"
}
Remove-Item "test\unit\flowers" -Recurse -Force

# Move database test
Move-Item "test\unit\database\DatabaseManagerTest.java" "test\unit\com\flowershop\repository\FlowerRepositoryTest.java"
Remove-Item "test\unit\database" -Recurse -Force

# Move service tests
Move-Item "test\unit\services\FlowerShopTest.java" "test\unit\com\flowershop\service\FlowerShopServiceTest.java"
Move-Item "test\unit\services\FlowerShopInteractiveTest.java" "test\unit\com\flowershop\service\FlowerShopInteractiveTest.java"
Remove-Item "test\unit\services" -Recurse -Force

# Move menu tests
Get-ChildItem -Path "test\unit\menu" -Filter "*.java" | ForEach-Object {
    Move-Item $_.FullName "test\unit\com\flowershop\controller\"
}
Remove-Item "test\unit\menu" -Recurse -Force

# Move UI tests
Get-ChildItem -Path "test\automation\ui" -Filter "*.java" | ForEach-Object {
    Move-Item $_.FullName "test\automation\com\flowershop\controller\"
}
Remove-Item "test\automation\ui" -Recurse -Force

Write-Host "Files moved successfully."
