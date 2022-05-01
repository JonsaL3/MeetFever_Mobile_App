package es.indytek.meetfever.models.opinion


import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.models.emoticono.Emoticono
import java.io.Serializable

data class Opinion(

    @SerializedName("Descripcion")
    var descripcion: String = "",
    @SerializedName("EMOTICONO")
    var eMOTICONO: Emoticono = Emoticono(-1,""),
    @SerializedName("Fecha")
    var fecha: String = "",
    @SerializedName("Id")
    var id: Int = 0,
    @SerializedName("Id_Autor") /* MODO SEMI LAZY, NO NECESITO EL AUTOR POR NORMA GENERAL PERO NO ME VIENE MAL SU ID */
    var idAutor: Int = 0,
    @SerializedName("Id_Empresa")
    var idEmpresa: Int = 0,
    @SerializedName("Titulo")
    var titulo: String = ""

) : Serializable