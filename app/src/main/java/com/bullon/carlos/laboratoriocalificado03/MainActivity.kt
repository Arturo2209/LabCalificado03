package com.bullon.carlos.laboratoriocalificado03

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bullon.carlos.laboratoriocalificado03.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewTeachers.layoutManager = LinearLayoutManager(this)

        fetchTeachers()
    }

    private fun fetchTeachers() {
        val request = Request.Builder()
            .url("https://private-effe28-tecsup1.apiary-mock.com/list/teacher")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error al cargar datos. Verifica tu conexión.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    try {

                        val jsonObject = Gson().fromJson(body, JsonObject::class.java)
                        val teachersJsonArray = jsonObject.getAsJsonArray("teachers")
                        val teacherListType = object : TypeToken<List<Teacher>>() {}.type
                        val teachers = Gson().fromJson<List<Teacher>>(teachersJsonArray, teacherListType)

                        runOnUiThread {
                            binding.recyclerViewTeachers.adapter = TeacherAdapter(this@MainActivity, teachers)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}
