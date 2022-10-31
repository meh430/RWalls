package mp.redditwalls.models

sealed class UiResult(
    private val loadingState: LoadingState? = null,
    private val errorMessage: String? = null
) {
    class Loading(loadingState: LoadingState) : UiResult(loadingState = loadingState)
    class Error(errorMessage: String) : UiResult(errorMessage = errorMessage)
    class Success : UiResult()
}