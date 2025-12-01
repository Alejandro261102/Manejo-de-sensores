# SensorApp IPN - Práctica de Desarrollo Móvil

Este proyecto corresponde a la solución del **Ejercicio 1: Implementación de Sensores**, desarrollado en Kotlin utilizando Jetpack Compose. La aplicación demuestra el uso de hardware del dispositivo, gestión eficiente de energía y personalización de temas institucionales (IPN y ESCOM).

## 📋 Datos del Estudiante
* **Nombre:** Hernandez Aranda Hector Alejandro
* **Boleta:** 2022630481
* **Grupo:** 7CV2
* **Materia:** Desarrollo de Aplicaciones Móviles Nativas

---

## 📱 Descripción del Proyecto
La aplicación es un monitor de sensores en tiempo real que implementa las siguientes características técnicas requeridas:

1.  **Lectura de Sensores:**
    * **Acelerómetro:** Muestra las coordenadas X, Y, Z en tiempo real.
    * **Sensor de Luz:** Muestra el valor en lux y una barra de progreso visual.
2.  **Gestión de Energía:**
    * Los sensores se desactivan automáticamente cuando la app pasa a segundo plano (`onPause`) para ahorrar batería.
    * Se envía una **Notificación** al usuario indicando que la app está en reposo.
3.  **Temas Personalizables:**
    * Switch para alternar entre **Tema Guinda (IPN)** y **Tema Azul (ESCOM)**.
    * Soporte para Modo Oscuro/Claro del sistema.
4.  **Control de Usuario:**
    * Interruptores individuales para habilitar o deshabilitar la lectura de cada sensor.

---

## 🛠 Requisitos Técnicos
* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose (Material3)
* **SDK Mínimo:** API 24 (Android 7.0)
* **Arquitectura:** Single Activity (MainActivity)

---

## 🚀 Guía de Ejecución y Pruebas

### 1. Ejecución en Dispositivo Físico
1.  Activa la *Depuración USB* en tu dispositivo.
2.  Conecta el celular y ejecuta la app desde Android Studio.
3.  Mueve el teléfono para ver los cambios en el acelerómetro o cubre el sensor frontal para variar la luz.

### 2. Ejecución en Emulador (Android Studio)
Si no dispones de un dispositivo físico, la app es totalmente funcional en el emulador utilizando las herramientas de **Sensores Virtuales**:

1.  Inicia la aplicación en el emulador.
2.  En la barra de herramientas del emulador (lado derecho), haz clic en los tres puntos **(...)** para abrir los *Extended Controls*.
3.  Selecciona **Virtual Sensors** en el menú lateral.
    * **Para Acelerómetro:** Ve a la pestaña *Device Pose* y rota el teléfono virtual. Verás los valores X, Y, Z cambiar en la app.
    * **Para Luz:** Ve a la pestaña *Additional Sensors*, busca "Light" y mueve el deslizador (slider) para simular cambios de iluminación (lux).

---

## 📂 Estructura de Archivos Clave

* `MainActivity.kt`: Contiene toda la lógica de sensores (`SensorEventListener`), gestión del ciclo de vida (`onResume`/`onPause`), notificaciones y la interfaz gráfica en Compose.
* `ui/theme/Color.kt`: Define los colores institucionales `GuindaIPN` (0xFF6C1D45) y `AzulESCOM` (0xFF003366).
* `ui/theme/Theme.kt`: Lógica para el cambio dinámico de temas.
* `AndroidManifest.xml`: Declaración de permisos (`POST_NOTIFICATIONS`) y características de hardware requeridas.

---

## ✅ Checklist de Cumplimiento

- [x] Integración de 2 sensores (Acelerómetro y Luz).
- [x] Tema Guinda (IPN) y Azul (ESCOM).
- [x] Adaptación a modo oscuro/claro.
- [x] Visualización en tiempo real.
- [x] Opción para habilitar/deshabilitar sensores.
- [x] Manejo de batería (desregistro de listeners en background).
- [x] Notificación de estado en segundo plano.