@echo off
echo Compiling Chat Application...
echo.

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all Java files with MySQL driver
javac -cp "lib/*" -d bin src/server/*.java src/client/*.java src/models/*.java src/database/*.java src/utils/*.java

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo Compilation successful!
    echo ========================================
    echo.
    echo To run the server: run-server.bat
    echo To run the client: run-client.bat
    echo.
) else (
    echo.
    echo ========================================
    echo Compilation failed!
    echo ========================================
    echo Please check for errors above.
)

pause