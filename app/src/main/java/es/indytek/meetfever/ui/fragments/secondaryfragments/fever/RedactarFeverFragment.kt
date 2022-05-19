package es.indytek.meetfever.ui.fragments.secondaryfragments.fever

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEmoticono
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentRedactarFeverBinding
import es.indytek.meetfever.models.emoticono.Emoticono
import es.indytek.meetfever.models.emoticono.EmoticonoWrapper
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Utils
import kotlinx.coroutines.*

private const val ARG_PARAM1 = "currentUsuario"

class RedactarFeverFragment : Fragment() {

    private lateinit var binding: FragmentRedactarFeverBinding
    private lateinit var currentUsuario: Usuario
    private lateinit var emoticonos: ArrayList<Emoticono>
    private lateinit var imageViews: ArrayList<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }

        emoticonos = ArrayList()
        imageViews = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRedactarFeverBinding.inflate(inflater, container, false)

        // pinto to.do lo necesario
        pintar()

        // arranco el contador de caracteres y lo limito a 250
        procesadorDeTextoYCaracteres()

        return binding.root
    }

    // cuento que el usuario no supere los 250 caracteres
    private fun procesadorDeTextoYCaracteres() {

        var empresaEncontrada = false

        binding.opinionContainer.addTextChangedListener { text ->

            Utils.setTextColorAsResource(binding.numeroCaracteresOpinion, R.color.gris_textos, requireContext())
            if (text.toString().length > 250) {
                binding.numeroCaracteresOpinion.setTextColor(Color.RED)
            }

            "${text.toString().length}/250 Caracteres".also {
                binding.numeroCaracteresOpinion.text = it
            }

            text?.let {

                if (text.contains("@")) {

                    // obtengo las palabras que empiezan por @
                    val empresas = text.toString().split(" ").filter {
                        it.startsWith("@")
                    }

                    val empresa = empresas[0]

                    // si una palabra empieza por @...
                    val empresaCortada = empresa.substring(1, empresa.length)

                    Log.d(":::", "EMPRESA ENCONTRADA -> $empresaEncontrada")
                    Log.d(":::", "EMPRESA CORTADA -> $empresaCortada")

                    if (!empresaEncontrada) {

                        WebServiceEmpresa.findEmpresaByNickname(empresaCortada, requireContext(), object: WebServiceGenericInterface {
                            override fun callback(any: Any) {

                                if (any == 0) {
                                    // TODO ERROR
                                } else {
                                    empresaEncontrada = true
                                    val empresaDescargada = any as Empresa

                                    // pongo la foto en la preview
                                    val foto = empresaDescargada.fotoPerfil
                                    foto?.let {
                                        Utils.putBase64ImageIntoImageViewWithPlaceholder(binding.previewEnterprise, foto, requireContext(), R.drawable.ic_default_enterprise_black_and_white)
                                    } ?: run {
                                        Utils.putResourceImageIntoImageView(binding.previewEnterprise, R.drawable.ic_default_enterprise_black_and_white, requireContext())
                                    }

                                    // oculto los mensajes de que no se encontró ninguna empresa
                                    binding.noSeleccionadoPreview.visibility = View.GONE
                                    binding.elLocalQueSeleccione.visibility = View.GONE

                                    // muestro el nombre de la empresa y su frase
                                    binding.nombreEmpresa.visibility = View.VISIBLE
                                    binding.nombreEmpresa.text = empresaDescargada.nick

                                    binding.descripcionEmpresa.visibility = View.VISIBLE
                                    binding.descripcionEmpresa.text = empresaDescargada.frase

                                    binding.degradado.visibility = View.VISIBLE
                                    binding.previewEnterprise.visibility = View.VISIBLE

                                    // marco esa palabra en el edit text de otro color
                                    val start = text.indexOf(empresa)
                                    val end = start + empresa.length
                                    binding.opinionContainer.text?.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.rosa_meet)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                                }

                            }
                        })

                    } else if (empresaCortada != binding.nombreEmpresa.text.toString()) {

                        empresaEncontrada = false

                        // oculto la foto etc
                        binding.nombreEmpresa.visibility = View.GONE
                        binding.descripcionEmpresa.visibility = View.GONE
                        binding.previewEnterprise.visibility = View.GONE
                        binding.degradado.visibility = View.GONE
                        binding.previewEnterprise.setImageDrawable(null)

                        // Vuelvo a mostrar lo de por defecto
                        binding.noSeleccionadoPreview.visibility = View.VISIBLE
                        binding.elLocalQueSeleccione.visibility = View.VISIBLE

                        // vuelvo a poner el color en su sitio
                        val start = text.indexOf(empresa)
                        val end = start + empresa.length
                        binding.opinionContainer.text?.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.gris_textos)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        binding.opinionContainer.setTextColor(requireContext().getColor(R.color.gris_textos))
                    }

                }

            }

        }

    }

    // pintar todzxo
    private fun pintar() {

        pintarIconosSeleccionables()
        pintarDatosUsuario()
    }

    // pinto los datos del usuario (em este caso la foto)
    private fun pintarDatosUsuario() {

        val fotoPorDefecto = if (currentUsuario is Empresa) R.drawable.ic_default_enterprise_black_and_white else R.drawable.ic_default_user_image
        val fotoPerfil = currentUsuario.fotoPerfil

        fotoPerfil?.let {
            Utils.putBase64ImageIntoImageViewCircularWithPlaceholder(binding.fotoPerfil, fotoPerfil, requireContext(), fotoPorDefecto)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageView(binding.fotoPerfil, fotoPorDefecto, requireContext())
        }

    }

    // pinto todos los emoticonos que puede seleccionar y estén en la base de datos remota
    private fun pintarIconosSeleccionables() {
        WebServiceEmoticono.obtenerTodosLosEmoticonos(requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

               // binding.animationView.visibility = View.GONE

                if (any == 0) {
                    // TODO ERROR
                } else {

                    val e = any as EmoticonoWrapper

                    //Animations.ocultarVistaSuavemente(binding.animationView, 500)

                    try {
//                        Animations.pintarGridRecyclerViewSuavemente(
//                            gridLayoutManager = GridLayoutManager(requireContext(), 11),
//                            recyclerView = binding.listaDeIconos,
//                            adapter = IconoRecyclerViewAdapter(emoticonos),
//                            duration = 200
//                        )

                        emoticonos.addAll(e)

                        createEmoticonoImageViews()

                    } catch (e: Exception) {
                        Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                    }



                }

            }
        })

    }

    fun createEmoticonoImageViews(){

        val emoticonosViews = ArrayList<ImageView>()

        for (emoticono in emoticonos){
            val decodedString: ByteArray = Base64.decode(emoticono.emoji, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            val imageView = ImageView(requireContext())
            imageView.setImageBitmap(decodedByte)

            imageViews.add(imageView)
            imageView.setOnClickListener {
                imageViews.forEach {
                    it.clearColorFilter()
                }

                imageView.setColorFilter(requireContext().getColor(R.color.rosa_meet), PorterDuff.Mode.SRC_ATOP)

                //TODO QUEDARME CON EL QUE SELECCIONE
            }

            emoticonosViews.add(imageView)

        }

        //creo el layoutparam

        val layout = binding.lechuga

        //le asigno una posición
        imageViews.forEach {

            layout.addView(it)

            // le doy una altura de 100
            it.layoutParams.height = 80
            it.layoutParams.width = 80
//            it.layoutParams.


        }




    }


    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            RedactarFeverFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}