package es.indytek.meetfever.ui.fragments.secondaryfragments.empresa

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
import es.indytek.meetfever.databinding.FragmentAllEmpresasBinding
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "todasLasEmpresas"

class AllEmpresasFragment : Fragment() {

    // bindeo de la vista
    private lateinit var binding: FragmentAllEmpresasBinding

    // Datos que necesito en este fragmento
    private lateinit var usuario: Usuario
    private lateinit var empresas: EmpresaWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
            empresas = it.getSerializable(ARG_PARAM2) as EmpresaWrapper
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllEmpresasBinding.inflate(inflater, container, false)

        // pinto toªdo lo necesario en este fragmento
        pintar()

        return binding.root
    }

    // pinto las distintas cosas que necesito
    private fun pintar() {
        pintarTodasLasEmpresas()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.containerAllEmpresas, 300)
        },100)
    }

    private fun pintarTodasLasEmpresas() {

        // Creo el layout manager que voy a usar en este recycler
        val girdLayoutManager = GridLayoutManager(context, 3)
        binding.recyclerAllEmpresas.layoutManager = girdLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter = EmpresaRecyclerViewAdapter(empresas.toList())
        binding.recyclerAllEmpresas.adapter = recyclerAdapter

    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, empresas: EmpresaWrapper) =
            AllEmpresasFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, empresas)
                }
            }
    }
}