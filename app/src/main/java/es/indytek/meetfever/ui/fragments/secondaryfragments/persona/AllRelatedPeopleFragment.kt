package es.indytek.meetfever.ui.fragments.secondaryfragments.persona

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
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServicePersona
import es.indytek.meetfever.databinding.FragmentAllRelatedPeopleBinding
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations

private const val ARG_PARAM1 = "usuario"

class AllRelatedPeopleFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentAllRelatedPeopleBinding

    // datos que necesito
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
        binding = FragmentAllRelatedPeopleBinding.inflate(inflater, container, false)

        // Pinto a todos los usuarios
        pintar()

        return binding.root
    }

    // lo pinto tosdo
    private fun pintar() {
        pintarPersonas()
    }

    // pintar todas las personas
    private fun pintarPersonas() {

        WebServicePersona.findAllRelatedPersonas(usuario, requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    //TODO ERROR
                } else {
                    val personas = any as PersonaWrapper
                    try {
                        Animations.pintarGridRecyclerViewSuavemente(
                            gridLayoutManager = GridLayoutManager(requireContext(), 3),
                            recyclerView = binding.recyclerAllInfluencers,
                            adapter = PersonaRecyclerViewAdapter(personas),
                        )
                    } catch (e: IllegalStateException) {
                        Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                    }
                }

            }
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            AllRelatedPeopleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}