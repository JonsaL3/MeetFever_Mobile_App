package es.indytek.meetfever.ui.fragments.secondaryfragments.persona

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServicePersona
import es.indytek.meetfever.databinding.FragmentAllPeopleBinding
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "usuario"

class AllPeopleFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentAllPeopleBinding

    // datos que necesito
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
        binding = FragmentAllPeopleBinding.inflate(inflater, container, false)

        // Pinto a todos los usuarios
        pintar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.ocultarElementosUI(actividad)
    }

    // lo pinto tosdo
    private fun pintar() {
        pintarPersonas()
    }

    // pintar todas las personas
    private fun pintarPersonas() {

        WebServicePersona.findAllPersonas(contexto, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    Utils.terminarCargaOnError(binding.loadingAnimation, binding.topInfluencersNone)
                } else {
                    val personas = any as PersonaWrapper
                    Utils.terminarCarga(contexto, binding.loadingAnimation){
                        Animations.pintarGridRecyclerViewSuavemente(
                            gridLayoutManager = GridLayoutManager(contexto, 3),
                            recyclerView = binding.recyclerAllInfluencers,
                            adapter = PersonaRecyclerViewAdapter(personas, usuario),
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
            AllPeopleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}