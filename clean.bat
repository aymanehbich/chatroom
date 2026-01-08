@echo off
echo Cleaning compiled files...
if exist bin (
    rmdir /s /q bin
    echo Bin directory removed.
) else (
    echo Bin directory doesn't exist.
)
echo Done!
pause