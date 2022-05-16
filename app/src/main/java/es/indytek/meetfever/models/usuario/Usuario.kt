package es.indytek.meetfever.models.usuario

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
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
    @SerializedName("Foto_Fondo")
    var fotoFondo: String? = "",
    @SerializedName("Foto_Perfil")
    var fotoPerfil: String? = "",
    @SerializedName("Telefono")
    var telefono: String? = "",
    @SerializedName("Frase")
    var frase: String? = ""

) : Serializable {

    open fun toJsonObject(): JSONObject = JSONObject().apply {
        put("Id", id)
        put("Correo", correo)
        put("Contrasena", contrasena)
        put("Nick", nick)
        put("Foto_Perfil", fotoPerfil)
        put("Foto_Fondo", fotoFondo)
        put("Telefono", telefono)
        put("Frase", frase)
    }

}