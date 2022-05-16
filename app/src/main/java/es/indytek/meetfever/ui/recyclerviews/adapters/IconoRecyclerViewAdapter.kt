package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderIconoBinding
import es.indytek.meetfever.databinding.ViewholderPersonaBinding
import es.indytek.meetfever.models.emoticono.Emoticono
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.ui.recyclerviews.viewholders.IconoViewHolder
import es.indytek.meetfever.ui.recyclerviews.viewholders.PersonaViewHolder

class IconoRecyclerViewAdapter (

    private val lista: List<Emoticono>

) : RecyclerView.Adapter<IconoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconoViewHolder {
        val binding = ViewholderIconoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IconoViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: IconoViewHolder, position: Int) {
        val valor: Emoticono = lista[position]
        holder.bind(valor, lista)
    }

    override fun getItemCount(): Int = lista.size

}