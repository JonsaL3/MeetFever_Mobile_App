package es.indytek.meetfever.ui.fragments.secondaryfragments.experiencia

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paypal.checkout.paymentbutton.PayPalButton
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceExperiencia
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentExperienciaBinding
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.ExplorerFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Constantes
import es.indytek.meetfever.utils.DialogAcceptCustomActionInterface
import es.indytek.meetfever.utils.DialogMaker
import es.indytek.meetfever.utils.Utils
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.math.roundToLong


private const val ARG_PARAM1 = "usuario"
private const val ARG_PARAM2 = "experiencia"

class ExperienciaFragment : Fragment() {

    // Bindeo de las vistas
    private lateinit var binding: FragmentExperienciaBinding

    // datos que necesito
    private lateinit var usuario: Usuario
    private lateinit var experiencia: Experiencia

    private var entradasVendidas: Int = 0

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(ARG_PARAM1) as Usuario
            experiencia = it.getSerializable(ARG_PARAM2) as Experiencia
        }

        entradasVendidas = experiencia.numeroEntradas

        Log.d(":::", "CVVVV -> " + experiencia.aforo.toString())
        getNumeroEntradas()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExperienciaBinding.inflate(inflater, container, false)

        // pinto todos los elementos que necesito en pantalla
        pintar()

        // arranco los listeners

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.ocultarElementosUI(requireActivity())

        binding.nEntradas.text = "1"

        arrancarListeners()

        dialog = DialogMaker(requireContext()).infoLoading()

    }

    // arranco los listeners
    @SuppressLint("SetTextI18n")
    private fun arrancarListeners() {
        binding.botonVerEmpresaTexto.setOnClickListener {
            val fragmento = PerfilFragment.newInstance (
                usuario = experiencia.empresa,
                currentUsuario = usuario
            )
            Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
        }

        binding.botonPagar.setOnClickListener {

            //TODO, falta verificar que una empresa no pueda comprar entradas.

            if (usuario is Persona){

                if(!(usuario as Persona).nombre.isNullOrEmpty() ||
                    !(usuario as Persona).apellido1.isNullOrEmpty() ||
                    !(usuario as Persona).apellido2.isNullOrEmpty() ||
                    !(usuario as Persona).dni.isNullOrEmpty()
                ){

                    val entradasSeleccionadas = binding.nEntradas.text.toString().toInt()
                    val entradasPosibles = entradasSeleccionadas + entradasVendidas

                    if(
                        entradasPosibles <= experiencia.aforo
                    ){
                        val fragmento = PasarelaDePagoFragment.newInstance(usuario as Persona, binding.nEntradas.text.toString().toInt(), experiencia)
                        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
                    }else{

                        DialogMaker(
                            requireContext(),
                            getString(R.string.error_tittle),
                            getString(R.string.error_no_entradas)
                        ).infoNoCustomActions()
                    }
                }else{

                    DialogMaker(
                        requireContext(),
                        getString(R.string.error_tittle),
                        getString(R.string.data_error_profile_message)
                    ).infoNoCustomActions()
                }
            }else{
                DialogMaker(
                    requireContext(),
                    getString(R.string.error_tittle),
                    getString(R.string.account_with_no_permission)
                ).infoNoCustomActions()
            }

        }

        binding.botonRestarEntradas.setOnClickListener {
            if(binding.nEntradas.text.toString().toInt() != 1){
                binding.nEntradas.text = (binding.nEntradas.text.toString().toInt() - 1).toString()
                binding.precioCifra.text = ((experiencia.precio * Constantes.COMISION_MEET_FEVER * Constantes.IVA).roundToInt() * binding.nEntradas.text.toString().toInt()).toString() + " €"
            }
        }

        binding.botonSumarEntradas.setOnClickListener {

            val entradasSeleccionadas = binding.nEntradas.text.toString().toInt()
            val entradasPosibles = entradasSeleccionadas + entradasVendidas

            if(entradasPosibles < experiencia.aforo){
                binding.nEntradas.text = (binding.nEntradas.text.toString().toInt() + 1).toString()
                binding.precioCifra.text = ((experiencia.precio * Constantes.COMISION_MEET_FEVER * Constantes.IVA).roundToInt() * binding.nEntradas.text.toString().toInt()).toString() + " €"
            }
        }


    }

    // funcion que se encarga de pintar to-do
    private fun pintar() {
        pintarExperiencia()
    }

    // pinto los datos de la experiencia
    private fun pintarExperiencia() {
        // pinto los textos de la experiencia
        binding.tituloExperiencia.text = experiencia.titulo
        binding.descripcionExperiencia.text = experiencia.descripcion
        binding.fechaExperiencia.text = experiencia.fechaCelebracion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")).toString()

        // calculo el precio mas el iba mas la comisión
        ((experiencia.precio * Constantes.COMISION_MEET_FEVER * Constantes.IVA).roundToInt().toString() + " €").also { binding.precioCifra.text = it }

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

    private fun getNumeroEntradas(){

        WebServiceExperiencia.conseguirNumeroEntradas(
            experiencia,
            requireContext(),
            object: WebServiceGenericInterface{
                override fun callback(any: Any) {

                    if (any !is Int){

                        dialog.dismiss()

                        entradasVendidas = any.toString().split(":")[1].split("}")[0].toInt()

                        binding.entradasCifra.text = getString(R.string.Aforo, entradasVendidas.toString(), experiencia.aforo.toString())
                    }else{

                        DialogMaker(
                            requireContext(),
                            getString(R.string.error_tittle),
                            getString(R.string.data_error_message)
                        ).infoCustomAccept(
                            customAcceptText = getString(R.string.back_to_home),
                            customAccept = object: DialogAcceptCustomActionInterface{
                                override fun acceptButton() {

                                    val fragmento = ExplorerFragment.newInstance(usuario)
                                    Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)

                                    val fragmentTransaction = fragmentManager?.beginTransaction()
                                    fragmentTransaction?.setCustomAnimations(
                                        R.anim.anim_fade_in,
                                        R.anim.anim_fade_out,
                                    )
                                    fragmentTransaction?.replace(R.id.frame_layout, fragmento, "")
                                    fragmentTransaction?.commit()

                                }
                            }
                        )

                    }

                }
            }
        )

    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(requireActivity())

        getNumeroEntradas()
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