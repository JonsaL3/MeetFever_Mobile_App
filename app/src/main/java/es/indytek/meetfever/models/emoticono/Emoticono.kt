package es.indytek.meetfever.models.emoticono

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Emoticono (

    /* clave primaria o identificador */
    @SerializedName("Id")
    val id: Int,

    /* el propio base 64 en formato texto */
    @SerializedName("Emoji")
    val emoji: String,

    /* Atributos que yo necesito internamente para el funcionamiento de la app */
    var isSelected: Boolean = false,

    ) : Serializable