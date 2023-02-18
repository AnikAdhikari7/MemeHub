package com.example.memehub

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.memehub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var currentImageUrl: String? = null
    private var githubRepo = "https://github.com/AnikAdhikari7/MemeHub"
    private var githubReleases = "Download MemeHub:\nhttps://github.com/AnikAdhikari7/MemeHub/releases"
    private lateinit var binding: ActivityMainBinding

    //variable for permission
    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title = "MemeHub"
        actionBar.subtitle = "developed by: github.com/AnikAdhikari7"


        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        memeLoad()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.github_logo -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubRepo))
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun memeLoad() {
        binding.progressBar.visibility = View.VISIBLE
        // Instantiate the RequestQueue.
        val imageUrl = "https://meme-api.com/gimme"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, imageUrl, null,
            { response ->
                currentImageUrl = response.getString("url")
                Glide.with(this).load(currentImageUrl).listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                }).into(binding.ivMemeImage)
            }, {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    fun btShare(view: View) {

        try {
            val bitmapDrawable = binding.ivMemeImage.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap
            val bitmapPath = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "$currentImageUrl", null)
            val bitmapUri = Uri.parse(bitmapPath)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
            intent.putExtra(Intent.EXTRA_TEXT, "Hey, Checkout this cool meme I got from MemeHub!\n$githubReleases")
            startActivity(Intent.createChooser(intent, "Share image:"))
        } catch (e: Exception) {
            checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE
            )
        }

    }


    fun btNext(view: View) {
        memeLoad()
    }


    // Function to check and request permission
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }


    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }


}