package es.indytek.meetfever.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ActivityMainBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.ExplorerFragment
import es.indytek.meetfever.ui.fragments.mainfragments.PeopleFragment
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.fever.RedactarFeverFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.usersettings.UserSettingsFragment
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    // bindeo de la vista
    private lateinit var binding: ActivityMainBinding

    // datos que necesito en todos los fragmentos
    private lateinit var currentUsuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // carga de datos del usuario
        cargarUsuarioDesdeBundle()

        // el resto cargarán en función del elemento que se seleccione en la navbar
        cargarFragmentosDesdeNavbar()

        // por último, cargo los botones suplementarios
        cargarBotonesSuplementarios()

        // por defecto cargo el explorer fragment, sin añadirlo a la pila de fragmentos para que no llegue a ocultarse nunca
        val fragmento = ExplorerFragment.newInstance(currentUsuario)
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragmento).commit()

        // cargo los listeners
        cargarListeners()

        // quito la pantalla de carga
        //Animations.ocultarVistaSuavemente(binding.pantallaDeCarga, 1000)

    }

    private fun cargarListeners() {
        binding.irAAjustes.setOnClickListener {
            val fragmento = UserSettingsFragment.newInstance(currentUsuario)
            Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
        }
    }

    // Muestro un fragmento u otro en función de lo que seleccione en la bottombar
    private fun cargarFragmentosDesdeNavbar() {

        // creo el listener
        binding.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {

            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                 when (newIndex) {
                    2 -> {
                        cargarTrendingsFragment()
                    }
                    0 -> {
                        cargarPeopleFragment()
                    }
                    else -> {
                        cargarExplorerFragment()
                    }
                }
            }

        })

    }

    // los botones extra floating
    private fun cargarBotonesSuplementarios() {

        binding.menuAccionRapida.setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                if (menuItem != null) {

                    when(menuItem.itemId) {
                        R.id.redactar_fever -> {
                            redactarFever()
                        }

                        R.id.ver_perfil -> {
                            cargarPerfil()
                        }
                    }

                }
                return super.onMenuItemSelected(menuItem)
            }
        })

    }

    // me cargo los datos del usuario que inició session
    private fun cargarUsuarioDesdeBundle() {
        val bundle = intent.extras
        if (bundle != null) {
            currentUsuario = bundle.getSerializable("usuario") as Usuario
        }
    }

    // Funciones de carga de fragmentos ############################################################
    private fun cargarExplorerFragment() {
        val fragmento = ExplorerFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    private fun cargarPeopleFragment() {
        val fragmento = PeopleFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    private fun cargarTrendingsFragment() {
        val fragmento = TrendingsFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    private fun cargarPerfil() {
        val fragmento = PerfilFragment.newInstance(currentUsuario, currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    private fun redactarFever() {
        val fragmento = RedactarFeverFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

}