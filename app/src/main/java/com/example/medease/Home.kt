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
    val docref = db.collection("Users").document(user?.email.toString())

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

        docref.get().addOnSuccessListener { document->
            if (document.exists()) {
                // Document exists, update it with the new data
                docref.collection("Profile").document("Profile").update("Name",user?.displayName.toString())
                    .addOnSuccessListener {
                        Toast.makeText(this,"Profile updated succesfuly",Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,"failed to update Profile ",Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Document doesn't exist, create it with the new data
                docref.collection("Profile").document("Profile").set(hashMapOf("Name" to user?.displayName.toString()))
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

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerlayout,
            binding.supportToolbar,
            R.string.opn,
            R.string.cln
        )

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,Home_fragment()).commit()
        binding.sidenavvieew.setCheckedItem(R.id.menu_home)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.sidenavvieew.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId){
                    R.id.menu_logout -> signout()
                    R.id.menu_News -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, News_fragment()).commit()

                        supportActionBar?.title = "MedNews"
                    }
                    R.id.menu_home -> {supportFragmentManager.beginTransaction().replace(R.id.fragment_container,Home_fragment()).commit()
                    supportActionBar?.title = "MedEase"}
                    R.id.menu_medication -> {supportFragmentManager.beginTransaction().replace(R.id.fragment_container,Medication_fragment()).commit()
                        supportActionBar?.title = "Medications"}
                    R.id.menu_medrec -> {supportFragmentManager.beginTransaction().replace(R.id.fragment_container,MedicalRecord_fragment()).commit()
                        supportActionBar?.title = "Medical Records"}
                    R.id.menu_find_med_fac -> {supportFragmentManager.beginTransaction().replace(R.id.fragment_container,MapsFragment()).commit()
                        supportActionBar?.title = "Find Medical Facility"}
                    R.id.menu_appointments -> {supportFragmentManager.beginTransaction().replace(R.id.fragment_container,AppointmentFragment()).commit()
                        supportActionBar?.title = "Schedule Appointment"}
                }
                binding.drawerlayout.closeDrawers()
                return true
            }


        })
        val sidenavHeader = binding.sidenavvieew.getHeaderView(0)
        val sideUsername = sidenavHeader.findViewById<TextView>(R.id.Side_nav_username)
        sideUsername.text = user?.displayName




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
            R.id.toolbar_profile -> TODO("Start the next activity when clicked and show profile there there")
        }
        return super.onOptionsItemSelected(item)
    }
}