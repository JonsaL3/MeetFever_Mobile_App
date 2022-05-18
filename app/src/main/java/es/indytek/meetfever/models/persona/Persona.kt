package es.indytek.meetfever.models.persona

import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.models.sexo.Sexo
import es.indytek.meetfever.models.usuario.Usuario
import org.json.JSONObject
import java.time.LocalDate

class Persona(

    /* Atributos del padre */
    id: Int,
    correo: String,
    contrasena: String,
    nick: String,
    fotoFondo: String?,
    fotoPerfil: String?,
    telefono: String?,
    frase: String?,

    /* Atributos propios */
    @SerializedName("Nombre")
    var nombre: String? = null,
    @SerializedName("Apellido1")
    var apellido1: String? = null,
    @SerializedName("Apellido2")
    var apellido2: String? = null,
    @SerializedName("Sexo")
    var sexo: Sexo? = null,
    @SerializedName("Fecha_Nacimiento")
    var fechaNacimiento: LocalDate? = null,
    @SerializedName("dni")
    var dni: String? = null,

    ) : Usuario(id, correo, contrasena, nick, fotoFondo, fotoPerfil, telefono, frase) {

    // constructor dado el correo, la contraseña y el nick y el resto a null
    constructor(correo: String, contrasena: String, nick: String) : this (
        0,
        correo,
        contrasena,
        nick,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null)

    override fun toJsonObject(): JSONObject = super.toJsonObject().apply {
        put("Nombre", nombre)
        put("Apellido1", apellido1)
        put("Apellido2", apellido2)
        put("Sexo", sexo.toString())
        put("Fecha_Nacimiento", fechaNacimiento.toString())
        put("dni", dni)
    }

    override fun toString(): String {
        return super.toString()
    }

}