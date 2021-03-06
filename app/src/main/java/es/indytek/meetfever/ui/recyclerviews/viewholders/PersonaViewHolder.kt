package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ViewholderPersonaBinding
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

class PersonaViewHolder(

    private val view: View,
    private val binding: ViewholderPersonaBinding,
    private val currentUsuario: Usuario

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Persona) {
        // pongo el texto
        binding.texto.text = objeto.nick.toString()

        // Si tiene foto de perfil, la pinto
        Utils.pintarFotoDePerfil(objeto, binding.imagen, view.context)

        if (Utils.isFamous(objeto)) {
            Animations.mostrarVistaSuavemente(binding.famousRectangle)
        }

        binding.viewholderPersona.setOnClickListener {
            onClick(objeto)
        }

    }

    // al hacer click necesito la lista de opiniones para pintarla
    private fun onClick(persona: Persona) {
        val activity = view.context as AppCompatActivity
        val fragmento = PerfilFragment.newInstance(persona, currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(activity.supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

}