package es.indytek.meetfever.data.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // valores por defecto de la base de datos
    companion object{
        const val DATABASE_NAME = "meetfever.db"
        const val DATABASE_VERSION = 1
    }

    // CREO TODAS LAS TABLAS LA PRIMERA VEZ
    override fun onCreate(p0: SQLiteDatabase?) {

    }

    // CADA VEZ QUE ACTUALICE BORRO TO.DO ANTES
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}