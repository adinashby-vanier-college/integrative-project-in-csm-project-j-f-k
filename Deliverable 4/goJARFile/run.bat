@echo off
:: Path to JavaFX SDK (adjust if needed)
set FX_DIR=javafx-sdk-22.0.2\lib

:: Launch the JavaFX application with required modules
java --module-path "%FX_DIR%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -jar GeometricalOpticsSimulation.jar

pause
