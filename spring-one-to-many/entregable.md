# Entregable: relación uno a muchos (Producto - Comentarios)

**1. ¿En qué tabla queda la llave foránea y qué propiedad de Java la controla?**
En la tabla `comentarios` (el lado "muchos"), en la columna `producto_id`. La controla la propiedad `Comment.producto` (`@ManyToOne`), que es el lado propietario. `Product.comentarios` usa `mappedBy = "producto"` y solo es el lado inverso: no crea ninguna columna en `producto`.

**2. ¿Cuál es la diferencia entre `List` y `Set`?**
`List` conserva el orden y admite repetidos. `Set` no promete orden y evita repetidos según `equals`/`hashCode`. Aquí se usa `Set<Comment>` porque un producto no debe tener el mismo comentario dos veces; hay que cuidar `equals`/`hashCode` en entidades.

**3. ¿Por qué los identificadores son `String` y qué tipo de valor guardan?**
Porque se generan con `GenerationType.UUID`: guardan un UUID en texto (36 caracteres, por ejemplo `3f2b8c1e-...`). Así los IDs son únicos sin depender de un contador de la base y no se pueden adivinar fácilmente como `1, 2, 3`.

**4. ¿Qué ocurre al eliminar un producto con comentarios asociados? ¿Qué cambia si se quita `CascadeType.ALL`?**
Con `CascadeType.ALL` (incluye `REMOVE`), JPA elimina primero los comentarios y luego el producto. Es un comportamiento de JPA, no un `ON DELETE CASCADE` de PostgreSQL. Si se quita `ALL`, JPA solo intenta borrar el producto y PostgreSQL rechaza la operación por la llave foránea de `comentarios.producto_id` (error de integridad, respuesta 500) mientras queden comentarios. Habría que borrar los comentarios antes.

**5. ¿Qué diferencia práctica hay entre los `FetchType` usados en `Product` y `Comment`?**
- `Product.comentarios` es `EAGER`: al cargar un producto, sus comentarios vienen cargados, por eso `GET /productos` los devuelve en el JSON. Puede traer datos de más y afectar el rendimiento.
- `Comment.producto` es `LAZY`: el producto de un comentario solo se carga cuando se accede a él. Con `spring.jpa.open-in-view=false`, acceder fuera de la transacción provocaría `LazyInitializationException`. Como `@JsonBackReference` oculta esa propiedad en el JSON, no se dispara.
