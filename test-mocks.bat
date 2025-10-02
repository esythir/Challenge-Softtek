@echo off
echo ========================================
echo  TESTANDO APLICACAO COM MOCKS
echo ========================================
echo.

echo 1. Parando aplicacao atual...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo 2. Executando com perfil de desenvolvimento (mocks)...
echo    - Firebase desabilitado
echo    - Usando dados mockados
echo.

.\mvnw.cmd spring-boot:run -Dspring.profiles.active=dev

pause
