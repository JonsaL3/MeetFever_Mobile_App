package es.indytek.meetfever.ui.fragments.secondaryfragments.fever

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.number.Scale.none
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.ViewCompat
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
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.opinion.Opinion
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Animations.agrandarViewSuavemente
import es.indytek.meetfever.utils.Animations.reducirViewSuavemente
import es.indytek.meetfever.utils.Utils
import kotlinx.coroutines.*
import java.security.KeyStore

private const val ARG_PARAM1 = "currentUsuario"

class RedactarFeverFragment : Fragment() {

    private lateinit var binding: FragmentRedactarFeverBinding
    private lateinit var currentUsuario: Usuario
    private lateinit var emoticonos: ArrayList<Emoticono>
    private lateinit var imageViews: LinkedHashMap<ImageView, Boolean>
    private var idEmpresa: Int = 0
    private var idExperiencia: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }

        emoticonos = ArrayList()
        imageViews = linkedMapOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRedactarFeverBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // pinto to.do lo necesario
        pintar()

        // arranco el contador de caracteres y lo limito a 250
        procesadorDeTextoYCaracteres()

        loadListeners()
    }

    private fun loadListeners(){
        binding.botonSendFever.setOnClickListener { sendFeverToServer() }
    }

    // cuento que el usuario no supere los 250 caracteres
    private fun procesadorDeTextoYCaracteres() {

        var empresaEncontrada = false
        var experienciaEncontrada = false

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
                                    this@RedactarFeverFragment.idEmpresa = empresaDescargada.id

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

                }else if(text.contains("#")){
                    // obtengo las palabras que empiezan por #
                    val experiencias = text.toString().split(" ").filter {
                        it.startsWith("#")
                    }

                    val experiencia = experiencias[0]

                    val experienciaCortada = experiencia.substring(1, experiencia.length)

                    Log.d(":::", "EMPRESA ENCONTRADA -> $experienciaEncontrada")
                    Log.d(":::", "EMPRESA CORTADA -> $experienciaCortada")




                }else{
                    empresaEncontrada = false

                    binding.opinionContainer.text?.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.gris_textos)), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    // oculto la foto etc
                    binding.nombreEmpresa.visibility = View.GONE
                    binding.descripcionEmpresa.visibility = View.GONE
                    binding.previewEnterprise.visibility = View.GONE
                    binding.degradado.visibility = View.GONE
                    binding.previewEnterprise.setImageDrawable(null)

                    // Vuelvo a mostrar lo de por defecto
                    binding.noSeleccionadoPreview.visibility = View.VISIBLE
                    binding.elLocalQueSeleccione.visibility = View.VISIBLE
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

                    Animations.ocultarVistaSuavemente(binding.animationView, 500)

                    try {

                        emoticonos.addAll(e)

                        createEmoticonoImageViews()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }



                }

            }
        })

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun createEmoticonoImageViews(){

        val emoticonosViews = ArrayList<ImageView>()

        for (emoticono in emoticonos){
            val decodedString: ByteArray = Base64.decode(emoticono.emoji, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            val imageView = ImageView(requireContext())
            imageView.setImageBitmap(decodedByte)

            imageViews[imageView] = false

            imageView.setOnClickListener {

                imageView.startAnimation(AnimationUtils.loadAnimation(requireContext(), androidx.appcompat.R.anim.abc_popup_enter))

                imageView.clearColorFilter()

                imageViews.forEach {
                    if(it.key != imageView){
                        imageViews[it.key] = false

                        it.key.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
                    }

                }

                imageViews[imageView] = true
            }

            emoticonosViews.add(imageView)

        }

        //creo el layoutparam

        val layout = binding.lechuga

        var animTime = 500L
        //le asigno una posición
        imageViews.forEach {

            layout.addView(it.key)
            Animations.mostrarVistaSuavemente(it.key, animTime)

            // le doy una altura de 100
            it.key.layoutParams.height = 140
            it.key.layoutParams.width = 140
            it.key.setPadding(20, 20, 20, 20)

            animTime+= 100L

        }
    }

    private fun sendFeverToServer(){

        val imageView = imageViews.filter { (_,v) -> !v }.toList()[0]
        val emoticono = emoticonos[imageViews.toList().indexOf(imageView)]

        val opinion =  Opinion(
            descripcion = binding.opinionContainer.text.toString(),
            eMOTICONO = emoticono,
            autor = currentUsuario,
            idEmpresa = idEmpresa,
            idExperiencia = idExperiencia,
            titulo = ""

        )

        Log.d(":::", opinion.toString())


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