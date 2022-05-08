package es.indytek.meetfever.ui.fragments.mainfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServicePersona
import es.indytek.meetfever.databinding.FragmentPeopleBinding
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.persona.AllPeopleFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.persona.AllRelatedPeopleFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"

class PeopleFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentPeopleBinding

    // datos que necesito
    private lateinit var currentUsuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPeopleBinding.inflate(layoutInflater)

        // Pinto todos los elementos que componen el fragment
        pintar()

        // cargo los listeners para ver las listas correspondientes
        cargarListeners()

        return binding.root
    }
    // listeners que necesito
    private fun cargarListeners() {

        binding.localesTrendingTexto.setOnClickListener {
            mostrarTodasLasPersonas()
        }

        binding.personasQueQuizasConozcasTexto.setOnClickListener {
            obtenerTodasLasPersonasRelacionadas()
        }

    }

    // pregunto al webservice por todas las personas
    private fun mostrarTodasLasPersonas() {
        val fragmento = AllPeopleFragment.newInstance(currentUsuario)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frame_layout,fragmento)?.commit()
    }

    // private fun pregunto al web service por todas las personas relacionadas
    private fun obtenerTodasLasPersonasRelacionadas() {
        val fragmento = AllRelatedPeopleFragment.newInstance(currentUsuario)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frame_layout,fragmento)?.commit()
    }

    // pinta los datos del tio que inició sesión
    private fun pintarNombreDelUsuarioQueInicioSesion() {

        // quiero saber que hora es para ver si es de dia o de noche
        val hora = LocalTime.now()

        // Le pongo un mensaje u otro en funcion de la hora
        if (hora.hour >= 18 || hora.hour <= 6) {
            "¡${this.getString(R.string.buenos_dias)} ${currentUsuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
        } else {
            "¡${this.getString(R.string.buenas_noches)} ${currentUsuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
        }

    }

    // pinta el recycler de las personas con mas seguidores
    private fun pintarTopPersonas() {

        WebServicePersona.findTop10PersonasConMasSeguidores(requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    val personas = any as PersonaWrapper
                    Animations.pintarLinearRecyclerViewSuavemente(
                        linearLayoutManager = LinearLayoutManager(requireContext()),
                        recyclerView = binding.topPersonasRecyclerView,
                        adapter = PersonaRecyclerViewAdapter(personas),
                        orientation = LinearLayoutManager.HORIZONTAL,
                        duration = 200
                    )
                }

            }
        })

    }

    private fun pintarPersonasQueQuizasConozca() {

        WebServicePersona.find10PersonasQueQuizasConozca(currentUsuario, requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                }
                else {
                    val personas = any as PersonaWrapper
                    Animations.pintarLinearRecyclerViewSuavemente(
                        linearLayoutManager = LinearLayoutManager(requireContext()),
                        recyclerView = binding.personasQueQuizasConozcasRecyclerView,
                        adapter = PersonaRecyclerViewAdapter(personas),
                        orientation = LinearLayoutManager.HORIZONTAL,
                        duration = 200
                    )
                }

            }
        })

    }

    // Esta funcion llama al resto de componentes y los pinta
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarTopPersonas()
        pintarPersonasQueQuizasConozca()
    }

    companion object {
        @JvmStatic
        fun newInstance(
            usuario: Usuario,
        ) =
            PeopleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}