package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderEmpresaBusquedaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.viewholders.EmpresaBusquedaViewHolder
import es.indytek.meetfever.utils.FromViewHolderToParent

class EmpresaBusquedaRecyclerViewAdapter(

    private val lista: List<Empresa>,
    private val currentUsuario: Usuario,
    private val passData: FromViewHolderToParent

) : RecyclerView.Adapter<EmpresaBusquedaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaBusquedaViewHolder {
        val binding = ViewholderEmpresaBusquedaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmpresaBusquedaViewHolder(binding.root, binding, currentUsuario, passData)
    }

    override fun onBindViewHolder(holder: EmpresaBusquedaViewHolder, position: Int) {
        val valor: Empresa = lista[position]
        holder.bind(valor)
    }

    override fun getItemCount(): Int = lista.size

}