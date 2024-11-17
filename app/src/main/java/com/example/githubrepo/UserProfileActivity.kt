package com.example.githubrepo

import GitHubAuthService
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var profileImage: ImageView
    private lateinit var userNickname: TextView
    private lateinit var userBio: TextView
    private lateinit var userCompany: TextView
    private lateinit var userLocation: TextView
    private lateinit var userPublicRepos: TextView
    private lateinit var userFollowers: TextView
    private lateinit var userFollowing: TextView
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var navigationView: NavigationView
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.userProfileToolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        initializeUI()

        accessToken = intent.getStringExtra("ACCESS_TOKEN")
        if (accessToken != null) {
            fetchUserProfile(accessToken!!)
        } else {
            Toast.makeText(this, "Token di accesso non trovato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeUI() {
        profileImage = findViewById(R.id.profileImage)
        userNickname = findViewById(R.id.userNickname)
        userBio = findViewById(R.id.userBio)
        userCompany = findViewById(R.id.userCompany)
        userLocation = findViewById(R.id.userLocation)
        userPublicRepos = findViewById(R.id.userPublicRepos)
        userFollowers = findViewById(R.id.userFollowers)
        userFollowing = findViewById(R.id.userFollowing)
    }

    private fun fetchUserProfile(accessToken: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GitHubAuthService::class.java)
        val call: Call<User> = service.getUserProfile("Bearer $accessToken")

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        updateUI(user)
                    }
                } else {
                    Toast.makeText(this@UserProfileActivity, "Errore nel recupero del profilo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@UserProfileActivity, "Errore di connessione", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(user: User) {
        userNickname.text = user.login
        userBio.text = user.bio ?: run {
            userBio.visibility = View.GONE
            ""
        }
        userCompany.text = user.company?.let { "Azienda: $it" } ?: run {
            userCompany.visibility = View.GONE
            ""
        }
        userLocation.text = user.location?.let { "Località: $it" } ?: run {
            userLocation.visibility = View.GONE
            ""
        }
        userPublicRepos.text = "Repository pubblici: ${user.publicRepos}"
        userFollowers.text = "Followers: ${user.followers}"
        userFollowing.text = "Following: ${user.following}"

        Picasso.get()
            .load(user.avatarUrl)
            .placeholder(R.drawable.user_menu)
            .error(R.drawable.user_menu)
            .into(profileImage)
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
            R.id.menu_home -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                finish()
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