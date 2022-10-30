package mp.redditwalls.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import mp.redditwalls.domain.models.DomainResult

abstract class FlowUseCase<D : Any, P : Any>(private val initialData: D) {
    protected var data: D
        private set
    private val _sharedFlow: MutableSharedFlow<DomainResult<D>> = MutableSharedFlow()
    val sharedFlow = _sharedFlow.asSharedFlow()

    init {
        data = initialData
    }

    protected abstract fun execute(params: P): Flow<D>

    suspend operator fun invoke(params: P) {
        execute(params).catch { e ->
            updateData(DomainResult.Error(message = e.localizedMessage.orEmpty()))
        }.flowOn(Dispatchers.IO).collect {
            updateData(DomainResult.Success(it))
        }
    }

    private suspend fun updateData(data: DomainResult<D>) {
        _sharedFlow.emit(data)
        this.data = data.data ?: initialData
    }
}