package com.example.githubrepo

import GitHubAuthService
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {

    private val CLIENT_ID = "Ov23litGyvF9SLS12cOe"
    private val CLIENT_SECRET = "c5ee7df9a526949b28dd6fd147bfec386e39ee10"
    private val REDIRECT_URI = "myapp://callback"
    private val AUTH_URL = "https://github.com/login/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d("LoginActivity", "onCreate: Checking if user is logged in")
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
        Log.d("LoginActivity", "checkIfLoggedIn: accessToken = $accessToken")

        if (accessToken != null) {
            Log.d("LoginActivity", "User is already logged in, navigating to MainActivity")
            navigateToMainActivity(accessToken)
        } else {
            Log.d("LoginActivity", "User is not logged in, staying on LoginActivity")
        }
    }

    private fun startGitHubLogin() {
        val authUrl = "$AUTH_URL?client_id=$CLIENT_ID&redirect_uri=$REDIRECT_URI"
        Log.d("LoginActivity", "startGitHubLogin: authUrl = $authUrl")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.d("LoginActivity", "onResume: Checking for redirect URI")
        val uri: Uri? = intent?.data
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            Log.d("LoginActivity", "onResume: Redirect URI found: $uri")
            val code = uri.getQueryParameter("code")
            if (code != null) {
                Log.d("LoginActivity", "Authorization code received: $code")
                exchangeCodeForToken(code)
            } else {
                Log.e("LoginActivity", "Authorization code is null")
                Toast.makeText(this, "Failed to receive authorization code", Toast.LENGTH_SHORT).show()
            }
            intent?.data = null
        } else {
            Log.d("LoginActivity", "No redirect URI in intent data")
        }
    }

    private fun exchangeCodeForToken(code: String) {
        Log.d("LoginActivity", "Exchanging code for access token with code: $code")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://github.com/")
            .build() // Rimuoviamo GsonConverterFactory, poiché ora trattiamo la risposta come testo

        val service = retrofit.create(GitHubAuthService::class.java)
        val call = service.getAccessToken(
            clientId = CLIENT_ID,
            clientSecret = CLIENT_SECRET,
            code = code,
            redirectUri = REDIRECT_URI
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: ""
                    Log.d("LoginActivity", "Raw access token response: $responseBody")

                    // Estrarre il token dalla risposta url encoded
                    val tokenParams = responseBody.split("&").associate {
                        val (key, value) = it.split("=")
                        key to value
                    }
                    val accessToken = tokenParams["access_token"]

                    if (accessToken != null) {
                        Log.d("LoginActivity", "Access token received: $accessToken")
                        saveAccessToken(accessToken)
                        navigateToMainActivity(accessToken)
                    } else {
                        Log.e("LoginActivity", "Access token is null in response")
                    }
                } else {
                    Log.e("LoginActivity", "Failed to receive access token: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("LoginActivity", "Error during token exchange", t)
            }
        })
    }

    private fun saveAccessToken(accessToken: String) {
        Log.d("LoginActivity", "Saving access token to SharedPreferences: $accessToken")
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            apply()
        }

        val savedToken = sharedPreferences.getString("ACCESS_TOKEN", null)
        Log.d("LoginActivity", "Saved token is: $savedToken")
    }

    private fun navigateToMainActivity(accessToken: String) {
        Log.d("LoginActivity", "Navigating to MainActivity with token")
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("ACCESS_TOKEN", accessToken)
        }
        startActivity(intent)
        finish()
    }
}