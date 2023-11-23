package com.example.medease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medease.Modal.Profiledata
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {


    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()
    var ref = db.collection("Users").document(auth?.currentUser?.email.toString()).collection("Profile").document("Profile")

    var displayname = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        getdata()
        var email = auth.currentUser?.email
        view.findViewById<TextView>(R.id.email).setText("Email : \n$email")
        // Split the full name into words


        // Extract the first letters of the first name and last name

        view.findViewById<ImageView>(R.id.edit_button).setOnClickListener {

            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.editprofile_dialog, null)
            dialog.setContentView(view)



           val age = view.findViewById<TextInputEditText>(R.id.editAge)
           val sex = view.findViewById<TextInputEditText>(R.id.editsex)
           val address = view.findViewById<TextInputEditText>(R.id.editaddress)
           val phnno = view.findViewById<TextInputEditText>(R.id.editcontact)


            view.findViewById<Button>(R.id.save_button).setOnClickListener{
                savedata(address.text.toString(),age.text.toString(),sex.text.toString(),phnno.text.toString())
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.cancelbutton).setOnClickListener{
                dialog.dismiss()
            }

            dialog.show()
        }





    }


    private fun getdata(){
        ref.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Convert Firestore document to your Profiledata class
                    val profileData = document.toObject(Profiledata::class.java)

                    if (profileData != null) {
                        // Use the retrieved data
                        displayname = profileData.name
                        val address = profileData.address
                        val age = profileData.age
                        val sex = profileData.sex
                        val contactNo = profileData.contactNo

                        val nameParts = displayname?.split(" ")
                        val firstNameFirstLetter = nameParts?.firstOrNull()?.get(0)
                        val lastNameFirstLetter = nameParts?.lastOrNull()?.get(0)
                        view?.findViewById<TextView>(R.id.Username)?.setText(displayname)

                        view?.findViewById<TextView>(R.id.image_textview)?.setText("$firstNameFirstLetter$lastNameFirstLetter")
                        view?.findViewById<TextView>(R.id.profilepic_text)?.setText("$firstNameFirstLetter$lastNameFirstLetter")
                        view?.findViewById<TextView>(R.id.address)?.setText("Address : $address")
                        view?.findViewById<TextView>(R.id.Phone_no)?.setText("PhoneNo : $contactNo")
                        view?.findViewById<TextView>(R.id.sex)?.setText("Sex : $sex")
                        view?.findViewById<TextView>(R.id.age)?.setText("Age : $age")
                    } else {
                        // Handle the case where conversion to Profiledata fails
                    }
                } else {
                    // Handle the case where the document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    private fun savedata(Address: String?,  Age : String?,  Sex: String?,  contactNo:String?){

        val data = Profiledata(displayname,Address,Age,Sex,contactNo)

        ref.set(data)
            .addOnSuccessListener {
                Toast.makeText(requireActivity(), "Profile data added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity(), "Failed to add profile data: $e", Toast.LENGTH_SHORT).show()
            }

    }
}