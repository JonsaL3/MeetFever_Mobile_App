package es.indytek.meetfever.ui.fragments.mainfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.databinding.FragmentTrendingsBinding
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"

class TrendingsFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentTrendingsBinding

    private lateinit var currentUsuario: Usuario

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

        return binding.root
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
            "¡${this.getString(R.string.buenos_dias)} ${currentUsuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
        } else {
            "¡${this.getString(R.string.buenas_noches)} ${currentUsuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
        }

    }

    // pinto las 100 opiniones con mas megustas de las ultimas 24 horas
    private fun pintarOpiniones() {

        WebServiceOpinion.find100OpinionesMasGustadas24h(requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    val top100OpinionesMasGustadas24H = any as OpinionWrapper
                    Animations.pintarLinearRecyclerViewSuavemente(
                        linearLayoutManager = LinearLayoutManager(requireContext()),
                        recyclerView = binding.topOpinionesRecycler,
                        adapter = OpinionRecyclerViewAdapter(top100OpinionesMasGustadas24H),
                        orientation = LinearLayoutManager.VERTICAL,
                        duration = 200
                    )
                }

            }
        })

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