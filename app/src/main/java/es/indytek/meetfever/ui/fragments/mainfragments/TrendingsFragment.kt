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
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.data.webservice.WebServicePersona
import es.indytek.meetfever.databinding.FragmentTrendingsBinding
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"

class TrendingsFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentTrendingsBinding
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
        binding = FragmentTrendingsBinding.inflate(inflater, container, false)

        // Pinto to.do lo relacionado con las opiniones
        pintar()

        // inicializo el motor de busqueda
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
                    WebServiceOpinion.buscarOpinion(s.toString(), requireContext(), object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                // TODO ERROR
                            } else {
                                val opiniones = any as OpinionWrapper
                                //ocultarContenido()
                                try {

                                    Animations.pintarLinearRecyclerViewSuavemente(
                                        linearLayoutManager = LinearLayoutManager(requireContext()),
                                        recyclerView = binding.busquedaOpinionesRecyclerView,
                                        adapter = OpinionRecyclerViewAdapter(opiniones, TrendingsFragment::class.java, currentUsuario),
                                        orientation = LinearLayoutManager.VERTICAL
                                    )
                                } catch (e: IllegalStateException) {
                                    Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                                }
                            }

                        }
                    })

                }

            }
        }

        binding.inputMainActivityTrendings.addTextChangedListener(textWatcher)

    }

    private fun ocultarContenido() {
        if (!contenidoOculto) {
            contenidoOculto = true
            Animations.ocultarVistaSuavemente(binding.feversValoradosTexto)
            Animations.ocultarVistaSuavemente(binding.topOpinionesRecycler)

            Animations.mostrarVistaSuavemente(binding.busquedaOpinionesRecyclerView)
        }
    }

    private fun mostrarContenido() {
        contenidoOculto = false
        Animations.mostrarVistaSuavemente(binding.feversValoradosTexto)
        Animations.mostrarVistaSuavemente(binding.topOpinionesRecycler)

        Animations.ocultarVistaSuavemente(binding.busquedaOpinionesRecyclerView)
    }

    // esta funcion llama a todas las funciones de dibujado
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarOpiniones()
    }

    // pinta los datos del tio que inició sesión
    private fun pintarNombreDelUsuarioQueInicioSesion() {

        // quiero saber que hora es para ver si es de dia o de noche
        val hora = LocalTime.now()

        // Le pongo un mensaje u otro en funcion de la hora
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

    // pinto las 100 opiniones con mas megustas de las ultimas 24 horas
    private fun pintarOpiniones() {

        WebServiceOpinion.find100OpinionesMasGustadas24h(currentUsuario.id, requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR

                        Utils.terminarCargaOnError(binding.loadingAnimationOpiniones, binding.opinionesNone)

                } else {
                    val top100OpinionesMasGustadas24H = any as OpinionWrapper
                    top100OpinionesMasGustadas24H.forEach {
                        Log.d(":::", it.numeroLikes.toString() + " " + it.like)
                    }
                    try {

                        Utils.terminarCarga(binding.loadingAnimationOpiniones){
                            Animations.pintarLinearRecyclerViewSuavemente(
                                linearLayoutManager = LinearLayoutManager(requireContext()),
                                recyclerView = binding.topOpinionesRecycler,
                                adapter = OpinionRecyclerViewAdapter(top100OpinionesMasGustadas24H, TrendingsFragment::class.java, currentUsuario),
                                orientation = LinearLayoutManager.VERTICAL,
                            )
                        }

                    } catch (e: IllegalStateException) {
                        Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                    }
                }

            }
        })

    }

    override fun onResume() {
        super.onResume()
        setBottomBarColorAndPosition()
    }

    private fun setBottomBarColorAndPosition() {
        val bottomBar = requireActivity().findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        Utils.mostrarBottomBar(requireActivity())
        bottomBar.indicatorColorRes = R.color.amarillo_meet
        bottomBar.selectTabAt(2, true)
    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            TrendingsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}