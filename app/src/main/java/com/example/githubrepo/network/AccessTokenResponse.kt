/**
 * AccessTokenResponse
 *
 * Questa classe rappresenta il modello dei dati per la risposta dell'API di GitHub
 * relativa al token di accesso.
 * Viene utilizzata per deserializzare i dati JSON restituiti dalla richiesta di scambio
 * del codice di autorizzazione con un token di accesso.
 */

package com.example.githubrepo.network

import com.google.gson.annotations.SerializedName

/**
 * Modello dei dati per la risposta relativa al token di accesso GitHub.
 *
 * @property accessToken Il token di accesso che consente all'app di autenticare le richieste API a nome dell'utente.
 * @property tokenType Il tipo di token restituito (ad esempio, "Bearer").
 */
data class AccessTokenResponse(
    @SerializedName("access_token") val accessToken: String, // Il token di accesso OAuth
    @SerializedName("token_type") val tokenType: String // Il tipo di token, di solito "Bearer"
)