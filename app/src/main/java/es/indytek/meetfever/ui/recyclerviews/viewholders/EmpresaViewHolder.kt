package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderEmpresaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.utils.Utils

class EmpresaViewHolder(

    view: View,
    private val binding: ViewholderEmpresaBinding

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Empresa) {
        // pongo el texto
        binding.texto.text = objeto.nombreEmpresa.toString()

        // Si tiene foto de perfil, la pinto
        val foto = objeto.fotoPerfil
        foto?.let {
            Utils.putBase64ImageIntoImageView(binding.imagen, it, itemView.context)
        }

    }

}