package mp.redditwalls.models

sealed class UiResult(
    val errorMessage: String? = null
) {
    class Loading : UiResult()
    class Error(errorMessage: String) : UiResult(errorMessage = errorMessage)
    class Success : UiResult()
}