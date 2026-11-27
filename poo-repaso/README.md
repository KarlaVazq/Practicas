# Ejercicios de repaso de programación orientada a objetos

Este repositorio contiene ejercicios introductorios de Java sobre colecciones,
constructores, encapsulamiento, herencia, polimorfismo, abstracción y
sobrecarga.

## Objetivo

Completa el código de la carpeta `Exercises` y comprueba el resultado con
`Tests.java`. El archivo de pruebas ya contiene los casos principales y **no
debe modificarse**.

> [!IMPORTANT]
> Trabaja con Java 8 o una versión posterior. Cada clase pública debe estar en
> un archivo cuyo nombre coincida exactamente con el nombre de la clase.

## Requisitos previos

- JDK instalado y disponible en la terminal.
- Un editor compatible con Java, como Visual Studio Code.
- Conocimientos básicos de variables, métodos, clases y paquetes.

Comprueba la instalación con:

```bash
java --version
javac --version
```

## Paso a paso para obtener la solución

### 1. Abre el proyecto

Abre la carpeta del repositorio en VS Code y verifica que la estructura sea
similar a la siguiente:

```text
JavaExesrcises/
├── Exercises/
│   ├── Colecciones.java
│   ├── DataTypes.java
│   ├── Padre.java
│   └── Sobrecarga.java
├── README.md
└── Tests.java
```

### 2. Revisa el código inicial

Lee cada archivo de `Exercises` y localiza los métodos incompletos. No cambies
las importaciones de `Tests.java` ni sus condiciones de validación. Cuando una
clase pública se separe en varios archivos, conserva el paquete:
```java
package Exercises;
```

### 3. Implementa un ejercicio a la vez

Completa primero `DataTypes`, después `Colecciones`, `Padre` y, por último,
`Sobrecarga`. Después de cada ejercicio, compila para detectar errores de
sintaxis, nombres de archivo o paquetes.

### 4. Compila desde la raíz del repositorio

Estos comandos crean las clases compiladas en `out`, sin mezclar archivos `.class`
con el código fuente:

```bash
rm -rf out
mkdir out
javac -d out Exercises/*.java Tests.java
```

### 5. Ejecuta las pruebas

```bash
java -cp out Tests
```
Una solución correcta debe imprimir `true`. Si aparece otro resultado, revisa
el ejercicio indicado por `Tests.java` y vuelve a compilar.

## Ejercicios incluidos

### 1. `DataTypes.java`: constructores

Implementa el constructor sin parámetros de `DataTypes` con estos valores:

| Atributo | Valor |
| --- | --- |
| `cadena` | `Hola Mundo` |
| `entero` | `10` |
| `flotante` | `5.9f` |
| `caracter` | `'A'` |
| `boleano` | `true` |

Conserva también el constructor con parámetros, porque las pruebas verifican
ambas formas de crear el objeto.

### 2. `Colecciones.java`: colecciones y `HashMap`

Completa `obtenerHash` para que:

1. Recorra `cars`, `bikes` y `bicicles`.
2. Ignore valores `null`, cadenas vacías y cadenas que solo contengan espacios.
3. Elimine elementos repetidos.
4. Guarde los elementos en `transport`.
5. Use claves incrementales desde `1` hasta el número de elementos guardados.
6. Regrese el `HashMap` resultante.

No agregues elementos manualmente a la colección solo para satisfacer la
prueba; el método debe trabajar con los datos existentes.

### 3. `Padre.java`: herencia

Separa las clases públicas en estos archivos:

- `Padre.java`
- `Hijo.java`
- `EspirituSanto.java`

En `Padre`, implementa `test()` para que:

1. Cree un `Hijo` usando el apellido recibido por `Padre` y el nombre `Juan`.
2. Cree un `EspirituSanto` usando el apellido del padre, el nombre del hijo y
   el apellido real `Lopez`.
3. Llame a `pruebaPaternidad()` y regrese su resultado.

Usa la comparación adecuada para cadenas. La prueba debe devolver `false` con
el caso proporcionado por `Tests.java`.

### 4. `Sobrecarga.java`: polimorfismo y sobreescritura

Completa `Alumno.reprobar()` con estas reglas:

- Si la calificación es menor que `6`, debe regresar `":("`.
- En cualquier otro caso, debe regresar `":)"`.

Mantén la relación de herencia con `Sobrecarga` y la implementación específica
de `Profesor`. El objetivo es observar cómo una misma operación puede tener
comportamientos distintos según el tipo de objeto.

## Prácticas adicionales sobre los cuatro pilares de POO

