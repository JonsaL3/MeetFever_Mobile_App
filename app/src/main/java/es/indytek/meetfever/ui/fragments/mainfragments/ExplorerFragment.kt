
package es.indytek.meetfever.ui.fragments.mainfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentExplorerBinding
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import java.time.LocalTime
import java.util.stream.Collectors

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "top10EmpresasConMasSeguidores"
private const val ARG_PARAM3 = "topExperienciasMasOpinadas"

class ExplorerFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentExplorerBinding

    // datos que necesito
    private lateinit var currentUsuario: Usuario
    private lateinit var top10EmpresasConMasSeguidores: EmpresaWrapper
    private lateinit var topExperienciasMasOpinadas: ExperienciaWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
            top10EmpresasConMasSeguidores = it.getSerializable(ARG_PARAM2) as EmpresaWrapper
            topExperienciasMasOpinadas = it.getSerializable(ARG_PARAM3) as ExperienciaWrapper
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentExplorerBinding.inflate(inflater, container, false)

        // Muestro to.dos los datos de este fragmento
        pintar()

        return binding.root
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

    // pinto las 4 experiencias mas mencionadas en opiniones
    private fun pintarExperienciasDestacadas() {
        // le digo cuantas columnas tiene que tener el recycler view
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.experienciaDestacadasRecyclerView.layoutManager = gridLayoutManager

        // le enchufo ahora el adapter
        val recyclerAdapter = ExperienciaRecyclerViewAdapter(
            // no creo que lleguen mas de 4 pero por si acaso
            topExperienciasMasOpinadas.stream().limit(4).collect(Collectors.toList())
        )

        binding.experienciaDestacadasRecyclerView.adapter = recyclerAdapter
    }

    // pinta el recycler de las empresas con mas seguidores
    private fun pintarTopEmpresas() {

        // Creo el layout manager que voy a usar en este recycler
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.localesTrendingRecycler.layoutManager = linearLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter: EmpresaRecyclerViewAdapter = EmpresaRecyclerViewAdapter(top10EmpresasConMasSeguidores.toList())
        binding.localesTrendingRecycler.adapter = recyclerAdapter

    }

    // esta función llama al resto de funciones para que pinten los elementos del fragment
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarExperienciasDestacadas()
        pintarTopEmpresas()

        // para evitar pantallas de carga feas, me espero otro medio segundo a que t1odo esté list
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.contenedorExplorer, 300)
        },100)
    }

    // Crear una instancia de este fragmento
    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario,
                        top10EmpresasConMasSeguidores: EmpresaWrapper,
                        topExperienciasMasOpinadas: ExperienciaWrapper) =
            ExplorerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, top10EmpresasConMasSeguidores)
                    putSerializable(ARG_PARAM3, topExperienciasMasOpinadas)
                }
            }
    }
}