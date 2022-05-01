package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderPersonaBinding
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.ui.recyclerviews.viewholders.PersonaViewHolder

class PersonaRecyclerViewAdapter (

    private val lista: List<Persona>

) : RecyclerView.Adapter<PersonaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val binding = ViewholderPersonaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonaViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        val valor: Persona = lista[position]
        holder.bind(valor)
    }

    override fun getItemCount(): Int = lista.size

}