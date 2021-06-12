package com.epf.min.scanfood


import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*

private const val CAMERA_REQUEST_CODE = 100

class HomeActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    /* Fonction onCreate() pour le scanner*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupPermissions()
        codeScanner()




    }

    /* Fonction qui implemente le scanner */

    private fun codeScanner() {
        codeScanner = CodeScanner(this, scanner_view)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {

                    var number = it.text
                    tv_textView.text = "Numéro de code barre détecté : "+it.text
                    println("Code barre trouvé : $number")
                    resultActivity(number)
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("EPF", "Erreur Camera:${it.message}")
                }
            }
        }

            scanner_view.setOnClickListener {
                codeScanner.startPreview()
            }
    }

    override fun onResume() {
            super.onResume()
            codeScanner.startPreview()

    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Vous devez autoriser l'application à se servir de votre appareil photo", Toast.LENGTH_SHORT)
                } else {
                    //ca marche
                }
            }
        }
    }

    fun resultActivity(number: String){

        val intent = Intent(this, ResultsProduct::class.java)
        intent.putExtra("number",number)
        startActivity(intent)
    }

}
