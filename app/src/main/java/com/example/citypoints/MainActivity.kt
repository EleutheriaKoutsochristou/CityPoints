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
        database = AppDatabase.getDatabase(this)

        adapter = PointAdapter(mutableListOf()) { point ->

            val intent = Intent(this, DetailActivity::class.java)

            intent.putExtra("pointId", point.id)

            startActivity(intent)
        }

        binding.recyclerViewPoints.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPoints.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddPointActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            database.pointDao().getAllPoints().collect { points ->
                adapter.updateData(points)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    
    // Removed onResume and loadPoints since we use Flow collect
}