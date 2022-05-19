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
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentAllEmpresasBinding
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "currentUsuario"

class AllEmpresasFragment : Fragment() {

    // bindeo de la vista
    private lateinit var binding: FragmentAllEmpresasBinding

    // Datos que necesito en este fragmento
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
        binding = FragmentAllEmpresasBinding.inflate(inflater, container, false)

        // pinto toªdo lo necesario en este fragmento
        pintar()

        return binding.root
    }

    // pinto las distintas cosas que necesito
    private fun pintar() {
        pintarTodasLasEmpresas()
    }

    private fun pintarTodasLasEmpresas() {

        WebServiceEmpresa.findAllEmpresas(requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    val empresas = any as EmpresaWrapper
                    try {
                        Animations.pintarGridRecyclerViewSuavemente(
                            gridLayoutManager = GridLayoutManager(requireContext(), 3),
                            recyclerView = binding.recyclerAllEmpresas,
                            adapter = EmpresaRecyclerViewAdapter(empresas, usuario),
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
            AllEmpresasFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}