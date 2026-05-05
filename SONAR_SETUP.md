# SonarCloud Setup Guide

## Configuración de SonarCloud para análisis de código y cobertura

Este proyecto está configurado para análisis automático en SonarCloud con métricas de cobertura usando JaCoCo.

## Configuración de Secrets en GitHub

Para que el workflow de GitHub Actions funcione, necesitas configurar los siguientes secrets en tu repositorio:

### Pasos para agregar secrets:

1. Ve a tu repositorio en GitHub
2. Navega a: `Settings` → `Secrets and variables` → `Actions`
3. Haz clic en `New repository secret`
4. Agrega los siguientes secrets:

#### 1. SONAR_TOKEN
- **Name**: `SONAR_TOKEN`
- **Value**: `bd74d42886c41644290f5cceaa4b42c49e6f220b`

### Secretos Automáticos
- `GITHUB_TOKEN`: Este se proporciona automáticamente por GitHub Actions, no necesitas configurarlo.

## Ejecución del Análisis

El análisis de SonarCloud se ejecuta automáticamente en:
- Push a la rama `main`
- Pull Requests a `main`
- Puedes ejecutarlo manualmente desde GitHub Actions

## Ejecución Local

Para ejecutar el análisis localmente:

```bash
mvn clean verify org.jacoco:jacoco-maven-plugin:report sonar:sonar \
  -Dsonar.token=bd74d42886c41644290f5cceaa4b42c49e6f220b
```

## Reportes de Cobertura

Los reportes de JaCoCo se generan en:
- `target/site/jacoco/jacoco.xml` - Reporte XML para SonarCloud
- `target/site/jacoco/index.html` - Reporte HTML visual
- `target/site/jacoco/jacoco.csv` - Reporte CSV con métricas detalladas

## Proyecto en SonarCloud

- **URL**: https://sonarcloud.io/project/overview?id=BancoUdea
- **Organization**: mario-alvarez
- **Project Key**: BancoUdea

## Métricas de Calidad

El proyecto está configurado para analizar:
- Cobertura de código con JaCoCo
- Análisis estático de código
- Detección de code smells, bugs y vulnerabilidades
- Complejidad ciclomática
- Duplicación de código
