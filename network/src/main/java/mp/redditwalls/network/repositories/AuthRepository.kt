package mp.redditwalls.network.repositories

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.network.Constants
import mp.redditwalls.network.models.AuthResponse
import mp.redditwalls.network.services.AuthService

class AuthRepository @Inject constructor(private val authService: AuthService) {
    suspend fun getAccessToken(): AuthResponse = withContext(Dispatchers.IO) {
        authService.getAccessToken(
            Constants.GRANT_TYPE,
            Constants.DEVICE_ID
        )
    }
}