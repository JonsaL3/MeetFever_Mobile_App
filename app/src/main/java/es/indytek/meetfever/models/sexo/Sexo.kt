package es.indytek.meetfever.models.sexo

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable

data class Sexo(

    @SerializedName("Id")
    var id: Int,
    @SerializedName("Sexo")
    var nombre: String

) : Serializable {

    override fun toString(): String {
        return "$id - $nombre"
    }

    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("Id", id)
        put("Sexo", nombre)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sexo

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


}