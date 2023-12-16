package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.medease.Modal.Profiledata
import com.example.medease.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val docref = db.collection("Users").document(user?.email.toString()).collection("Profile").document("Profile")

    private lateinit var mygooglesigninclient: GoogleSignInClient

    private lateinit var binding: ActivityHomeBinding


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    } 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.supportToolbar)
        supportActionBar?.title = "MedEase"

        var bottomnav = binding.bottomNavigationView

        docref.get().addOnSuccessListener { document->
            if (document.exists()) {
                // Document exists, update it with the new data
                docref.update("name",user?.displayName.toString())
                    .addOnSuccessListener {
                        //Toast.makeText(this,"Profile updated succesfuly",Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,"failed to update Profile ",Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Document doesn't exist, create it with the new data
                val profiledata = Profiledata(user?.displayName.toString(),"Empty","Empty","Empty","Empty")
                docref.set(profiledata)
                    .addOnSuccessListener {
                        Toast.makeText(this,"success to add Profile ",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,"failed to update Profile y",Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this,"failed to get Profile ",Toast.LENGTH_SHORT).show()
        }


        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,Home_fragment()).commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            var fragment: Fragment? = null
            when (it.itemId) {
                R.id.menu_home -> {
                    fragment = Home_fragment()
                    supportActionBar?.title = "MedEase"
                }
                R.id.menu_medication -> {
                    fragment = Medication_fragment()
                    supportActionBar?.title = "Medications"
                }
                R.id.menu_medrec -> {
                    fragment = MedicalRecord_fragment()
                    supportActionBar?.title = "Medical Records"
                }
                R.id.menu_profile -> {
                    fragment = ProfileFragment()
                    supportActionBar?.title = "Profile"
                }
                R.id.menu_appointments -> {
                    fragment = AppointmentFragment()
                    supportActionBar?.title = "Schedule Appointment"
                }
            }

            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
                return@setOnItemSelectedListener true
            }

            return@setOnItemSelectedListener false
        }






        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestProfile()
            .build()


        mygooglesigninclient = GoogleSignIn.getClient(this, gso)


    }

    private fun signout() {
        auth.signOut()
        mygooglesigninclient.signOut().addOnCompleteListener(this) {
            startActivity(Intent(this@Home, SignInActivity::class.java))
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.toolbar_profile -> {supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            binding.bottomNavigationView.selectedItemId = R.id.menu_profile}
        }
        return super.onOptionsItemSelected(item)
    }


}