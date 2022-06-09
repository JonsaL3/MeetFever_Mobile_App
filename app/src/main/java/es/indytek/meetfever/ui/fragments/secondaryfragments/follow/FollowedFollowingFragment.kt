package es.indytek.meetfever.ui.fragments.secondaryfragments.follow

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.databinding.FragmentFollowedFollowingBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.models.usuario.UsuarioWrapper
import es.indytek.meetfever.ui.recyclerviews.adapters.SeguidorSeguidoRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "currentUser"
private const val ARG_PARAM2 = "selectedUser"
private const val ARG_PARAM4 = "list"
private const val ARG_PARAM3 = "following"

class FollowedFollowingFragment : Fragment() {

    private lateinit var currentUser: Usuario
    private lateinit var selectedUser: Usuario
    private lateinit var binding: FragmentFollowedFollowingBinding
    private lateinit var listaUsuarios: UsuarioWrapper

    private var following: Boolean = false

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUser = it.getSerializable(ARG_PARAM1) as Usuario
            selectedUser = it.getSerializable(ARG_PARAM2) as Usuario
            following = it.getBoolean(ARG_PARAM3)
            listaUsuarios = it.getSerializable(ARG_PARAM4) as UsuarioWrapper
        }
        contexto = requireContext()
        actividad = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowedFollowingBinding.inflate(inflater, container, false)

        // pinto los seguidores o seguidos
        pintar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.ocultarElementosUI(actividad)
    }

    private fun pintar() {
        pintarColorDelFondo()
        pintarListaDeSeguidoresOSeguidos()
    }

    private fun pintarColorDelFondo() {

        currentUser.fotoPerfil?.let {
            binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Utils.getDominantColorInImageFromBase64(it))
            binding.colorFondo2.backgroundTintList = ColorStateList.valueOf(Utils.getDominantColorInImageFromBase64(it))
        }?: kotlin.run {
            binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.colorFondo2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }

    }

    private fun pintarListaDeSeguidoresOSeguidos() {

        val testo = if (following) "Seguidores" else "Seguidos"

        binding.seguidorSeguido.text = testo

        try {
            Animations.pintarLinearRecyclerViewSuavemente(
                linearLayoutManager = LinearLayoutManager(contexto),
                recyclerView = binding.recyclerViewSeguidores,
                adapter = SeguidorSeguidoRecyclerViewAdapter(listaUsuarios.toList(), currentUser),
                orientation = LinearLayoutManager.VERTICAL
            )
        } catch (e: IllegalStateException) {
            Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
            Utils.enviarRegistroDeErrorABBDD(
                context = contexto,
                stacktrace = e.message.toString(),
            )
        }

    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(actividad)
    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, currentUsuario: Usuario, listaUsuarios: UsuarioWrapper, following: Boolean) =
            FollowedFollowingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, currentUsuario)
                    putSerializable(ARG_PARAM4, listaUsuarios)
                    putBoolean(ARG_PARAM3, following)
                }
            }
    }
}