package es.indytek.meetfever.utils

import android.view.View

object Animations {

    fun mostrarVistaSuavemente(view: View, duration: Long) {
        // cambio el view visibility a visible suavemente
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(duration).start()
    }

    fun ocultarVistaSuavemente(view: View, duration: Long) {
        // cambio el view visibility a invisible suavemente
        view.visibility = View.VISIBLE
        view.alpha = 1f
        view.animate().alpha(0f).setDuration(duration).start()
    }

}