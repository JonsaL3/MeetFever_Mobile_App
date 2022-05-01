package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderPersonaBinding
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.utils.Utils

class PersonaViewHolder(

    view: View,
    private val binding: ViewholderPersonaBinding

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Persona) {
        // pongo el texto
        binding.texto.text = objeto.nick.toString()

        // Si tiene foto de perfil, la pinto
        val foto = objeto.fotoPerfil
        foto?.let {
            Utils.putBase64ImageIntoImageViewCircular(binding.imagen, it, itemView.context)
        }

    }

}