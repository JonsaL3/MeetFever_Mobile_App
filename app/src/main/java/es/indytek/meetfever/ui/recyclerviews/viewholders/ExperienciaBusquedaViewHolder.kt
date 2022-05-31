package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.graphics.PorterDuff
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ViewholderEmpresaBusquedaBinding
import es.indytek.meetfever.databinding.ViewholderExperienciaBusquedaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.FromViewHolderToParent
import es.indytek.meetfever.utils.Utils

class ExperienciaBusquedaViewHolder(

    private val view: View,
    private val binding: ViewholderExperienciaBusquedaBinding,
    private val currentUsuario: Usuario,
    private val passData: FromViewHolderToParent

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Experiencia) {
        binding.nombreExperiencia.text = objeto.titulo
        binding.descripcionExperiencia.text = objeto.descripcion
        binding.viewholderExperiencia.setOnClickListener { onClick(objeto) }
    }

    private fun onClick(experiencia: Experiencia) {

        val buscadorDeExperiencias = (view.context as AppCompatActivity).findViewById<FrameLayout>(R.id.selector_experiencia)
        val mostrarExperienciaSeleccionada = (view.context as AppCompatActivity).findViewById<LinearLayout>(R.id.experiencia_seleccionada)
        val imagenExperienciaSeleccionada = (view.context as AppCompatActivity).findViewById<ImageView>(R.id.imagen_experiencia_seleccionada)
        val tituloExperienciaSeleccionada = (view.context as AppCompatActivity).findViewById<TextView>(R.id.titulo_experiencia_seleccionada)
        val testo = (view.context as AppCompatActivity).findViewById<TextView>(R.id.testo_experiencia_seleccionada)

        passData.passthroughData(experiencia.id)

        tituloExperienciaSeleccionada.text = experiencia.titulo

        experiencia.foto?.let {
            Utils.putBase64ImageIntoImageViewWithPlaceholder(imagenExperienciaSeleccionada, it, itemView.context, R.drawable.ic_default_experiencie_true_tone)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageView(imagenExperienciaSeleccionada, R.drawable.ic_default_experiencie_true_tone, itemView.context)
        }

        testo.text = view.context.getString(R.string.experiencia_seleccionada)

        Animations.ocultarVistaSuavemente(buscadorDeExperiencias)
        Animations.mostrarVistaSuavemente(mostrarExperienciaSeleccionada)

    }

}