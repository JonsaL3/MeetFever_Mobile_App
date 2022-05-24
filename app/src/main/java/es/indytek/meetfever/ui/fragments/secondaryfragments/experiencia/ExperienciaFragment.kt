package es.indytek.meetfever.ui.fragments.secondaryfragments.experiencia

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentExperienciaBinding
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Constantes
import es.indytek.meetfever.utils.Utils
import java.math.BigDecimal
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "experiencia"

class ExperienciaFragment : Fragment() {

    // Bindeo de las vistas
    private lateinit var binding: FragmentExperienciaBinding

    // datos que necesito
    private lateinit var usuario: Usuario
    private lateinit var experiencia: Experiencia


    // Datos relacionados con paypal
    val clientKey: String = "AQkdu2M1YNJLU-M13rYdku9FcWHdam7ELImmxDyAJBVUCFUzYbB-bHcW7izS_RfNrv-ZW49uvOPNrR9F"
    val PAYPAL_REQUEST_CODE = 123

    // Paypal Configuration Object
    private val config = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // on below line we are passing a client id.
        .clientId(clientKey)

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

        // arranco los listeners
        arrancarListeners()

        return binding.root
    }

    // arranco los listeners
    private fun arrancarListeners() {
        binding.botonVerEmpresaTexto.setOnClickListener {
            val fragmento = PerfilFragment.newInstance (
                usuario = experiencia.empresa,
                currentUsuario = usuario
            )
            Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
        }

        binding.botonPagar.setOnClickListener {
            obtenerPago()
        }

    }

    // funcion que se encarga de pintar to-do
    private fun pintar() {
        pintarNombreDelUsuarioQueInicioSesion()
        pintarExperiencia()
    }

    // pinto los datos de la experiencia
    private fun pintarExperiencia() {
        // pinto los textos de la experiencia
        binding.tituloExperiencia.text = experiencia.titulo
        binding.descripcionExperiencia.text = experiencia.descripcion
        binding.fechaExperiencia.text = experiencia.fechaCelebracion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")).toString()

        // calculo el precio mas el iba mas la comisión
        ((experiencia.precio * Constantes.COMISION_MEET_FEVER * Constantes.IVA).roundToInt().toString() + "€").also { binding.precioCifra.text = it }

        // pinto la imagen de la experiencia
        val foto = experiencia.foto
        foto?.let {
            Utils.putBase64ImageIntoImageViewCircularWithPlaceholder(binding.imagenExperiencia, it, requireContext(), R.drawable.ic_default_experiencie_true_tone)
            binding.degradadoExperiencia.setColorFilter(Utils.getDominantColorInImageFromBase64(foto), PorterDuff.Mode.SRC_ATOP)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageView(binding.imagenExperiencia, R.drawable.ic_default_experiencie_true_tone, requireContext())
        }

        // pinto la imagen de la empresa que organiza la experiencia
        Utils.pintarFotoDePerfil(experiencia.empresa, binding.imagenEmpresaExperiencia, requireContext())

        // pinto el degradado de la imagen en consecuencia
        experiencia.empresa.fotoPerfil?.let {

            val color = Utils.getDominantColorInImageFromBase64(it)
            binding.degradadoEmpresaExperiencia.backgroundTintList = ColorStateList.valueOf(color)
        }?: kotlin.run {
            binding.degradadoEmpresaExperiencia.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        }

        // lo mismo con el degradado de la experiencia
        experiencia.foto?.let {
            val color = Utils.getDominantColorInImageFromBase64(it)
            binding.degradadoExperiencia.backgroundTintList = ColorStateList.valueOf(color)
        }?: kotlin.run {
            binding.degradadoExperiencia.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
        }

        // pinto el nombre de la empresa que organiza la experiencia
        binding.botonVerEmpresaTexto.text = experiencia.empresa.nombreEmpresa

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

    private fun obtenerPago() {

        // Creating a paypal payment on below line.

        // Creating a paypal payment on below line.
        val payment = PayPalPayment(
            BigDecimal(10), "USD", "Course Fees",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        // Creating Paypal Payment activity intent

        // Creating Paypal Payment activity intent
        val intent = Intent(requireContext(), PaymentActivity::class.java)

        //putting the paypal configuration to the intent

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        // Putting paypal payment to the intent

        // Putting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        // Starting the intent activity for result
        // the request code will be used on the method onActivityResult

        // Starting the intent activity for result
        // the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(requireActivity())
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