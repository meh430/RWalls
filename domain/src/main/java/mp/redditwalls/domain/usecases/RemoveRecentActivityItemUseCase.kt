package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.RecentActivityRepository

class RemoveRecentActivityItemUseCase @Inject constructor(
    private val recentActivityRepository: RecentActivityRepository,
) : UseCase<Int, Unit>() {
    override suspend fun execute(params: Int) {
        recentActivityRepository.deleteDbRecentActivityItem(params)
    }
}