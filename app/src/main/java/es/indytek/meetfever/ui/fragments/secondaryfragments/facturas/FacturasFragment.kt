package es.indytek.meetfever.ui.fragments.secondaryfragments.facturas

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceFactura
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentFacturasBinding
import es.indytek.meetfever.models.factura.FacturaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.FacturaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.TitularesRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "currentUsuario"

class FacturasFragment : Fragment() {

    private lateinit var currentUsuario: Usuario
    private lateinit var binding: FragmentFacturasBinding

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
        contexto = requireContext()
        actividad = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacturasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pintar()
    }

    private fun pintar() {
        pintarRecyclerFacturas()
    }

    private fun pintarRecyclerFacturas() {

        WebServiceFactura.obtenerFacturasDeUnUsuario(currentUsuario.id, contexto, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                Log.d(":::", any.toString())

                if (any == 0) {
                    Utils.terminarCargaOnError(binding.loadingAnimation, binding.textoNone)
                } else {
                    Animations.ocultarVistaSuavemente(binding.loadingAnimation)
                    val facturas = any as FacturaWrapper
                    Animations.pintarLinearRecyclerViewSuavemente(
                        linearLayoutManager = LinearLayoutManager(contexto),
                        recyclerView = binding.recyclerFacturas,
                        adapter = FacturaRecyclerViewAdapter(facturas.map { f -> f.factura }, currentUsuario),
                        orientation = LinearLayoutManager.VERTICAL
                    )

                }

            }
        })

    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarElementosUI(actividad)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Usuario) =
            FacturasFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}