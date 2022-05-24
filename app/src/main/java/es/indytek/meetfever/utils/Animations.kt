package es.indytek.meetfever.utils

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


object Animations {

    fun mostrarVistaSuavemente(
        view: View,
        duration: Long = Constantes.TIEMPO_DE_ANIMACIONES) {
        // cambio el view visibility a visible suavemente
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(duration).start()
    }

    fun ocultarVistaSuavemente(
        view: View,
        duration: Long = Constantes.TIEMPO_DE_ANIMACIONES) {
        // cambio el view visibility a invisible suavemente
        view.visibility = View.VISIBLE
        view.alpha = 1f
        view.animate().alpha(0f).setDuration(duration).start()
    }

    fun esperarYOcultarVistaSuavemente(
        view: View,
        duration: Long = Constantes.TIEMPO_DE_ANIMACIONES) {

        view.visibility = View.VISIBLE
        view.postDelayed({
            ocultarVistaSuavemente(view, duration)
        }, duration)
    }

    fun pintarGridRecyclerViewSuavemente(
        gridLayoutManager: GridLayoutManager,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        duration: Long = Constantes.TIEMPO_DE_ANIMACIONES) {

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        mostrarVistaSuavemente(recyclerView, duration)

    }

    fun pintarLinearRecyclerViewSuavemente(
        linearLayoutManager: LinearLayoutManager,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        duration: Long = Constantes.TIEMPO_DE_ANIMACIONES,
        orientation: Int) {

        linearLayoutManager.orientation = orientation
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        mostrarVistaSuavemente(recyclerView, duration)

    }

    fun setLayoutHeight(
        view: View,
        height: Int,
        duration: Long = Constantes.TIEMPO_DE_ANIMACIONES) {

        val anim = ValueAnimator.ofInt(view.measuredHeight, height)

        anim.addUpdateListener { valueAnimator ->
            val animVal = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animVal
            view.layoutParams = layoutParams
        }

        anim.duration = duration
        anim.start()
    }

    fun setLayoutHeightWithTopMargin(
        view: View,
        height: Int,
        topMargin: Int,
        duration: Long = Constantes.TIEMPO_DE_ANIMACIONES) {

        val anim = ValueAnimator.ofInt(view.measuredHeight, height)

        anim.addUpdateListener { valueAnimator ->
            val animVal = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animVal
            (layoutParams as ViewGroup.MarginLayoutParams).topMargin = topMargin
            view.layoutParams = layoutParams
        }

        anim.duration = duration
        anim.start()
    }

}