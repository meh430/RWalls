package mp.redditwalls.network

import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import mp.redditwalls.network.repositories.AuthRepository
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Route

internal class TokenAuthenticator @Inject constructor(
    private val authRepository: AuthRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response) = runBlocking {
        try {
            val authResponse = authRepository.getAccessToken()
            response.request.newBuilder()
                .header("Authorization", "Bearer ${authResponse.accessToken}")
                .build().also {
                    AccessToken.accessToken = authResponse.accessToken
                }
        } catch (e: Exception) {
            null
        }
    }
}

internal object AccessToken {
    var accessToken: String = ""
}