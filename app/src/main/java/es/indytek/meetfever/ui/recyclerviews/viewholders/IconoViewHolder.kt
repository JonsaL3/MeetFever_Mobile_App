package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.graphics.PorterDuff
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.databinding.ViewholderIconoBinding
import es.indytek.meetfever.databinding.ViewholderPersonaBinding
import es.indytek.meetfever.models.emoticono.Emoticono
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.models.usuario.UsuarioWrapper
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Utils

class IconoViewHolder(

    private val view: View,
    private val binding: ViewholderIconoBinding,

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Emoticono, listaEmoticonos: List<Emoticono>) {

        try {
            Utils.putBase64ImageIntoImageView(binding.imagenEmoticono, objeto.emoji, view.context)
        } catch (e: IllegalArgumentException) {
            Log.e(":::", "Parece que alguien dejó un emoji de prueba que no se puede utilizar....")
        }

        if (objeto.isSelected) {
            binding.imagenEmoticono.setColorFilter(view.context.getColor(R.color.rosa_meet), PorterDuff.Mode.SRC_ATOP)
        }

    }

}