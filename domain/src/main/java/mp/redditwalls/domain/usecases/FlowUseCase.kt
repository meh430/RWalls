package mp.redditwalls.domain.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import mp.redditwalls.domain.models.DomainResult

abstract class FlowUseCase<D : Any, P : Any>(private val initialData: D) {
    private var initialized = false
    protected var data: D
        private set

    private val _sharedFlow: MutableSharedFlow<DomainResult<D>> = MutableSharedFlow()
    val sharedFlow = _sharedFlow.asSharedFlow()

    private val _paramsFlow: MutableSharedFlow<P> = MutableSharedFlow()
    protected val paramsFlow = _paramsFlow.asSharedFlow()

    private var job: Job? = null

    init {
        data = initialData
    }

    // Make sure to subscribe before calling this!
    fun init(coroutineScope: CoroutineScope) {
        job?.cancel()
        job = coroutineScope.launch {
            initialized = true
            execute().retryWhen { e, _ ->
                updateData(DomainResult.Error(message = e.localizedMessage.orEmpty()))
                true
            }.flowOn(Dispatchers.IO).collect { result ->
                updateData(DomainResult.Success(result))
            }
        }
    }

    protected abstract fun execute(): Flow<D>

    suspend operator fun invoke(params: P) {
        if (!initialized) {
            throw IllegalStateException("init method must be called first")
        }
        _paramsFlow.emit(params)
    }

    private suspend fun updateData(data: DomainResult<D>) {
        _sharedFlow.emit(data)
        this.data = data.data ?: initialData
    }
}