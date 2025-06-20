# Sistema Distribuido de Transferencia de Archivos en Java

Este proyecto implementa un sistema cliente-servidor distribuido que permite subir, descargar, eliminar y renombrar archivos, 
dividiéndolos en fragmentos almacenados en tres servidores secundarios.

---

## Requisitos

- Java JDK 17 o superior
- NetBeans, IntelliJ o cualquier IDE que soporte Maven
- Sistema operativo compatible (Windows, Linux, macOS)

---

## Estructura del Proyecto

```
proyecto/
├── cliente/               # Interfaz gráfica Swing + lógica de conexión
├── maestro/               # Servidor Maestro
├── secundario/            # Código de los nodos secundarios
├── common/                # Clases compartidas: protocolos, constantes, utilidades de los archivos
└── README.md              # Este archivo
```

---

## Instrucciones de Ejecución

### 1. Ejecutar el Servidor Maestro
Abrí tu IDE y ejecutá:

```java
maestro/ServidorMaestro.java
```

Esto levantará el servidor en el puerto **8000**.

---

### 2. Ejecutar los 3 Servidores Secundarios

Uno a uno, abrí el archivo:

```java
secundario/ServidorSecundario.java
```

Y **cambia manualmente el valor de `argumentos`** así:

```java
String[] argumentos = {"1"};  // Para el nodo 1
String[] argumentos = {"2"};  // Para el nodo 2
String[] argumentos = {"3"};  // Para el nodo 3
```

Se debe asegurar de levantar **tres instancias** (una por nodo), cada una con su número.

- El **nodo 1** = Se levantará el servidor en el puerto **9001**.
- El **nodo 2** = Se levantará el servidor en el puerto **9002**.
- El **nodo 3** = Se levantará el servidor en el puerto **9003**.
---

### 3. Ejecutar el Cliente (GUI)

Por último, ejecutar:

```java
cliente/ClienteGUI.java
```

Esto abrirá la interfaz gráfica donde podrás:

- 📤 Subir archivos
- 📥 Descargar archivos
- ✏️ Renombrar
- 🗑️ Eliminar
- 🔄 Sincronizar lista de archivos remotos/locales

---

## Protocolo de Comunicación

- `UPLOAD` → Subir archivo
- `DOWNLOAD` → Descargar archivo
- `STORE` → Almacenar fragmento en nodo
- `FRAG_REQUEST` → Solicitar fragmento
- `DELETE` → Eliminar archivo
- `RENAME` → Renombrar archivo
- `LIST` → Listar archivos disponibles

> El archivo se fragmenta en tres partes y se almacena una en cada servidor.

---

## Carpetas creadas automáticamente

- `/descargas`: almacena los archivos que el cliente descarga.
- `/fragmentos`: usada por los nodos para guardar las partes.

---

## Autores

Desarrolladores:
**
- Katherine guatemala Barrientos.
- Ignacio Artavia Salaza.
- Joseph Quiros Murillo.
**  
Universidad Nacional - Ingeniería en Sistemas / Sistemas Operativos
