package com.example.whatsapp.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.whatsapp.Constanse
import com.example.whatsapp.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.ByteArrayOutputStream
import java.io.File

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private var databaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var myUri: String = ""

    //private var uploadTask: StorageTask? = null
    var currentUser: FirebaseUser? = null
    private var mStorage: StorageReference? = null
    var mDatabase: DatabaseReference? = null
    var GALLERY_ID = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mAuth = FirebaseAuth.getInstance()
        initViews()
    }

    private fun initViews() {
        currentUser = FirebaseAuth.getInstance().currentUser
        btn_change_image_settings.setOnClickListener(this)
        btn_change_status_settings.setOnClickListener(this)

        val userId = currentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("Users").child(userId)

        mStorage = FirebaseStorage.getInstance().reference

        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var displayName = snapshot.child("display_name").value
                var image = snapshot.child("image").value.toString()
                var status = snapshot.child("status").value
                var thumb_image = snapshot.child("thumb_image").value

                txt_name_settings_activity.text = displayName.toString()
                txt_status_settings_activity.text = status.toString()

                if (image != "default") {
                    Picasso.get().load(image).placeholder(R.drawable.placeholder).into(img_profile_settings)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            btn_change_status_settings.id -> {
                var statusIntent = Intent(this, StatusActivity::class.java)
                statusIntent.putExtra(Constanse.STATUS, txt_status_settings_activity.text.toString())
                startActivity(statusIntent)

            }
            btn_change_image_settings.id -> {
                var galleryIntent = Intent()
                galleryIntent.type = "image/*"
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(galleryIntent, "Choose Image"),
                    GALLERY_ID)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK && data != null) {
            var imageUri: Uri? = data.data
            CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(this)
        }
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            var result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var userId = currentUser!!.uid
                var thumbFile = File(resultUri.path)

                var thumbBitMap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(65)
                    .compressToBitmap(thumbFile)
                var byteArray = ByteArrayOutputStream()
                thumbBitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

                var thumbByteArray: ByteArray = byteArray.toByteArray()

                var filePath = mStorage!!.child("chat_profile_images")
                    .child(userId + ".jpg")

                var thumbFilePath = mStorage!!.child("thumb").child(userId + ".jpg")


                filePath.putFile(resultUri)
                    .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                        if (task.isSuccessful) {
                            filePath.downloadUrl.addOnSuccessListener { uri: Uri ->
                                if (uri != null) {
                                    val downloadUrl = uri.toString()
                                    var uploadTask = thumbFilePath.putBytes(thumbByteArray)
                                    uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                                      thumbFilePath.downloadUrl.addOnSuccessListener {
                                          uri:Uri->
                                          if (uri!=null){
                                              var thumbUrl=uri.toString()
                                              if (task.isSuccessful) {
                                                  var updateObject = HashMap<String, Any>()
                                                  updateObject.put("image", downloadUrl)
                                                  updateObject.put("thumb_image", thumbUrl)
                                                  mDatabase!!.updateChildren(updateObject)
                                                      .addOnCompleteListener { task: Task<Void> ->
                                                          if (task.isSuccessful) {
                                                              Toast.makeText(this,
                                                                  "isSuccessful",
                                                                  Toast.LENGTH_LONG).show()

                                                          }
                                                      }
                                              }
                                          }
                                      }
                                    }
                                }
                                }
                            }
                        }


                    }
            }
        }
    }

