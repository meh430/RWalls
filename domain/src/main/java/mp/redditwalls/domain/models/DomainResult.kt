package mp.redditwalls.domain.models

sealed class DomainResult<T>(
    val data: T? = null,
    val message: String = ""
) {
    class Success<T>(data: T) : DomainResult<T>(data)
    class Error<T>(message: String, data: T? = null) : DomainResult<T>(data, message)
}