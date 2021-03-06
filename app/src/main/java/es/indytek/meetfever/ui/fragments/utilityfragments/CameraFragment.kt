package es.indytek.meetfever.ui.fragments.utilityfragments

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentCameraBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val ARG_PARAM1 = "currentUsuario"

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var name: String
    private lateinit var currentUsuario: Usuario

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Usuario
        }
        contexto = requireContext()
        actividad = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Creo un hilo para la c??mara (creo)
        cameraExecutor = Executors.newSingleThreadExecutor()
        // Request camera permissions
        solicitarPermisos()
        startCamera()
        cargarListeners()
    }

    // CONTROLAR LA C??MARA #########################################################################
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hacerFoto() {

        Log.d(":::", "SE HA PRESIONADO EL BOTON DE HACER UNA FOTO")

        // Obtengo una referencia estable del caso de uso de captura de im??genes modificable
        val imageCapture = imageCapture ?: return

        // El nombre que tendr?? el archivo de la foto
        name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {

            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MeetFever")
            }

            put(MediaStore.Images.Media.IS_PENDING, 1)

        }

        // El archivo de salida con sus metadatos
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            actividad.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // Esto se ejecuta cuando una foto es realizada
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(contexto), object : ImageCapture.OnImageSavedCallback {

            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(contexto, "Foto realizada con ??xito.", Toast.LENGTH_SHORT).show()
                Log.e(":::", "Error al tomar la foto."+ exc.message, exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val msg = "Foto guardada con ??xito" + output.savedUri
                Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show()
                Log.d(":::", msg)

                val uri = output.savedUri
                uri?.let {
                    // devuelvo los datos al fragmento anterior
                    returnImagenAlFragmentoAnterior("/storage/emulated/0/Pictures/MeetFever/$name.jpg")
                }
            }

        })
    }

    // ARRANCAR LA C??MARA ##########################################################################
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(contexto)

        cameraProviderFuture.addListener({
            // Se utiliza para vincular el ciclo de vida de las c??maras al propietario del ciclo de vida
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Muestro la preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            // construyo el realizador de fotograf??as
            imageCapture = ImageCapture.Builder().build()

            // Por defecto, uso la c??mara trasera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Fallo en el bindeo de caso de uso.", exc)
                Utils.enviarRegistroDeErrorABBDD(
                    context = contexto,
                    stacktrace = exc.message.toString(),
                )
            }

        }, ContextCompat.getMainExecutor(contexto))

    }

    // TODDO LO RELACIONADO CON LOS PERMISOS DE LA C??MARA ##########################################
    private fun solicitarPermisos() {

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(actividad,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
            if (allPermissionsGranted()) {
                startCamera()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // TO.DO MOSTRAR UN DIALOG
                Toast.makeText(contexto, getString(R.string.usuario_no_permisos), Toast.LENGTH_SHORT).show()
                //finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(contexto, it) == PackageManager.PERMISSION_GRANTED
    }

    // Otros #######################################################################################
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun cargarListeners() {

        binding.hacerFotoBoton.setOnClickListener {
            hacerFoto()
        }

    }

    private fun returnImagenAlFragmentoAnterior(ruta: String) {

        // meto en el bundle el bitmap
        val resultBundle = Bundle().apply {
            putString("FOTO", ruta)
        }
        setFragmentResult("FOTO", resultBundle)

        // me vuelvo al fragmento que deje pausado antes
        requireActivity().supportFragmentManager.popBackStackImmediate()

    }

    override fun onResume() {
        super.onResume()
        Utils.ocultarBottomBar(actividad)
        Utils.ocultarElementosUI(actividad)
    }

    companion object {

        // Nombre del archivo de salida
        private const val TAG = "MeetFever-CameraX"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        // Permisos de la c??mara
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

        @JvmStatic
        fun newInstance(currentUsuario: Usuario) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, currentUsuario)
                }
            }
    }
}
