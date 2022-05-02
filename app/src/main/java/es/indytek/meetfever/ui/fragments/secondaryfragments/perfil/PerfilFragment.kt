package es.indytek.meetfever.ui.fragments.secondaryfragments.perfil

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.databinding.FragmentPerfilBinding
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "opiniones"

class PerfilFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentPerfilBinding

    // datos que voy a necesitar en este fragmento
    private lateinit var usuario: Usuario
    private lateinit var opinionesUsuario: OpinionWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
            opinionesUsuario = it.getSerializable(ARG_PARAM2) as OpinionWrapper
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)

        // pinto tohdo lo relacionado con el usuario
        pintar()

        return binding.root
    }

    // Esta función me pinta to.do lo que necesito en este fragmento
    private fun pintar() {
        pintarDatosUsuario()
        pintarOpiniones()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.containerProfile, 300)
        },100)
    }

    private fun pintarDatosUsuario() {
        binding.tvNombre.text = usuario.nick
        binding.tvFrase.text = usuario.frase

        // TODO TENER EN CUENTA SHAPABLE IMAGE Y GRADIENTES NO SE MUESTRAN
        val fotoFondo = usuario.fotoFondo
        fotoFondo?.let {
            Utils.putBase64ImageIntoImageViewWithoutCorners(binding.backgroundProfile, fotoFondo, requireContext())
        }

        val fotoPerfil = usuario.fotoPerfil
        fotoPerfil?.let {
            Utils.putBase64ImageIntoImageViewCircular(binding.profilePicture, fotoPerfil, requireContext())
        }

    }

    private fun pintarOpiniones() {

        // Creo el layout manager que voy a usar en este recycler
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.opinionesUsuarioRecycler.layoutManager = linearLayoutManager

        // lo mismo para el recycler view
        val recyclerAdapter = OpinionRecyclerViewAdapter(opinionesUsuario.toList())
        binding.opinionesUsuarioRecycler.adapter = recyclerAdapter

    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, opiniones: OpinionWrapper) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, opiniones)
                }
            }
    }
}