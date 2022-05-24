package es.indytek.meetfever.ui.fragments.secondaryfragments.empresa

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceExperiencia
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentAllExperiencesBinding
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "usuario"

class AllExperiencesFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentAllExperiencesBinding

    // datos que necesito en este fragmento
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllExperiencesBinding.inflate(inflater, container, false)

        // Pinto tohdo lo que necesito en este fragmento
        pintar()

        return binding.root
    }

    // pinto todas las experiencias en este fragmento y lo que necesite
    private fun pintar() {
        pintarTodasLasExperiencias()
    }

    // pinto las experiencias
    private fun pintarTodasLasExperiencias() {

        WebServiceExperiencia.findAllExperiencias(requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    val experiencias = any as ExperienciaWrapper
                    try {
                        Animations.pintarGridRecyclerViewSuavemente(
                            gridLayoutManager = GridLayoutManager(requireContext(), 2),
                            recyclerView = binding.recyclerAllExperiences,
                            adapter = ExperienciaRecyclerViewAdapter(experiencias, usuario),
                        )
                    } catch (e: IllegalStateException) {
                        Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                    }
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(requireActivity())
    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            AllExperiencesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}