package es.indytek.meetfever.models.typeAdapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

class LocalDateTypeAdapter : TypeAdapter<LocalDate>() {

    override fun write(out: JsonWriter?, value: LocalDate?) {
        out?.value(value.toString())
    }

    override fun read(into: JsonReader?): LocalDate {
        return LocalDate.parse(into?.nextString())
    }

}