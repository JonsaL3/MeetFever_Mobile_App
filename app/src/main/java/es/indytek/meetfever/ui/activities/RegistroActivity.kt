package es.indytek.meetfever.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceSexo
import es.indytek.meetfever.data.webservice.WebServiceUsuario
import es.indytek.meetfever.databinding.ActivityLoginBinding
import es.indytek.meetfever.databinding.ActivityRegistroBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.sexo.Sexo
import es.indytek.meetfever.models.sexo.SexoWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.CreateMD5

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // arranco los listeners de los botones
        arrancarListeners()

        // cargo los sexos de la base de datos
        cargarSpinnerSexos()

    }

    private fun arrancarListeners() {

        binding.botonRegistrarse.setOnClickListener {
            registrarse()
        }

        binding.botonVolverAInicioDeSesion.setOnClickListener {
            irAInicioDeSesion()
        }

        binding.checkEmpresa.setOnCheckedChangeListener { compoundButton, isChecked ->

            if (isChecked) {
                binding.spinnerSexo.visibility = android.view.View.GONE
                binding.spinnerSexo.isEnabled = false
            } else {
                binding.spinnerSexo.visibility = android.view.View.VISIBLE
                binding.spinnerSexo.isEnabled = true
            }

        }

    }

    private fun cargarSpinnerSexos() {

        WebServiceSexo.getAllSexos(this, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // todo error
                } else {

                    Log.d("::::", "sexos: $any")

                    val sexos = any as SexoWrapper
                    // creo un array adapter para el spinner
                    val adapter = ArrayAdapter(this@RegistroActivity, android.R.layout.simple_spinner_item, sexos)
                    // seteo el layout para el spinner
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // seteo el adapter al spinner
                    binding.spinnerSexo.adapter = adapter

                }

            }
        })

    }

    private fun registrarse() {

        val cuenta: Usuario
        val correo = binding.inputCorreoRegistro.text.toString()
        val nickname = binding.inputNickname.text.toString()
        val contrasena = binding.inputContrasenaRegistro.text.toString()
        val contrasena2 = binding.inputContrasena2Registro.text.toString()
        val isEmpresa = binding.checkEmpresa.isChecked
        val sexo: Sexo? = binding.spinnerSexo.selectedItem as Sexo?

        if (correo.isEmpty() || nickname.isEmpty() || contrasena.isEmpty() || contrasena2.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isCorreoElectronico(correo)) {
            Toast.makeText(this, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (contrasena != contrasena2) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEmpresa && sexo == null) {
            Toast.makeText(this, "Selecciona un sexo.", Toast.LENGTH_SHORT).show()
            return
        }

        cuenta = if (isEmpresa) {
            Empresa(correo, contrasena, nickname)
        } else {
            Persona(correo, contrasena, nickname, sexo!!)
        }

        cuenta.contrasena = CreateMD5().create(cuenta.contrasena)

        Log.d(":::","cuenta: $cuenta")

        WebServiceUsuario.registrarse(cuenta, this, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    // TODO ERROR
                } else {
                    irAInicioDeSesion()
                    Toast.makeText(this@RegistroActivity, "Registro exitoso, inicie sesión", Toast.LENGTH_SHORT).show()
                }

            }
        })

    }

    private fun isCorreoElectronico(correo: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    private fun irAInicioDeSesion() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }



}