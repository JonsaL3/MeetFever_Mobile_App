package es.indytek.meetfever.models.sexo

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

data class Sexo(

    @SerializedName("Id")
    val id: Int,
    @SerializedName("Sexo")
    val nombre: String

) : Serializable {

    override fun toString(): String {
        return nombre
    }

    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("Id", id)
        put("Sexo", nombre)
    }

}