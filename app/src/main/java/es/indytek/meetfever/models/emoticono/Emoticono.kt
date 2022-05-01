package es.indytek.meetfever.models.emoticono

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Emoticono (

    @SerializedName("id")
    val id: Int,
    @SerializedName("Emoji")
    val emoji: String,

) : Serializable {

}