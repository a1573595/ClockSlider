package com.a1573595.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.a1573595.clockslider.ClockSlider
import com.a1573595.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clockSlider.setOnTimeChangedListener(object : ClockSlider.OnTimeChangedListener {
            override fun onStartChanged(hour: Int, minute: Int) {
                Log.e("test_start", "%02d:%02d".format(hour, minute))
            }

            override fun onEndChanged(hour: Int, minute: Int) {
                Log.e("test_end", "%02d:%02d".format(hour, minute))
            }
        })

        binding.btnAddHR.setOnClickListener {
            binding.clockSlider.startHours += 1
        }

        binding.btnStartEnable.setOnClickListener {
            binding.clockSlider.isStartEnabled = !binding.clockSlider.isStartEnabled
        }

        binding.btnEndEnable.setOnClickListener {
            binding.clockSlider.isEndEnabled = !binding.clockSlider.isEndEnabled
        }
    }
}