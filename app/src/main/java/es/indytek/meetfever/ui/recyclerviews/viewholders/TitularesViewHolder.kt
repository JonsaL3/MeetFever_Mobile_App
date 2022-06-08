package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderTitularBinding
import es.indytek.meetfever.models.factura.Titular
import es.indytek.meetfever.models.usuario.Usuario

class TitularesViewHolder (

    private val view: View,
    private val binding: ViewholderTitularBinding,
    private val currentUsuario: Usuario

) : RecyclerView.ViewHolder(view) {

    fun bind(titular: Titular) {

        binding.nombreBeneficirio.text = titular.nombreTitular

        (titular.apellido1Titular + " " + titular.apellido2Titular).also {
            binding.apellidosBeneficiario.text = it
        }

    }

}