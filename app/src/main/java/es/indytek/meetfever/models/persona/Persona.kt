package es.indytek.meetfever.models.persona

import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.models.usuario.Usuario
import java.time.LocalDate

class Persona (

    /* Atributos del padre */
    id: Int,
    correo: String,
    contrasena: String,
    nick: String,
    fotoFondo: String,
    fotoPerfil: String,
    telefono: String,
    frase: String,

    /* Atributos propios */
    @SerializedName("Nombre")
    var nombre: String,
    @SerializedName("Apellido1")
    var apellido1: String,
    @SerializedName("Apellido2")
    var apellido2: String,
    @SerializedName("Sexo")
    var sexo: String,
    @SerializedName("Fecha_Nacimiento")
    var fechaNacimiento: LocalDate,
    @SerializedName("dni")
    var dni: String,

    ) : Usuario(id, correo, contrasena, nick, fotoFondo, fotoPerfil, telefono, frase) {

}