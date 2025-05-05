@echo off
echo Building Low Latency Trading Router Demo...

rem Create build directory
mkdir build\classes 2>nul

rem Compile main class
echo Compiling source files...
javac -d build\classes src\main\java\com\trading\SimpleMain.java

rem Create JAR file
echo Creating JAR file...
jar cfe build\trading-router-demo.jar com.trading.SimpleMain -C build\classes .

echo Build complete!
echo.
echo To run in standard mode:
echo   java -jar build\trading-router-demo.jar standard
echo.
echo To run in zeroGC mode:
echo   java -XX:+UseZGC -XX:+AlwaysPreTouch -XX:+DisableExplicitGC ^
echo        -jar build\trading-router-demo.jar zerogc
