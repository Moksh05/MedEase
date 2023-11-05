package com.example.medease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.MedRec
import com.example.medease.adapters.medrecAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MedicalRecord_fragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString()).collection("Medical Records")

    private lateinit var recyclerveiw: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical_record_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.d("medrecfailure", "Working till line 40")
        val addbutton = view.findViewById<FloatingActionButton>(R.id.add_medical_record_button)

        recyclerveiw = view.findViewById<RecyclerView>(R.id.medrec_recyclerview)

        recyclerveiw.layoutManager = LinearLayoutManager(requireActivity())


        getmedrec()

        addbutton.setOnClickListener {
            startActivity(Intent(requireActivity(),add_edit_medrec::class.java))
        }


    }
    private fun getmedrec(){
        collRef.get().addOnSuccessListener { querySnapshot ->
            var documentList = mutableListOf<MedRec>()
            Log.d("medrecfailure", "Working till line 60 ${querySnapshot.documents.toString()}")
            for (document in querySnapshot){

                if (document!= null){
                    Toast.makeText(requireActivity(),"${document.id}",Toast.LENGTH_SHORT).show()
                    val title = document.getString("tittle")?: ""
                    val description = document.get("description")?:""
                    Log.d("medrecfailure", " line 67 $description  hii ${document.get("description")}")
                    val fileName = document.getString("fileName")?:""
                    val fileurl = document.getString("fileurl")?:""
                    val date = document.getString("date")?:""

                    val medRec = MedRec(title, description.toString(), date, fileName, fileurl)
                    documentList.add(medRec)

                }
                Log.d("medrecfailure", "Working till line 74")
                recyclerveiw.adapter = medrecAdapter(documentList)

            }


            Log.d("medrecfailure", "Working till line 80")

        }.addOnFailureListener { e ->
            Toast.makeText(requireActivity(),"failed to fetch med rec $e", Toast.LENGTH_LONG).show()
        }
    }



}