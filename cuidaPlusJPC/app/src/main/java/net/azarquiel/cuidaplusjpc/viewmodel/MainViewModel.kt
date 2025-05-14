package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.view.MainActivity

class MainViewModel(mainActivity: MainActivity) : ViewModel() {

   val usuarioVM = UsuarioViewModel()

}
