package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderExperienciaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.ui.recyclerviews.viewholders.EmpresaViewHolder
import es.indytek.meetfever.ui.recyclerviews.viewholders.ExperienciaViewHolder

class ExperienciaRecyclerViewAdapter(

    private val lista: List<Experiencia>

) : RecyclerView.Adapter<ExperienciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienciaViewHolder {
        val binding = ViewholderExperienciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienciaViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ExperienciaViewHolder, position: Int) {
        val valor: Experiencia = lista[position]
        holder.bind(valor)
    }

    override fun getItemCount(): Int = lista.size

}