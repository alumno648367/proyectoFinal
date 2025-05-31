package net.azarquiel.cuidaplusjpc.viewmodel

import Usuario
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.GrupoFamiliar
import net.azarquiel.cuidaplusjpc.navigation.AppScreens
import net.azarquiel.cuidaplusjpc.utils.cargarEnfermedadesDesdeAssets
import net.azarquiel.cuidaplusjpc.utils.cargarMedicamentosMaestroDesdeAssets
import net.azarquiel.cuidaplusjpc.utils.cargarTratamientosMaestroDesdeAssets
import net.azarquiel.cuidaplusjpc.view.MainActivity

class MainViewModel(mainActivity: MainActivity) : ViewModel() {

   val usuarioVM = UsuarioViewModel()
   val grupoVM = GrupoFamiliarViewModel()
   val pacienteVM = PacienteViewModel()
   val enfermedadVM = EnfermedadViewModel()
   val enfermedadPacienteVM = EnfermedadPacienteViewModel()
   val tratamientoVM = TratamientoViewModel()
   val medicamentoVM = MedicamentoViewModel()
   val tratamientoMaestroVM = TratamientoMaestroViewModel()
   val medicamentoMaestroVM = MedicamentoMaestroViewModel()
   val citaVM = CitaViewModel()

   val auth = FirebaseAuth.getInstance()
   val db = FirebaseFirestore.getInstance()

   // Estado observable de sesión iniciada
   val isUserLoggedIn = mutableStateOf(auth.currentUser != null)

   init {
      auth.addAuthStateListener {
         isUserLoggedIn.value = it.currentUser != null
      }
   }

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
            usuarioVM.usuario.observeForever { usuario ->
               val grupoId = usuario?.grupos?.firstOrNull()
               if (!grupoId.isNullOrEmpty()) {
                  grupoVM.cargarGrupo(grupoId)
               }
            }
            onSuccess(uid)
         }
         .addOnFailureListener {
            onFailure(it.message ?: "Error al iniciar sesión")
         }
   }

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

   fun recuperarPassword(
      email: String,
      onResult: (Boolean, String) -> Unit
   ) {
      auth.sendPasswordResetEmail(email)
         .addOnSuccessListener { onResult(true, "Correo de recuperación enviado") }
         .addOnFailureListener { onResult(false, it.message ?: "Error al enviar el correo") }
   }

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

   fun clearAllData() {
      usuarioVM.clearUsuario()
      grupoVM.clearGrupo()
      pacienteVM.clearPacientes()
      citaVM.clearCitas()
      enfermedadPacienteVM.clearEnfermedades()
      tratamientoVM.clearTratamientos()
      medicamentoVM.clearMedicamentos()
   }

   fun cerrarSesion(navController: NavHostController) {
      clearAllData()
      FirebaseAuth.getInstance().signOut()
      navController.navigate(AppScreens.HomeScreen.route) {
         popUpTo(0) { inclusive = true }
         launchSingleTop = true
      }
   }

   fun subirEnfermedadesAFirebase(context: Context) {
      val enfermedades = cargarEnfermedadesDesdeAssets(context)
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

   fun subirMedicamentosMaestro(context: Context) {
      val lista = cargarMedicamentosMaestroDesdeAssets(context)
      val col = db.collection("medicamentos_maestro")
      for (m in lista) {
         col.document(m.medicamentoId).set(m)
      }
   }

   fun subirTratamientosMaestro(context: Context) {
      val lista = cargarTratamientosMaestroDesdeAssets(context)
      val col = db.collection("tratamientos_maestro")
      for (t in lista) {
         col.document(t.tratamientoId).set(t)
      }
   }
}
