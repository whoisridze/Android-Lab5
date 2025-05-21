package com.whoisridze.lab5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var powerConnectedReceiver: BroadcastReceiver
    private lateinit var airplaneModeReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = GridAdapter(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initBroadcastReceivers()
    }

    private fun initBroadcastReceivers() {
        powerConnectedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Toast.makeText(context, "Charger connected", Toast.LENGTH_SHORT).show()
            }
        }

        airplaneModeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val isAirplaneModeOn = Settings.System.getInt(
                    context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0
                ) != 0
                Toast.makeText(
                    context,
                    "Airplane mode is ${if (isAirplaneModeOn) "on" else "off"}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("MainActivity", "Airplane mode is ${if (isAirplaneModeOn) "on" else "off"}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            powerConnectedReceiver,
            IntentFilter(Intent.ACTION_POWER_CONNECTED)
        )
        registerReceiver(
            airplaneModeReceiver,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(powerConnectedReceiver)
        unregisterReceiver(airplaneModeReceiver)
    }
}

class GridAdapter(private val context: Context) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {
    private val items = List(50) {
        GridItem(
            (1..99).random(),
            Color.rgb(
                (0..255).random(),
                (0..255).random(),
                (0..255).random()
            )
        )
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val valueText: TextView = itemView.findViewById(R.id.itemValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = items[position]
        holder.valueText.text = item.value.toString()
        holder.itemView.setBackgroundColor(item.color)

        holder.itemView.setOnClickListener {
            showDialog(context, item.value)
        }
    }

    override fun getItemCount() = items.size

    private fun showDialog(context: Context, value: Int) {
        AlertDialog.Builder(context)
            .setTitle("Selected Value")
            .setMessage("You selected: $value")
            .setPositiveButton("OK", null)
            .show()
    }

    data class GridItem(val value: Int, val color: Int)
}