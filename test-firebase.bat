@echo off
echo ========================================
echo  TESTANDO APLICACAO COM FIREBASE REAL
echo ========================================
echo.

echo 1. Parando aplicacao atual...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo 2. Executando com perfil de producao (Firebase real)...
echo    - Certifique-se de que o firebase-service-account.json esta configurado
echo    - Certifique-se de que as regras do Firestore estao configuradas
echo.

.\mvnw.cmd spring-boot:run -Dspring.profiles.active=prod

pause
