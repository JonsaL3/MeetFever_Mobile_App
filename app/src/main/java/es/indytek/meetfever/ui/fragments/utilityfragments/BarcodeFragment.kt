package es.indytek.meetfever.ui.fragments.utilityfragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServicePersona
import es.indytek.meetfever.data.webservice.WebServiceUsuario
import es.indytek.meetfever.databinding.FragmentBarcodeBinding
import es.indytek.meetfever.models.mesigue.MeSigue
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Utils

private const val ARG_PARAM1 = "currentusuario"

class BarcodeFragment : Fragment() {

    private lateinit var binding: FragmentBarcodeBinding
    private lateinit var currentUsuario: Usuario
    private var isCodigoEscaneado = false

    // Cosas que necesito para el lector de códigos QR
    private lateinit var detector: BarcodeDetector
    private val processor = object : Detector.Processor<Barcode> {

        override fun release() {}
        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {

            if (detections != null && detections.detectedItems.isNotEmpty()) {
                val barcode = detections.detectedItems
                if (barcode.size() > 0) {
                    val qr = barcode.valueAt(0).displayValue
                    buscarQrEscaneado(qr)
                }
            }

        }
    }
    private lateinit var cameraSource: CameraSource
    private val surfaceCallBack = object : SurfaceHolder.Callback {

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) { cameraSource.stop() }

        override fun surfaceCreated(p0: SurfaceHolder) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )
                cameraSource.start(binding.cameraSurfaceView.holder)
            else
                requestPermissions(arrayOf(Manifest.permission.CAMERA),1001)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return
                }

                cameraSource.start(binding.cameraSurfaceView.holder)

            } else {
                Toast.makeText(context, "Error con los permisos necesarios.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBarcodeBinding.inflate(inflater, container, false)

        // Arranco el lector de códigos QR
        cargarLectorDeBarcode()

        return binding.root
    }

    private fun buscarQrEscaneado(qr: String) {

        if (!isCodigoEscaneado) {
            isCodigoEscaneado = true
            Handler(Looper.getMainLooper()).postDelayed({

                try {

                    val qrObtenido = qr.split(";")
                    if (qrObtenido[0] != "MEETFEVERMOBILEAPP")
                        throw Exception("Qr no válido")

                    val idUsuarioASeguir = qrObtenido[1].toInt()

                    WebServiceUsuario.seguirDejarDeSeguir(currentUsuario.id, idUsuarioASeguir, requireContext(), object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            if (any == 0) {
                                // TODO ERROR
                            } else {

                                // todo esta comprobaxcion es erronea no hacer mucho caso
                                if (any.toString().lowercase().contains("true")) {
                                    Log.d(":::", "USUARIO SEGUIDO")
                                } else {
                                    Log.d(":::", "USUARIO DEJADO DE SEGUIR")
                                }

                            }

                        }
                    })

                } catch (e: Exception) {
                    e.printStackTrace()
                    // TODO QR ERRONEO
                }

            }, 0)
        }

    }

    private fun cargarLectorDeBarcode() {
        detector = BarcodeDetector.Builder(requireContext()).setBarcodeFormats(Barcode.QR_CODE).build()
        detector.setProcessor(processor)

        cameraSource = CameraSource.Builder(requireContext(),detector)
            .setRequestedFps(25f)
            .setAutoFocusEnabled(true).build()

        binding.cameraSurfaceView.holder.addCallback(surfaceCallBack)
    }

    // devuelvo los valores al fragmento anterior
    private fun returnQrExitosoAlFragmentoAnterior(qr: String) {

        val resultBundle = Bundle().apply {
            putString("QR", qr)
        }
        setFragmentResult("QR", resultBundle)

        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().supportFragmentManager.popBackStackImmediate()
        },0)

    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarElementosUI(requireActivity())
    }

    companion object {
        @JvmStatic
        fun newInstance(currentUsuario: Usuario) =
            BarcodeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, currentUsuario)
                }
            }
    }
}