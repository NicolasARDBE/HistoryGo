# HistoryGo: Aplicación Móvil de Turismo Inmersivo con Realidad Mixta

## 📱 Descripción

**HistoryGo** es una aplicación móvil para Android que transforma los recorridos turísticos tradicionales en experiencias inmersivas usando tecnologías de **realidad mixta**, **geolocalización**, **multimedia interactivo**, e **imágenes 360°**. El proyecto fue desarrollado como una solución para enriquecer los tours guiados en Bogotá, permitiendo a los visitantes explorar sitios históricos con total flexibilidad horaria y narrativa digital enriquecida.

## 🎯 Objetivo del Proyecto

Desarrollar una aplicación móvil inmersiva que mejore la experiencia de los tours turísticos en Bogotá mediante el uso de realidad aumentada, modelos 3D, imágenes 360°, audios históricos y calificación del recorrido por parte del usuario.

## 🚀 Funcionalidades Clave

- Autenticación y gestión de usuarios con **AWS Cognito**
- Geolocalización y activación de contenido por **geovallas**
- Visualización de **imágenes 360°** y contenido en **realidad aumentada**
- Narración multimedia con control de reproducción
- Sistema de **calificación y comentarios** de la experiencia
- Compatibilidad con **idioma español e inglés**

## 🛠️ Tecnologías Utilizadas

- **Android Studio** con **Kotlin y Java**
- **ARCore**, **SceneView**, **PanoramaGL**
- **AWS Cognito**, **Lambda**, **DynamoDB**, **S3**, **CloudFront**
- **OpenStreetMap** + **Geofence API**

# Instrucciones para ejecutar el proyecto

1. Clonar el proyecto:  
   ```sh
   git clone https://github.com/NicolasARDBE/HistoryGo.git
2. Agregar un archivo local.properties en la raiz del proyecto donde se especifique la ubicación del SDK:

Ejemplo:
   sdk.dir=C:/Users/nreyes/AppData/Local/Android/Sdk
