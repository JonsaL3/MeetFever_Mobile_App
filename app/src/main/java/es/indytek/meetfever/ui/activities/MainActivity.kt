package es.indytek.meetfever.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ActivityMainBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.ExplorerFragment
import es.indytek.meetfever.ui.fragments.mainfragments.PeopleFragment
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.fever.RedactarFeverFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.usersettings.UserSettingsFragment
import es.indytek.meetfever.ui.fragments.utilityfragments.BarcodeFragment
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.time.LocalTime

class MainActivity : AppCompatActivity() {

    // bindeo de la vista
    private lateinit var binding: ActivityMainBinding

    // datos que necesito en todos los fragmentos
    private lateinit var currentUsuario: Usuario

    //toggle del drawer
    private lateinit var toggle: ActionBarDrawerToggle
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

        loadDataToDrawer()

        loadDataIntoUI()

    }


    private fun cargarListeners() {
        binding.irAAjustes.setOnClickListener {
            val fragmento = UserSettingsFragment.newInstance(currentUsuario)
            Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
        }

        binding.openDrawer.setOnClickListener {
            Log.d(":::","XD")
            binding.drawerLayout.open()
        }
    }

    private fun loadDataIntoUI(){

        val hora = LocalTime.now()

        if (hora.hour >= 18 || hora.hour <= 6) {
            "¡${this.getString(R.string.buenas_noches)} ${currentUsuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
        } else {
            "¡${this.getString(R.string.buenos_dias)} ${currentUsuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
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

                        R.id.meet_scanner -> {
                            cargarMeetScanner()
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

    private fun chargeDrawer(){

        //cargo la barra indicandole: contexto, drawerlayout,
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer)

        //le meto al drawerlayout un listener para abrir el menu
        binding.drawerLayout.addDrawerListener(toggle)

        //permite sincronizar el icono del menu y el panel de navegacion
        toggle.syncState()

        //enchufo el icono de navegacion

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeButtonEnabled(true)

        //le asigno color al boton.
        toggle.drawerArrowDrawable.color = getColor(R.color.white)

        //asigno a la actionbar un logo personalizado.
        //pracambiarlo ir a layout action_bar
        supportActionBar?.setDisplayShowCustomEnabled(true)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        //listeners en los items del nav y asignar los fragmentos a cda boton

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){

//                R.id.nav_invoice -> {
//
//
//
//                }
//
//                R.id.nav_support -> {
//
//                }
//                R.id.nav_add_log_out -> {
//
//                }

            }


            binding.drawerLayout.closeDrawer(GravityCompat.START)

            true
        }
    }

    private fun loadDataToDrawer(){

        val header : View = binding.navView.getHeaderView(0)

        currentUsuario.fotoPerfil?.let {
            Utils.putBase64ImageIntoImageViewWithoutCornersWithPlaceholder(
                header.findViewById(R.id.imagen),
                it,
                this,
                R.drawable.ic_default_background_image
            )
        }

        currentUsuario.fotoFondo?.let {
            Utils.putBase64ImageIntoImageViewWithoutCornersWithPlaceholder(
                header.findViewById(R.id.backgroundProfile),
                it,
                this,
                R.drawable.ic_default_background_image
            )
        }

        header.findViewById<TextView>(R.id.nick).apply {
            this.text = currentUsuario.nick
        }

        header.findViewById<TextView>(R.id.frase).apply {
            this.text = currentUsuario.frase
        }
    }

    private fun chargeDrawer(){

        //cargo la barra indicandole: contexto, drawerlayout,
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer)

        //le meto al drawerlayout un listener para abrir el menu
        binding.drawerLayout.addDrawerListener(toggle)

        //permite sincronizar el icono del menu y el panel de navegacion
        toggle.syncState()

        //enchufo el icono de navegacion

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeButtonEnabled(true)

        //le asigno color al boton.
        toggle.drawerArrowDrawable.color = getColor(R.color.white)

        //asigno a la actionbar un logo personalizado.
        //pracambiarlo ir a layout action_bar
        supportActionBar?.setDisplayShowCustomEnabled(true)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        //listeners en los items del nav y asignar los fragmentos a cda boton

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){

//                R.id.nav_invoice -> {
//
//
//
//                }
//
//                R.id.nav_support -> {
//
//                }
//                R.id.nav_add_log_out -> {
//
//                }

            }


            binding.drawerLayout.closeDrawer(GravityCompat.START)

            true
        }
    }

    private fun loadDataToDrawer(){

        val header : View = binding.navView.getHeaderView(0)

        currentUsuario.fotoPerfil?.let {
            Utils.putBase64ImageIntoImageViewWithoutCornersWithPlaceholder(
                header.findViewById(R.id.imagen),
                it,
                this,
                R.drawable.ic_default_background_image
            )
        }

        currentUsuario.fotoFondo?.let {
            Utils.putBase64ImageIntoImageViewWithoutCornersWithPlaceholder(
                header.findViewById(R.id.backgroundProfile),
                it,
                this,
                R.drawable.ic_default_background_image
            )
        }

        header.findViewById<TextView>(R.id.nick).apply {
            this.text = currentUsuario.nick
        }

        header.findViewById<TextView>(R.id.frase).apply {
            this.text = currentUsuario.frase
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

    private fun cargarMeetScanner() {
        val fragmento = BarcodeFragment.newInstance(currentUsuario)
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