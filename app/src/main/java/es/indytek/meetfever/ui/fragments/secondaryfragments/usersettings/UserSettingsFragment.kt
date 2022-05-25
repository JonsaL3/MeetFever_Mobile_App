package es.indytek.meetfever.ui.fragments.secondaryfragments.usersettings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceSexo
import es.indytek.meetfever.databinding.FragmentUserSettingsBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.sexo.SexoWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "currentUsuario"

class UserSettingsFragment : Fragment() {

    private lateinit var binding: FragmentUserSettingsBinding

    private lateinit var currentUsuario: Usuario

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
        cargarDatosActualesDelUsuario()

        Utils.ocultarElementosUI(requireActivity())
    }

    private fun cargarDatosActualesDelUsuario() {

        pintarDatosGenerales()

        if (currentUsuario is Empresa) {
            pintarDatosEmpresa()
        } else if (currentUsuario is Persona) {
            pintarDatosPersona()
        }

    }

    private fun pintarDatosGenerales() {

        // foto de perfil
        Utils.pintarFotoDePerfil(currentUsuario, binding.fotoPerfil, requireContext())

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