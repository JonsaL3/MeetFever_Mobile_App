package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderExperienciaBusquedaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.viewholders.EmpresaBusquedaViewHolder
import es.indytek.meetfever.ui.recyclerviews.viewholders.ExperienciaBusquedaViewHolder
import es.indytek.meetfever.utils.FromViewHolderToParent

class ExperienciaBusquedaRecyclerViewAdapter(

    private val lista: List<Experiencia>,
    private val currentUsuario: Usuario,
    private val passData: FromViewHolderToParent

) : RecyclerView.Adapter<ExperienciaBusquedaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienciaBusquedaViewHolder {
        val binding = ViewholderExperienciaBusquedaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExperienciaBusquedaViewHolder(binding.root, binding, currentUsuario, passData)
    }

    override fun onBindViewHolder(holder: ExperienciaBusquedaViewHolder, position: Int) {
        val valor: Experiencia = lista[position]
        holder.bind(valor)
    }

    override fun getItemCount(): Int = lista.size

}