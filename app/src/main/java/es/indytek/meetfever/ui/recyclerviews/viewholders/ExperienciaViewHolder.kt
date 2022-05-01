package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderExperienciaBinding
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.utils.Utils

class ExperienciaViewHolder(

    view: View,
    private val binding: ViewholderExperienciaBinding

) : RecyclerView.ViewHolder(view) {

    // T\odo lo que compone a una experiencia gráficamente va aqui
    fun bind(objeto: Experiencia) {

        binding.nombreTopExperiencia.text = objeto.titulo

        val foto = objeto.foto
        foto?.let {
            Utils.putBase64ImageIntoImageView(binding.imagenTopExperiencia, it, itemView.context)
        }
    }

}