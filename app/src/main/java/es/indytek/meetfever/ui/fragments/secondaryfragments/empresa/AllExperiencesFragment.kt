package es.indytek.meetfever.ui.fragments.secondaryfragments.empresa

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
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

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
        contexto = requireContext()
        actividad = requireActivity()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.ocultarElementosUI(actividad)
    }

    // pinto todas las experiencias en este fragmento y lo que necesite
    private fun pintar() {
        pintarTodasLasExperiencias()
    }

    // pinto las experiencias
    private fun pintarTodasLasExperiencias() {

        WebServiceExperiencia.findAllExperiencias(contexto, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    Utils.terminarCargaOnError(binding.loadingAnimation,binding.experiencesNone)
                } else {
                    val experiencias = any as ExperienciaWrapper
                    Utils.terminarCarga(contexto, binding.loadingAnimation) {
                        Animations.pintarGridRecyclerViewSuavemente(
                            gridLayoutManager = GridLayoutManager(contexto, 2),
                            recyclerView = binding.recyclerAllExperiences,
                            adapter = ExperienciaRecyclerViewAdapter(experiencias, usuario),
                        )
                    }
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(actividad)
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