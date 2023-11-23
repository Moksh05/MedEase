package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.Appointment
import com.example.medease.adapters.jJOINappointmentADAPTER
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject



class Home_fragment : Fragment() {

    private lateinit var mygooglesigninclient: GoogleSignInClient
private lateinit var recyclerView: RecyclerView
    val db = FirebaseFirestore.getInstance()
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("Appointments")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)

        val sideusername = view.findViewById<TextView>(R.id.Hellouser)
        val Signoutbutton = view.findViewById<Button>(R.id.signout)
        recyclerView = view.findViewById(R.id.join_appointment_recyclerview)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestProfile()
            .build()

        mygooglesigninclient = GoogleSignIn.getClient(requireActivity(), gso)
        if (FirebaseAuth.getInstance().currentUser != null) {
            sideusername.text = "Hello!\n${FirebaseAuth.getInstance().currentUser?.displayName}"
        }

        Signoutbutton.setOnClickListener {
            signout()
        }


        view.findViewById<CardView>(R.id.medicationcard).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container,Medication_fragment()).commit()

            navView.selectedItemId = R.id.menu_medication
        }
        view.findViewById<CardView>(R.id.medreccard).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container,MedicalRecord_fragment()).commit()

            // Update the selected item in the bottom navigation
            navView.selectedItemId = R.id.menu_medrec // Replace with the ID of the desired navigation item

        }

        view.findViewById<CardView>(R.id.news_card).setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(),NewsArticles::class.java))

        }

        recyclerView.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,true)
        getupcomingappointment()
    }

    private fun signout() {
        FirebaseAuth.getInstance().signOut()
        mygooglesigninclient.signOut().addOnCompleteListener(requireActivity()) {
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun getupcomingappointment(){

        collRef.get().addOnSuccessListener { documents ->
            var appointmentlist = mutableListOf<Appointment>()
            for (document in documents){
                if (document!= null){
                    Log.d("appointmentcrash","failed at line 59")
                    var appointment = document.toObject(Appointment::class.java)

                    Log.d("DocCatCrash","${document.toObject<Appointment>()}Error at 115")
                    appointmentlist.add(appointment)
                }
            }
            val adapter = jJOINappointmentADAPTER(appointmentlist)
            recyclerView.adapter = adapter
            Log.d("appointmentcrash","failed at line 67")
        }.addOnFailureListener { e->
            Toast.makeText(requireActivity(),"Failed to retrieve  $e ", Toast.LENGTH_LONG).show()
        }

    }

}