package es.indytek.meetfever.models.experiencia

import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.models.empresa.Empresa
import java.io.Serializable
import java.time.LocalDateTime

data class Experiencia(

    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Aforo")
    var aforo: Int = 0,
    @SerializedName("Descripcion")
    var descripcion: String? = "",
    @SerializedName("Empresa")
    var empresa: Empresa = Empresa("", "",""),
    @SerializedName("Fecha_Celebracion")
    var fechaCelebracion: LocalDateTime = LocalDateTime.now(),
    @SerializedName("Foto")
    var foto: String? = "",
    @SerializedName("Precio")
    var precio: Float = 0f,
    @SerializedName("Titulo")
    var titulo: String? = ""

) : Serializable
