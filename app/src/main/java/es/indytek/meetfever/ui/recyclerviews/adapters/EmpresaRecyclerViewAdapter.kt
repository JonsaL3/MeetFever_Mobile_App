package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderEmpresaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.ui.recyclerviews.viewholders.EmpresaViewHolder

class EmpresaRecyclerViewAdapter(

    private val lista: List<Empresa>

) : RecyclerView.Adapter<EmpresaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val binding = ViewholderEmpresaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmpresaViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val valor: Empresa = lista[position]
        holder.bind(valor)
    }

    override fun getItemCount(): Int = lista.size

}