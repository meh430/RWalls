package mp.redditwalls.network.models

sealed class ApiResponse<T>(val data: T? = null, val message: String = "") {
    class Success<T>(data: T) : ApiResponse<T>(data = data)
    class Error<T>(message: String) : ApiResponse<T>(message = message)
}