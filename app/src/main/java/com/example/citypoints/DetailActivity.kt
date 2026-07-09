package com.example.citypoints

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.citypoints.databinding.ActivityDetailBinding
import androidx.lifecycle.lifecycleScope
import com.example.citypoints.database.AppDatabase
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import android.net.Uri
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var database: AppDatabase
    private var pointId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = "Λεπτομέρειες"
            setDisplayHomeAsUpEnabled(true)
        }
        
        database = AppDatabase.getDatabase(this)

        pointId = intent.getIntExtra("pointId", -1)

        lifecycleScope.launch {
            database.pointDao().getPointById(pointId).collect { point ->
                point?.let {
                    binding.tvTitle.text = it.title
                    binding.tvCategory.text = "Κατηγορία: ${it.category}"
                    binding.tvStatus.text = "Κατάσταση: ${it.status}"
                    binding.ratingBar.rating = it.rating
                    binding.tvComments.text = "Σχόλια: ${it.comments}"
                    binding.tvLocation.text = "Τοποθεσία:\n${it.latitude}, ${it.longitude}"
                    if (it.photoPath.isNotEmpty()) {
                        binding.imgPhoto.setImageURI(Uri.parse(it.photoPath))
                    }
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this@DetailActivity, EditPointActivity::class.java)
            intent.putExtra("pointId", pointId)
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Διαγραφή σημείου")
                .setMessage("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτό το σημείο;")

                .setPositiveButton("Διαγραφή") { _, _ ->
                    deletePoint()
                }

                .setNegativeButton("Ακύρωση", null)

                .show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    private fun deletePoint() {

        lifecycleScope.launch {

            val point = database.pointDao().getPointByIdOnce(pointId)

            point?.let {

                database.pointDao().delete(it)

                runOnUiThread {
                    Toast.makeText(
                        this@DetailActivity,
                        "Το σημείο διαγράφηκε.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                finish()
            }
        }
    }
}
