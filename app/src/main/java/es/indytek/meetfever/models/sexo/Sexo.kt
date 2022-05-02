package es.indytek.meetfever.models.sexo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Sexo(

    @SerializedName("Id")
    val id: Int,
    @SerializedName("Sexo")
    val nombre: String

) : Serializable