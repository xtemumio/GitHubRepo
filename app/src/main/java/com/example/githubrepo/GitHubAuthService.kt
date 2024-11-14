import com.example.githubrepo.AccessTokenResponse
import com.example.githubrepo.Repository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface GitHubAuthService {
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String
    ): Call<ResponseBody>

    @GET("user/repos")
    fun getRepositories(
        @Header("Authorization") accessToken: String
    ): Call<List<Repository>>
}