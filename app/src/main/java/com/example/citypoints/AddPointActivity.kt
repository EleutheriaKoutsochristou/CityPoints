package com.example.citypoints

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.citypoints.database.AppDatabase
import com.example.citypoints.database.SustainablePoint
import com.example.citypoints.databinding.ActivityAddPointBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import android.net.Uri

class AddPointActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityAddPointBinding
    private lateinit var database: AppDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedImageUri: Uri? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getCurrentLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getCurrentLocation()
            }
            else -> {
                Toast.makeText(this, "Απαιτείται άδεια τοποθεσίας για αυτή τη λειτουργία", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                selectedImageUri = uri
                activityBinding.imgPhoto.setImageURI(uri)
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityAddPointBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        setSupportActionBar(activityBinding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Κατηγορίες
        val categories = listOf(
            "Ανακύκλωση",
            "Μετακίνηση",
            "Ενέργεια",
            "Νερό",
            "Πράσινο"
        )

        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        activityBinding.spCategory.adapter = categoryAdapter

        // Καταστάσεις
        val statuses = listOf(
            "Λειτουργεί",
            "Χρειάζεται συντήρηση",
            "Προσωρινά μη διαθέσιμο"
        )

        val statusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            statuses
        )

        activityBinding.spStatus.adapter = statusAdapter

        activityBinding.btnGetLocation.setOnClickListener {
            checkLocationPermissions()
        }

        activityBinding.btnPhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        activityBinding.btnSave.setOnClickListener {
            savePoint()
        }


        ViewCompat.setOnApplyWindowInsetsListener(activityBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        
        activityBinding.btnCancel.setOnClickListener {
            finish()
        }

    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    activityBinding.etLatitude.setText(location.latitude.toString())
                    activityBinding.etLongitude.setText(location.longitude.toString())
                    Toast.makeText(this, "Η τοποθεσία λήφθηκε επιτυχώς", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Δεν ήταν δυνατή η λήψη της τοποθεσίας. Βεβαιωθείτε ότι το GPS είναι ενεργοποιημένο.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Σφάλμα κατά τη λήψη τοποθεσίας: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun savePoint() {
        val title = activityBinding.etTitle.text.toString()
        val category = activityBinding.spCategory.selectedItem.toString()
        val status = activityBinding.spStatus.selectedItem.toString()
        val rating = activityBinding.ratingBar.rating
        val comments = activityBinding.etComments.text.toString()
        val latitude = activityBinding.etLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = activityBinding.etLongitude.text.toString().toDoubleOrNull() ?: 0.0

        if (title.isBlank()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε τον τίτλο", Toast.LENGTH_SHORT).show()
            return
        }

        val point = SustainablePoint(
            title = title,
            category = category,
            rating = rating,
            status = status,
            latitude = latitude,
            longitude = longitude,
            photoPath = selectedImageUri?.toString() ?: "",
            comments = comments
        )

        lifecycleScope.launch {
            database.pointDao().insert(point)

            runOnUiThread {
                Toast.makeText(this@AddPointActivity, "Το σημείο αποθηκεύτηκε!", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}
