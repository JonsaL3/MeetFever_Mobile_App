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

        loadListeners()

        Utils.ocultarElementosUI(requireActivity())
    }

    private fun loadListeners(){
        binding.botonSendFever.setOnClickListener { sendFeverToServer() }
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

                    Log.d(":::", "ME ACTUALIZO")

                    // obtengo las palabras que empiezan por @
                    val split =  text.toString().split(" ")
                    val empresas = split.filter {
                        it.startsWith("@")
                    }

                    try {
                        var empresa = empresas[0]

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

                        // Vuelvo a mostrar lo de por defecto
                        binding.noSeleccionadoPreview.visibility = View.VISIBLE

                            binding.opinionContainer.setTextColor(requireContext().getColor(R.color.gris_textos))
                        }

                    } catch(e: Exception) {
                        Log.e(":::", "No se puede buscar una empresa vacia")
                    }

                    //si el texto contiene ese patron, hará la busqueda.
                }else if(text.matches(Regex("#.* "))) {

                    // obtengo las palabras que empiezan por #
                    val experiencias = text.toString().split(" ").filter {
                        it.startsWith("#")
                    }

                    val experiencia = experiencias[0]

                    val experienciaCortada = experiencia.substring(1, experiencia.length)

                    Log.d(":::", "EXPERIENCIA ENCONTRADA -> $experienciaEncontrada")
                    Log.d(":::", "EXPERIENCIA CORTADA -> $experienciaCortada")

                    if (!experienciaEncontrada) {

                        WebServiceExperiencia.findExperienciaByTitulo(experienciaCortada, requireContext(), object: WebServiceGenericInterface {
                            override fun callback(any: Any) {

                                if (any == 0) {
                                    // TODO ERROR
                                } else {
                                    experienciaEncontrada = true
                                    val experienciasDescargadas = any as ExperienciaWrapper

                                    selectExperience(experienciasDescargadas)

                                    // marco esa palabra en el edit text de otro color
                                    val start = text.indexOf(experiencia)
                                    val end = start + experiencia.length
                                    binding.opinionContainer.text?.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.rosa_meet)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                                }

                            }
                        })

                    } else if (experienciaCortada != binding.nombreEmpresa.text.toString()) {

                        experienciaEncontrada = false

                        // oculto la foto etc
                        binding.experienciaNombre.visibility = View.GONE
                        binding.experienciaDescripcion.visibility = View.GONE
                        binding.experienciaPreview.visibility = View.GONE
                        binding.experienciaPreviewDegradado.visibility = View.GONE
                        binding.experienciaPreview.setImageDrawable(null)

                        // Vuelvo a mostrar lo de por defecto
                        binding.noSeleccionadoExperienciaPreview.visibility = View.VISIBLE

                        // vuelvo a poner el color en su sitio
                        val start = text.indexOf(experiencia)
                        val end = start + experiencia.length
                        binding.opinionContainer.text?.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.gris_textos)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        binding.opinionContainer.setTextColor(requireContext().getColor(R.color.gris_textos))
                    }


                } else {

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

    private fun selectExperience(experiencias: ExperienciaWrapper){

        experiencias.forEach {
            //creo la caja de experiencia
            val linearLayout = LinearLayout(requireContext())
            linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            //creo sufoto
            val imageView = ImageView(requireContext())
            imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            it.foto?.let {foto ->
                Utils.putBase64ImageIntoImageViewWithPlaceholder(binding.previewEnterprise, foto, requireContext(), R.drawable.ic_default_enterprise_black_and_white)
            } ?: run {
                Utils.putResourceImageIntoImageView(binding.previewEnterprise, R.drawable.ic_default_enterprise_black_and_white, requireContext())
            }

            //anado la foto al linear layout
            linearLayout.addView(imageView)

            //creo el linearlayout que contiene a la descripcion y el titulo
            val linearLayout2 = LinearLayout(requireContext())
            linearLayout2.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayout2.orientation = LinearLayout.VERTICAL

            //creo el titulo
            val textView = TextView(requireContext())
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.text = it.titulo

            //creo la descripcion
            val textView2 = TextView(requireContext())
            textView2.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView2.text = it.descripcion

            //por ultimo lo aniado al papa
            binding.experienciasLayout.addView(linearLayout)

            //creo un imageview
            val view = View(requireContext())
            view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)

            //le asigno un color
            view.setBackgroundColor(requireContext().getColor(R.color.gris_textos))

            //le asigno un padding
            view.setPadding(10, 0, 10, 0)

            //lo aniado a su papa
            binding.experienciasLayout.addView(view)

        }

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

        binding.opinionContainer.setText(removeDuplicate(binding.opinionContainer.text.toString().toCharArray(), binding.opinionContainer.text.toString().length))
        Toast.makeText(requireContext(), binding.opinionContainer.text.toString(), Toast.LENGTH_SHORT).show()

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