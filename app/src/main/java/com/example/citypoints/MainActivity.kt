package com.example.citypoints
import android.util.Log
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.citypoints.databinding.ActivityMainBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.citypoints.adapter.PointAdapter
import com.example.citypoints.database.AppDatabase
import kotlinx.coroutines.launch
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: PointAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        setContentView(binding.root)
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "citypoints_db"
        ).build()

        adapter = PointAdapter(mutableListOf()) { point ->
            // Εδώ αργότερα θα ανοίγει η DetailActivity
        }

        binding.recyclerViewPoints.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPoints.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddPointActivity::class.java)
            startActivity(intent)
        }

        loadPoints()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    override fun onResume() {
        super.onResume()
        loadPoints()
    }
    private fun loadPoints() {
        lifecycleScope.launch {
            val points = database.pointDao().getAllPoints()
            adapter.updateData(points)
        }
    }
}