package mp.redditwalls.domain.usecases

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import mp.redditwalls.domain.models.DomainResult

abstract class FlowUseCase<D : Any, P : Any>(private val initialData: D) {
    protected var data: D
        private set
    private val _sharedFlow: MutableSharedFlow<DomainResult<D>> = MutableSharedFlow()
    val sharedFlow = _sharedFlow.asSharedFlow()

    init {
        data = initialData
    }

    protected suspend fun updateData(data: DomainResult<D>) {
        _sharedFlow.emit(data)
        this.data = data.data ?: initialData
    }

    abstract suspend operator fun invoke(params: P)
}