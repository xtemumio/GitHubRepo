package com.example.githubrepo

import GitHubAuthService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // GridLayout per visualizzare le card dei repository
    private lateinit var repositoryGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "MainActivity started")

        // Inizializzazione della GridLayout per i repository
        repositoryGrid = findViewById(R.id.repositoryGrid)
        Log.d("MainActivity", "repositoryGrid initialized")

        // Recupera il token di accesso dall'intent
        val accessToken = intent.getStringExtra("ACCESS_TOKEN")
        if (accessToken != null) {
            Log.d("MainActivity", "Access token received: $accessToken")
            fetchRepositories(accessToken) // Effettua la richiesta per ottenere i repository
        } else {
            Log.e("MainActivity", "Error: Access token not received")
            Toast.makeText(this, "Errore: Token di accesso non trovato", Toast.LENGTH_SHORT).show()
        }
    }

    // Metodo per recuperare i repository tramite Retrofit
    private fun fetchRepositories(accessToken: String) {
        Log.d("MainActivity", "fetchRepositories: Initializing Retrofit service")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GitHubAuthService::class.java)
        val call = service.getRepositories("Bearer $accessToken")

        Log.d("MainActivity", "fetchRepositories: Making API call")

        call.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    val repositories = response.body() ?: emptyList()
                    Log.d("MainActivity", "onResponse: Fetched ${repositories.size} repositories")
                    displayRepositories(repositories)
                } else {
                    Log.e(
                        "MainActivity",
                        "onResponse: Error fetching repositories - ${
                            response.errorBody()?.string()
                        }"
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "Errore nel recupero dei repository",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Log.e("MainActivity", "onFailure: API call failed", t)
                Toast.makeText(this@MainActivity, "Errore di connessione", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // Metodo per visualizzare i repository come card all'interno della GridLayout
    private fun displayRepositories(repositories: List<Repository>) {
        Log.d("MainActivity", "displayRepositories: Displaying repositories in GridLayout")
        repositoryGrid.removeAllViews() // Pulisce le viste esistenti nella griglia

        val inflater = LayoutInflater.from(this)

        for (repo in repositories) {
            Log.d("MainActivity", "displayRepositories: Adding repository ${repo.name} to grid")

            // Inflate della card layout per il repository
            val repoView = inflater.inflate(R.layout.repository_card, repositoryGrid, false)

            // Riferimenti agli elementi della card
            val titleView = repoView.findViewById<TextView>(R.id.repoTitle)
            val descriptionView = repoView.findViewById<TextView>(R.id.repoDescription)
            val languageView = repoView.findViewById<TextView>(R.id.repoLanguage)
            val starsView = repoView.findViewById<TextView>(R.id.repoStars)

            // Imposta i dati del repository nella card
            titleView.text = repo.name
            descriptionView.text = repo.description ?: "No description"
            languageView.text = "Language: ${repo.language ?: "N/A"}"
            starsView.text = "Stars: ${repo.stars}"

            // Aggiunge la card alla griglia
            repositoryGrid.addView(repoView)
        }

        Log.d("MainActivity", "displayRepositories: Finished displaying repositories")
    }
}