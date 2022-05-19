package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderSeguidorSeguidoBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.viewholders.SeguidorSeguidoViewHolder

class SeguidorSeguidoRecyclerViewAdapter (

    private val lista: List<Usuario>

) : RecyclerView.Adapter<SeguidorSeguidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeguidorSeguidoViewHolder {
        val binding = ViewholderSeguidorSeguidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeguidorSeguidoViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: SeguidorSeguidoViewHolder, position: Int) {
        val valor: Usuario = lista[position]
        holder.bind(valor)
    }

    override fun getItemCount(): Int = lista.size

}