package es.indytek.meetfever.ui.fragments.mainfragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServicePersona
import es.indytek.meetfever.databinding.FragmentPeopleBinding
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.persona.AllPeopleFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.persona.AllRelatedPeopleFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import nl.joery.animatedbottombar.AnimatedBottomBar

private const val ARG_PARAM1 = "usuario"

class PeopleFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentPeopleBinding
    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    // datos que necesito
    private lateinit var currentUsuario: Usuario

    private var contenidoOculto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
        contexto = requireContext()
        actividad = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeopleBinding.inflate(layoutInflater)

        // Pinto todos los elementos que componen el fragment
        pintar()

        // cargo los listeners para ver las listas correspondientes
        cargarListeners()

        // inicio el motor de busqueda
        motorDeBusqueda()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.mostrarElementosUI(actividad)
    }

    // Preparo las busquedas
    private fun motorDeBusqueda() {

        val textWatcher = object: TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isEmpty()) {
                    mostrarContenido()
                } else {
                    ocultarContenido()
                    WebServicePersona.buscarPersonas(s.toString(), contexto, object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                Animations.ocultarVistaSuavemente(binding.busquedaPersonasRecyclerView)
                            } else {
                                val personas = any as PersonaWrapper
                                //ocultarContenido()
                                try {

                                    Utils.terminarCarga(contexto, binding.loadingAnimationPersonasQueQuizasConoczcas){
                                        Animations.pintarGridRecyclerViewSuavemente(
                                            gridLayoutManager = GridLayoutManager(contexto, 3),
                                            recyclerView = binding.busquedaPersonasRecyclerView,
                                            adapter = PersonaRecyclerViewAdapter(personas, currentUsuario)
                                        )
                                    }

                                } catch (e: IllegalStateException) {
                                    Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                                    Utils.enviarRegistroDeErrorABBDD(
                                        context = contexto,
                                        stacktrace = e.message.toString(),
                                    )
                                }
                            }

                        }
                    })

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.inputMainActivityPeople.addTextChangedListener(textWatcher)

    }

    private fun ocultarContenido() {
        if (!contenidoOculto) {
            contenidoOculto = true
            Animations.ocultarVistaSuavemente(binding.defaultPeople)
            Animations.mostrarVistaSuavemente(binding.customPeople)
        }
    }

    private fun mostrarContenido() {
        if (contenidoOculto) {
            contenidoOculto = false
            Animations.ocultarVistaSuavemente(binding.customPeople)
            Animations.mostrarVistaSuavemente(binding.defaultPeople)
        }
    }

    // listeners que necesito
    private fun cargarListeners() {

        binding.localesTrendingTexto.setOnClickListener {
            mostrarTodasLasPersonas()
        }

        binding.personasQueQuizasConozcasTexto.setOnClickListener {
            obtenerTodasLasPersonasRelacionadas()
        }

    }

    // pregunto al webservice por todas las personas
    private fun mostrarTodasLasPersonas() {
        val fragmento = AllPeopleFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior((actividad as AppCompatActivity).supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    // private fun pregunto al web service por todas las personas relacionadas
    private fun obtenerTodasLasPersonasRelacionadas() {
        val fragmento = AllRelatedPeopleFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior((actividad as AppCompatActivity).supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    // pinta el recycler de las personas con mas seguidores
    private fun pintarTopPersonas() {

        WebServicePersona.findTop10PersonasConMasSeguidores(contexto, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    Utils.terminarCargaOnError(binding.loadingAnimationTopInfluencers,binding.topInfluencersNone)
                } else {
                    val personas = any as PersonaWrapper
                    Utils.terminarCarga(contexto, binding.loadingAnimationTopInfluencers){
                        Animations.pintarLinearRecyclerViewSuavemente(
                            linearLayoutManager = LinearLayoutManager(contexto),
                            recyclerView = binding.topPersonasRecyclerView,
                            adapter = PersonaRecyclerViewAdapter(personas, currentUsuario),
                            orientation = LinearLayoutManager.HORIZONTAL,
                        )
                    }
                }

            }
        })

    }

    private fun pintarPersonasQueQuizasConozca() {

        WebServicePersona.find10PersonasQueQuizasConozca(currentUsuario, contexto, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    Utils.terminarCargaOnError(binding.loadingAnimationPersonasQueQuizasConoczcas, binding.personasQueNone)
                }
                else {
                    val personas = any as PersonaWrapper
                    Utils.terminarCarga(contexto, binding.loadingAnimationPersonasQueQuizasConoczcas) {
                        Animations.pintarLinearRecyclerViewSuavemente(
                            linearLayoutManager = LinearLayoutManager(contexto),
                            recyclerView = binding.personasQueQuizasConozcasRecyclerView,
                            adapter = PersonaRecyclerViewAdapter(personas, currentUsuario),
                            orientation = LinearLayoutManager.HORIZONTAL,
                        )
                    }
                }

            }
        })

    }

    // Esta funcion llama al resto de componentes y los pinta
    private fun pintar() {
        pintarTopPersonas()
        pintarPersonasQueQuizasConozca()
    }

    override fun onResume() {
        super.onResume()
        setBottomBarColorAndPosition()
        binding.inputMainActivityPeople.setText("")
    }

    private fun setBottomBarColorAndPosition() {
        val bottomBar = (actividad as AppCompatActivity).findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        Utils.mostrarBottomBar(actividad)
        bottomBar.indicatorColorRes = R.color.azul_meet
        bottomBar.selectTabAt(0, true)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            usuario: Usuario,
        ) =
            PeopleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}