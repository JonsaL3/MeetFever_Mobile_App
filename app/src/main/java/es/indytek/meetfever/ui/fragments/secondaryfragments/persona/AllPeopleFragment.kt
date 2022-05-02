package es.indytek.meetfever.ui.fragments.secondaryfragments.persona

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentAllPeopleBinding
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "personas"

class AllPeopleFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentAllPeopleBinding

    // datos que necesito
    private lateinit var usuario: Usuario
    private lateinit var personas: PersonaWrapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
            personas = it.getSerializable(ARG_PARAM2) as PersonaWrapper
        }
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

    // lo pinto tosdo
    private fun pintar() {
        pintarPersonas()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.allPeopleContainer, 300)
        },100)
    }

    // pintar todas las personas
    private fun pintarPersonas() {
        // Creo el layout manager que voy a usar en este recycler
        val girdLayoutManager = GridLayoutManager(context, 3)
        binding.recyclerAllInfluencers.layoutManager = girdLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter = PersonaRecyclerViewAdapter(personas)
        binding.recyclerAllInfluencers.adapter = recyclerAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, personas: PersonaWrapper) =
            AllPeopleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, personas)
                }
            }
    }
}