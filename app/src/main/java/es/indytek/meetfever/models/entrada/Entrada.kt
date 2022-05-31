package es.indytek.meetfever.models.entrada

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Entrada(
    @SerializedName("Id_Persona")
    var idPersona: Int = -1,
    @SerializedName("Id_Experiencia")
    var idExperiencia: Int = -1,
    @SerializedName("Fecha")
    var fecha: LocalDateTime = LocalDateTime.now(),
    @SerializedName("Nombre")
    var nombre: String = "",
    @SerializedName("Apellido1")
    var apellido1: String = "",
    @SerializedName("Apellido2")
    var apellido2: String = "",
    @SerializedName("Dni")
    var dni: String = "",
    @SerializedName("Id_Paypal")
    var idPaypal: String = ""
) {





}