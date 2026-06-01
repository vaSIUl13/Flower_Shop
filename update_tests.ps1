$ErrorActionPreference = 'Stop'

# 1. Update Model Tests
Get-ChildItem -Path "test\unit\com\flowershop\model" -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package flowers;', 'package com.flowershop.model;'
    $content = $content -replace 'import flowers\.\*;', 'import com.flowershop.model.*;'
    Set-Content -Path $_.FullName -Value $content
}

# 2. Update Repository Test
$repoTest = "test\unit\com\flowershop\repository\FlowerRepositoryTest.java"
$content = Get-Content $repoTest -Raw
$content = $content -replace 'package database;', 'package com.flowershop.repository;'
$content = $content -replace 'import flowers\.\*;', 'import com.flowershop.model.*;'
$content = $content -replace 'DatabaseManager', 'FlowerRepository'
$content = $content -replace 'TestDatabaseManager', 'TestFlowerRepository'
Set-Content -Path $repoTest -Value $content

# 3. Update Service Tests
Get-ChildItem -Path "test\unit\com\flowershop\service" -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package services;', 'package com.flowershop.service;'
    $content = $content -replace 'import flowers\.\*;', 'import com.flowershop.model.*;'
    $content = $content -replace 'import flowers\.Flower;', 'import com.flowershop.model.Flower;'
    $content = $content -replace 'class FlowerShopTest', 'class FlowerShopServiceTest'
    $content = $content -replace 'FlowerShop shop', 'FlowerShopService shop'
    $content = $content -replace 'shop = new FlowerShop\(\);', 'shop = new FlowerShopService();'
    $content = $content -replace 'new FlowerShop\(\)', 'new FlowerShopService()'
    $content = $content -replace 'import services\.FlowerShop;', 'import com.flowershop.service.FlowerShopService;'
    Set-Content -Path $_.FullName -Value $content
}

# 4. Update Controller/Menu Tests
Get-ChildItem -Path "test\unit\com\flowershop\controller" -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package menu;', 'package com.flowershop.controller;'
    $content = $content -replace 'import services\.FlowerShop;', 'import com.flowershop.service.FlowerShopService;'
    $content = $content -replace 'import flowers\.\*;', 'import com.flowershop.model.*;'
    $content = $content -replace 'FlowerShop shop', 'FlowerShopService shop'
    $content = $content -replace 'new FlowerShop\(\)', 'new FlowerShopService()'
    Set-Content -Path $_.FullName -Value $content
}

# 5. Update Automation Test
$uiTest = "test\automation\com\flowershop\controller\FlowerShopAppTest.java"
$content = Get-Content $uiTest -Raw
$content = $content -replace 'package ui;', 'package com.flowershop.controller;'
Set-Content -Path $uiTest -Value $content

Write-Host "File contents updated successfully."
