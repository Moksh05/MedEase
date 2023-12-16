package com.example.medease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.databinding.ActivitySigninBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    lateinit var mygooglesigninclient: GoogleSignInClient
    val Req_Code: Int = 123
    private var firebaseAuth = FirebaseAuth.getInstance()
    val currentuser = firebaseAuth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // default google sign in rather than play games
                .requestIdToken(getString(R.string.web_client_id)).requestEmail().build()
        //above: it request google for email id of the user to give to us
        Log.d("loginfailure", "working1")

        mygooglesigninclient = GoogleSignIn.getClient(this, gso)
        Log.d("loginfailure", "working2")


        binding.signupText.setOnClickListener {
            binding.LoginText.setText("Sign Up")
            binding.welcomeRegister.setText("Register Yourself")
            binding.viewflipper.setOutAnimation(this, R.anim.slide_left)
            binding.viewflipper.setInAnimation(this, R.anim.slide_right)
            binding.viewflipper.showNext()
        }
        binding.signinText.setOnClickListener {
            binding.LoginText.setText("Login")
            binding.welcomeRegister.setText("Welcome User")
            binding.viewflipper.setInAnimation(this, R.anim.slide_left)
            binding.viewflipper.setOutAnimation(this, R.anim.slide_right)
            binding.viewflipper.showPrevious()
        }
        binding.loginButton.setOnClickListener {
            if (binding.signinUsername.text.toString().trim()
                    .isEmpty() || binding.signinPassword.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(this, "Please Enter Username and password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                signin()
            }
        }

        binding.signupButton.setOnClickListener {
            if (binding.signupUsername.text.toString().trim()
                    .isEmpty() || binding.signupEmail.text.toString().trim()
                    .isEmpty() || binding.signupPassword.text.toString().trim()
                    .isEmpty() || binding.signupConfirmpassword.text.toString().trim()
                    .isEmpty()
            ) {
                Toast.makeText(this, "Please fill All the fields", Toast.LENGTH_SHORT).show()
            } else {
                signup()
            }


        }

        binding.signinWithGoogle.setOnClickListener {
            Toast.makeText(this, "Please Wait! Opening Google login", Toast.LENGTH_SHORT).show()
            signInwithgoogle()
        }

    }

    private fun signin() {
        val email = binding.signinUsername.text.toString().trim()
        val password = binding.signinPassword.text.toString().trim()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (firebaseAuth.currentUser?.isEmailVerified!!) {


                    Toast.makeText(this, "Email is verified , Signing In", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@SignInActivity, Home::class.java))





                } else {
                    Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
                }

            }

        }.addOnFailureListener {

            Toast.makeText(this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signup() {
        val username = binding.signupUsername.text.toString().trim()
        val Email = binding.signupEmail.text.toString().trim()
        val password = binding.signupPassword.text.toString().trim()
        val cnfpasssword = binding.signupConfirmpassword.text.toString().trim()
        if (!Email.contains("@gmail.com")) {
            Toast.makeText(
                this,
                "Please use a valid email, provided by gmail.com",
                Toast.LENGTH_SHORT
            ).show()
            binding.signupEmail.text = null
            return
        }

        if (password != cnfpasssword) {
            Toast.makeText(
                this,
                "The password doesnt match please enter cacrefully",
                Toast.LENGTH_SHORT
            ).show()
            binding.signupConfirmpassword.text = null
            return
        }

        if (password.length < 6) {
            Toast.makeText(
                this,
                "Enter a valid password , having atleast 6 characters",
                Toast.LENGTH_SHORT
            ).show()
            binding.signupPassword.text = null
            return
        }



        firebaseAuth.createUserWithEmailAndPassword(Email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.user?.updateProfile(userProfileChangeRequest { displayName = username })
                firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Account Created, Please verify email and log in",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.viewflipper.displayedChild = 0

                    }
                }?.addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }


            }


        }.addOnFailureListener { e->
            Toast.makeText(
                this,
                "Signing Up failed please try again later \n ${e.toString()}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun signInwithgoogle() {

        val signINintent = mygooglesigninclient.signInIntent
        Log.d("loginfailure", "notworking1")
        launcher.launch(signINintent)

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                Log.d("loginfailure", "notworking2")
                handleResut(task)
            }
        }

    private fun handleResut(task: Task<GoogleSignInAccount>) {
        Log.d("loginfailure", "notworking3")
        if (task.isSuccessful) {
            Log.d("loginfailure", "notworking4 ${task.result.toString()}")
            val account: GoogleSignInAccount = task.result
            if (account != null) {
                UpdateUI(account)
            } else {
                Toast.makeText(this, "Login failed try again later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this@SignInActivity, Home::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login failed try again later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isEmailVerified!!) {
            startActivity(Intent(this@SignInActivity, Home::class.java))
            finish()
        }
    }
}