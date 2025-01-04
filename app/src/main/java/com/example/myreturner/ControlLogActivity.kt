package com.example.myreturner

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myreturner.databinding.ActivityControlLogBinding
import com.google.android.material.button.MaterialButton

private const val SETTINGS = "SETTINGS"
private const val PASSWORD = "PASSWORD"

class ControlLogActivity : AppCompatActivity() {

    private lateinit var editPassword: EditText
    private lateinit var loginBtn: MaterialButton
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityControlLogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editPassword = binding.editPassword
        loginBtn = binding.login
        loginBtn.setOnClickListener {

            if (editPassword.length() == 0){
                createDialog(
                    resources.getString(R.string.the_password_is_incorrect),
                    resources.getString(R.string.enter_correct_password)
                )
                alertDialog?.show()
            }
            if (editPassword.length() != 0) {
                val password = editPassword.text.toString()
                val passwordCorrect =
                    getSharedPreferences(SETTINGS, MODE_PRIVATE).getString(PASSWORD, "")
                if (password == passwordCorrect) {
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.login_completed),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    openDoubleSetActivity()
                    finish()
                } else {
                    createDialog(
                        resources.getString(R.string.the_password_is_incorrect),
                        resources.getString(R.string.enter_correct_password)
                    )
                    alertDialog?.show()

                }


            }

        }
    }

    private fun createDialog(messageOk: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(messageOk)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setNegativeButton(resources.getString(R.string.closed)) { _: DialogInterface, _: Int ->
        }
        alertDialog = alertDialogBuilder.create()
    }

    private fun openDoubleSetActivity() {


        val runSettings = Intent(
            applicationContext,
            DoubleSetActivity::class.java
        )

        startActivity(runSettings)

    }

}