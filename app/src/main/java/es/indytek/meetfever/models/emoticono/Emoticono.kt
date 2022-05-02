package es.indytek.meetfever.models.emoticono

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Emoticono (

    @SerializedName("Id")
    val id: Int,
    @SerializedName("Emoji")
    val emoji: String,

) : Serializable