package es.indytek.meetfever.models.empresa

import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.models.usuario.Usuario
import org.json.JSONObject

class Empresa(

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
    @SerializedName("Nombre_Empresa")
    var nombreEmpresa: String? = null,
    @SerializedName("Cif")
    var cif: String? = null,
    @SerializedName("Direccion_Fiscal")
    var direccionFiscal: String? = null,
    @SerializedName("Direccion_Facturacion")
    var direccionFacturacion: String? = null,
    @SerializedName("Nombre_Persona")
    var nombrePersona: String? = null,
    @SerializedName("Apellido1_Persona")
    var apellido1Persona: String? = null,
    @SerializedName("Apellido2_Persona")
    var apellido2Persona: String? = null,
    @SerializedName("Dni_Persona")
    var dniPersona: String? = null,

    ) : Usuario(id, correo, contrasena, nick, fotoFondo, fotoPerfil, telefono, frase) {
    // constructor dado el correo, la contraseña y el nick
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
        null,
        null,
        null)


    override fun toJsonObject(): JSONObject = super.toJsonObject().apply {
        put("Nombre_Empresa", nombreEmpresa)
        put("Cif", cif)
        put("Direccion_Fiscal", direccionFiscal)
        put("Direccion_Facturacion", direccionFacturacion)
        put("Nombre_Persona", nombrePersona)
        put("Apellido1_Persona", apellido1Persona)
        put("Apellido2_Persona", apellido2Persona)
        put("Dni_Persona", dniPersona)
    }

    override fun toString(): String {
        return super.toString() + " " + nombreEmpresa + " " + cif + " " + direccionFiscal + " " + direccionFacturacion + " " + nombrePersona + " " + apellido1Persona + " " + apellido2Persona + " " + dniPersona
    }

}