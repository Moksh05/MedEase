package com.example.medease

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.medease.Modal.currentmed
import com.example.medease.adapters.CurrentMEdAdapter
import com.example.medease.adapters.SuggestionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Medication_fragment : Fragment() {


    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString()).collection("Medications")

    private lateinit var currentmedRecyclerView: RecyclerView
    //private lateinit var RecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medication_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        currentmedRecyclerView = view.findViewById(R.id.currentmed_recyclerview)
        currentmedRecyclerView.layoutManager = LinearLayoutManager(requireActivity())


        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).setOnRefreshListener {
            view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false
            getMedicationdata()
        }

        getMedicationdata()


        //RecyclerView = view.findViewById(R.id.suggestion_recyclerveiw)
        //RecyclerView.layoutManager = LinearLayoutManager(requireActivity())
       //search

        view.findViewById<FloatingActionButton>(R.id.add_medication_button).setOnClickListener {
            startActivity(Intent(requireActivity(),addedit_medication::class.java))
        }

        view.findViewById<ImageView>(R.id.history).setOnClickListener {
            val intent = Intent(requireActivity(),History::class.java)
            intent.putExtra("HISTORY_TYPE","Medication")
            startActivity(intent)
        }



    }

    private fun getMedicationdata() {
        view?.findViewById<ProgressBar>(R.id.loading_bar)?.visibility = View.VISIBLE
        view?.findViewById<RecyclerView>(R.id.currentmed_recyclerview)?.visibility = View.GONE
        var currentMedList = mutableListOf<currentmed>()
        collRef.get().addOnSuccessListener { documents ->
            for (document in documents){
                Log.d("MedicationCrash","working till 66")
                if (document!=null){
                    val medname = document.getString("medName")
                    val dose = document.getString("dose")
                    val Noofdays = document.getString("noofDays")
                    val aftermeal = document.getBoolean("aftermeal")
                    val selectedtimeList = document.get("selectedtime") as MutableList<String>

                    Log.d("MedicationCrash","working till 73 $selectedtimeList")
                    Log.d("MedicationCrash","working till 73 $medname $dose $Noofdays $aftermeal")


                    val currentmeddata = currentmed(medname!!,dose!!,Noofdays!!,selectedtimeList,aftermeal!!)
                    Log.d("MedicationCrash","working till 78 $currentmeddata")
                    currentMedList.add(currentmeddata)
                    Log.d("MedicationCrash","working till 79 $currentMedList")
                }
            }
            var adapter= CurrentMEdAdapter(currentMedList,true)
            currentmedRecyclerView.adapter = adapter
            view?.findViewById<RecyclerView>(R.id.currentmed_recyclerview)?.visibility = View.VISIBLE
            view?.findViewById<ProgressBar>(R.id.loading_bar)?.visibility = View.GONE

        }.addOnFailureListener { e->
            view?.findViewById<ProgressBar>(R.id.loading_bar)?.visibility = View.GONE
            view?.findViewById<RecyclerView>(R.id.currentmed_recyclerview)?.visibility = View.VISIBLE
            Toast.makeText(requireActivity(),"Failure of task $e",Toast.LENGTH_LONG).show()
        }
    }


//    private fun search() {
//        Log.d("searchfailure", "Reached hte search function")
//        view?.findViewById<androidx.appcompat.widget.SearchView>(R.id.medication_searchbar)
//            ?.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
//                androidx.appcompat.widget.SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean {
//                    if (query != null) {
//                        db.collection("Medicine").get().addOnSuccessListener { documents ->
//
//                            val matchingMedicineNames = mutableListOf<String>()
//                            matchingMedicineNames.clear()
//
//                            for (document in documents) {
//                                val medicineName = document.id.toString().lowercase()
//
//                                // Check if the medicine name contains the search string
//                                if (medicineName.contains(query.lowercase())) {
//
//                                    matchingMedicineNames.add(medicineName)
//                                    RecyclerView.adapter = SuggestionAdapter(matchingMedicineNames)
//
//
//                                    Log.d(
//                                        "downloadurl",
//                                        "Med = ${matchingMedicineNames.get(0)} line 60"
//                                    )
//                                    Toast.makeText(
//                                        requireActivity(),
//                                        " Med = $matchingMedicineNames[0]",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//
//                            }
//                        }.addOnFailureListener { e ->
//                            Toast.makeText(requireActivity(), "${e.toString()}", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    if (newText != null) {
//                        Log.d("searchfailure", "Reached change is working hte search function")
//                        db.collection("Medicine").get().addOnSuccessListener { documents ->
//
//                            val matchingMedicineNames = mutableListOf<String>()
//                            matchingMedicineNames.clear()
//
//                            for (document in documents) {
//                                val medicineName = document.id.lowercase()
//
//
//                                if (medicineName.contains(newText.lowercase())) {
//                                    matchingMedicineNames.add(medicineName)
//                                    RecyclerView.adapter = SuggestionAdapter(matchingMedicineNames)
//                                }
//                            }
//                        }
//
//                            .addOnFailureListener { e ->
//                                Log.e("FirestoreError", "Error querying Firestore: $e")
//                            }
//                    }
//                    return true
//                }
//
//            })
//    }
//

}