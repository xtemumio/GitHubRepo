package com.example.githubrepo.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepo.R
import com.example.githubrepo.repository.Repository
import com.example.githubrepo.repository.RepositoryDetailActivity

class RepositoryAdapter : ListAdapter<Repository, RepositoryAdapter.RepositoryViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repository_card, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = getItem(position)
        holder.bind(repository)
    }

    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.repoTitle)
        private val description: TextView = itemView.findViewById(R.id.repoDescription)
        private val language: TextView = itemView.findViewById(R.id.repoLanguage)
        private val starsContainer: LinearLayout = itemView.findViewById(R.id.starsContainer)
        private val infoButton: ImageButton = itemView.findViewById(R.id.infoButton)

        fun bind(repository: Repository) {
            title.text = repository.name
            description.text = repository.description ?: "No description"
            language.text = repository.language ?: "N/A"

            // Mostra le stelle
            displayStars(repository.stars, starsContainer)

            // Listener per il pulsante info
            infoButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, RepositoryDetailActivity::class.java).apply {
                    putExtra("name", repository.name)
                    putExtra("description", repository.description)
                    putExtra("language", repository.language)
                    putExtra("stars", repository.stars)
                    putExtra("forks", repository.forks)
                    putExtra("ownerImageUrl", repository.ownerImageUrl)
                    putExtra("ownerName", repository.ownerName)
                }
                context.startActivity(intent)
            }
        }

        // Funzione per visualizzare le stelle
        private fun displayStars(starCount: Int, starsContainer: LinearLayout) {
            val maxStars = 5
            starsContainer.removeAllViews()
            val starSize = itemView.resources.getDimensionPixelSize(R.dimen.star_size)

            for (i in 1..starCount.coerceAtMost(maxStars)) {
                val starImageView = ImageView(itemView.context)
                starImageView.setImageResource(R.drawable.star)
                val params = LinearLayout.LayoutParams(starSize, starSize)
                params.marginEnd = 4
                starImageView.layoutParams = params
                starsContainer.addView(starImageView)
            }

            for (i in (starCount + 1)..maxStars) {
                val starImageView = ImageView(itemView.context)
                starImageView.setImageResource(R.drawable.star_grey)
                val params = LinearLayout.LayoutParams(starSize, starSize)
                params.marginEnd = 4
                starImageView.layoutParams = params
                starsContainer.addView(starImageView)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Repository>() {
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Repository, newItem: Repository) = oldItem == newItem
    }
}