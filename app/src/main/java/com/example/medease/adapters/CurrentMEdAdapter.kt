package com.example.medease.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.Modal.currentmed
import com.example.medease.R
import com.example.medease.addedit_medication
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

val db = FirebaseFirestore.getInstance()
val auth = FirebaseAuth.getInstance()
val collRef =
    db.collection("Users").document(auth.currentUser?.email.toString()).collection("Medications")

class CurrentMEdAdapter(val CurrentMedList: MutableList<currentmed>,val deleteable:Boolean) :
    RecyclerView.Adapter<CurrentMEdAdapter.currentmedViewHolder>() {

    var MedList = mutableListOf<currentmed>()

    inner class currentmedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val medicinename = view.findViewById<TextView>(R.id.Medicine_name)
        val medicinenameimg = view.findViewById<TextView>(R.id.image_textview)
        val dose = view.findViewById<TextView>(R.id.dose_and_days)
        val intruction = view.findViewById<TextView>(R.id.intruction)
        val selectedMed = view.findViewById<CardView>(R.id.currentmed_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrentMEdAdapter.currentmedViewHolder {
        return currentmedViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.currentmedicine_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return CurrentMedList.size
    }

    override fun onBindViewHolder(holder: currentmedViewHolder, position: Int) {

        val medData = CurrentMedList[position]


        holder.medicinename.text = medData.MedName
        holder.dose.text = "Dose : " + medData.Dose + " | Till : " + medData.NoofDays + " Days"
        if (medData.aftermeal) {
            holder.intruction.text = "After Meal"
        } else {
            holder.intruction.text = "Before Meal"
        }
        holder.medicinenameimg.text = medData.MedName.substring(0, 2).uppercase()

        holder.selectedMed.setOnClickListener {
            val jsondata = Gson().toJson(medData)
            val intent = Intent(holder.selectedMed.context, addedit_medication::class.java)
            intent.putExtra("SELECTED_MED", jsondata)
            intent.putExtra("EDITABLE",deleteable)
            holder.selectedMed.context.startActivity(intent)
        }


        holder.selectedMed.setOnLongClickListener {
          if (deleteable){
              val medData = CurrentMedList[position]

              // Create a confirmation dialog using AlertDialog
              val builder = AlertDialog.Builder(holder.selectedMed.context)
              builder.setTitle("Confirmation")
              builder.setMessage("Are you sure you want to delete this medication?")
              builder.setPositiveButton("Yes") { _, _ ->
                  // Handle the delete confirmation
                  // Perform the actual delete operation (e.g., from Firebase Firestore)
                  collRef.document(medData.MedName).delete().addOnSuccessListener {
                      Toast.makeText(holder.selectedMed.context, "Record Deleted", Toast.LENGTH_SHORT).show()
                  }.addOnFailureListener {
                      Toast.makeText(holder.selectedMed.context, "Failed to delete record", Toast.LENGTH_SHORT).show()
                  }

                  CurrentMedList.removeAt(position)
                  notifyItemRemoved(position)
              }
              builder.setNegativeButton("No") { _, _ ->
                  // Do nothing or dismiss the dialog
              }
              builder.show()
          }else{
              Toast.makeText(holder.selectedMed.context, "Cannot edit history", Toast.LENGTH_SHORT).show()
          }

            true
        }

    }





}