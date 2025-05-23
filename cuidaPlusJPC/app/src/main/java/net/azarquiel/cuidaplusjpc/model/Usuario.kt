import java.util.Date

data class Usuario(
    var usuarioId: String = "",
    var nombre: String = "",
    var fechaNacimiento: Date = Date(),  // tipo Date real
    var email: String = "",
    var numTelefono: Long = 0L,
    var grupos: List<String> = emptyList()

)
