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
import com.example.medease.Modal.MedRec
import com.example.medease.R
import com.example.medease.add_edit_medrec
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson



class medrecAdapter(val medreclist: MutableList<MedRec>,val deletable : Boolean) : RecyclerView.Adapter<medrecAdapter.medrecViewHolder>() {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString()).collection("Medical Records")
    var storageref= FirebaseStorage.getInstance()
    inner class medrecViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val selectedmedrec = view.findViewById<CardView>(R.id.medrecitem_layout)
        val tittle = view.findViewById<TextView>(R.id.medrec_tittle)
        val Desc = view.findViewById<TextView>(R.id.medrec_desc)
        val fileName = view.findViewById<TextView>(R.id.medrec_uploadedfilename)
        val date = view.findViewById<TextView>(R.id.medrec_dateuploaded)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): medrecAdapter.medrecViewHolder {
        return medrecViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.med_rec_item,parent,false))
    }

    override fun onBindViewHolder(holder: medrecAdapter.medrecViewHolder, position: Int) {
        holder.tittle.text =medreclist[position].Tittle
        holder.Desc.text =medreclist[position].Description
        holder.fileName.text = medreclist[position].fileName
        holder.date.text = medreclist[position].Date


        holder.selectedmedrec.setOnClickListener {
            val selectedMedRec = medreclist[position]
//converting this to a json to pass it to next activity
            val selectedMedRecJson = Gson().toJson(selectedMedRec)
//this .context refer to the parant activity context which is hosting the recycler view
            val intent = Intent(holder.selectedmedrec.context,add_edit_medrec::class.java)
            intent.putExtra("SELECTED_MEDREC",selectedMedRecJson)
            intent.putExtra("EDITABLE",deletable)
            holder.selectedmedrec.context.startActivity(intent)
        }

        holder.selectedmedrec.setOnLongClickListener {
            if (deletable){
                val medData = medreclist[position]

                // Create a confirmation dialog using AlertDialog
                val builder = AlertDialog.Builder(holder.selectedmedrec.context)
                builder.setTitle("Confirmation")
                builder.setMessage("Are you sure you want to delete this medication?")
                builder.setPositiveButton("Yes") { _, _ ->
                    // Handle the delete confirmation
                    medreclist.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, medreclist.size)

                    // Perform the actual delete operation (e.g., from Firebase Firestore)
                    collRef.document(medData.Tittle).delete().addOnSuccessListener {
                        Toast.makeText(holder.selectedmedrec.context, "Record Deleted", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(holder.selectedmedrec.context, "Failed to delete record", Toast.LENGTH_SHORT).show()
                    }
                    if (medreclist[position].fileurl!=null){
                        storageref.getReferenceFromUrl(medreclist[position].fileurl).delete().addOnSuccessListener {
                            Toast.makeText(holder.selectedmedrec.context, "record pdf deleted Deleted", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(holder.selectedmedrec.context, "failed to delete record pdf", Toast.LENGTH_SHORT).show()
                        }
                    }





                }
                builder.setNegativeButton("No") { _, _ ->
                    // Do nothing or dismiss the dialog
                }
                builder.show()
            }else{
                Toast.makeText(holder.selectedmedrec.context, "Non deletable", Toast.LENGTH_SHORT).show()

            }

            true
        }
    }

    override fun getItemCount(): Int {
       return medreclist.size
    }
}