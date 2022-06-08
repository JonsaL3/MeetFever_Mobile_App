package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderFacturaBinding
import es.indytek.meetfever.models.factura.Factura
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.TitularesRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Constantes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FacturaViewHolder (

    private val view: View,
    private val binding: ViewholderFacturaBinding,
    private val currentUsuario: Usuario

) : RecyclerView.ViewHolder(view) {

    fun bind(factura: Factura) {

        binding.nombreComprador.text = factura.nombrePersona

        (factura.apellido1Persona + " " + factura.apellido2Persona).also {
            binding.apellidosComprador.text = it
        }

        binding.tituloExperiencia.text = factura.tituloExperiencia
        binding.fechaExperiencia.text = LocalDateTime.parse(factura.fechaEntradas)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        binding.empresaOrganizadora.text = factura.nombreEmpresa

        val precio = factura.precio * Constantes.IVA * Constantes.COMISION_MEET_FEVER

        (precio.toInt().toString() + "€").also {
            binding.precioUnitario.text = it
        }

        ((precio.toInt() * factura.titulares.size).toString() + "€").also {
            binding.precioTotal.text = it
        }

        binding.idTransaccion.text = factura.idTransaccion

        Animations.pintarLinearRecyclerViewSuavemente(
            linearLayoutManager = LinearLayoutManager(view.context),
            recyclerView = binding.listaDeTitulares,
            adapter = TitularesRecyclerViewAdapter(factura.titulares.map {
                    t -> t.titular
            }, currentUsuario),
            orientation = LinearLayoutManager.VERTICAL
        )

    }

}