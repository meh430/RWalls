package mp.redditwalls.viewmodels

import kotlinx.coroutines.CoroutineScope

abstract class DelegateViewModel {
    protected lateinit var coroutineScope: CoroutineScope

    fun init(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }
}