package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.databinding.ViewholderPersonaBinding
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.models.usuario.UsuarioWrapper
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Utils

class PersonaViewHolder(

    private val view: View,
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

        binding.viewholderPersona.setOnClickListener {
            onClick(objeto)
        }

    }

    // al hacer click necesito la lista de opiniones para pintarla
    private fun onClick(persona: Persona) {

        val activity = view.context as AppCompatActivity
        var opiniones: OpinionWrapper

        // pido la opinion
        WebServiceOpinion.obtenerOpinionPorIdAutor(persona, itemView.context, object: WebServiceGenericInterface{
            override fun callback(any: Any) {

                if (any == 0) {
                    opiniones = OpinionWrapper()
                } else {
                    opiniones = any as OpinionWrapper
                }

                val fragmento = PerfilFragment.newInstance(persona, opiniones)
                activity.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
            }
        })

    }

}