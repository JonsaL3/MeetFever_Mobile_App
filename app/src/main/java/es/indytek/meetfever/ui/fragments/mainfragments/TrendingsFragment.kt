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
import es.indytek.meetfever.databinding.FragmentTrendingsBinding
import es.indytek.meetfever.models.opinion.Opinion
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "100opinionesconmasmg"

class TrendingsFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentTrendingsBinding

    private lateinit var currentUsuario: Usuario
    private lateinit var top100OpinionesMasGustadas24H: OpinionWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
            top100OpinionesMasGustadas24H = it.getSerializable(ARG_PARAM2) as OpinionWrapper
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingsBinding.inflate(inflater, container, false)

        // Pinto todo lo relacionado con las opiniones
        pintar()

        return binding.root
    }

    // esta funcion llama a todas las funciones de dibujado
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarOpiniones()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.contenedorTrendings, 300)
        }, 100)
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

        // Creo el layout manager que voy a usar en este recycler
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.topOpinionesRecycler.layoutManager = linearLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter = OpinionRecyclerViewAdapter(top100OpinionesMasGustadas24H.toList())
        binding.topOpinionesRecycler.adapter = recyclerAdapter

    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, top100OpinionesMasGustadas24H: OpinionWrapper) =
            TrendingsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, top100OpinionesMasGustadas24H)
                }
            }
    }
}