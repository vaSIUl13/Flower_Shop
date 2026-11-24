# 🌸 Flower Shop

A JavaFX desktop application for managing a flower shop’s catalog and creating custom bouquets. Browse and search flowers, combine them with accessories, and keep the catalog and bouquet data in a local SQLite database.

## ✨ Features

- Browse a catalog of roses, tulips, chamomile, lilies, peonies, and orchids.
- Search the catalog for flowers and bouquets.
- Add and remove flowers from the catalog.
- Create, rename, and manage bouquets.
- Add flowers and accessories to bouquets.
- View flower and bouquet information, including prices and flower-specific characteristics.
- Store application data locally in SQLite—no database server required.
- Use the JavaFX graphical interface or the additional console menu.

## 🧰 Technology stack

### Desktop UI

| Tool | Role |
|---|---|
| ![JavaFX](https://img.shields.io/badge/JavaFX-Desktop_UI-5382A1?logo=openjdk&logoColor=white) | Builds the desktop interface and UI components |
| ![CSS](https://img.shields.io/badge/CSS-Styles-1572B6?logo=css&logoColor=white) | Styles the JavaFX interface |

### Application

| Tool | Role |
|---|---|
| ![Java](https://img.shields.io/badge/Java-21%2B-007396?logo=openjdk&logoColor=white) | Application language |
| ![JDBC](https://img.shields.io/badge/JDBC-Database_access-007396) | Connects the application to SQLite |
| ![Log4j](https://img.shields.io/badge/Apache_Log4j_2-Logging-D22128?logo=apache&logoColor=white) | Application logging |
| ![SLF4J](https://img.shields.io/badge/SLF4J-Logging_facade-222222) | Logging facade |

### Database

| Tool | Role |
|---|---|
| ![SQLite](https://img.shields.io/badge/SQLite-Local_database-003B57?logo=sqlite&logoColor=white) | Stores flower, bouquet, and accessory data |
| ![SQLite JDBC](https://img.shields.io/badge/SQLite_JDBC-Driver-003B57) | Provides SQLite connectivity through JDBC |

### Testing and coverage

| Tool | Role |
|---|---|
| ![JUnit 5](https://img.shields.io/badge/JUnit_5-Unit_tests-25A162?logo=junit5&logoColor=white) | Runs unit tests |
| ![TestFX](https://img.shields.io/badge/TestFX-JavaFX_UI_tests-5382A1) | Supports automated JavaFX UI tests |
| ![JaCoCo](https://img.shields.io/badge/JaCoCo-Code_coverage-D42029) | Generates code coverage reports |
| ![Hamcrest](https://img.shields.io/badge/Hamcrest-Test_matchers-4E9A06) | Provides matchers for tests |

### Development tools

| Tool | Role |
|---|---|
| ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-IDE-000000?logo=intellijidea&logoColor=white) | Recommended IDE; the repository includes an IntelliJ module file |
| ![PowerShell](https://img.shields.io/badge/PowerShell-Automation-5391FE?logo=powershell&logoColor=white) | Runs the repository’s build, test, and coverage script |
| ![Git](https://img.shields.io/badge/Git-Version_control-F05032?logo=git&logoColor=white) | Version control |

## 🚀 Run locally

### Requirements

- JDK 21 or newer
- JavaFX SDK compatible with your JDK
- Git
- IntelliJ IDEA (recommended), or a Java IDE/terminal
- PowerShell if you want to use the included coverage script

JavaFX is not bundled with the repository. The application libraries are in `lib/`, and test libraries are in `lib/test/`.

### 1. Clone the repository

```bash
git clone https://github.com/vaSIUl13/Flower_Shop.git
cd Flower_Shop

### 2. Configure JavaFX

The JavaFX SDK is not included in the repository. Download and extract a JavaFX SDK version compatible with your JDK from [openjfx.io](https://openjfx.io/).

Set `PATH_TO_FX` to the SDK’s `lib` directory. For example:

**Windows (PowerShell):**

```powershell
$env:PATH_TO_FX = "C:\path\to\javafx-sdk-21\lib"
```

**Linux/macOS:**

```bash
export PATH_TO_FX="/path/to/javafx-sdk-21/lib"
```

Replace the example path with the actual location of your JavaFX SDK.

### 3. Compile and run

Run these commands from the repository root directory.

**Windows (PowerShell):**

```powershell
New-Item -ItemType Directory -Force -Path out | Out-Null
$sources = Get-ChildItem -Path src -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

javac --release 21 --module-path "$env:PATH_TO_FX" --add-modules javafx.controls -classpath "lib/*" -d out $sources

java --module-path "$env:PATH_TO_FX" --add-modules javafx.controls -classpath "out;lib/*" com.flowershop.Main
```

**Linux/macOS:**

```bash
mkdir -p out
javac --release 21 --module-path "$PATH_TO_FX" --add-modules javafx.controls -classpath "lib/*" -d out $(find src -name "*.java")

java --module-path "$PATH_TO_FX" --add-modules javafx.controls -classpath "out:lib/*" com.flowershop.Main
```

The application window should open after the final command. Keep the JavaFX SDK path configured when starting the application again.
