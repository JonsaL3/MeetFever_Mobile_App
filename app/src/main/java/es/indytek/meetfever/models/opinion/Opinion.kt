package es.indytek.meetfever.models.opinion


import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.models.emoticono.Emoticono
import es.indytek.meetfever.models.usuario.Usuario
import java.io.Serializable
import java.time.LocalDateTime

data class Opinion(

    @SerializedName("Descripcion")
    var descripcion: String = "",
    @SerializedName("Emoticono")
    var eMOTICONO: Emoticono = Emoticono(-1,""),
    @SerializedName("Fecha")
    var fecha: LocalDateTime = LocalDateTime.now(),
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Autor")
    var autor: Usuario,
    @SerializedName("Id_Empresa")
    var idEmpresa: Int = 0,
    @SerializedName("Titulo")
    var titulo: String = ""

) : Serializable