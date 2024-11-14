package com.example.githubrepo

import GitHubAuthService
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var repositoryAdapter: RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        repositoryAdapter = RepositoryAdapter()
        recyclerView.adapter = repositoryAdapter

        // Ottieni il token di accesso dall’intent
        val accessToken = intent.getStringExtra("ACCESS_TOKEN")
        if (accessToken != null) {
            fetchRepositories(accessToken)
        } else {
            Toast.makeText(this, "Errore: Token di accesso non trovato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRepositories(accessToken: String) {
        val service = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .build()
            .create(GitHubAuthService::class.java)

        val call = service.getRepositories("Bearer $accessToken")
        call.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    val repositories = response.body() ?: emptyList()
                    repositoryAdapter.submitList(repositories)
                } else {
                    Toast.makeText(this@MainActivity, "Errore nel recupero dei repository", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }
}