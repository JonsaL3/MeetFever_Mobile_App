package es.indytek.meetfever.ui.fragments.secondaryfragments.perfil

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.data.webservice.WebServiceUsuario
import es.indytek.meetfever.databinding.FragmentPerfilBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "usuarioGenerico"

class PerfilFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentPerfilBinding

    // datos que voy a necesitar en este fragmento
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
        binding = FragmentPerfilBinding.inflate(inflater, container, false)

        // pinto tohdo lo relacionado con el usuario
        pintar()

        return binding.root
    }

    // Esta función me pinta to.do lo que necesito en este fragmento
    private fun pintar() {
        pintarDatosUsuario()
        pintarOpiniones()
    }

    private fun pintarDatosUsuario() {

        binding.tvNombre.text = usuario.nick
        binding.tvFrase.text = usuario.frase

        // TODO TENER EN CUENTA SHAPABLE IMAGE Y GRADIENTES NO SE MUESTRAN
        val fotoFondo = usuario.fotoFondo
        fotoFondo?.let {
            Utils.putBase64ImageIntoImageViewWithoutCornersWithPlaceholder(binding.backgroundProfile, fotoFondo, requireContext(), R.drawable.ic_default_background_image)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageViewWithoutCorners(binding.backgroundProfile, R.drawable.ic_default_background_image, requireContext())
        }

        val fotoPorDefecto = if (usuario is Empresa) R.drawable.ic_default_enterprise_black_and_white else R.drawable.ic_default_user_image

        val fotoPerfil = usuario.fotoPerfil
        fotoPerfil?.let {
            // pongo la foto de perfil
            Utils.putBase64ImageIntoImageViewCircularWithPlaceholder(binding.profilePicture, fotoPerfil, requireContext(), fotoPorDefecto)

            // pinto el fondo el perfil en consecuencia
            val color = Utils.getDominantColorInImageFromBase64(it)

            if (color == Color.BLACK) { // puede ocurrir que el base 64 este mal formado, por lo que devolverá negro y arruinará la targeta
                binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                // hago el color mas claro without Utils
                binding.colorFondo.backgroundTintList = ColorStateList.valueOf(color)
            }

        }?: kotlin.run {
            // pongo la foto de perfil
            Utils.putResourceImageIntoImageView(binding.profilePicture, fotoPorDefecto, requireContext())

            // el color en blanco en consecuencia
            binding.fragmentPerfil.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }


    }

    private fun pintarOpiniones() {

        WebServiceOpinion.obtenerOpinionPorIdAutor(usuario, requireContext() ,object : WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    val opiniones = any as OpinionWrapper
                    try {
                        Animations.pintarLinearRecyclerViewSuavemente(
                            linearLayoutManager = LinearLayoutManager(requireContext()),
                            recyclerView = binding.opinionesUsuarioRecycler,
                            adapter = OpinionRecyclerViewAdapter(opiniones, PerfilFragment::class.java),
                            orientation = LinearLayoutManager.VERTICAL,
                            duration = 200
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
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}