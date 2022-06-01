package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ViewholderEmpresaBusquedaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.FromViewHolderToParent
import es.indytek.meetfever.utils.Utils

class EmpresaBusquedaViewHolder(

    private val view: View,
    private val binding: ViewholderEmpresaBusquedaBinding,
    private val currentUsuario: Usuario,
    private val passData: FromViewHolderToParent

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Empresa) {
        binding.nombreEmpresa.text = objeto.nick
        binding.descripcionEmpresa.text = objeto.frase
        binding.viewholderEmpresa.setOnClickListener { onClick(objeto) }

        Log.d("ID DE EMPRESA EN VIEWHOLDER:::", objeto.id.toString())

    }

    private fun onClick(empresa: Empresa) {

        val buscadorDeEmpresas = (view.context as AppCompatActivity).findViewById<FrameLayout>(R.id.selector_empresa)
        val mostrarEmpresaSeleccionada = (view.context as AppCompatActivity).findViewById<LinearLayout>(R.id.empresa_seleccionada)
        val imageEmpresaSeleccionada = (view.context as AppCompatActivity).findViewById<ImageView>(R.id.image_empresa_seleccionada)
        val nombreEmpresaSeleccinada = (view.context as AppCompatActivity).findViewById<TextView>(R.id.nick_empresa_seleccionada)
        val testo = (view.context as AppCompatActivity).findViewById<TextView>(R.id.testo_empresa_seleccionada)

        Log.d(":::","ABBBBB->" +  empresa.id)
        passData.passthroughData(empresa.id)

        nombreEmpresaSeleccinada.text = empresa.nick

        empresa.fotoPerfil?.let {
            Utils.pintarFotoDePerfil(empresa, imageEmpresaSeleccionada, view.context)
        }

        testo.text = view.context.getString(R.string.empresa_seleccionada)

        Animations.ocultarVistaSuavemente(buscadorDeEmpresas)
        Animations.mostrarVistaSuavemente(mostrarEmpresaSeleccionada)

    }

}