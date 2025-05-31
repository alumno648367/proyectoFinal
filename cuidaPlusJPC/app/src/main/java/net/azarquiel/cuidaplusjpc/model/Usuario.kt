import java.util.Date

data class Usuario(
    var usuarioId: String = "",                    // ID del usuario (coincide con FirebaseAuth.uid)
    var nombre: String = "",                       // Nombre completo del usuario
    var fechaNacimiento: Date = Date(),            // Fecha de nacimiento
    var email: String = "",                        // Correo electrónico
    var numTelefono: Long = 0L,                    // Número de teléfono
    var grupos: List<String> = emptyList()         // Lista de IDs de grupos familiares a los que pertenece
)
