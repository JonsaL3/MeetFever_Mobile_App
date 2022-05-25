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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.data.webservice.WebServiceUsuario
import es.indytek.meetfever.databinding.FragmentPerfilBinding
import es.indytek.meetfever.models.mesigue.MeSigue
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.models.usuario.UsuarioWrapper
import es.indytek.meetfever.ui.fragments.secondaryfragments.follow.FollowedFollowingFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import nl.joery.animatedbottombar.AnimatedBottomBar

private const val ARG_PARAM1 = "usuarioGenerico"
private const val ARG_PARAM2 = "usuario"

class PerfilFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentPerfilBinding

    // datos que voy a necesitar en este fragmento
    private lateinit var usuario: Usuario
    private lateinit var currentUsuario: Usuario

    // mesigge
    private lateinit var meSigue: MeSigue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
            currentUsuario = it.getSerializable(ARG_PARAM2) as Usuario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)

        // pinto tohdo lo relacionado con el usuario
        pintar()

        // arranco los listeners
        arrancarListeners()

        // precargo los seguidores y los seguidos
        precargarSeguidoresYSeguidos()

        return binding.root
    }

    private fun arrancarListeners() {

        binding.seguirNoSeguir.setOnClickListener {
            seguirODejarDeSeguir()
        }

    }

    private fun precargarSeguidoresYSeguidos() {

        WebServiceUsuario.obtenerSeguidores(usuario.id, requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    val seguidores = any as UsuarioWrapper
                    binding.tvNSeguidores.text = seguidores.size.toString()
                    Animations.mostrarVistaSuavemente(binding.seguidoresLayout)

                    binding.seguidoresLayout.setOnClickListener {
                        val fragmento = FollowedFollowingFragment.newInstance(usuario, currentUsuario, seguidores,true)
                        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
                    }

                }

            }
        })

        WebServiceUsuario.obtenerSeguidos(usuario.id, requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    val seguidos = any as UsuarioWrapper
                    binding.tvNseguidos.text = seguidos.size.toString()
                    Animations.mostrarVistaSuavemente(binding.seguidosLayout)

                    binding.seguidosLayout.setOnClickListener {
                        val fragmento = FollowedFollowingFragment.newInstance(usuario, currentUsuario, seguidos,false)
                        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
                    }

                }

            }
        })

    }

    // Esta función me pinta to.do lo que necesito en este fragmento
    private fun pintarBotonSeguir() {

        if (currentUsuario != usuario) {

            WebServiceUsuario.isSeguidoPorUser(currentUsuario.id, usuario.id, requireContext(), object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) {
                        // todo error
                    } else {
                        meSigue = any as MeSigue
                        if (meSigue.mesigue) {
                            Log.d(":::", "LO SIGO -> $meSigue")
                            Animations.mostrarVistaSuavemente(binding.dejarDeSeguirBoton)
                        } else {
                            Log.d(":::", "NO LO SIGO $meSigue")
                            Animations.mostrarVistaSuavemente(binding.seguirBoton)
                        }
                    }

                }
            })

        }

    }

    private fun seguirODejarDeSeguir() {

        WebServiceUsuario.seguirDejarDeSeguir(currentUsuario.id, usuario.id, requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // todo error
                } else {

                    if (meSigue.mesigue) {
                        meSigue.mesigue = false
                        Animations.mostrarVistaSuavemente(binding.seguirBoton)
                        Animations.ocultarVistaSuavemente(binding.dejarDeSeguirBoton)
                        binding.tvNSeguidores.text =
                            ((binding.tvNSeguidores.text.toString().toInt()) - 1).toString()
                    } else {
                        meSigue.mesigue = true
                        Animations.mostrarVistaSuavemente(binding.dejarDeSeguirBoton)
                        Animations.ocultarVistaSuavemente(binding.seguirBoton)
                        ((binding.tvNSeguidores.text.toString().toInt()) + 1).toString()
                            .also { binding.tvNSeguidores.text = it }
                    }

                }

            }
        })

    }

    private fun pintar() {
        pintarDatosUsuario()
        pintarOpiniones()
        pintarBotonSeguir()
    }

    private fun pintarDatosUsuario() {

        // textos
        binding.tvNombre.text = usuario.nick
        binding.tvFrase.text = usuario.frase

        // foto de perfil
        Utils.pintarFotoDePerfil(usuario, binding.profilePicture, requireContext())

        // foto de fondo
        val fotoFondo = usuario.fotoFondo
        fotoFondo?.let {
            Utils.putBase64ImageIntoImageViewWithoutCornersWithPlaceholder(binding.backgroundProfile, it, requireContext(), R.drawable.ic_default_background_image)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageViewWithoutCorners(binding.backgroundProfile, R.drawable.ic_default_background_image, requireContext())
        }

        // degradado del fondo en función de la foto del perfil
        val foto = usuario.fotoPerfil
        foto?.let { // si tengo foto obtengo su color predominante

            val color = Utils.getDominantColorInImageFromBase64(it)

            if (color == Color.BLACK) { // puede ocurrir que el base 64 este mal formado, por lo que devolverá negro y arruinará la targeta
                binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                binding.colorFondo.backgroundTintList = ColorStateList.valueOf(color)
            }

        }?: kotlin.run { // si no tiene foto lo pongo de blanco
            binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }


    }

    private fun pintarOpiniones() {

        WebServiceOpinion.obtenerOpinionPorIdAutor(usuario, currentUsuario, requireContext() ,object : WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR

                    Animations.ocultarVistaSuavemente(binding.loadingAnimationOpinionesPerfil, 500)
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        binding.feversByUserNone.visibility = View.VISIBLE
                        Animations.mostrarVistaSuavemente(binding.feversByUserNone,500)
                    },500)
                } else {
                    val opiniones = any as OpinionWrapper
                    try {
                        Animations.ocultarVistaSuavemente(binding.loadingAnimationOpinionesPerfil, 500)

                        Handler(Looper.getMainLooper()).postDelayed(Runnable {

                            binding.loadingAnimationOpinionesPerfil.visibility = View.GONE

                            Animations.pintarLinearRecyclerViewSuavemente(
                                linearLayoutManager = LinearLayoutManager(requireContext()),
                                recyclerView = binding.opinionesUsuarioRecycler,
                                adapter = OpinionRecyclerViewAdapter(opiniones, PerfilFragment::class.java, currentUsuario),
                                orientation = LinearLayoutManager.VERTICAL,
                            )
                        },500)

                    } catch (e: IllegalStateException) {
                        Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                    }
                }

            }
        })

    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(requireActivity())
    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, currentUsuario: Usuario) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, currentUsuario)
                }
            }
    }
}