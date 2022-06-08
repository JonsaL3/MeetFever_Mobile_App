package es.indytek.meetfever.models.factura


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Titular(
    @SerializedName("Apellido_1_Titular")
    var apellido1Titular: String = "",
    @SerializedName("Apellido_2_Titular")
    var apellido2Titular: String = "",
    @SerializedName("Nombre_Titular")
    var nombreTitular: String = ""
) : Serializable