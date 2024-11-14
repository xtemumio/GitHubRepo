package com.example.githubrepo

import GitHubAuthService
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private val CLIENT_ID = "Ov23litGyvF9SLS12cOe"
    private val CLIENT_SECRET = "c5ee7df9a526949b28dd6fd147bfec386e39ee10"
    private val REDIRECT_URI = "myapp://callback"
    private val AUTH_URL = "https://github.com/login/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkIfLoggedIn()

        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            startGitHubLogin()
        }
    }

    private fun checkIfLoggedIn() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("ACCESS_TOKEN", null)

        if (accessToken != null) {
            // Se il token è presente, bypassa il login
            Log.d("LoginActivity", "User is already logged in with token: $accessToken")
            navigateToMainActivity()
        } else {
            // Altrimenti, continua con il login
            Log.d("LoginActivity", "User is not logged in")
        }
    }

    private fun startGitHubLogin() {
        val authUrl = "$AUTH_URL?client_id=$CLIENT_ID&redirect_uri=$REDIRECT_URI"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.d("LoginActivity", "onResume: Checking for redirect URI")
        val uri: Uri? = intent?.data
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            Log.d("LoginActivity", "onResume: Redirect URI found")
            val code = uri.getQueryParameter("code")
            if (code != null) {
                Log.d("LoginActivity", "Authorization code received: $code")
                exchangeCodeForToken(code)
            } else {
                Log.e("LoginActivity", "Authorization code is null")
            }
            intent?.data = null
        } else {
            Log.d("LoginActivity", "No redirect URI in intent data")
        }
    }

    private fun exchangeCodeForToken(code: String) {
        Log.d("LoginActivity", "Exchanging code for access token")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GitHubAuthService::class.java)
        val call = service.getAccessToken(
            clientId = CLIENT_ID,
            clientSecret = CLIENT_SECRET,
            code = code,
            redirectUri = REDIRECT_URI
        )

        call.enqueue(object : Callback<AccessTokenResponse> {
            override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                if (response.isSuccessful) {
                    val accessToken = response.body()?.accessToken
                    if (accessToken != null) {
                        Log.d("LoginActivity", "Access token received: $accessToken")
                        saveAccessToken(accessToken)
                        navigateToMainActivity()
                    } else {
                        Log.e("LoginActivity", "Access token is null in response")
                    }
                } else {
                    Log.e("LoginActivity", "Failed to receive access token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                Log.e("LoginActivity", "Error during token exchange", t)
                // Gestione degli errori come in precedenza
            }
        })
    }

    private fun saveAccessToken(accessToken: String) {
        Log.d("LoginActivity", "Saving access token to SharedPreferences")
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            apply()
        }
    }

    private fun navigateToMainActivity() {
        Log.d("LoginActivity", "Navigating to MainActivity")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
