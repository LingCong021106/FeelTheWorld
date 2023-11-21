package com.example.feeltheworld

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.feeltheworld.databinding.ActivityMainBinding
import com.google.android.material.internal.ViewUtils.hideKeyboard


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.frame.setOnClickListener{hideKeyboard(view)}
        binding.queryEditText.setOnEditorActionListener{
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
            true
        }
    }
}