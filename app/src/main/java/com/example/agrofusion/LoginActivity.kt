package com.example.agrofusion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.agrofusion.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneFocusListener()
        passwordFocusListener()
        binding.loginButton.setOnClickListener {
            submitDetails()
        }
    }

    private fun submitDetails() {

        validLogin()

//        binding.phoneContainer.helperText = validPhone()
//        binding.passwordContainer.helperText = validPassword()
//
//        val validPhone =binding.phoneContainer.helperText == null
//        val validPassword =binding.passwordContainer.helperText == null



//        if( validPhone && validPassword) validLogin()
//        else invalidLogin()
    }

    private fun invalidLogin() {

        var message = ""

        if(binding.passwordContainer.helperText != null) message += "\n\nPassword: " + binding.passwordContainer.helperText
        if(binding.phoneContainer.helperText != null) message += "\n\nPhone: " + binding.phoneContainer.helperText

        AlertDialog.Builder(this)
            .setTitle("Invalid Login")
            .setMessage(message)
            .setPositiveButton("Okay"){_,_ ->
                // What to do? Do Nothing
            }
            .show()
    }

    private fun validLogin() {
        Toast.makeText(this, "Successful Login", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun phoneFocusListener() {
        binding.phoneContainer.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.phoneContainer.helperText = validPhone()
            }
        }
    }

    private fun validPhone(): String? {
        val phoneText = binding.phoneEditText.text.toString()
        if(phoneText.length != 10) return "Phone number must 10 digits"
        if (!phoneText.matches(".*[0-9].*".toRegex())) return "Must be all digits"
        return null
    }

    private fun passwordFocusListener(){
        binding.passwordEditText.setOnFocusChangeListener { _, focused ->
            if(!focused){
                binding.passwordContainer.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordEditText.text.toString()

        if(passwordText.length < 8) return "Minimum 8 Character Password"

        if (!passwordText.matches(".*[A-Z].*".toRegex())) return "Password must contain 1 Upper-case character"

        if (!passwordText.matches(".*[a-z].*".toRegex())) return "Password must contain 1 Lower-case character"

        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) return "Password must contain 1 Special character (@#\$%^&+=)"
        return null
    }


}