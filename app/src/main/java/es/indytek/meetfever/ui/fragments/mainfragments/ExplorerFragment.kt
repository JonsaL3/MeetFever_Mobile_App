
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
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceExperiencia
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentExplorerBinding
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.empresa.AllEmpresasFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.empresa.AllExperiencesFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import java.time.LocalTime
import java.util.stream.Collectors
import kotlin.math.E

private const val ARG_PARAM1 = "usuario"

class ExplorerFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentExplorerBinding

    // datos que necesito
    private lateinit var currentUsuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentExplorerBinding.inflate(inflater, container, false)

        // Muestro to.dos los datos de este fragmento
        pintar()

        // arranco los listeners que necesito
        arrancarListeners()

        return binding.root
    }

    // preparo los listeners
    private fun arrancarListeners() {

        binding.experienciasDestacadasTexto.setOnClickListener{
            mostrarTodasLasExperiencias()
        }

        binding.localesTrendingTexto.setOnClickListener {
            mostrarTodasLasEmpresas()
        }
    }

    // muestro todas las empresas en otro fragmento
    private fun mostrarTodasLasEmpresas() {
        val fragmento = AllEmpresasFragment.newInstance(currentUsuario)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frame_layout,fragmento)?.commit()
    }

    private fun mostrarTodasLasExperiencias() {
        val fragmento = AllExperiencesFragment.newInstance(currentUsuario)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frame_layout,fragmento)?.commit()
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

    // pinto las 4 experiencias mas mencionadas en opiniones
    private fun pintarExperienciasDestacadas() {

        // busco las experiencias mas mencionadas
        WebServiceExperiencia.findTop4ExperienciasMasComentadas(requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                }
                else {
                    // Una vez encontradas las pinto suavemente
                    val experiencias = any as ExperienciaWrapper
                    Animations.pintarGridRecyclerViewSuavemente(
                        gridLayoutManager = GridLayoutManager(requireContext(), 2),
                        recyclerView = binding.experienciaDestacadasRecyclerView,
                        adapter = ExperienciaRecyclerViewAdapter(experiencias, currentUsuario),
                        duration = 200
                    )

                }

            }
        })
    }

    // pinta el recycler de las empresas con mas seguidores
    private fun pintarTopEmpresas() {

        // Busco las 10 empresas o locales (es lo mismo) con mas seguidores
        WebServiceEmpresa.findTop10EmpresasConMasSeguidores(requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {
                if (any == 0) {
                    // TODO ERROR
                }
                else {
                    // Una vez encontradas las pinto suavemente
                    val empresas = any as EmpresaWrapper
                    Animations.pintarLinearRecyclerViewSuavemente(
                        linearLayoutManager = LinearLayoutManager(requireContext()),
                        recyclerView = binding.localesTrendingRecycler,
                        adapter = EmpresaRecyclerViewAdapter(empresas),
                        orientation = LinearLayoutManager.HORIZONTAL,
                        duration = 200
                    )

                }
            }
        })

    }

    // esta función llama al resto de funciones para que pinten los elementos del fragment
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarExperienciasDestacadas()
        pintarTopEmpresas()
    }

    // Crear una instancia de este fragmento
    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            ExplorerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}