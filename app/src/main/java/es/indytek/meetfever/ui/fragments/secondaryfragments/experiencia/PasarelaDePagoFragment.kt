package es.indytek.meetfever.ui.fragments.secondaryfragments.experiencia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentPasarelaDePagoBinding
import es.indytek.meetfever.models.persona.Persona
import java.time.LocalDateTime


private const val ARG_PARAM1 = "currentUsuario"
private const val ARG_PARAM2 = "numeroDeEntradas"
class PasarelaDePagoFragment : Fragment() {

    // datos que necesito
    private lateinit var currentUsuario: Persona
    private var numeroDeEntradas: Int = 0

    private lateinit var binding: FragmentPasarelaDePagoBinding

    private lateinit var fecha: LocalDateTime

    private lateinit var arrayEditTextViewNombre: ArrayList<TextView>
    private lateinit var arrayEditTextViewApellido1: ArrayList<TextView>
    private lateinit var arrayEditTextViewApellido2: ArrayList<TextView>
    private lateinit var arrayEditTextViewDni: ArrayList<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Persona
            numeroDeEntradas = it.getSerializable(ARG_PARAM2) as Int
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPasarelaDePagoBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fecha = LocalDateTime.now()

        mostrarFormulario()
    }

    private fun mostrarFormulario(){

        val parentLayout = binding.parentLayout

        var childLayout = LinearLayout(requireContext())
        childLayout.orientation = LinearLayout.VERTICAL
        childLayout.background = requireContext().getDrawable(R.drawable.ic_custom_boton)
        childLayout.setPadding(10,10,10,10)

        var editTextNombre = EditText(requireContext())
        editTextNombre.setTextColor(resources.getColor(R.color.white))
        editTextNombre.setText(currentUsuario.nombre)

        childLayout.addView(editTextNombre)

        var editTextApellido1 = EditText(requireContext())
        editTextApellido1.setTextColor(resources.getColor(R.color.white))
        editTextApellido1.setText(currentUsuario.apellido1)

        childLayout.addView(editTextApellido1)

        var editTextApellido2 = EditText(requireContext())
        editTextApellido2.setTextColor(resources.getColor(R.color.white))
        editTextApellido2.setText(currentUsuario.apellido2)

        childLayout.addView(editTextApellido2)

        var editTextFechaNac = EditText(requireContext())
        editTextFechaNac.setTextColor(resources.getColor(R.color.white))
        editTextFechaNac.setText(currentUsuario.fechaNacimiento.toString())

        childLayout.addView(editTextFechaNac)

        parentLayout.addView(childLayout)

        for (i in 2..numeroDeEntradas){

            childLayout = LinearLayout(requireContext())
            childLayout.orientation = LinearLayout.VERTICAL
            childLayout.background = requireContext().getDrawable(R.drawable.ic_custom_boton)
            childLayout.setPadding(10,10,10,10)

            editTextNombre = EditText(requireContext())
            editTextNombre.setTextColor(resources.getColor(R.color.white))
            editTextNombre.hint = "Nombre titular $i"
            editTextNombre.setHintTextColor(resources.getColor(R.color.gris_textos))

            childLayout.addView(editTextNombre)

            editTextApellido1 = EditText(requireContext())
            editTextApellido1.setTextColor(resources.getColor(R.color.white))
            editTextApellido1.hint = "Primer apellido titular $i"
            editTextApellido1.setHintTextColor(resources.getColor(R.color.gris_textos))

            childLayout.addView(editTextApellido1)

            editTextApellido2 = EditText(requireContext())
            editTextApellido2.setTextColor(resources.getColor(R.color.white))
            editTextApellido2.hint = "Segundo apellido tituar $i"
            editTextApellido2.setHintTextColor(resources.getColor(R.color.gris_textos))

            childLayout.addView(editTextApellido2)

            editTextFechaNac = EditText(requireContext())
            editTextFechaNac.setTextColor(resources.getColor(R.color.white))
            editTextFechaNac.hint = "Fecha de nacimiento titular $i"
            editTextFechaNac.setHintTextColor(resources.getColor(R.color.gris_textos))

            childLayout.addView(editTextFechaNac)


            val layoutParamsChild = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParamsChild.setMargins(0,30,0,0)

            parentLayout.addView(childLayout, layoutParamsChild)

            val view = View(requireContext())
            view.setBackgroundColor(resources.getColor(R.color.gris_textos))

            val layoutParamsView = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
            )
            layoutParamsChild.setMargins(0,10,0,0)
            parentLayout.addView(view, layoutParamsView)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(currentUsuario: Persona, numeroDeEntradas: Int) =
            PasarelaDePagoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, currentUsuario)
                    putSerializable(ARG_PARAM2, numeroDeEntradas)
                }
            }
    }
}