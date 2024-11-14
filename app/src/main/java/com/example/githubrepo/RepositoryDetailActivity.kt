package com.example.githubrepo

// RepositoryDetailActivity.kt
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RepositoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_detail)

        val repositoryId = intent.getIntExtra("REPOSITORY_ID", -1)

        // Qui puoi aggiungere codice per caricare i dettagli della repository in base all'ID
        findViewById<TextView>(R.id.detailTextView).text = "Dettagli della repository con ID: $repositoryId"
    }
}