/**
 * GitHubAuthService
 *
 * Questa interfaccia definisce i metodi per interagire con l'API di GitHub.
 * Contiene metodi per autenticare un utente, ottenere i repository e il profilo dell'utente autenticato.
 */

package com.example.githubrepo.network

import Repository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interfaccia che rappresenta i servizi di autenticazione e recupero dati di GitHub.
 */
interface GitHubAuthService {

    /**
     * Effettua una richiesta per ottenere un token di accesso OAuth da GitHub.
     *
     * @param clientId L'ID del client registrato su GitHub.
     * @param clientSecret Il secret del client registrato su GitHub.
     * @param code Il codice di autorizzazione ricevuto durante il flusso OAuth.
     * @param redirectUri L'URI di reindirizzamento registrato su GitHub.
     * @return Una chiamata Retrofit `Call<ResponseBody>` contenente la risposta grezza dall'API di GitHub.
     */
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Call<ResponseBody>

    /**
     * Recupera la lista dei repository dell'utente autenticato.
     *
     * @param accessToken Il token di accesso necessario per autenticare la richiesta.
     * @return Una chiamata Retrofit `Call<List<Repository>>` contenente la lista dei repository.
     */
    @GET("user/repos")
    fun getRepositories(
        @Header("Authorization") accessToken: String
    ): Call<List<Repository>>

    /**
     * Recupera il profilo dell'utente autenticato.
     *
     * @param authToken Il token di accesso necessario per autenticare la richiesta.
     * @return Una chiamata Retrofit `Call<User>` contenente i dettagli del profilo utente.
     */
    @GET("user")
    fun getUserProfile(
        @Header("Authorization") authToken: String
    ): Call<com.example.githubrepo.models.User>
}