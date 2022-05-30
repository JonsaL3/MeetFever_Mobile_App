package es.indytek.meetfever.ui.fragments.mainfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"

class PeopleFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentPeopleBinding

    // datos que necesito
    private lateinit var currentUsuario: Usuario

    private var contenidoOculto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
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
        Utils.mostrarElementosUI(requireActivity())
    }

    // Preparo las busquedas
    private fun motorDeBusqueda() {

        val textWatcher = object: TextWatcher {

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().isEmpty()) {
                    mostrarContenido()
                } else {
                    ocultarContenido()
                    WebServicePersona.buscarPersonas(s.toString(), requireContext(), object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                // TODO ERROR
                            } else {
                                val personas = any as PersonaWrapper
                                //ocultarContenido()
                                try {

                                    Utils.terminarCarga(requireContext(), binding.loadingAnimationPersonasQueQuizasConoczcas){
                                        Animations.pintarGridRecyclerViewSuavemente(
                                            gridLayoutManager = GridLayoutManager(requireContext(), 3),
                                            recyclerView = binding.busquedaPersonasRecyclerView,
                                            adapter = PersonaRecyclerViewAdapter(personas, currentUsuario)
                                        )
                                    }

                                } catch (e: IllegalStateException) {
                                    Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                                    Utils.enviarRegistroDeErrorABBDD(
                                        context = requireContext(),
                                        stacktrace = e.message.toString(),
                                    )
                                }
                            }

                        }
                    })

                }

            }
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
        contenidoOculto = false
        Animations.ocultarVistaSuavemente(binding.customPeople)
        Animations.mostrarVistaSuavemente(binding.defaultPeople)
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
        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    // private fun pregunto al web service por todas las personas relacionadas
    private fun obtenerTodasLasPersonasRelacionadas() {
        val fragmento = AllRelatedPeopleFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    // pinta el recycler de las personas con mas seguidores
    private fun pintarTopPersonas() {

        WebServicePersona.findTop10PersonasConMasSeguidores(requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR

                        Utils.terminarCargaOnError(binding.loadingAnimationTopInfluencers,binding.topInfluencersNone)

                } else {
                    val personas = any as PersonaWrapper
                    Utils.terminarCarga(requireContext(), binding.loadingAnimationTopInfluencers){
                        Animations.pintarLinearRecyclerViewSuavemente(
                            linearLayoutManager = LinearLayoutManager(requireContext()),
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

        WebServicePersona.find10PersonasQueQuizasConozca(currentUsuario, requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                    Utils.terminarCargaOnError(binding.loadingAnimationPersonasQueQuizasConoczcas, binding.personasQueNone)
                }
                else {
                    val personas = any as PersonaWrapper
                    Utils.terminarCarga(requireContext(), binding.loadingAnimationPersonasQueQuizasConoczcas) {
                        Animations.pintarLinearRecyclerViewSuavemente(
                            linearLayoutManager = LinearLayoutManager(requireContext()),
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
    }

    private fun setBottomBarColorAndPosition() {
        val bottomBar = requireActivity().findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        Utils.mostrarBottomBar(requireActivity())
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