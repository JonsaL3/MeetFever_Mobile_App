package es.indytek.meetfever.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ActivityMainBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.ExplorerFragment
import es.indytek.meetfever.ui.fragments.mainfragments.PeopleFragment
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.utils.Animations
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

        // por defecto cargo el explorer fragment
        cargarExplorerFragment()

        // quito la pantalla de carga
        Animations.ocultarVistaSuavemente(binding.pantallaDeCarga, 1000)

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
                        binding.bottomBar.indicatorColorRes = R.color.amarillo_meet
                        cargarTrendingsFragment()
                    }
                    0 -> {
                        binding.bottomBar.indicatorColorRes = R.color.azul_meet
                        cargarPeopleFragment()
                    }
                    else -> {
                        binding.bottomBar.indicatorColorRes = R.color.rosa_meet
                        cargarExplorerFragment()
                    }
                }
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
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
    }

    private fun cargarPeopleFragment() {
        val fragmento = PeopleFragment.newInstance(currentUsuario)
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
    }

    private fun cargarTrendingsFragment() {
        val fragmento = TrendingsFragment.newInstance(currentUsuario)
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
    }

}