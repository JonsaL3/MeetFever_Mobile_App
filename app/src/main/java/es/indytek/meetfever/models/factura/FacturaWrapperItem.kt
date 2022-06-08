package es.indytek.meetfever.models.factura


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FacturaWrapperItem(
    @SerializedName("Factura")
    var factura: Factura = Factura()
) : Serializable