package es.indytek.meetfever.ui.fragments.secondaryfragments.experiencia

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentExperienciaBinding
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Constantes
import es.indytek.meetfever.utils.Utils
import java.time.LocalTime

private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "experiencia"

class ExperienciaFragment : Fragment() {

    // Bindeo de las vistas
    private lateinit var binding: FragmentExperienciaBinding

    // datos que necesito
    private lateinit var usuario: Usuario
    private lateinit var experiencia: Experiencia

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
            experiencia = it.getSerializable(ARG_PARAM2) as Experiencia
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExperienciaBinding.inflate(inflater, container, false)

        // pinto todos los elementos que necesito en pantalla
        pintar()

        return binding.root
    }

    // funcion que se encarga de pintar todo
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarExperiencia()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.mostrarVistaSuavemente(binding.experienicaContainer, 300)
        },100)
    }

    // pinto los datos de la experiencia
    private fun pintarExperiencia() {
        binding.tituloExperiencia.text = experiencia.titulo
        binding.descripcionExperiencia.text = experiencia.descripcion
        binding.fechaExperiencia.text = experiencia.fechaCelebracion.format("dd:MM:yyyy a las HH:mm").toString()
        binding.precioCifra.text = (experiencia.precio * Constantes.COMISION_MEET_FEVER * Constantes.IVA).toString() + "€"
        experiencia.foto?.let {
            Utils.putBase64ImageIntoImageView(binding.imagenExperiencia, experiencia.foto!!, requireContext())
        }
    }

    // pinta los datos del tio que inició sesión
    private fun pintarNombreDelUsuarioQueInicioSesion() {

        // quiero saber que hora es para ver si es de dia o de noche
        val hora = LocalTime.now()

        // Le pongo un mensaje u otro en funcion de la hora
        if (hora.hour >= 18 || hora.hour <= 6) {
            "¡${this.getString(R.string.buenos_dias)} ${usuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
        } else {
            "¡${this.getString(R.string.buenas_noches)} ${usuario.nick}!".also {
                binding.textoBuenosDias.text = it
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario, experiencia: Experiencia) =
            ExperienciaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                    putSerializable(ARG_PARAM2, experiencia)
                }
            }
    }
}