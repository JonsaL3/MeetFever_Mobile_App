package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.graphics.PorterDuff
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ViewholderExperienciaBinding
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.experiencia.ExperienciaFragment
import es.indytek.meetfever.utils.Utils

class ExperienciaViewHolder(

    view: View,
    private val binding: ViewholderExperienciaBinding,
    private val usuario: Usuario

) : RecyclerView.ViewHolder(view) {

    // T\odo lo que compone a una experiencia gráficamente va aqui
    fun bind(objeto: Experiencia) {

        binding.nombreTopExperiencia.text = objeto.titulo

        val foto = objeto.foto
        foto?.let {
            Utils.putBase64ImageIntoImageViewWithPlaceholder(binding.imagenTopExperiencia, it, itemView.context, R.drawable.ic_default_experiencie_true_tone)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageView(binding.imagenTopExperiencia, R.drawable.ic_default_experiencie_true_tone, itemView.context)
        }

        binding.viewholderExperiencia.setOnClickListener {
            onClick(objeto)
        }
    }

    private fun onClick(objeto: Experiencia) {
        val activity = itemView.context as AppCompatActivity
        val fragmento = ExperienciaFragment.newInstance(usuario, objeto)
        Utils.cambiarDeFragmentoGuardandoElAnterior(activity.supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

}