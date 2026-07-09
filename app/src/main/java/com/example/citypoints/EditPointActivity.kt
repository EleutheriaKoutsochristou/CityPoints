package com.example.citypoints

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.citypoints.database.AppDatabase
import com.example.citypoints.database.SustainablePoint
import com.example.citypoints.databinding.ActivityEditPointBinding
import kotlinx.coroutines.launch

class EditPointActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPointBinding
    private lateinit var database: AppDatabase
    private var pointId: Int = -1
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.imgPhoto.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        binding = ActivityEditPointBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        database = AppDatabase.getDatabase(this)

        pointId = intent.getIntExtra("pointId", -1)

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
        binding.spCategory.adapter = categoryAdapter

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
        binding.spStatus.adapter = statusAdapter

        binding.btnSave.text = "Ενημέρωση"

        binding.btnSave.setOnClickListener {
            updatePoint()
        }

        binding.btnPhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        loadPointData()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun loadPointData() {
        if (pointId == -1) return

        lifecycleScope.launch {
            database.pointDao().getPointById(pointId).collect { point ->
                point?.let {
                    // Only populate if the fields are empty to avoid overwriting user input
                    if (binding.etTitle.text.isNullOrEmpty()) {
                        binding.etTitle.setText(it.title)
                        
                        val categoryIndex = (binding.spCategory.adapter as ArrayAdapter<String>).getPosition(it.category)
                        if (categoryIndex >= 0) binding.spCategory.setSelection(categoryIndex)

                        val statusIndex = (binding.spStatus.adapter as ArrayAdapter<String>).getPosition(it.status)
                        if (statusIndex >= 0) binding.spStatus.setSelection(statusIndex)

                        binding.ratingBar.rating = it.rating
                        binding.etComments.setText(it.comments)
                        binding.etLatitude.setText(it.latitude.toString())
                        binding.etLongitude.setText(it.longitude.toString())
                        
                        if (it.photoPath.isNotEmpty()) {
                            selectedImageUri = Uri.parse(it.photoPath)
                            binding.imgPhoto.setImageURI(selectedImageUri)
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun updatePoint() {
        val title = binding.etTitle.text.toString()
        val category = binding.spCategory.selectedItem.toString()
        val status = binding.spStatus.selectedItem.toString()
        val rating = binding.ratingBar.rating
        val comments = binding.etComments.text.toString()
        val latitude = binding.etLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = binding.etLongitude.text.toString().toDoubleOrNull() ?: 0.0

        if (title.isBlank()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε τον τίτλο", Toast.LENGTH_SHORT).show()
            return
        }

        val point = SustainablePoint(
            id = pointId,
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
            database.pointDao().update(point)

            runOnUiThread {
                Toast.makeText(this@EditPointActivity, "Το σημείο ενημερώθηκε!", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}
