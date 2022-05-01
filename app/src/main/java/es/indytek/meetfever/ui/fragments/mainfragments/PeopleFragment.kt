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
import es.indytek.meetfever.databinding.FragmentPeopleBinding
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.PersonaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "topPersonasConMasSeguidores"
private const val ARG_PARAM3 = "personasQueQuizásConozcas"

class PeopleFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentPeopleBinding

    // datos que necesito
    private lateinit var currentUsuario: Usuario
    private lateinit var topPersonasConMasSeguidores: PersonaWrapper
    private lateinit var diezPersonasQueQuizasConozcas: PersonaWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
            topPersonasConMasSeguidores = it.getSerializable(ARG_PARAM2) as PersonaWrapper
            diezPersonasQueQuizasConozcas = it.getSerializable(ARG_PARAM3) as PersonaWrapper
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPeopleBinding.inflate(layoutInflater)
        // Pinto todos los elementos que componen el fragment
        pintar()

        return binding.root
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

        // Creo el layout manager que voy a usar en este recycler
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.topPersonasRecyclerView.layoutManager = linearLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter = PersonaRecyclerViewAdapter(topPersonasConMasSeguidores.toList())
        binding.topPersonasRecyclerView.adapter = recyclerAdapter

    }

    private fun pintarPersonasQueQuizasConozca() {

        // Creo el layout manager que voy a usar en este recycler
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.personasQueQuizasConozcasRecyclerView.layoutManager = linearLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter = PersonaRecyclerViewAdapter(diezPersonasQueQuizasConozcas.toList())
        binding.personasQueQuizasConozcasRecyclerView.adapter = recyclerAdapter

    }

    // Esta funcion llama al resto de componentes y los pinta
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarTopPersonas()
        pintarPersonasQueQuizasConozca()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.contendorPeople, 300)
        },100)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            usuario: Usuario,
            topPersonasConMasSeguidores: PersonaWrapper,
            diezPersonasQueQuizasConozcas: PersonaWrapper
        ) =
            PeopleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, topPersonasConMasSeguidores)
                    putSerializable(ARG_PARAM3, diezPersonasQueQuizasConozcas)
                }
            }
    }
}