package com.example.githubrepo.repository

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.githubrepo.R
import com.squareup.picasso.Picasso
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class RepositoryDetailActivity : AppCompatActivity() {

    private lateinit var repoName: TextView
    private lateinit var repoDescription: TextView
    private lateinit var repoLanguage: TextView
    private lateinit var starsContainer: LinearLayout
    private lateinit var repoForks: TextView
    private lateinit var repoOwnerImage: ImageView
    private lateinit var repoOwnerName: TextView
    private lateinit var contributionChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_detail)

        // Configura la Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.detailToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Dettagli Repository"

        // Inizializza le viste
        repoName = findViewById(R.id.repoName)
        repoDescription = findViewById(R.id.repoDescription)
        repoLanguage = findViewById(R.id.repoLanguage)
        starsContainer = findViewById(R.id.starsContainer)
        repoForks = findViewById(R.id.repoForks)
        repoOwnerImage = findViewById(R.id.repoOwnerImage)
        repoOwnerName = findViewById(R.id.repoOwnerName)
        contributionChart = findViewById(R.id.repoContributionChart)

        // Ottieni i dati dall'intent
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val language = intent.getStringExtra("language")
        val stars = intent.getIntExtra("stars", 0)
        val forks = intent.getIntExtra("forks", 0)
        val ownerImageUrl = intent.getStringExtra("ownerImageUrl")
        val ownerName = intent.getStringExtra("ownerName")

        // Imposta i dati
        repoName.text = name
        repoDescription.text = description ?: "Nessuna descrizione"
        repoLanguage.text = "Linguaggio: ${language ?: "N/A"}"
        repoForks.text = "Forks: $forks"
        repoOwnerName.text = ownerName ?: "Proprietario non disponibile"
        Picasso.get().load(ownerImageUrl).placeholder(R.drawable.repo_icon).into(repoOwnerImage)

        displayStars(stars)

        setupContributionChart()
    }

    private fun displayStars(starCount: Int) {
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

    private fun setupContributionChart() {
        val entries = mutableListOf<Entry>()
        val contributions = listOf(5, 12, 7, 10, 20, 15, 30) // Dati esempio

        contributions.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Contributi").apply {
            color = Color.BLUE
            valueTextColor = Color.WHITE
            lineWidth = 2f
        }

        val lineData = LineData(dataSet)
        contributionChart.data = lineData

        contributionChart.description = Description().apply { text = "" }
        contributionChart.setBackgroundColor(Color.TRANSPARENT)
        contributionChart.animateY(1000)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}