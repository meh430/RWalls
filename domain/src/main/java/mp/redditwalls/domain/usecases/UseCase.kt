package mp.redditwalls.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.models.DomainResult

abstract class UseCase<P : Any, R : Any> {
    protected abstract suspend fun execute(params: P): R

    suspend operator fun invoke(params: P): DomainResult<R> = withContext(Dispatchers.IO) {
        try {
            DomainResult.Success(execute(params))
        } catch (e: Exception) {
            DomainResult.Error(message = e.localizedMessage.orEmpty())
        }
    }
}