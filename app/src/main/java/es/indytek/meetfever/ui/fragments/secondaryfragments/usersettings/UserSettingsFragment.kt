package es.indytek.meetfever.ui.fragments.secondaryfragments.usersettings

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServicePersona
import es.indytek.meetfever.data.webservice.WebServiceSexo
import es.indytek.meetfever.databinding.FragmentUserSettingsBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.sexo.Sexo
import es.indytek.meetfever.models.sexo.SexoWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.ui.fragments.utilityfragments.CameraFragment
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.DialogAcceptCustomActionInterface
import es.indytek.meetfever.utils.DialogMaker
import es.indytek.meetfever.utils.Extensions.toBase64
import es.indytek.meetfever.utils.Extensions.toBase64String
import es.indytek.meetfever.utils.Utils
import java.io.File


private const val ARG_PARAM1 = "currentUsuario"

class UserSettingsFragment : Fragment() {

    private lateinit var binding: FragmentUserSettingsBinding
    private lateinit var currentUsuario: Usuario
    private var isFromProfilePic = false
    private var fotoPerfil: String? = null
    private var fotoFondo: String? = null

    // para acceder a la galeria
    private val REQUEST_GALLERY = 1

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    private lateinit var listaSexos: SexoWrapper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
        contexto = requireContext()
        actividad = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarDatosActualesDelUsuario()
        cargarListeners()
    }

    private fun cargarListeners() {

        // botones
        binding.botonSacarFotoPerfil.setOnClickListener {
            irAlFragmentoDeLaCamara()
            isFromProfilePic = true
        }

        binding.botonSacarFotoFondo.setOnClickListener {
            irAlFragmentoDeLaCamara()
            isFromProfilePic = false
        }

        binding.confirmarCambios.setOnClickListener {
            confirmarCambios()
            Toast.makeText(contexto, "Espere mientras el servidor recibe la petición.", Toast.LENGTH_LONG).show()
        }

        binding.buscarFotoFondo.setOnClickListener {
            buscarImagenEnGaleria()
            isFromProfilePic = false
        }

        binding.buscarFotoPerfil.setOnClickListener {
            buscarImagenEnGaleria()
            isFromProfilePic = true
        }

        // respuestas de fragmentos
        setFragmentResultListener("FOTO") { _, bundle ->

            val foto: String? = bundle.getString("FOTO")

            foto?.let {

                val archivoDeImagen = File(foto)
                // file to bitmap
                val bitmap = BitmapFactory.decodeFile(archivoDeImagen.absolutePath)
                val base64FromFile = bitmap.toBase64String()// lo pinto en el imageview

                if (isFromProfilePic) {
                    fotoPerfil = base64FromFile
                    Utils.pintarFotoDePerfilDirectamente(currentUsuario, base64FromFile, binding.fotoPerfil, contexto)
                } else {
                    fotoFondo = base64FromFile
                    Utils.putBase64ImageIntoImageView(binding.fotoFondo, base64FromFile, contexto)
                }

            }

        }
    }

    private fun irAlFragmentoDeLaCamara() {
        val fragemnt = CameraFragment.newInstance(currentUsuario)
        try {
            Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager, fragemnt, "", R.id.frame_layout)
        } catch (e: Exception) {
            Utils.enviarRegistroDeErrorABBDD(e.stackTraceToString(), 0, contexto)
        }
    }

    private fun cargarDatosActualesDelUsuario() {

        pintarDatosGenerales()

        if (currentUsuario is Empresa) {
            pintarDatosEmpresa()
        } else if (currentUsuario is Persona) {
            pintarDatosPersona()
        }

    }

    private fun confirmarCambios() {

        if (currentUsuario is Empresa) {

            val empresaActualizada = currentUsuario as Empresa

            binding.correo.text.toString().let { empresaActualizada.correo = it }
            binding.nickname.text.toString().let { empresaActualizada.nick = it }
            binding.telefono.text.toString().let { empresaActualizada.telefono = it }
            binding.frase.text.toString().let { empresaActualizada.frase = it }
            binding.nombreEmpresa.text.toString().let { empresaActualizada.nombreEmpresa = it }
            binding.cif.text.toString().let { empresaActualizada.cif = it }
            binding.direccionFacturacion.text.toString().let { empresaActualizada.direccionFacturacion = it }
            binding.direccionFiscal.text.toString().let { empresaActualizada.direccionFiscal = it }
            binding.nombrePersonaFisica.text.toString().let { empresaActualizada.nombrePersona = it }
            binding.apellido1Persona.text.toString().let { empresaActualizada.apellido1Persona = it }
            binding.apellido2Persona.text.toString().let { empresaActualizada.apellido2Persona = it }
            binding.dniPersona.text.toString().let { empresaActualizada.dniPersona = it }

            fotoPerfil?.let { empresaActualizada.fotoPerfil = it }
            fotoFondo?.let { empresaActualizada.fotoFondo = it }

            if (empresaActualizada.telefono == "") {
                empresaActualizada.telefono = null
            }

            if (empresaActualizada.frase == "") {
                empresaActualizada.frase = null
            }

            if (empresaActualizada.nombreEmpresa == "") {
                empresaActualizada.nombreEmpresa = null
            }

            if (empresaActualizada.cif == "") {
                empresaActualizada.cif = null
            }

            if (empresaActualizada.direccionFacturacion == "") {
                empresaActualizada.direccionFacturacion = null
            }

            if (empresaActualizada.direccionFiscal == "") {
                empresaActualizada.direccionFiscal = null
            }

            if (empresaActualizada.nombrePersona == "") {
                empresaActualizada.nombrePersona = null
            }

            if (empresaActualizada.apellido1Persona == "") {
                empresaActualizada.apellido1Persona = null
            }

            if (empresaActualizada.apellido2Persona == "") {
                empresaActualizada.apellido2Persona = null
            }

            if (empresaActualizada.dniPersona == "") {
                empresaActualizada.dniPersona = null
            }

            if (empresaActualizada.fotoPerfil == "") {
                empresaActualizada.fotoPerfil = null
            }

            if (empresaActualizada.fotoFondo == "") {
                empresaActualizada.fotoFondo = null
            }

            WebServiceEmpresa.actualizarEmpresa(empresaActualizada, contexto, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) {
                        DialogMaker(contexto, requireContext().getString(R.string.error), getString(R.string.no_se_pudo_actualizar_empresa)).warningNoCustomActions()
                    } else {
                        currentUsuario = empresaActualizada
                        Log.d(":::, ", "EMPRESA ACTUALIZADA CORRECTAMENTE")
                        DialogMaker(contexto, getString(R.string.exito), getString(R.string.empresa_actualizada_correctamente)).infoCustomAccept("Aceptar", object: DialogAcceptCustomActionInterface {
                            override fun acceptButton() {
                                val fragmento = PerfilFragment.newInstance(currentUsuario, currentUsuario)
                                Utils.cambiarDeFragmentoGuardandoElAnterior((actividad as AppCompatActivity).supportFragmentManager, fragmento, "", R.id.frame_layout)
                            }
                        })
                    }

                }
            })

        } else {

            val personaAActualizar = currentUsuario as Persona

            binding.correo.text.toString().let { personaAActualizar.correo = it }
            binding.nickname.text.toString().let { personaAActualizar.nick = it }
            binding.telefono.text.toString().let { personaAActualizar.telefono = it }
            binding.frase.text.toString().let { personaAActualizar.frase = it }
            binding.nombrePersonaPersona.text.toString().let { personaAActualizar.nombre = it }
            binding.apellidoPersonaPersona.text.toString().let { personaAActualizar.apellido1 = it }
            binding.apellido2PersonaPersona.text.toString().let { personaAActualizar.apellido2 = it }
            binding.dniPersonaPersona.text.toString().let { personaAActualizar.dni = it }

            if (personaAActualizar.telefono == "") {
                personaAActualizar.telefono = null
            }

            if (personaAActualizar.frase == "") {
                personaAActualizar.frase = null
            }

            if (personaAActualizar.nombre == "") {
                personaAActualizar.nombre = null
            }

            if (personaAActualizar.apellido1 == "") {
                personaAActualizar.apellido1 = null
            }

            if (personaAActualizar.apellido2 == "") {
                personaAActualizar.apellido2 = null
            }

            if (personaAActualizar.dni == "") {
                personaAActualizar.dni = null
            }

            if (personaAActualizar.fotoPerfil == "") {
                personaAActualizar.fotoPerfil = null
            }

            if (personaAActualizar.fotoFondo == "") {
                personaAActualizar.fotoFondo = null
            }

            val idSexo = listaSexos.find { it.nombre == binding.spinnerSexoPersona.selectedItem.toString() }?.id
            val nombreSexo = binding.spinnerSexoPersona.selectedItem.toString()

            val sexo = Sexo(idSexo!!, nombreSexo)
            personaAActualizar.sexo = sexo

            fotoPerfil?.let { personaAActualizar.fotoPerfil = it }
            fotoFondo?.let { personaAActualizar.fotoFondo = it }

            Log.d(":::", personaAActualizar.toJsonObject().toString())

            WebServicePersona.actualizarPersona(personaAActualizar, contexto, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) {
                        DialogMaker(contexto, requireContext().getString(R.string.error), getString(R.string.no_se_pudo_actualizar_persona)).warningNoCustomActions()
                    } else {
                        currentUsuario = personaAActualizar
                        Log.d(":::, ", "USUARIO ACTUALIZADA CORRECTAMENTE")
                        DialogMaker(contexto, getString(R.string.exito), getString(R.string.empresa_actualizada_correctamente)).infoCustomAccept("Aceptar", object: DialogAcceptCustomActionInterface {
                            override fun acceptButton() {
                                val fragmento = PerfilFragment.newInstance(currentUsuario, currentUsuario)
                                Utils.cambiarDeFragmentoGuardandoElAnterior((actividad as AppCompatActivity).supportFragmentManager, fragmento, "", R.id.frame_layout)
                            }
                        })
                    }

                }
            })

        }

    }

    private fun pintarDatosGenerales() {

        fotoPerfil?.let {

            currentUsuario.fotoPerfil = it
            Utils.pintarFotoDePerfil(currentUsuario, binding.fotoPerfil, contexto)
            val color = Utils.getDominantColorInImageFromBase64(it)

            if (color == Color.BLACK) { // puede ocurrir que el base 64 este mal formado, por lo que devolverá negro y arruinará la targeta
                binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                binding.colorFondo2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            } else {
                binding.colorFondo.backgroundTintList = ColorStateList.valueOf(color)
                binding.colorFondo2.backgroundTintList = ColorStateList.valueOf(color)
            }

        }?: run {

            Utils.pintarFotoDePerfil(currentUsuario, binding.fotoPerfil, contexto)

            currentUsuario.fotoPerfil?.let {

                val color = Utils.getDominantColorInImageFromBase64(it)

                if (color == Color.BLACK) { // puede ocurrir que el base 64 este mal formado, por lo que devolverá negro y arruinará la targeta
                    binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                    binding.colorFondo2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                } else {
                    binding.colorFondo.backgroundTintList = ColorStateList.valueOf(color)
                    binding.colorFondo2.backgroundTintList = ColorStateList.valueOf(color)
                }

            }?: kotlin.run { // si no tiene foto lo pongo de blanco
                binding.colorFondo.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                binding.colorFondo2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }

        }

        // foto de fondo

        fotoFondo?.let {
            Utils.putBase64ImageIntoImageView(binding.fotoFondo, it, contexto)
        }?: run {
            currentUsuario.fotoFondo?.let {
                Utils.putBase64ImageIntoImageView(binding.fotoFondo, it, contexto)
            }
        }



        binding.correo.setText(currentUsuario.correo)

        binding.nickname.setText(currentUsuario.nick)

        binding.telefono.setText(currentUsuario.telefono)

        binding.frase.setText(currentUsuario.frase)

    }

    private fun pintarDatosEmpresa() {

        val empresa = currentUsuario as Empresa

        binding.nombreEmpresa.setText(empresa.nombreEmpresa)

        binding.cif.setText(empresa.cif)

        binding.direccionFacturacion.setText(empresa.direccionFacturacion)

        binding.nombrePersonaFisica.setText(empresa.nombrePersona)

        binding.apellido1Persona.setText(empresa.apellido1Persona)

        binding.apellido2Persona.setText(empresa.apellido2Persona)

        binding.dniPersona.setText(empresa.dniPersona)

        Animations.mostrarVistaSuavemente(binding.datosEmpresa)
    }

    private fun pintarDatosPersona() {

        val persona = currentUsuario as Persona

        binding.nombrePersonaPersona.setText(persona.nombre)

        binding.apellidoPersonaPersona.setText(persona.apellido1)

        binding.apellido2PersonaPersona.setText(persona.apellido2)

        WebServiceSexo.getAllSexos(contexto, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    Toast.makeText(contexto, "Error al obtener los sexos del servidor.", Toast.LENGTH_SHORT).show()
                } else {
                    val sexos = any as SexoWrapper
                    listaSexos = sexos
                    sexos.sortedBy { it.id }
                    val adapter = ArrayAdapter(contexto, android.R.layout.simple_spinner_item, sexos.map{ s -> s.nombre })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSexoPersona.adapter = adapter
                    binding.spinnerSexoPersona.setSelection(sexos.indexOf(persona.sexo))
                }

            }
        })

        if (persona.fechaNacimiento.toString() != "null") binding.fechaNacimientoPersona.setText(persona.fechaNacimiento.toString())

        binding.dniPersonaPersona.setText(persona.dni)

        Animations.mostrarVistaSuavemente(binding.datosPersona)
    }

    // Busco la imagen en la galeria
    private fun buscarImagenEnGaleria() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, REQUEST_GALLERY)
    }

    // la recibo y la pinto a voluntad
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY){
            val imageUri = data?.data

            imageUri?.let {

                try {

                    // PEDAZO DE CHAPUZA
                    if (isFromProfilePic) {
                        binding.fotoPerfil.setImageURI(imageUri)
                        fotoPerfil = binding.fotoPerfil.toBase64()
                        Utils.pintarFotoDePerfilDirectamente(currentUsuario, fotoPerfil!!, binding.fotoPerfil, contexto)
                    } else {
                        binding.fotoFondo.setImageURI(imageUri)
                        fotoFondo = binding.fotoFondo.toBase64()
                        Utils.putBase64ImageIntoImageView(binding.fotoFondo, fotoFondo!!, contexto)
                    }

                } catch (e: RuntimeException) {
                    Toast.makeText(contexto, getString(R.string.error_al_cargar_imagen), Toast.LENGTH_SHORT).show()
                    Utils.pintarFotoDePerfil(currentUsuario, binding.fotoPerfil, contexto)
                    Utils.enviarRegistroDeErrorABBDD(
                        context = contexto,
                        stacktrace = e.message.toString(),
                    )
                }

            }

        }

    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(actividad)
        Utils.ocultarElementosUI(actividad)
    }

    companion object {
        @JvmStatic
        fun newInstance(currentUsuario: Usuario) =
            UserSettingsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, currentUsuario)
                }
            }
    }
}