package es.indytek.meetfever.models.factura


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Factura(
    @SerializedName("Apellido_1_Persona")
    var apellido1Persona: String = "",
    @SerializedName("Apellido_2_Persona")
    var apellido2Persona: String = "",
    @SerializedName("Dni_Persona")
    var dniPersona: String = "",
    @SerializedName("Fecha_Entradas")
    var fechaEntradas: String = "",
    @SerializedName("Id_Empresa")
    var idEmpresa: Int = 0,
    @SerializedName("Nombre_Empresa")
    var nombreEmpresa: String = "",
    @SerializedName("Nombre_Persona")
    var nombrePersona: String = "",
    @SerializedName("Titulares")
    var titulares: List<Titulare> = listOf(),
    @SerializedName("Titulo_Experiencia")
    var tituloExperiencia: String = "",
    @SerializedName("Precio")
    var precio: Double = 0.0,
    @SerializedName("Id_Transaccion")
    var idTransaccion: String = "",
) : Serializable