package es.indytek.meetfever.models.usuario

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.data.sqlite.Tablas
import java.io.Serializable

abstract class Usuario (

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
    var fotoFondo: String? = "", // TODO BASE 64
    @SerializedName("Foto_Fondo")
    var fotoPerfil: String? = "", // TODO BASE 64
    @SerializedName("Telefono")
    var telefono: String? = "",
    @SerializedName("Frase")
    var frase: String? = ""

) : Serializable {

}