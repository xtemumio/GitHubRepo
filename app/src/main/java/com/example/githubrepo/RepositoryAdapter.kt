package com.example.githubrepo// com.example.githubrepo.RepositoryAdapter.kt
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class RepositoryAdapter : ListAdapter<Repository, RepositoryAdapter.RepositoryViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repository, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = getItem(position)
        holder.bind(repository)
    }

    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val language: TextView = itemView.findViewById(R.id.language)
        private val stars: TextView = itemView.findViewById(R.id.stars)
        private val infoButton: ImageButton = itemView.findViewById(R.id.info_button)

        fun bind(repository: Repository) {
            title.text = repository.name
            description.text = repository.description
            language.text = repository.language
            stars.text = repository.stars.toString()

            infoButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, RepositoryDetailActivity::class.java)
                intent.putExtra("REPOSITORY_ID", repository.id)
                context.startActivity(intent)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Repository>() {
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Repository, newItem: Repository) = oldItem == newItem
    }
}