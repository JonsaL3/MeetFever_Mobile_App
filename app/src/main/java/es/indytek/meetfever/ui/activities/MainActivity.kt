package es.indytek.meetfever.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ActivityMainBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.ExplorerFragment
import es.indytek.meetfever.ui.fragments.mainfragments.PeopleFragment
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.facturas.FacturasFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.fever.RedactarFeverFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.usersettings.UserSettingsFragment
import es.indytek.meetfever.ui.fragments.utilityfragments.BarcodeFragment
import es.indytek.meetfever.utils.*
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

        chargeDrawer()

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

        binding.botonSoporte.setOnClickListener {
            // preparlo lo que necesito para irme a la main activity
            val bundle = Bundle()
            val intent = Intent(this, WebActivity::class.java)

            // guardo el usuario en el bundle
            bundle.putSerializable("usuario", currentUsuario)
            intent.putExtras(bundle)

            // me voy a la otra actividad
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
                        desactivarBottomBarTemporalmente()
                        cargarTrendingsFragment()
                    }
                    0 -> {
                        desactivarBottomBarTemporalmente()
                        cargarPeopleFragment()
                    }
                    else -> {
                        desactivarBottomBarTemporalmente()
                        cargarExplorerFragment()
                    }
                }
            }

        })

    }

    private fun desactivarBottomBarTemporalmente() {
        desactivarBottomBar()
        Handler(Looper.getMainLooper()).postDelayed({
            reactivarBottomBar()
        },Constantes.TIEMPO_DE_ANIMACIONES * 2)
    }

    private fun desactivarBottomBar() {
        binding.bottomBar.setTabEnabledAt(0, false)
        binding.bottomBar.setTabEnabledAt(1, false)
    }

    private fun reactivarBottomBar() {
        binding.bottomBar.setTabEnabledAt(0, true)
        binding.bottomBar.setTabEnabledAt(1, true)
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
                            val intent = Intent(this@MainActivity, QrActivity::class.java)
                            intent.putExtra("user", Gson().toJson(currentUsuario))
                            startActivity(intent)
                            //cargarMeetScanner()
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

    private fun chargeDrawer() {

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

        binding.botonLogOut.setOnClickListener {
            DialogMaker(this, "Cerrar sesión", "¿Realmente desea cerrar la sesión?").warningAcceptCustomAction("Cerrar sesión.", "Cancelar.", object: DialogAcceptCustomActionInterface {
                override fun acceptButton() {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    this@MainActivity.startActivity(intent)
                    (this@MainActivity as AppCompatActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })
        }

        binding.botonMisFacturas.setOnClickListener {
            binding.drawerLayout.close()
            cargarFacturasFragment()
        }

    }

    // Funciones de carga de fragmentos ############################################################
    private fun cargarExplorerFragment() {
        val fragmento = ExplorerFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    private fun cargarFacturasFragment() {
        val fragmento = FacturasFragment.newInstance(currentUsuario)
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