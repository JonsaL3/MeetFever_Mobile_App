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
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.databinding.FragmentTrendingsBinding
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"

class TrendingsFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentTrendingsBinding
    private lateinit var currentUsuario: Usuario

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

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
        binding = FragmentTrendingsBinding.inflate(inflater, container, false)

        // Pinto to.do lo relacionado con las opiniones
        pintar()

        // inicializo el motor de busqueda
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
                    WebServiceOpinion.buscarOpinion(s.toString(), currentUsuario.id, contexto, object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                Animations.ocultarVistaSuavemente(binding.busquedaOpinionesRecyclerView)
                            } else {
                                val opiniones = any as OpinionWrapper
                                //ocultarContenido()
                                try {
                                    Animations.pintarLinearRecyclerViewSuavemente(
                                        linearLayoutManager = LinearLayoutManager(contexto),
                                        recyclerView = binding.busquedaOpinionesRecyclerView,
                                        adapter = OpinionRecyclerViewAdapter(opiniones, TrendingsFragment::class.java, currentUsuario),
                                        orientation = LinearLayoutManager.VERTICAL
                                    )
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

        binding.inputMainActivityTrendings.addTextChangedListener(textWatcher)

    }

    private fun ocultarContenido() {
        if (!contenidoOculto) {
            contenidoOculto = true
            Animations.mostrarVistaSuavemente(binding.opinionesCustom)
            Animations.ocultarVistaSuavemente(binding.opinionesDefault)
        }
    }

    private fun mostrarContenido() {
        contenidoOculto = false
        Animations.mostrarVistaSuavemente(binding.opinionesDefault)
        Animations.ocultarVistaSuavemente(binding.opinionesCustom)
    }

    // esta funcion llama a todas las funciones de dibujado
    private fun pintar() {
        pintarOpiniones()
    }

    // pinto las 100 opiniones con mas megustas de las ultimas 24 horas
    private fun pintarOpiniones() {

        WebServiceOpinion.find100OpinionesMasGustadas24h(currentUsuario.id, contexto, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    Utils.terminarCargaOnError(binding.loadingAnimationOpiniones, binding.opinionesNone)
                } else {
                    val top100OpinionesMasGustadas24H = any as OpinionWrapper
                    top100OpinionesMasGustadas24H.forEach {
                        Log.d(":::", it.numeroLikes.toString() + " " + it.like)
                    }
                    Utils.terminarCarga(contexto, binding.loadingAnimationOpiniones){
                        Animations.pintarLinearRecyclerViewSuavemente(
                            linearLayoutManager = LinearLayoutManager(contexto),
                            recyclerView = binding.topOpinionesRecycler,
                            adapter = OpinionRecyclerViewAdapter(top100OpinionesMasGustadas24H, TrendingsFragment::class.java, currentUsuario),
                            orientation = LinearLayoutManager.VERTICAL,
                        )
                    }
                }

            }
        })

    }

    override fun onResume() {
        super.onResume()
        setBottomBarColorAndPosition()
        binding.inputMainActivityTrendings.setText("")
    }

    private fun setBottomBarColorAndPosition() {
        val bottomBar = (actividad as AppCompatActivity).findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        Utils.mostrarBottomBar(actividad)
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