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


    private lateinit var medical : MedRec
    var taskOngoing = 0
    private lateinit var binding: ActivityAddEditMedrecBinding
    private lateinit var uri:Uri
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val MedrecHisCollref = db.collection("Users").document(auth.currentUser?.email.toString())
        .collection("MedRec History")
    var storageref= FirebaseStorage.getInstance().reference.child("Medical Records").child(auth.currentUser?.email.toString())
    private lateinit var  launcher : ActivityResultLauncher<Intent>
    val collRef = db.collection("Users").document(auth.currentUser?.email.toString()).collection("Medical Records")

    var displayName:String? = ""
    var downloadUrl:String? = ""
var editable= true
    var downloaded = 0
    var formatter = SimpleDateFormat("EEE, d MMM YYYY HH:mm a")
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditMedrecBinding.inflate(layoutInflater)
        setContentView(binding.root)





        val selectedMedrecJson = intent.getStringExtra("SELECTED_MEDREC")
        if (selectedMedrecJson!=null){
                val gson = Gson()
                val MedRECInfo = gson.fromJson(selectedMedrecJson,MedRec::class.java)

            if (intent.getBooleanExtra("EDITABLE",true)){


                binding.addmedrecTittleEdittext.setText(MedRECInfo.Tittle)
                binding.addmedrecDescEdittext.setText(MedRECInfo.Description)

                if (MedRECInfo.fileName == "" || MedRECInfo.fileName.isNullOrBlank()){
                    binding.attachmentPreview.visibility = View.GONE
                    binding.attachmentview.visibility = View.GONE
                }else{
                    binding.attachmentPreview.setText(MedRECInfo.fileName)
                    binding.attachmentview.visibility = View.VISIBLE
                    binding.attachmentview.visibility = View.VISIBLE
                }


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
            else{
                binding.doneButtonMedrec.isClickable = false
                binding.doneButtonMedrec.visibility = View.GONE
                binding.attachMedicalRecordButton.visibility = View.GONE

                binding.addmedrecDescEdittext.isFocusable = false;
                binding.addmedrecDescEdittext.isFocusableInTouchMode = false;
                binding.addmedrecDescEdittext.isClickable = false;

                binding.attachmentPreview.isFocusable = false;
                binding.attachmentPreview.isFocusableInTouchMode = false;
                binding.attachmentPreview.isClickable = false;

                binding.addmedrecTittleEdittext.isFocusable = false;
                binding.addmedrecTittleEdittext.isFocusableInTouchMode = false;
                binding.addmedrecTittleEdittext.isClickable = false;

                binding.addmedrecTittleEdittext.setText(MedRECInfo.Tittle)
                binding.addmedrecDescEdittext.setText(MedRECInfo.Description)
                binding.attachmentPreview.setText(MedRECInfo.fileName)
                binding.attachmentPreview.visibility = View.VISIBLE
                binding.attachmentview.visibility = View.VISIBLE
                binding.backButtonMedrec.setOnClickListener {
                    onBackPressed()
                }
            }


        }
        else{
            binding.doneButtonMedrec.setOnClickListener {
                if (taskOngoing == 0){
                    adddata()
                    setdata()
                    onBackPressed()
                }else{
                    Toast.makeText(this,"Please wait the task is being done",Toast.LENGTH_SHORT).show()
                }

            }



            binding.attachMedicalRecordButton.setOnClickListener {
                showfile()
            }

        }




        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                taskOngoing = 1
                uri = it.data?.data!!

                val cursor = this@add_edit_medrec.contentResolver.query(it.data?.data!!, null, null, null, null)
                cursor?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                        binding.attachmentPreview.visibility = View.VISIBLE
                        binding.attachmentview.visibility = View.VISIBLE
                        binding.attachmentPreview.text = displayName

                        // Proceed with Firestore document creation here


                        taskOngoing =0

                    }
                }

                // Upload the file to Firebase Storage
                val uploadTask = storageref.child(uri.lastPathSegment.toString()).putFile(uri)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(this, "Document has been uploaded", Toast.LENGTH_SHORT).show()

                    // Get the download URL
                    storageref.child(uri.lastPathSegment.toString()).downloadUrl.addOnSuccessListener { downloadUri ->
                        downloadUrl = downloadUri.toString()

                        // Get the display name using a cursor

                    }
                }
            }
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

    private fun adddata(){


        medical = MedRec(
            binding.addmedrecTittleEdittext.text.toString(),
            binding.addmedrecDescEdittext.text.toString(),
            formatter.format(Date()), displayName, downloadUrl
        )

        binding.attachmentview.visibility = View.VISIBLE
        binding.attachmentPreview.visibility = View.VISIBLE
        binding.attachmentPreview.setText(displayName)
    }
    private fun setdata(){
        collRef.document(binding.addmedrecTittleEdittext.text.toString())
            .set(medical)
            .addOnSuccessListener {
                Toast.makeText(this, "MEDICAL RECORD UPLOADED", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error occurred, unable to upload: $e", Toast.LENGTH_SHORT).show()
            }

        MedrecHisCollref.document(binding.addmedrecTittleEdittext.text.toString())
            .set(medical)
            .addOnSuccessListener {
                Toast.makeText(this, "MEDICAL RECORD UPLOADED to history", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error occurred, unable to upload in history: $e", Toast.LENGTH_SHORT).show()
            }
    }
}