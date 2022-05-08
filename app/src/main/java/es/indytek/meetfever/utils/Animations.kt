package es.indytek.meetfever.utils

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import java.util.stream.Collectors

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

    fun esperarYOcultarVistaSuavemente(view: View, duration: Long) {
        view.visibility = View.VISIBLE
        view.postDelayed({
            ocultarVistaSuavemente(view, duration)
        }, duration)
    }

    fun pintarGridRecyclerViewSuavemente(gridLayoutManager: GridLayoutManager, recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>, duration: Long) {
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        mostrarVistaSuavemente(recyclerView, duration)
    }

    fun pintarLinearRecyclerViewSuavemente(linearLayoutManager: LinearLayoutManager, recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>, duration: Long, orientation: Int) {
        linearLayoutManager.orientation = orientation
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        mostrarVistaSuavemente(recyclerView, duration)
    }

}