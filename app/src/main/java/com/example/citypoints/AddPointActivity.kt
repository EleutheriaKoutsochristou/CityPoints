package com.example.citypoints

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.citypoints.databinding.ActivityAddPointBinding
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.citypoints.database.AppDatabase
import com.example.citypoints.database.SustainablePoint
import kotlinx.coroutines.launch

class AddPointActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityAddPointBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityAddPointBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        enableEdgeToEdge()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "citypoints_db"
        ).build()

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
            photoPath = "", // TODO: Add photo selection functionality
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