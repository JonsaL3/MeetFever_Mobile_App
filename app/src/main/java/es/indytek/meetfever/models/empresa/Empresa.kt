package es.indytek.meetfever.models.empresa

import com.google.gson.annotations.SerializedName
import es.indytek.meetfever.models.usuario.Usuario

class Empresa (

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
    @SerializedName("Nombre_Empresa")
    var nombreEmpresa: String = "",
    @SerializedName("Cif")
    var cif: String = "",
    @SerializedName("Direccion_Fiscal")
    var direccionFiscal: String = "",
    @SerializedName("Direccion_Facturacion")
    var direccionFacturacion: String = "",
    @SerializedName("Nombre_Persona")
    var nombrePersona: String = "",
    @SerializedName("Apellido1_Persona")
    var apellido1Persona: String = "",
    @SerializedName("Apellido2_Persona")
    var apellido2Persona: String = "",
    @SerializedName("Dni_Persona")
    var dniPersona: String = "",

) : Usuario(id, correo, contrasena, nick, fotoFondo, fotoPerfil, telefono, frase) {

}