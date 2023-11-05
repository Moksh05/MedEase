package com.example.medease

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.Modal.MedRec
import com.example.medease.databinding.ActivityAddEditMedrecBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class add_edit_medrec : AppCompatActivity() {


    var taskOngoing = 0
    private lateinit var binding: ActivityAddEditMedrecBinding
    private lateinit var uri:Uri
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var storageref= FirebaseStorage.getInstance().reference.child("Medical Records").child(auth.currentUser?.email.toString())
    private lateinit var  launcher : ActivityResultLauncher<Intent>
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString()).collection("Medical Records")

    private lateinit var displayName:String
    private lateinit var downloadUrl:String

    var downloaded = 0

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditMedrecBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var formatter = SimpleDateFormat("EEE, d MMM YYYY HH:mm a")


        val selectedMedrecJson = intent.getStringExtra("SELECTED_MEDREC")
        if (selectedMedrecJson!=null){
            val gson = Gson()
            val MedRECInfo = gson.fromJson(selectedMedrecJson,MedRec::class.java)

            binding.addmedrecTittleEdittext.setText(MedRECInfo.Tittle)
            binding.addmedrecDescEdittext.setText(MedRECInfo.Description)
            binding.attachmentPreview.setText(MedRECInfo.fileName)
            binding.attachmentview.visibility = View.VISIBLE

            binding.attachmentview.setOnClickListener {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val duri= Uri.parse(MedRECInfo.fileurl)

               if (downloaded ==0){
                   downloaded=1
                   val request = DownloadManager.Request(duri)
                   request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,MedRECInfo.fileName)
                   request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                   downloadManager.enqueue(request)
               }else{
                   Toast.makeText(this,"ALready downloaded",Toast.LENGTH_SHORT).show()
               }
            }

        }



        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                taskOngoing = 1
                uri = it.data?.data!!

                // Upload the file to Firebase Storage
                val uploadTask = storageref.child(uri.lastPathSegment.toString()).putFile(uri)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(this, "Document has been uploaded", Toast.LENGTH_SHORT).show()

                    // Get the download URL
                    storageref.child(uri.lastPathSegment.toString()).downloadUrl.addOnSuccessListener { downloadUri ->
                        downloadUrl = downloadUri.toString()

                        // Get the display name using a cursor
                        val cursor = this@add_edit_medrec.contentResolver.query(it.data?.data!!, null, null, null, null)
                        cursor?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                                binding.attachmentPreview.visibility = View.VISIBLE
                                binding.attachmentPreview.text = displayName

                                // Proceed with Firestore document creation here
                                val MedicalRecord = MedRec(
                                    binding.addmedrecTittleEdittext.text.toString(),
                                    binding.addmedrecDescEdittext.text.toString(),
                                    formatter.format(Date()), displayName, downloadUrl
                                )

                                collRef.document(binding.addmedrecTittleEdittext.text.toString())
                                    .set(MedicalRecord)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "MEDICAL RECORD UPLOADED", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error occurred, unable to upload: $e", Toast.LENGTH_SHORT).show()
                                    }
                                taskOngoing =0

                            }
                        }
                    }
                }
            }
        }



        binding.doneButtonMedrec.setOnClickListener {
            if (taskOngoing == 0){
                onBackPressed()
            }else{
                Toast.makeText(this,"Please wait the task is being done",Toast.LENGTH_SHORT).show()
            }

        }

        binding.attachMedicalRecordButton.setOnClickListener {
            showfile()
        }
        binding.backButtonMedrec.setOnClickListener {
            onBackPressed()
        }


    }

    private fun showfile(){

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"

        launcher.launch(intent)



    }

}