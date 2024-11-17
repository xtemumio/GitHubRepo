package com.example.githubrepo

import GitHubAuthService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
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

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Inizializza RecyclerView
        recyclerView = findViewById(R.id.repositoryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
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

    private fun fetchRepositories(accessToken: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GitHubAuthService::class.java)
        val call = service.getRepositories("Bearer $accessToken")

        call.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                if (response.isSuccessful) {
                    val repositories = response.body() ?: emptyList()
                    adapter.submitList(repositories)
                } else {
                    Toast.makeText(this@MainActivity, "Errore nel recupero dei repository", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }

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
                        updateNavigationHeader(it)
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

    private fun updateNavigationHeader(user: User) {
        val headerView = navigationView.getHeaderView(0)
        val navUserName = headerView.findViewById<TextView>(R.id.navUserName)
        val navUserEmail = headerView.findViewById<TextView>(R.id.navUserEmail)
        val navProfileImage = headerView.findViewById<ImageView>(R.id.navProfileImage)

        navUserName.text = user.name
        navUserEmail.text = user.email ?: "Email non disponibile"
        Picasso.get().load(user.avatarUrl).placeholder(R.drawable.user_menu).into(navProfileImage)
    }

    private fun displayStars(starCount: Int, starsContainer: LinearLayout) {
        val maxStars = 5
        starsContainer.removeAllViews()
        val starSize = resources.getDimensionPixelSize(R.dimen.star_size)

        for (i in 1..starCount.coerceAtMost(maxStars)) {
            val starImageView = ImageView(this)
            starImageView.setImageResource(R.drawable.star)
            val params = LinearLayout.LayoutParams(starSize, starSize)
            params.marginEnd = 4
            starImageView.layoutParams = params
            starsContainer.addView(starImageView)
        }

        for (i in (starCount + 1)..maxStars) {
            val starImageView = ImageView(this)
            starImageView.setImageResource(R.drawable.star_grey)
            val params = LinearLayout.LayoutParams(starSize, starSize)
            params.marginEnd = 4
            starImageView.layoutParams = params
            starsContainer.addView(starImageView)
        }
    }

    private fun handleLogout() {
        getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit().remove("ACCESS_TOKEN").apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        Toast.makeText(this, "Logout effettuato", Toast.LENGTH_SHORT).show()
    }

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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}