package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderOpinionBinding
import es.indytek.meetfever.models.opinion.Opinion
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.viewholders.OpinionViewHolder

class OpinionRecyclerViewAdapter(

    private val lista: List<Opinion>,
    private val claseOrigen: Class<*>,
    private val currentUsuario: Usuario

) : RecyclerView.Adapter<OpinionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpinionViewHolder {
        val binding = ViewholderOpinionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OpinionViewHolder(binding.root, binding, claseOrigen, currentUsuario)
    }

    override fun onBindViewHolder(holder: OpinionViewHolder, position: Int) {
        val valor: Opinion = lista[position]
        holder.bind(valor)
    }

    override fun getItemCount(): Int = lista.size

}