package net.azarquiel.cuidaplusjpc.viewmodel

import Usuario
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.GrupoFamiliar
import net.azarquiel.cuidaplusjpc.utils.cargarEnfermedadesDesdeAssets
import net.azarquiel.cuidaplusjpc.view.MainActivity

class MainViewModel(mainActivity: MainActivity) : ViewModel() {

   // ViewModels específicos para usuario y grupo familiar
   val usuarioVM = UsuarioViewModel()
   val grupoVM = GrupoFamiliarViewModel()
   val pacienteVM = PacienteViewModel()

   // Instancias únicas de FirebaseAuth y Firestore
   val auth = FirebaseAuth.getInstance()
   val db = FirebaseFirestore.getInstance()

   /**
    * Inicia sesión con email y contraseña
    * - Si tiene éxito: lanza escucha del usuario y ejecuta onSuccess con el UID
    * - Si falla: ejecuta onFailure con mensaje de error
    */
   fun loginConEmail(
      email: String,
      password: String,
      onSuccess: (String) -> Unit,
      onFailure: (String) -> Unit
   ) {
      auth.signInWithEmailAndPassword(email, password)
         .addOnSuccessListener {
            val uid = it.user?.uid ?: ""
            usuarioVM.empezarEscucha(uid)
            // Cargar grupo automáticamente al detectar el usuario
            usuarioVM.usuario.observeForever { usuario ->
               val grupoId = usuario?.grupos?.firstOrNull()
               if (!grupoId.isNullOrEmpty()) {
                  grupoVM.cargarGrupo(grupoId)  // ESTO trae el nombre desde Firestore
               }
            }
            onSuccess(uid)
         }
         .addOnFailureListener {
            onFailure(it.message ?: "Error al iniciar sesión")
         }
   }

   /**
    * Inicia sesión con cuenta de Google (token)
    * - onSuccess se lanza si entra correctamente
    * - onFailure se lanza si hay error
    */
   fun loginConGoogle(
      idToken: String,
      onSuccess: () -> Unit,
      onFailure: (String) -> Unit
   ) {
      val credential = GoogleAuthProvider.getCredential(idToken, null)
      auth.signInWithCredential(credential)
         .addOnSuccessListener { onSuccess() }
         .addOnFailureListener { onFailure(it.message ?: "Error al iniciar con Google") }
   }

   /**
    * Envía un correo para recuperar la contraseña
    * - onResult(true, msg): correo enviado
    * - onResult(false, msg): fallo al enviarlo
    */
   fun recuperarPassword(
      email: String,
      onResult: (Boolean, String) -> Unit
   ) {
      auth.sendPasswordResetEmail(email)
         .addOnSuccessListener { onResult(true, "Correo de recuperación enviado") }
         .addOnFailureListener { onResult(false, it.message ?: "Error al enviar el correo") }
   }

   /**
    * Registra un usuario con email y contraseña
    * - onSuccess: cuenta creada
    * - onFailure: error al crearla
    */
   fun registroConEmail(
      email: String,
      password: String,
      onSuccess: () -> Unit,
      onFailure: (String) -> Unit
   ) {
      auth.createUserWithEmailAndPassword(email, password)
         .addOnSuccessListener { onSuccess() }
         .addOnFailureListener { onFailure(it.message ?: "Error al registrar usuario") }
   }

   /**
    * Registra un usuario con Google (token)
    * - onSuccess: entra correctamente
    * - onFailure: no se pudo completar
    */
   fun registroConGoogle(
      idToken: String,
      onSuccess: () -> Unit,
      onFailure: (String) -> Unit
   ) {
      val credential = GoogleAuthProvider.getCredential(idToken, null)
      auth.signInWithCredential(credential)
         .addOnSuccessListener { onSuccess() }
         .addOnFailureListener { onFailure(it.message ?: "Error al registrar con Google") }
   }

   /**
    * Crea un nuevo grupo familiar y guarda el usuario
    * - Verifica que no exista un grupo con ese nombre
    * - Crea grupo y guarda el usuario dentro
    */
   fun crearGrupoYUsuario(
      nombreGrupo: String,
      uid: String,
      usuario: Usuario,
      onSuccess: () -> Unit,
      onFailure: (String) -> Unit
   ) {
      db.collection("gruposFamiliares")
         .whereEqualTo("nombre", nombreGrupo)
         .get()
         .addOnSuccessListener { result ->
            if (!result.isEmpty) {
               onFailure("Ya existe un grupo con ese nombre")
            } else {
               val grupoId = db.collection("gruposFamiliares").document().id
               val grupo = GrupoFamiliar(
                  grupoFamiliarId = grupoId,
                  nombre = nombreGrupo,
                  miembros = listOf(uid),
                  pacientes = emptyList()
               )
               grupoVM.crearGrupo(grupo,
                  onSuccess = {
                     val usuarioConGrupo = usuario.copy(grupos = listOf(grupoId))
                     usuarioVM.guardarUsuario(usuarioConGrupo,
                        onSuccess = onSuccess,
                        onFailure = { onFailure("Error al guardar usuario") }
                     )
                  },
                  onFailure = { onFailure("Error al crear grupo") }
               )
            }
         }
         .addOnFailureListener {
            onFailure("Error al verificar grupo: ${it.message}")
         }
   }

   /**
    * Une a un usuario a un grupo familiar existente y lo guarda
    * - Añade su UID al grupo
    * - Guarda el usuario en la base de datos
    */
   fun unirseAGrupoYGuardarUsuario(
      grupoId: String,
      uid: String,
      usuario: Usuario,
      onSuccess: () -> Unit,
      onFailure: (String) -> Unit
   ) {
      grupoVM.añadirMiembro(grupoId, uid)
      usuarioVM.guardarUsuario(usuario,
         onSuccess = onSuccess,
         onFailure = { onFailure("Error al guardar usuario") }
      )
   }
   fun unirseAGrupoPorNombre(
      nombreGrupo: String,
      uid: String,
      usuario: Usuario,
      onSuccess: () -> Unit,
      onFailure: (String) -> Unit
   ) {
      grupoVM.obtenerGrupoPorNombre(nombreGrupo) { grupo ->
         if (grupo == null) {
            onFailure("No se encontró ningún grupo con ese nombre")
         } else {
            val usuarioConGrupo = usuario.copy(grupos = listOf(grupo.grupoFamiliarId))
            unirseAGrupoYGuardarUsuario(grupo.grupoFamiliarId, uid, usuarioConGrupo, onSuccess, onFailure)
         }
      }
   }
   fun subirEnfermedadesAFirebase(context: Context) {
      val enfermedades = cargarEnfermedadesDesdeAssets(context)
      val db = FirebaseFirestore.getInstance()
      val col = db.collection("enfermedades")

      for (enfermedad in enfermedades) {
         col.document(enfermedad.enfermedadId).set(enfermedad)
            .addOnSuccessListener {
               Log.d("Firebase", "Subida: ${enfermedad.nombre}")
            }
            .addOnFailureListener {
               Log.e("Firebase", "Error: ${it.message}")
            }
      }
   }


}
