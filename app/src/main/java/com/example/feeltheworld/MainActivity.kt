package com.example.feeltheworld


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.feeltheworld.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //toolbar
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)


        binding.chatBoxBtn.setOnClickListener{
            startActivity(Intent(this, ChatBox::class.java))
        }

        binding.objectRecogBtn.setOnClickListener{
            startActivity(Intent(this, RealTime::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.quit){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}