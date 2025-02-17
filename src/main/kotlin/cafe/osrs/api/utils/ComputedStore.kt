package cafe.osrs.api.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class ComputedStore <T> (
    private val cacheTime: Duration = 1.minutes,
    private val action: suspend () -> T
) {
    private val mutex = Mutex()
    private var lastGet = -1L
    private var lastGetResult: T? = null

    private fun shouldRefresh() = lastGet == -1L || (getUnixTime() > lastGet + cacheTime.inWholeSeconds)

    private suspend fun refresh() {
        lastGet = getUnixTime()
        lastGetResult = action.invoke()
    }

    suspend fun get(): T {
        mutex.withLock {
            if(shouldRefresh()) refresh()
            return lastGetResult ?: throw IllegalStateException("ComputedStore has not been refreshed yet")
        }
    }

    val lastRefresh: Long get() = lastGet
    val nextRefresh: Long get() = lastGet + cacheTime.inWholeSeconds
}