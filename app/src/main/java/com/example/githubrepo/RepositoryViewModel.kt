package com.example.githubrepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class RepositoryViewModel : ViewModel() {

    // Funzione per ottenere la lista di drawable delle stelle
    fun getStarsDrawableList(starCount: Int): List<Int> {
        val maxStars = 5
        val starsDrawableList = mutableListOf<Int>()

        // Aggiungi stelle gialle per il numero di stelle ricevute
        for (i in 1..starCount) {
            starsDrawableList.add(R.drawable.star)
        }

        // Aggiungi stelle grigie per il resto fino a 5
        for (i in (starCount + 1)..maxStars) {
            starsDrawableList.add(R.drawable.star_grey)
        }

        return starsDrawableList
    }
}