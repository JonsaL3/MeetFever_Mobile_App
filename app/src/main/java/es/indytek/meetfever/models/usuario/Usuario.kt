package es.indytek.meetfever.models.usuario

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Usuario (

    /* identificador de un usuario */
    @SerializedName("Id")
    var id: Int,

    /* atributos de un usuario */
    @SerializedName("Correo")
    var correo: String = "",
    @SerializedName("Contrasena")
    var contrasena: String = "",
    @SerializedName("Nick")
    var nick: String = "",
    @SerializedName("Foto_Perfil")
    var fotoFondo: String? = "",
    @SerializedName("Foto_Fondo")
    var fotoPerfil: String? = "",
    @SerializedName("Telefono")
    var telefono: String? = "",
    @SerializedName("Frase")
    var frase: String? = ""

) : Serializable {

}