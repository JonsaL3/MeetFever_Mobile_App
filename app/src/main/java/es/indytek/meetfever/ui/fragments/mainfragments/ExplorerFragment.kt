
package es.indytek.meetfever.ui.fragments.mainfragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceExperiencia
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentExplorerBinding
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.empresa.AllEmpresasFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.empresa.AllExperiencesFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.EmpresaRecyclerViewAdapter
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.lang.Exception

private const val ARG_PARAM1 = "usuario"

class ExplorerFragment : Fragment() {

    // bindeo de las vistas
    private lateinit var binding: FragmentExplorerBinding

    // datos que necesito
    private lateinit var currentUsuario: Usuario

    private var contenidoOculto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentExplorerBinding.inflate(inflater, container, false)

        // Muestro to.dos los datos de este fragmento
        pintar()

        // arranco los listeners que necesito
        arrancarListeners()

        // arranco el motor de busqueda
        motorDeBusqueda()

        //throw Exception("Error")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.mostrarElementosUI(requireActivity())

        /*try {
            throw Exception("Esto es una excepcion de prueba")
        } catch (e: Exception) {
            Log.e(":::", "Excepcion")
            Utils.enviarRegistroDeErrorABBDD(
                context = requireContext(),
                stacktrace = e.message.toString(),
            )
        }*/

    }

    // Preparo las busquedas
    private fun motorDeBusqueda() {

        val textWatcher = object: TextWatcher {

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.toString().isEmpty()) {
                    mostrarContenido()
                } else {
                    ocultarContenido()
                    WebServiceEmpresa.buscarEmpresa(s.toString(), requireContext(), object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                // TODO ERROR
                            } else {
                                val empresas = any as EmpresaWrapper
                                //ocultarContenido()
                                try {
                                    Animations.pintarGridRecyclerViewSuavemente(
                                        gridLayoutManager = GridLayoutManager(requireContext(), 3),
                                        recyclerView = binding.localesEncontradosRecycler,
                                        adapter = EmpresaRecyclerViewAdapter(empresas, currentUsuario),
                                    )
                                } catch (e: IllegalStateException) {
                                    Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                                    Utils.enviarRegistroDeErrorABBDD(
                                        context = requireContext(),
                                        stacktrace = e.message.toString(),
                                    )
                                }
                            }

                        }
                    })

                    WebServiceExperiencia.buscarExperiencia(s.toString(), requireContext(), object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                // TODO ERROR
                            } else {
                                val experiencias = any as ExperienciaWrapper
                                //ocultarContenido()
                                try {
                                    Animations.pintarGridRecyclerViewSuavemente(
                                        gridLayoutManager = GridLayoutManager(requireContext(), 3),
                                        recyclerView = binding.experienciasEncontradasRecycler,
                                        adapter = ExperienciaRecyclerViewAdapter(experiencias, currentUsuario)
                                    )
                                } catch (e: IllegalStateException) {
                                    Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                                    Utils.enviarRegistroDeErrorABBDD(
                                        context = requireContext(),
                                        stacktrace = e.message.toString(),
                                    )
                                }
                            }

                        }
                    })

                }

            }
        }

        binding.inputMainActivityExplorer.addTextChangedListener(textWatcher)

    }

    private fun ocultarContenido() {
        if (!contenidoOculto) {
            contenidoOculto = true
            Animations.ocultarVistaSuavemente(binding.explorerNormal)

            Animations.mostrarVistaSuavemente(binding.explorerBusqueda)
        }
    }

    private fun mostrarContenido() {
        contenidoOculto = false
        Animations.ocultarVistaSuavemente(binding.explorerBusqueda)

        Animations.mostrarVistaSuavemente(binding.explorerNormal)
    }

    // preparo los listeners
    private fun arrancarListeners() {

        binding.experienciasDestacadasTexto.setOnClickListener{
            mostrarTodasLasExperiencias()
        }

        binding.localesTrendingTexto.setOnClickListener {
            mostrarTodasLasEmpresas()
        }

    }

    // muestro todas las empresas en otro fragmento
    private fun mostrarTodasLasEmpresas() {
        val fragmento = AllEmpresasFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    private fun mostrarTodasLasExperiencias() {
        val fragmento = AllExperiencesFragment.newInstance(currentUsuario)
        Utils.cambiarDeFragmentoGuardandoElAnterior(requireActivity().supportFragmentManager,fragmento, "", R.id.frame_layout)
    }

    // pinto las 4 experiencias mas mencionadas en opiniones
    private fun pintarExperienciasDestacadas() {

        // busco las experiencias mas mencionadas
        WebServiceExperiencia.findTop4ExperienciasMasComentadas(requireContext(), object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                    Utils.terminarCargaOnError(binding.loadingAnimationExperienciasDestacadas, binding.experienciasDestacadasTextoNone)

                }
                else {
                    // Una vez encontradas las pinto suavemente
                    val experiencias = any as ExperienciaWrapper
                    Utils.terminarCarga(requireContext(), binding.loadingAnimationExperienciasDestacadas) {
                        Animations.pintarGridRecyclerViewSuavemente(
                            gridLayoutManager = GridLayoutManager(requireContext(), 2),
                            recyclerView = binding.experienciaDestacadasRecyclerView,
                            adapter = ExperienciaRecyclerViewAdapter(experiencias.sortedBy { it.titulo }, currentUsuario),
                        )
                    }

                }

            }
        })
    }

    // pinta el recycler de las empresas con mas seguidores
    private fun pintarTopEmpresas() {

        // Busco las 10 empresas o locales (es lo mismo) con mas seguidores
        WebServiceEmpresa.findTop10EmpresasConMasSeguidores(requireContext(), object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {
                if (any == 0) {
                    // TODO ERROR
                    Utils.terminarCargaOnError(binding.loadingAnimationTopLocales, binding.localesTrendingTextoNone)
                }
                else {
                    // Una vez encontradas las pinto suavemente
                    val empresas = any as EmpresaWrapper
                    Utils.terminarCarga(requireContext(), binding.loadingAnimationTopLocales){
                        Animations.pintarLinearRecyclerViewSuavemente(
                            linearLayoutManager = LinearLayoutManager(requireContext()),
                            recyclerView = binding.localesTrendingRecycler,
                            adapter = EmpresaRecyclerViewAdapter(empresas, currentUsuario),
                            orientation = LinearLayoutManager.HORIZONTAL,
                        )
                    }
                }
            }
        })

    }

    // esta función llama al resto de funciones para que pinten los elementos del fragment
    private fun pintar() {
        pintarExperienciasDestacadas()
        pintarTopEmpresas()
    }

    override fun onResume() {
        super.onResume()
        setBottomBarColorAndPosition()
    }

    private fun setBottomBarColorAndPosition() {
        val bottomBar = requireActivity().findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        Utils.mostrarBottomBar(requireActivity())
        bottomBar.indicatorColorRes = R.color.rosa_meet
        bottomBar.selectTabAt(1, true)
    }

    // Crear una instancia de este fragmento
    companion object {
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            ExplorerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, usuario)
                }
            }
    }
}