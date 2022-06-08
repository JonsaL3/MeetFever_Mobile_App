package es.indytek.meetfever.models.factura


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Titulare(
    @SerializedName("Titular")
    var titular: Titular = Titular()
) : Serializable