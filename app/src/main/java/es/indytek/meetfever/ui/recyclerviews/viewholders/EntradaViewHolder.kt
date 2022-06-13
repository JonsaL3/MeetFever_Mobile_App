package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ViewholderEntradaBinding
import es.indytek.meetfever.databinding.ViewholderSeguidorSeguidoBinding
import es.indytek.meetfever.models.entrada.Entrada
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Utils

class EntradaViewHolder(

    private val view: View,
    private val binding: ViewholderEntradaBinding

) : RecyclerView.ViewHolder(view) {

    fun bind(entrada: Entrada, experiencia: Experiencia, position: Int) {

        binding.textViewNEntrada.text  = itemView.context.getString(R.string.n_entrada_t, (position+1).toString())

        experiencia.foto?.let {
            Utils.putBase64ImageIntoImageViewWithPlaceholder(binding.experienciaFoto, it, itemView.context, R.drawable.ic_default_experiencie_true_tone)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageView(binding.experienciaFoto, R.drawable.ic_default_experiencie_true_tone, itemView.context)
        }

        if (position == 0){
            if (entrada.nombre != "null") binding.nombreTitular.setText(entrada.nombre)
            if (entrada.apellido1 != "null") binding.apellido1Titular.setText(entrada.apellido1)
            if (entrada.apellido2 != "null") binding.apellido2Titular.setText(entrada.apellido2)
            if (entrada.dni != "null") binding.dniTitular.setText(entrada.dni)
        }

        val textWatcherNombre = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0.toString().isNotEmpty()){
                    entrada.nombre = p0.toString()
                }
            }

        }

        val textWatcherApellido1 = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0.toString().isNotEmpty()){
                    entrada.apellido1 = p0.toString()
                }
            }

        }

        val textWatcherApellido2 = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0.toString().isNotEmpty()){
                    entrada.apellido2 = p0.toString()
                }
            }

        }

        val textWatcherDni = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0.toString().isNotEmpty()){
                    entrada.dni = p0.toString()
                }
            }

        }

        binding.nombreTitular.addTextChangedListener(textWatcherNombre)
        binding.apellido1Titular.addTextChangedListener(textWatcherApellido1)
        binding.apellido2Titular.addTextChangedListener(textWatcherApellido2)
        binding.dniTitular.addTextChangedListener(textWatcherDni)

    }

}