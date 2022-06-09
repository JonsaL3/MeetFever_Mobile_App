package es.indytek.meetfever.ui.fragments.secondaryfragments.fever

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.*
import es.indytek.meetfever.databinding.FragmentRedactarFeverBinding
import es.indytek.meetfever.models.emoticono.Emoticono
import es.indytek.meetfever.models.emoticono.EmoticonoWrapper
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.opinion.Opinion
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaBusquedaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaBusquedaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.OpinionRecyclerViewAdapter
import es.indytek.meetfever.utils.*
import java.time.LocalDateTime
import kotlin.Any
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Exception
import kotlin.Int
import kotlin.also
import kotlin.apply
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.let
import kotlin.toString

private const val ARG_PARAM1 = "currentUsuario"

class RedactarFeverFragment : Fragment() {

    private lateinit var binding: FragmentRedactarFeverBinding
    private lateinit var currentUsuario: Usuario
    private lateinit var emoticonos: ArrayList<Emoticono>
    private lateinit var imageViews: LinkedHashMap<ImageView, Boolean>

    private var selectedEmpresaId: Int? = null
    private var selectedExperiencia: Int? = null

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }

        emoticonos = ArrayList()
        imageViews = linkedMapOf()

        contexto = requireContext()
        actividad = requireActivity()
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

        // arranco los listeners
        arrancarListeners()

        Utils.ocultarElementosUI(actividad)
    }

    private fun publicarFever() {
        val descripcion = binding.opinionContainer.text.toString()
        val fecha = LocalDateTime.now()
        val idAutor = currentUsuario.id
        val idEmpresa = selectedEmpresaId
        val idExperiencia = selectedExperiencia
        var idEmoji = -1

        try {
            val posEmoticono = imageViews.filter { it.value }.toList()[0]
            val emoticono = emoticonos[imageViews.toList().indexOf(posEmoticono)]
            idEmoji = emoticono.id
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        if (descripcion.isEmpty()) {
            Toast.makeText(contexto, "Debes escribir una opinión...", Toast.LENGTH_SHORT).show()
            return
        }

        if (idEmoji == -1) {
            Toast.makeText(contexto, "Debes seleccionar un emoticono...", Toast.LENGTH_SHORT).show()
            return
        }

        // La experiencia es opcional....

        WebServiceOpinion.insertarOpinion(
            descripcion,
            fecha,
            idEmoji,
            idAutor,
            idEmpresa,
            idExperiencia,
            contexto,
            object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) {

                    } else {
                        DialogMaker(contexto,
                            "Exito",
                            "Fever publicado con éxito"
                        ).infoCustomAccept(
                            "Aceptar",
                            object: DialogAcceptCustomActionInterface {
                            override fun acceptButton() {
                                (actividad as AppCompatActivity).supportFragmentManager.popBackStackImmediate()
                            }
                        })
                    }

                }
            })


    }

    private fun arrancarListeners() {

        binding.selectorEmpresa.setOnClickListener {
            Animations.ocultarVistaSuavemente(binding.selectorEmpresa)
        }

        binding.selectorExperiencia.setOnClickListener {
            Animations.ocultarVistaSuavemente(binding.selectorExperiencia)
        }

        binding.botonSeleccionarExperiencia.setOnClickListener {
            Animations.mostrarVistaSuavemente(binding.selectorExperiencia)
            motorBusquedaExperiencia()
        }

        binding.botonBuscarEmpresa.setOnClickListener {

            Animations.mostrarVistaSuavemente(binding.selectorEmpresa)
            motorBusquedaEmpresa()

        }

        binding.botonSendFever.setOnClickListener {
            publicarFever()
        }

    }

    private fun motorBusquedaExperiencia() {

        val textWatcher = object: TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                Log.d(":::B", s.toString())

                if (s.toString().isEmpty()) {
                    Animations.mostrarVistaSuavemente(binding.textoErrorBusquedaExperiencia)
                    Animations.ocultarVistaSuavemente(binding.recyclerSelectorExperiencias)
                } else {
                    WebServiceExperiencia.buscarExperiencia(s.toString(), contexto, object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                Animations.mostrarVistaSuavemente(binding.textoErrorBusquedaExperiencia)
                                Animations.ocultarVistaSuavemente(binding.recyclerSelectorExperiencias)
                            } else {
                                val experiencias = any as ExperienciaWrapper
                                Animations.ocultarVistaSuavemente(binding.textoErrorBusquedaExperiencia)
                                //ocultarContenido()
                                try {
                                    Animations.pintarLinearRecyclerViewSuavemente(
                                        linearLayoutManager = LinearLayoutManager(contexto),
                                        recyclerView = binding.recyclerSelectorExperiencias,
                                        adapter = ExperienciaBusquedaRecyclerViewAdapter(experiencias, currentUsuario, object: FromViewHolderToParent {
                                            override fun passthroughData(any: Any) {
                                                val idExperiencia = any as Int
                                                selectedExperiencia = idExperiencia
                                            }
                                        }),
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

                        }
                    })

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.inputTituloExperiencia.addTextChangedListener(textWatcher)

    }

    private fun motorBusquedaEmpresa() {

        val textWatcher = object: TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                Log.d(":::B", s.toString())

                if (s.toString().isEmpty()) {
                    Animations.mostrarVistaSuavemente(binding.textoErrorBusquedaEmpresa)
                    Animations.ocultarVistaSuavemente(binding.recyclerSelectorEmpresas)
                } else {
                    WebServiceEmpresa.buscarEmpresa(s.toString(), contexto, object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                Animations.mostrarVistaSuavemente(binding.textoErrorBusquedaEmpresa)
                                Animations.ocultarVistaSuavemente(binding.recyclerSelectorEmpresas)
                            } else {
                                val empresas = any as EmpresaWrapper
                                Animations.ocultarVistaSuavemente(binding.textoErrorBusquedaEmpresa)
                                //ocultarContenido()
                                try {
                                    Animations.pintarLinearRecyclerViewSuavemente(
                                        linearLayoutManager = LinearLayoutManager(contexto),
                                        recyclerView = binding.recyclerSelectorEmpresas,
                                        adapter = EmpresaBusquedaRecyclerViewAdapter(empresas, currentUsuario, object: FromViewHolderToParent {
                                            override fun passthroughData(any: Any) {
                                                val idEmpresa = any as Int
                                                selectedEmpresaId = idEmpresa

                                            }
                                        }),
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

                        }
                    })

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.inputNickEmpresa.addTextChangedListener(textWatcher)

    }
    // cuento que el usuario no supere los 250 caracteres
    private fun procesadorDeTextoYCaracteres() {

        binding.opinionContainer.addTextChangedListener { text ->

            // Primero actualizo el contador de caracteres
            Utils.setTextColorAsResource(binding.numeroCaracteresOpinion, R.color.gris_textos, contexto)
            if (text.toString().length > 250) {
                binding.numeroCaracteresOpinion.setTextColor(Color.RED)
            }
            "${text.toString().length}/250 Caracteres".also {
                binding.numeroCaracteresOpinion.text = it
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
        Utils.pintarFotoDePerfil(currentUsuario, binding.fotoPerfil, contexto)
    }

    // pinto todos los emoticonos que puede seleccionar y estén en la base de datos remota
    private fun pintarIconosSeleccionables() {
        WebServiceEmoticono.obtenerTodosLosEmoticonos(contexto, object: WebServiceGenericInterface {
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
                            context = contexto,
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

            val imageView = ImageView(contexto)
            imageView.setImageBitmap(decodedByte)

            imageViews[imageView] = false

            imageView.setOnClickListener {

                binding.noSeleccionado.text = getString(R.string.meetmoji_seleccionado)

                imageView.startAnimation(AnimationUtils.loadAnimation(contexto, androidx.appcompat.R.anim.abc_popup_enter))

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
        Utils.ocultarBottomBar(actividad)
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