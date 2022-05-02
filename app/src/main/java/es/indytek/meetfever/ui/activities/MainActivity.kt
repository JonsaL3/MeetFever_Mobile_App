package es.indytek.meetfever.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.*
import es.indytek.meetfever.databinding.ActivityMainBinding
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.persona.PersonaWrapper
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

    // Datos del explorer fragment
    private lateinit var top10EmpresasConMasSeguidores: EmpresaWrapper
    private lateinit var topExperienciasMasOpinadas: ExperienciaWrapper

    // datos del people fragment
    private lateinit var topPersonasConMasSeguidores: PersonaWrapper
    private lateinit var diezPersonasQueQuizasConozcas: PersonaWrapper

    // datos del trending fragment
    private lateinit var top100OpinionesMasGustadas24H: OpinionWrapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // carga de datos del usuario
        cargarUsuarioDesdeBundle()

        // el resto cargarán en función del elemento que se seleccione en la navbar
        cargarFragmentosDesdeNavbar()

        // precargo todos los datos necesarios para los 3 fragmentos principales
        cargarDatosPrincipales()

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

    // Métodos relacionados con la carga de datos #################################################
    /*
    * Los datos de todas las actividades o fragmentos siempre serán cargados por el que los llama, para que
    * una vez cargue el nuevo ya esté con todos sus datos y disimular los tiempos de carga
    */
    private fun cargarDatosPrincipales() {

        // busco las 10 empresas con mas seguidores
        var mTop10EmpresasConMasSeguidores: EmpresaWrapper? = null
        WebServiceEmpresa.findTop10EmpresasConMasSeguidores(this, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {
                if (any == 0)
                    mTop10EmpresasConMasSeguidores = EmpresaWrapper()
                else
                    mTop10EmpresasConMasSeguidores = any as EmpresaWrapper
            }
        })

        // busco las 4 experiencias mas mencionadas en opiniones
        var mTopExperienciasMasOpinadas: ExperienciaWrapper? = null
        WebServiceExperiencia.findTop4ExperienciasMasComentadas(this, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {
                if (any == 0)
                    mTopExperienciasMasOpinadas = ExperienciaWrapper()
                else
                    mTopExperienciasMasOpinadas = any as ExperienciaWrapper
            }
        })

        var mTopPersonasConMasSeguidores: PersonaWrapper? = null
        WebServicePersona.findTop10PersonasConMasSeguidores(this, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {
                if (any == 0)
                    mTopPersonasConMasSeguidores = PersonaWrapper()
                else
                    mTopPersonasConMasSeguidores = any as PersonaWrapper
            }
        })

        var mDiezPersonasQueQuizasConozcas: PersonaWrapper? = null
        WebServicePersona.find10PersonasQueQuizasConozca(currentUsuario, this, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {
                if (any == 0)
                    mDiezPersonasQueQuizasConozcas = PersonaWrapper()
                else
                    mDiezPersonasQueQuizasConozcas = any as PersonaWrapper
            }
        })

        var mTop100OpinionesMasGustadas24H: OpinionWrapper? = null
        WebServiceOpinion.find100OpinionesMasGustadas24h(this, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {
                if (any == 0)
                    mTop100OpinionesMasGustadas24H = OpinionWrapper()
                else
                    mTop100OpinionesMasGustadas24H = any as OpinionWrapper
            }
        })

        // espero a que cargue to-do y procedo
        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            mTop10EmpresasConMasSeguidores?.let {
                top10EmpresasConMasSeguidores = it
            }

            mTopExperienciasMasOpinadas?.let {
                topExperienciasMasOpinadas = it
            }

            mTopPersonasConMasSeguidores?.let {
                topPersonasConMasSeguidores = it
            }

            mDiezPersonasQueQuizasConozcas?.let {
                diezPersonasQueQuizasConozcas = it
            }

            mTop100OpinionesMasGustadas24H?.let {
                top100OpinionesMasGustadas24H = it
            }

            // Quito la pantalla de carga
            Animations.ocultarVistaSuavemente(binding.pantallaDeCarga, 500)

            // cargo el fragmento principal por defecto
            cargarExplorerFragment()

        },500)

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

        try {
            val fragmento = ExplorerFragment
                .newInstance(currentUsuario, top10EmpresasConMasSeguidores, topExperienciasMasOpinadas)
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
        } catch (e: UninitializedPropertyAccessException) {
            // todo mensaje de reintentando
            cargarDatosPrincipales()
        }

    }

    private fun cargarPeopleFragment() {

        try {
            val fragmento = PeopleFragment
                .newInstance(currentUsuario, topPersonasConMasSeguidores, diezPersonasQueQuizasConozcas)
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
        } catch (e: UninitializedPropertyAccessException) {
            // todo mensaje de reintentando
            cargarDatosPrincipales()
        }

    }

    private fun cargarTrendingsFragment() {

        try {
            val fragmento = TrendingsFragment.newInstance(currentUsuario, top100OpinionesMasGustadas24H)
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
        } catch (e: UninitializedPropertyAccessException) {
            // todo mensaje de reintentando
            cargarDatosPrincipales()
        }

    }

}