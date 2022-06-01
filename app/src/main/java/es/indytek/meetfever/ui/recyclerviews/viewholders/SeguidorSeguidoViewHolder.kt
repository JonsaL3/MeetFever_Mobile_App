package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ViewholderSeguidorSeguidoBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils

class SeguidorSeguidoViewHolder(

    private val view: View,
    private val binding: ViewholderSeguidorSeguidoBinding,
    private val currentUsuario: Usuario

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Usuario) {

        if (Utils.isFamous(objeto))
            Animations.mostrarVistaSuavemente(binding.famousEdges)

        binding.nickname.text = objeto.nick
        binding.frase.text = objeto.frase

        val foto = objeto.fotoPerfil
        Utils.pintarFotoDePerfil(objeto, binding.fotoPerfil, view.context)

        // pinto la targeta del fondo en función del color principal del usuario
        currentUsuario.fotoPerfil?.let {
            val color = Utils.getDominantColorInImageFromBase64(it)

            if (Utils.colorIsConsideredDark(color)) { // puede ocurrir que el base 64 este mal formado, por lo que devolverá negro y arruinará la targeta
                binding.viewholderSeguidorSeguido.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            } else {
                binding.viewholderSeguidorSeguido.backgroundTintList = ColorStateList.valueOf(color)
            }

        }?: kotlin.run {
            binding.viewholderSeguidorSeguido.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }

        binding.viewholderSeguidorSeguido.setOnClickListener {
            val fragmento = PerfilFragment.newInstance(objeto, currentUsuario)
            Utils.cambiarDeFragmentoGuardandoElAnterior((view.context as AppCompatActivity).supportFragmentManager,fragmento, "", R.id.frame_layout)
        }

    }

}