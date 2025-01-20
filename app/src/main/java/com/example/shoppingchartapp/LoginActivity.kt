package com.example.shoppingchartapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.shoppingchartapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val userEmail = auth.currentUser?.email
        binding.textViewUser.text = userEmail

        binding.logOutButton.setOnClickListener{
            auth.signOut()
            binding.textViewUser.text = ""
        }

        binding.buttonRegister.setOnClickListener{
            auth.createUserWithEmailAndPassword(
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString()
            ).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_LONG).show()
                    auth.signInWithEmailAndPassword(
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    )
                    this.finish()
                }
                else{
                    Toast.makeText(this, "Problem with registration: " + it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.buttonLogin.setOnClickListener {
            auth.signInWithEmailAndPassword(
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString()
            ).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this, "Signed in successfully", Toast.LENGTH_LONG).show()
                    this.finish()
                }
                else{
                    Toast.makeText(this, "Problem with sign in: " + it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}