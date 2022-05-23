package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ViewholderSeguidorSeguidoBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Utils

class SeguidorSeguidoViewHolder(

    private val view: View,
    private val binding: ViewholderSeguidorSeguidoBinding,
    private val currentUsuario: Usuario

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Usuario) {

        binding.nickname.text = objeto.nick
        binding.frase.text = objeto.frase

        val foto = objeto.fotoPerfil
        foto?.let {
            Utils.putBase64ImageIntoImageViewCircularWithPlaceholder(binding.fotoPerfil, it, itemView.context, R.drawable.ic_default_user_image)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageViewCircular(binding.fotoPerfil, R.drawable.ic_default_experience, itemView.context)
        }

        // pinto la targeta del fondo en función del color principal del usuario
        currentUsuario.fotoPerfil?.let {
            val color = Utils.getDominantColorInImageFromBase64(it)
            binding.viewholderSeguidorSeguido.backgroundTintList = ColorStateList.valueOf(color)
        }?: kotlin.run {
            binding.viewholderSeguidorSeguido.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }

    }

}