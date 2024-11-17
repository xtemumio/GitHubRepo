/**
 * User
 *
 * Questa classe rappresenta il modello dei dati per un utente GitHub.
 * È utilizzata per deserializzare la risposta JSON dell'API di GitHub.
 * Contiene informazioni personali e statistiche del profilo di un utente.
 */

package com.example.githubrepo.models

import com.google.gson.annotations.SerializedName

/**
 * Modello dei dati per un utente GitHub.
 * @property login Il nome utente GitHub (nickname) dell'utente.
 * @property name Il nome completo dell'utente (se disponibile).
 * @property bio La biografia dell'utente (se disponibile).
 * @property company L'azienda o organizzazione dell'utente (se specificata).
 * @property location La località geografica dell'utente (se specificata).
 * @property email L'email pubblica dell'utente (se disponibile).
 * @property publicRepos Il numero di repository pubblici creati dall'utente.
 * @property avatarUrl L'URL dell'immagine del profilo dell'utente.
 * @property followers Il numero di follower dell'utente.
 * @property following Il numero di utenti seguiti dall'utente.
 * @property contributions Un array che rappresenta i contributi dell'utente (se forniti dall'API).
 */
data class User(
    val login: String?, // Nome utente o nickname su GitHub
    val name: String?, // Nome completo (potrebbe essere null se non specificato)
    val bio: String?, // Biografia dell'utente
    val company: String?, // Azienda o organizzazione associata all'utente
    val location: String?, // Località geografica dell'utente
    val email: String?, // Email pubblica dell'utente (può essere null)
    @SerializedName("public_repos") val publicRepos: Int, // Numero di repository pubblici
    @SerializedName("avatar_url") val avatarUrl: String, // URL dell'immagine del profilo
    val followers: Int?, // Numero di follower
    val following: Int?, // Numero di utenti seguiti
    val contributions: IntArray? // Array dei contributi, rappresenta i dati di attività dell'utente
)