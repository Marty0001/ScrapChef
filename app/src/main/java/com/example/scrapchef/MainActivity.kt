package com.example.scrapchef

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = InputStreamReader(assets.open("top-1k-ingredients.csv"))
        val reader = BufferedReader(input)

        var line : String
        var displayData : String = ""
        var count = 0

        while (reader.readLine().also {line = it} !=null && count < 5){
            val row : List<String> = line.split(";")
            displayData = displayData + row[0] + " " + row[1] + "\n"
            count++
        }

        Log.i("MAIN", displayData)

    }
}