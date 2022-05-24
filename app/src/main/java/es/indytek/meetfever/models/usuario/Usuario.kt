package es.indytek.meetfever.models.usuario

import android.util.Log
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
    var fotoFondo: String? = null,
    @SerializedName("Foto_Perfil")
    var fotoPerfil: String? = null,
    @SerializedName("Telefono")
    var telefono: String? = null,
    @SerializedName("Frase")
    var frase: String? = null

) : Serializable {

    open fun toJsonObject(): JSONObject = JSONObject().apply {
        Log.d(":::", "LLAMO A PAPA")
        put("Id", id.toString())
        put("Correo", correo)
        put("Contrasena", contrasena)
        put("Nick", nick)
        put("Foto_Perfil", fotoPerfil)
        put("Foto_Fondo", fotoFondo)
        put("Telefono", telefono)
        put("Frase", frase)
    }

    override fun toString(): String {
        return super.toString() + " " + id + " " + correo + " " + contrasena + " " + nick + " " + fotoFondo + " " + fotoPerfil + " " + telefono + " " + frase
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Usuario

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


}