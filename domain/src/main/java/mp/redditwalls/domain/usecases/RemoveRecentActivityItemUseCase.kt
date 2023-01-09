package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.RecentActivityRepository

class RemoveRecentActivityItemUseCase @Inject constructor(
    private val recentActivityRepository: RecentActivityRepository
) : UseCase<List<Int>, Unit>() {
    override suspend fun execute(params: List<Int>) {
        if (params.isEmpty()) {
            recentActivityRepository.deleteAllDbRecentActivityItem()
        } else {
            recentActivityRepository.deleteDbRecentActivityItem(params)
        }
    }
}