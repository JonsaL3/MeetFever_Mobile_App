package es.indytek.meetfever.ui.fragments.secondaryfragments.usersettings

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceSexo
import es.indytek.meetfever.databinding.FragmentUserSettingsBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.sexo.SexoWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.utilityfragments.CameraFragment
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Extensions.toBase64String
import es.indytek.meetfever.utils.Utils
import java.io.File

private const val ARG_PARAM1 = "currentUsuario"

class UserSettingsFragment : Fragment() {

    private lateinit var binding: FragmentUserSettingsBinding
    private lateinit var currentUsuario: Usuario
    private var isFromProfilePic = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
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

        cargarListeners()

        cargarDatosActualesDelUsuario()

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
                    currentUsuario.fotoPerfil = base64FromFile
                    Utils.pintarFotoDePerfilDirectamente(currentUsuario, base64FromFile, binding.fotoPerfil, requireContext())
                } else {
                    currentUsuario.fotoFondo = base64FromFile
                    Utils.putBase64ImageIntoImageView(binding.fotoFondo, base64FromFile, requireContext())
                }

            }

        }
    }

    private fun irAlFragmentoDeLaCamara() {
        val fragemnt = CameraFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager, fragemnt, "", R.id.frame_layout)
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

            WebServiceEmpresa.actualizarEmpresa(empresaActualizada, requireContext(), object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) {
                        // TODO error
                    } else {

                        Log.d(":::, ", "EMPRESA ACTUALIZADA CORRECTAMENTE")

                    }

                }
            })


        } else {

        }

    }

    private fun pintarDatosGenerales() {

        // foto de perfil
        Utils.pintarFotoDePerfil(currentUsuario, binding.fotoPerfil, requireContext())

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

        // foto de fondo
        currentUsuario.fotoFondo?.let {
            Utils.putBase64ImageIntoImageView(binding.fotoFondo, it, requireContext())
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

        WebServiceSexo.getAllSexos(requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERRROR
                } else {
                    val sexos = any as SexoWrapper
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sexos)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSexoPersona.adapter = adapter
                    binding.spinnerSexoPersona.setSelection(sexos.indexOf(persona.sexo))
                }

            }
        })

        binding.fechaNacimientoPersona.setText(persona.fechaNacimiento.toString())

        binding.dniPersonaPersona.setText(persona.dni)

        Animations.mostrarVistaSuavemente(binding.datosPersona)
    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(requireActivity())
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