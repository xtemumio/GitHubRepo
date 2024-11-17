/**
 * RepositoryAdapter
 *
 * Questo adapter è responsabile della gestione e visualizzazione della lista dei repository
 * in un RecyclerView. Utilizza DiffUtil per ottimizzare l'aggiornamento degli elementi.
 * Ogni elemento rappresenta un repository e include informazioni come nome, descrizione,
 * linguaggio di programmazione, numero di stelle e un pulsante per visualizzare i dettagli.
 */

package com.example.githubrepo.main

import Repository
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
import com.example.githubrepo.repository.RepositoryDetailActivity

class RepositoryAdapter : ListAdapter<Repository, RepositoryAdapter.RepositoryViewHolder>(
    DiffCallback()
) {

    /**
     * Crea un nuovo ViewHolder per ogni elemento della lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repository_card, parent, false)
        return RepositoryViewHolder(view)
    }

    /**
     * Associa i dati del repository al ViewHolder.
     * @param holder Il ViewHolder da aggiornare.
     * @param position La posizione dell'elemento nella lista.
     */
    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = getItem(position)
        holder.bind(repository)
    }

    /**
     * ViewHolder per rappresentare un singolo elemento della lista dei repository.
     */
    class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.repoTitle) // Nome del repository
        private val description: TextView = itemView.findViewById(R.id.repoDescription) // Descrizione del repository
        private val language: TextView = itemView.findViewById(R.id.repoLanguage) // Linguaggio di programmazione
        private val starsContainer: LinearLayout = itemView.findViewById(R.id.starsContainer) // Contenitore delle stelle
        private val infoButton: ImageButton = itemView.findViewById(R.id.infoButton) // Pulsante per i dettagli

        /**
         * Associa i dati del repository agli elementi della vista.
         * @param repository I dati del repository da visualizzare.
         */
        fun bind(repository: Repository) {
            title.text = repository.name
            description.text = repository.description ?: "No description"
            language.text = repository.language ?: "N/A"

            // Mostra il numero di stelle del repository
            displayStars(repository.stars, starsContainer)

            // Listener per il pulsante delle informazioni
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
                context.startActivity(intent) // Avvia l'activity dei dettagli
            }
        }

        /**
         * Visualizza le stelle in base al numero fornito.
         * @param starCount Il numero di stelle da visualizzare.
         * @param starsContainer Il layout in cui visualizzare le stelle.
         */
        private fun displayStars(starCount: Int, starsContainer: LinearLayout) {
            val maxStars = 5
            starsContainer.removeAllViews() // Rimuove le stelle esistenti
            val starSize = itemView.resources.getDimensionPixelSize(R.dimen.star_size) // Dimensione delle stelle

            // Aggiunge le stelle piene
            for (i in 1..starCount.coerceAtMost(maxStars)) {
                val starImageView = ImageView(itemView.context)
                starImageView.setImageResource(R.drawable.star) // Stella piena
                val params = LinearLayout.LayoutParams(starSize, starSize)
                params.marginEnd = 4 // Spaziatura tra le stelle
                starImageView.layoutParams = params
                starsContainer.addView(starImageView)
            }

            // Aggiunge le stelle grigie (vuote) per raggiungere il numero massimo
            for (i in (starCount + 1)..maxStars) {
                val starImageView = ImageView(itemView.context)
                starImageView.setImageResource(R.drawable.star_grey) // Stella grigia
                val params = LinearLayout.LayoutParams(starSize, starSize)
                params.marginEnd = 4
                starImageView.layoutParams = params
                starsContainer.addView(starImageView)
            }
        }
    }

    /**
     * DiffUtil per ottimizzare l'aggiornamento degli elementi del RecyclerView.
     */
    class DiffCallback : DiffUtil.ItemCallback<Repository>() {
        /**
         * Determina se due elementi rappresentano lo stesso repository.
         */
        override fun areItemsTheSame(oldItem: Repository, newItem: Repository) = oldItem.id == newItem.id

        /**
         * Determina se il contenuto di due elementi è identico.
         */
        override fun areContentsTheSame(oldItem: Repository, newItem: Repository) = oldItem == newItem
    }
}