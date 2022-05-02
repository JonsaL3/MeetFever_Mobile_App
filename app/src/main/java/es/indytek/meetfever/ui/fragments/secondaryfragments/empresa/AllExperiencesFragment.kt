package es.indytek.meetfever.ui.fragments.secondaryfragments.empresa

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentAllExperiencesBinding
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "experiencias"

class AllExperiencesFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentAllExperiencesBinding

    // datos que necesito en este fragmento
    private lateinit var usuario: Usuario
    private lateinit var experiencias: ExperienciaWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
            experiencias = it.getSerializable(ARG_PARAM2) as ExperienciaWrapper
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllExperiencesBinding.inflate(inflater, container, false)

        // Pinto todo lo que necesito en este fragmento
        pintar()

        return binding.root
    }

    // pinto todas las experiencias en este fragmento y lo que necesite
    private fun pintar() {
        pintarTodasLasExperiencias()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.containerAllExperiences, 300)
        },100)
    }

    // pinto las experiencias
    private fun pintarTodasLasExperiencias() {
        // Creo el layout manager que voy a usar en este recycler
        val girdLayoutManager = GridLayoutManager(context, 2)
        binding.recyclerAllExperiences.layoutManager = girdLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter = ExperienciaRecyclerViewAdapter(experiencias.toList(), usuario)
        binding.recyclerAllExperiences.adapter = recyclerAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, experiencias: ExperienciaWrapper) =
            AllExperiencesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, experiencias)
                }
            }
    }
}