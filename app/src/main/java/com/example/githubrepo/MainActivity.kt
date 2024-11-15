package com.example.githubrepo

import GitHubAuthService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var repositoryGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "MainActivity started")

        repositoryGrid = findViewById(R.id.repositoryGrid)
        Log.d("MainActivity", "repositoryGrid initialized")

        val accessToken = intent.getStringExtra("ACCESS_TOKEN")
        if (accessToken != null) {
            Log.d("MainActivity", "Access token received: $accessToken")
            fetchRepositories(accessToken)
        } else {
            Log.e("MainActivity", "Error: Access token not received")
            Toast.makeText(this, "Errore: Token di accesso non trovato", Toast.LENGTH_SHORT).show()
        }
    }

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
                        "onResponse: Error fetching repositories - ${response.errorBody()?.string()}"
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

    private fun displayRepositories(repositories: List<Repository>) {
        Log.d("MainActivity", "displayRepositories: Displaying repositories in GridLayout")
        repositoryGrid.removeAllViews()

        val inflater = LayoutInflater.from(this)

        for (repo in repositories) {
            Log.d("MainActivity", "displayRepositories: Adding repository ${repo.name} to grid")

            val repoView = inflater.inflate(R.layout.repository_card, repositoryGrid, false)

            val titleView = repoView.findViewById<TextView>(R.id.repoTitle)
            val descriptionView = repoView.findViewById<TextView>(R.id.repoDescription)
            val languageView = repoView.findViewById<TextView>(R.id.repoLanguage)
            val starsContainer = repoView.findViewById<LinearLayout>(R.id.starsContainer)

            titleView.text = repo.name
            descriptionView.text = repo.description ?: "No description"
            languageView.text = "Language: ${repo.language ?: "N/A"}"

            // Visualizza le stelle
            displayStars(repo.stars, starsContainer)

            repositoryGrid.addView(repoView)
        }

        Log.d("MainActivity", "displayRepositories: Finished displaying repositories")
    }

    // Funzione per visualizzare le stelle come immagini
    private fun displayStars(starCount: Int, starsContainer: LinearLayout) {
        val maxStars = 5
        starsContainer.removeAllViews()

        // Setta la dimensione per le stelle
        val starSize = resources.getDimensionPixelSize(R.dimen.star_size)

        // Aggiungi stelle gialle per il numero di stelle del repository
        for (i in 1..starCount.coerceAtMost(maxStars)) {
            val starImageView = ImageView(this)
            starImageView.setImageResource(R.drawable.star)
            val params = LinearLayout.LayoutParams(starSize, starSize)
            params.marginEnd = 4 // Margine tra le stelle
            starImageView.layoutParams = params
            starsContainer.addView(starImageView)
        }

        // Aggiungi stelle grigie per il resto fino a 5 stelle
        for (i in (starCount + 1)..maxStars) {
            val starImageView = ImageView(this)
            starImageView.setImageResource(R.drawable.star_grey)
            val params = LinearLayout.LayoutParams(starSize, starSize)
            params.marginEnd = 4
            starImageView.layoutParams = params
            starsContainer.addView(starImageView)
        }
    }
}