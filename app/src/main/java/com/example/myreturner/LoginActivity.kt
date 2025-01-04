package com.example.myreturner

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myreturner.databinding.ActivityLoginBinding

private const val SETTINGS = "SETTINGS"
private const val PASSWORD = "PASSWORD"
private const val IS_FIRST_START = "IS_FIRST_START"

class LoginActivity : AppCompatActivity() {
    private lateinit var enterPasswordBtn: Button
    private lateinit var editPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editPassword = binding.editPassword
        enterPasswordBtn = binding.enterPassword
        enterPasswordBtn.setOnClickListener {
            if (editPassword.length() != 0) {
                val password = editPassword.text.toString()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit().putString(PASSWORD, password)
                    .apply()
                getSharedPreferences(SETTINGS, MODE_PRIVATE).edit().putBoolean(IS_FIRST_START, true).apply()
                Toast.makeText(applicationContext, resources.getString(R.string.
                password_saved), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(applicationContext, resources.getString(R.string.enter_your_new_password), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

}