Estas actividades amplían los ejercicios existentes. Crea archivos nuevos para
ellas y agrega pruebas separadas; no modifiques `Tests.java`.

### Encapsulamiento: `CuentaBancaria.java`

Crea una clase con los atributos privados `titular` y `saldo`. Proporciona
constructores y métodos públicos para consultar el saldo, depositar y retirar.
Valida que no se acepten depósitos negativos ni retiros mayores que el saldo.
Evita exponer los atributos directamente.

### Abstracción: `Figura.java`

Crea una clase abstracta `Figura` con el método abstracto `calcularArea()`.
Implementa `Circulo` y `Rectangulo`. Cada subclase debe calcular su área con
sus propios atributos y validar que sus dimensiones sean mayores que cero.

### Herencia: `Vehiculo.java`

Crea una clase base `Vehiculo` con marca y modelo. Implementa las clases
`Automovil` y `Motocicleta`, que hereden esos datos y agreguen una propiedad
propia. Incluye un método común para mostrar la información del vehículo.

### Polimorfismo: `Notificacion.java`

Define una clase abstracta o una interfaz `Notificacion` con el método
`enviar(String mensaje)`. Implementa `NotificacionCorreo` y
`NotificacionConsola`. Guarda varios objetos en una colección de tipo
`Notificacion` y ejecútalos mediante la misma llamada.

### Integración de los pilares: `SistemaBiblioteca.java`

Diseña un pequeño sistema con las clases `Libro`, `Usuario` y `Biblioteca`.
Usa encapsulamiento para proteger el estado, herencia para distinguir tipos de
usuario, abstracción para definir las operaciones comunes y polimorfismo para
calcular las reglas de préstamo de cada tipo de usuario.

## Lista de comprobación

- [x] El proyecto compila sin errores con `javac`.
- [x] `Tests.java` no fue modificado.
- [x] Cada clase pública está en su propio archivo.
- [x] Los métodos devuelven los tipos y valores esperados.
- [x] Se validan valores nulos, vacíos, repetidos o inválidos cuando aplica.
- [x] Las nuevas prácticas tienen sus propias pruebas.
- [x] El programa termina mostrando `true`.

## Resolución del ejercicio

Cada clase pública está en su propio archivo dentro de `Exercises/` (paquete `Exercises`).

| Ejercicio | Archivos | Idea clave |
|---|---|---|
| Constructores | `DataTypes.java` | El constructor vacío delega con `this(...)` al constructor completo. |
| Colecciones | `Colecciones.java` | Un `LinkedHashSet` quita repetidos y conserva el orden; se descartan `null`, vacíos y espacios; las claves van de 1 a N. |
| Herencia | `Padre.java`, `Hijo.java`, `EspirituSanto.java` | `Padre.test()` crea `Hijo("Juan", apellido)` y `EspirituSanto(..., "Lopez")`; compara con `equals()`. |
| Polimorfismo/sobreescritura | `Sobrecarga.java`, `Alumno.java`, `Profesor.java` | `reprobar()` devuelve `:(` si la calificación es menor que 6 y `:)` en otro caso; `Profesor` lo sobreescribe. |

**Prácticas adicionales (los cuatro pilares)**

| Pilar | Archivos | Regla validada |
|---|---|---|
| Encapsulamiento | `CuentaBancaria.java` | Atributos privados; sin depósitos negativos ni retiros mayores al saldo. |
| Abstracción | `Figura.java`, `Circulo.java`, `Rectangulo.java` | Cada figura calcula su área y exige dimensiones mayores que cero. |
| Herencia | `Vehiculo.java`, `Automovil.java`, `Motocicleta.java` | Datos comunes en la base, propiedad propia en cada hija. |
| Polimorfismo | `Notificacion.java`, `NotificacionCorreo.java`, `NotificacionConsola.java` | Una lista de `Notificacion` se recorre con la misma llamada `enviar(...)`. |
| Integración | `Libro.java`, `Usuario.java`, `UsuarioRegular.java`, `UsuarioPremium.java`, `Biblioteca.java` | Regular presta 2 libros y Premium 5, mediante `getMaximoPrestamos()`. |

Las pruebas de estas prácticas están en `PruebasAdicionales.java`; `Tests.java` no se modificó.

**Compilar y ejecutar**

```bash
rm -rf out && mkdir out
javac -d out Exercises/*.java Tests.java PruebasAdicionales.java
java -cp out Tests
java -cp out PruebasAdicionales
```

Ambas ejecuciones deben terminar mostrando `true`.
