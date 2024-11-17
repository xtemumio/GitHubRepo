/**
 * MainActivity
 *
 * Questa classe rappresenta la schermata principale dell'app. Gestisce:
 * - La visualizzazione dei repository dell'utente in un RecyclerView.
 * - Il recupero dei dati dell'utente (nome, avatar, email) per il menu laterale.
 * - La gestione della navigazione tramite il menu laterale.
 * - Il logout dell'utente.
 */

package com.example.githubrepo.main

import Repository
import com.example.githubrepo.network.GitHubAuthService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepo.R
import com.example.githubrepo.login.LoginActivity
import com.example.githubrepo.models.User
import com.example.githubrepo.profile.UserProfileActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RepositoryAdapter
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inizializzazione del menu laterale (DrawerLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // Configurazione della toolbar con il menu laterale
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Inizializza il RecyclerView per mostrare i repository
        recyclerView = findViewById(R.id.repositoryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Layout griglia a 2 colonne
        adapter = RepositoryAdapter()
        recyclerView.adapter = adapter

        // Recupera il token di accesso
        accessToken = intent.getStringExtra("ACCESS_TOKEN")
        if (accessToken != null) {
            fetchRepositories(accessToken!!)
            fetchUserProfile(accessToken!!)
        } else {
            Toast.makeText(this, "Errore: Token di accesso non trovato", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Recupera i repository dell'utente da GitHub e li visualizza nel RecyclerView.
     * @param accessToken Il token di accesso per l'autenticazione con l'API di GitHub.
     */
    private fun fetchRepositories(accessToken: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GitHubAuthService::class.java)
        val call = service.getRepositories("Bearer $accessToken")

        call.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    val repositories = response.body() ?: emptyList()
                    adapter.submitList(repositories) // Aggiorna il RecyclerView con i dati ricevuti
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Errore nel recupero dei repository",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Recupera il profilo dell'utente da GitHub e aggiorna il menu laterale.
     * @param accessToken Il token di accesso per l'autenticazione con l'API di GitHub.
     */
    private fun fetchUserProfile(accessToken: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GitHubAuthService::class.java)
        val call = service.getUserProfile("Bearer $accessToken")

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        updateNavigationHeader(it) // Aggiorna l'header del menu laterale
                    }
                } else {
                    Log.e("MainActivity", "Errore nel recupero del profilo utente")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Errore di connessione", t)
            }
        })
    }

    /**
     * Aggiorna l'header del menu laterale con i dati dell'utente.
     * @param user Oggetto User con le informazioni dell'utente.
     */
    private fun updateNavigationHeader(user: User) {
        val headerView = navigationView.getHeaderView(0)
        val navUserName = headerView.findViewById<TextView>(R.id.navUserName)
        val navUserEmail = headerView.findViewById<TextView>(R.id.navUserEmail)
        val navProfileImage = headerView.findViewById<ShapeableImageView>(R.id.navProfileImage)

        // Mostra il nome dell'utente o il login come fallback
        navUserName.text = user.name ?: user.login ?: "Nome non disponibile"
        // Mostra l'email solo se disponibile, altrimenti nasconde il TextView
        navUserEmail.text = user.email ?: run {
            navUserEmail.visibility = View.GONE
            ""
        }

        // Carica l'immagine del profilo usando Picasso
        Picasso.get().load(user.avatarUrl)
            .placeholder(R.drawable.user_menu)
            .error(R.drawable.user_menu)
            .into(navProfileImage)
    }

    /**
     * Effettua il logout dell'utente e naviga verso la LoginActivity.
     */
    private fun handleLogout() {
        getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit().remove("ACCESS_TOKEN").apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        Toast.makeText(this, "Logout effettuato", Toast.LENGTH_SHORT).show()
    }

    /**
     * Gestisce gli eventi del menu laterale.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> drawerLayout.closeDrawer(GravityCompat.START)
            R.id.menu_profile -> {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("ACCESS_TOKEN", accessToken)
                startActivity(intent)
            }
            R.id.menu_logout -> handleLogout()
        }
        return true
    }

    /**
     * Chiude il menu laterale se aperto; altrimenti gestisce il comportamento del tasto indietro.
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}