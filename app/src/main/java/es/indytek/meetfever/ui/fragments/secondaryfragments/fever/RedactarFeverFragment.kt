package es.indytek.meetfever.ui.fragments.secondaryfragments.fever

import android.annotation.SuppressLint
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
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEmoticono
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceExperiencia
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentRedactarFeverBinding
import es.indytek.meetfever.models.emoticono.Emoticono
import es.indytek.meetfever.models.emoticono.EmoticonoWrapper
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.opinion.Opinion
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import java.lang.String
import java.util.*
import kotlin.Any
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.CharArray
import kotlin.Exception
import kotlin.Int
import kotlin.also
import kotlin.apply
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.let
import kotlin.run
import kotlin.toString

private const val ARG_PARAM1 = "currentUsuario"

class RedactarFeverFragment : Fragment() {

    private lateinit var binding: FragmentRedactarFeverBinding
    private lateinit var currentUsuario: Usuario
    private lateinit var emoticonos: ArrayList<Emoticono>
    private lateinit var imageViews: LinkedHashMap<ImageView, Boolean>
    private var idEmpresa: Int = 0
    private var idExperiencia: Int = 0

    /*private var atCount: Int = 0
    private var padCount: Int = 0*/


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

        Utils.ocultarElementosUI(requireActivity())
    }

    fun removeDuplicate(str: CharArray, length: Int): kotlin.String {
        //Creating index variable to use it as index in the modified string
        var index = 0

        // Traversing character array
        for (i in 0 until length) {

            // Check whether str[i] is present before or not
            var j: Int = 0
            while (j < i) {
                if (str[i] == str[j] && str[i] == '@') {
                    break
                }
                j++
            }

            // If the character is not present before, add it to resulting string
            if (j == i) {
                str[index++] = str[i]
            }
        }
        println(String.valueOf(Arrays.copyOf(str, index)))
        return String.valueOf(Arrays.copyOf(str, index))
    }


    // cuento que el usuario no supere los 250 caracteres
    private fun procesadorDeTextoYCaracteres() {

        var ultimaEmpresaEncontrada: Empresa? = null

        binding.opinionContainer.addTextChangedListener { text ->

            var ultimoCaracter: kotlin.String? = null

            // Primero actualizo el contador de caracteres
            Utils.setTextColorAsResource(binding.numeroCaracteresOpinion, R.color.gris_textos, requireContext())
            if (text.toString().length > 250) {
                binding.numeroCaracteresOpinion.setTextColor(Color.RED)
            }
            "${text.toString().length}/250 Caracteres".also {
                binding.numeroCaracteresOpinion.text = it
            }

            // REALIZO LAS BUSQUEDAS EN BASE A @ ###############################################
            try {
                // me quedo con lo que hay después del arroba.
                val busquedaEmpresa = text.toString().split(" ").filter {
                    it.contains("@")
                }[0].removePrefix("@")

                // busco la empresa en la base de datos
                if (ultimaEmpresaEncontrada == null) {

                    // TODO PULIR CUANDO PONGO COSAS DESPUÉS DE LA EMPRESA ENCONTRADA
                    // TODO PONER DE COLOR EL TEXTO QUE CORRESPONDE CON EL NICK DE LA EMPRESA
                    // TODO LAS EXPERIENCIAS POR TITULO
                    WebServiceEmpresa.findEmpresaByNickname(busquedaEmpresa, requireContext(), object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            ultimaEmpresaEncontrada = if (any == 0) {
                                // en caso de fracaso borro lo que haya
                                ocultarEmpresa()
                                null
                            } else {
                                // en caso de exito me quedo con la empresa y marco como encontrada
                                val empresa = any as Empresa
                                mostrarEmpresa(empresa)
                                empresa
                            }

                        }
                    })

                } else {

                    if (!text.toString().contains(ultimaEmpresaEncontrada!!.nick)) {
                        ocultarEmpresa()
                        ultimaEmpresaEncontrada = null
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            // si no hay arrobas es que no ha buscado nada.
            Log.d(":::->", text.toString())
            if (!text!!.contains("@")) {
                ocultarEmpresa()
                ultimaEmpresaEncontrada = null
            }

        }

    }

    private fun mostrarEmpresa(empresa: Empresa) {

        empresa.fotoPerfil?.let {
            Utils.putBase64ImageIntoImageView(binding.previewEnterprise, it, requireContext())
        }

        binding.nombreEmpresa.text = empresa.nombreEmpresa
        binding.descripcionEmpresa.text = empresa.frase

        binding.noSeleccionadoPreview.visibility = View.GONE
        binding.elLocalQueSeleccione.visibility = View.GONE

        binding.previewEnterprise.visibility = View.VISIBLE
        binding.degradado.visibility = View.VISIBLE
        binding.nombreEmpresa.visibility = View.VISIBLE
        binding.descripcionEmpresa.visibility = View.VISIBLE
        binding.previewEnterprise.visibility = View.VISIBLE

    }

    private fun ocultarEmpresa() {

        binding.previewEnterprise.visibility = View.GONE
        binding.degradado.visibility = View.GONE
        binding.nombreEmpresa.visibility = View.GONE
        binding.descripcionEmpresa.visibility = View.GONE
        binding.previewEnterprise.visibility = View.GONE

        binding.noSeleccionadoPreview.visibility = View.VISIBLE
        binding.elLocalQueSeleccione.visibility = View.VISIBLE

    }

    private fun contarNumeroDeArrobasEnUnaFrase(frase: kotlin.String): Int {
        var numeroDeArrobas = 0
        for (element in frase) {
            if (element == '@') {
                numeroDeArrobas++
            }
        }
        return numeroDeArrobas
    }

    private fun contarNumeroDeHashTagsEnUnaFrase(frase: kotlin.String): Int {
        var numeroDeHashtags = 0
        for (element in frase) {
            if (element == '#') {
                numeroDeHashtags++
            }
        }
        return numeroDeHashtags
    }

    private fun loadExperienceOnUI(experiencia: Experiencia){
        // pongo la foto en la preview
        val foto = experiencia.foto
        this@RedactarFeverFragment.idExperiencia = experiencia.id


        foto?.let {
            Utils.putBase64ImageIntoImageViewWithPlaceholder(binding.experienciaPreview, foto, requireContext(), R.drawable.ic_default_enterprise_black_and_white)
        } ?: run {
            Utils.putResourceImageIntoImageView(binding.experienciaPreview, R.drawable.ic_default_enterprise_black_and_white, requireContext())
        }

        // oculto los mensajes de que no se encontró ninguna empresa
        binding.noSeleccionadoPreview.visibility = View.GONE

        // muestro el nombre de la empresa y su frase
        binding.experienciaNombre.visibility = View.VISIBLE
        binding.experienciaNombre.text = experiencia.titulo

        binding.experienciaDescripcion.visibility = View.VISIBLE
        val wordsDescription = experiencia.descripcion?.split(" ")
        binding.experienciaDescripcion.text =
            wordsDescription?.get(0) ?: " " +
                    wordsDescription?.get(1) ?: " " +
                    wordsDescription?.get(2) ?: " " +
                    wordsDescription?.get(3) ?: "" + "..."

        binding.experienciaPreviewDegradado.visibility = View.VISIBLE
        binding.experienciaPreview.visibility = View.VISIBLE
    }

    // pintar todzxo
    private fun pintar() {

        pintarIconosSeleccionables()
        pintarDatosUsuario()
    }

    // pinto los datos del usuario (em este caso la foto)
    private fun pintarDatosUsuario() {
        Utils.pintarFotoDePerfil(currentUsuario, binding.fotoPerfil, requireContext())
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
                        Utils.enviarRegistroDeErrorABBDD(
                            context = requireContext(),
                            stacktrace = e.message.toString(),
                        )
                    }



                }

            }
        })

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createEmoticonoImageViews(){

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

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(requireActivity())
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