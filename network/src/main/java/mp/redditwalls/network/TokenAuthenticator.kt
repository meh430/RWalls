package mp.redditwalls.network

import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import mp.redditwalls.network.models.ApiResponse
import mp.redditwalls.network.repositories.AuthRepository
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Route

internal class TokenAuthenticator @Inject constructor(
    private val authRepository: AuthRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response) = runBlocking {
        when (val authResponse = authRepository.getAccessToken()) {
            is ApiResponse.Error -> null
            is ApiResponse.Success -> response.request.newBuilder()
                .header("Authorization", "Bearer ${authResponse.data?.accessToken}")
                .build().also {
                    AccessToken.accessToken = authResponse.data?.accessToken ?: ""
                }
        }

    }
}

internal object AccessToken {
    var accessToken: String = ""
}