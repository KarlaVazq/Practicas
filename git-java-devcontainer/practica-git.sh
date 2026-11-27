#!/usr/bin/env bash
# Reproduce la práctica completa de Git y Java en un repositorio nuevo.
# Uso (desde esta carpeta, dentro del Dev Container):  bash practica-git.sh
# Guarda la evidencia en reporte-practica.txt y la sube con un último commit.
set -u
cd "$(dirname "$0")"

# Identidad de Git: usa la tuya si ya está configurada
git config user.name  >/dev/null 2>&1 || git config user.name  "Estudiante"
git config user.email >/dev/null 2>&1 || git config user.email "estudiante@example.com"

[ -d .git ] || git init -b main >/dev/null
git config core.pager cat

paso() { echo; echo "### $*"; }

{
paso "1. Versiones del entorno"
git --version; java -version 2>&1; javac -version 2>&1; mvn --version 2>&1 | head -1

paso "2. Compilar y ejecutar los ejemplos"
mkdir -p bin
javac -d bin src/Saludo.java src/Calculadora.java
java -cp bin Saludo
java -cp bin Calculadora
echo "-- Modificación: se agrega una operación (dividir) a Calculadora.java"
if ! grep -q "dividir" src/Calculadora.java; then
  sed -i 's|static int multiplicar(int a, int b) { return a \* b; }|&\n    static int dividir(int a, int b) { return a / b; }|' src/Calculadora.java
  sed -i 's|System.out.println("8 x 4 = " + multiplicar(8, 4));|&\n        System.out.println("8 / 4 = " + dividir(8, 4));|' src/Calculadora.java
fi
javac -d bin src/Saludo.java src/Calculadora.java && java -cp bin Calculadora

paso "3. Preparar el repositorio"
git status
git status --short
git add README.md .gitignore src/Saludo.java src/Calculadora.java
git diff --cached --stat
echo "-- ¿Hay .class preparados? (debe quedar vacío):"; git diff --cached --name-only | grep '\.class$' || true

paso "4. Crear y corregir commits"
git commit -m "Agrega ejercicio de Git y Java"
git log --oneline -1
echo "" >> README.md
echo "> Evidencia: práctica realizada y verificada con los comandos de este reporte." >> README.md
git diff
git add README.md
git diff --cached
git commit --amend --no-edit
git log --oneline -1
git show --stat --oneline HEAD | tail -4

paso "5. Rama y merge"
git switch -c mejora-instrucciones
printf '\n> Aclaración: ejecuta los comandos desde la raíz del repositorio.\n' >> README.md
git add README.md
git commit -m "Aclara las instrucciones del ejercicio"
git switch main
git merge --no-ff mejora-instrucciones -m "Integra mejora-instrucciones"
git log --oneline --all --decorate --graph

paso "6. Remoto y ramas"
git remote -v
git branch --all
if git remote | grep -q .; then
  git push -u origin main
  git pull
else
  echo "Sin remoto configurado: se publicará después con 'git remote add origin <URL>' y 'git push -u origin main'."
fi

paso "7. Parche"
git diff HEAD^ HEAD > cambio.patch
echo "-- Primeras líneas de cambio.patch:"; head -15 cambio.patch
echo "-- '+' = línea agregada, '-' = línea eliminada, '@@' = ubicación del cambio."
git diff > cambios-sin-commit.patch; echo "(cambios sin commit: $(wc -l < cambios-sin-commit.patch) líneas)"

paso "8. Comprobación de .gitignore"
touch ejemplo.bin ejemplo.javac
git check-ignore -v bin/Saludo.class ejemplo.bin ejemplo.javac cambio.patch
git status --ignored --short
echo "-- git status --short (sin archivos sin seguimiento):"; git status --short
} 2>&1 | tee reporte-practica.txt

git add -f reporte-practica.txt practica-git.sh
git commit -m "Agrega reporte de la práctica" >/dev/null
echo; echo "Estado final:"; git status --short; git log --oneline --all --decorate --graph
