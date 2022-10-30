package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.domain.models.toDomainRecentActivityItem
import mp.redditwalls.local.repositories.RecentActivityRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetRecentActivityUseCase @Inject constructor(
    private val recentActivityRepository: RecentActivityRepository,
    private val preferencesRepository: PreferencesRepository
): FlowUseCase<List<DomainRecentActivityItem>, Unit>(emptyList()) {
    override suspend operator fun invoke(params: Unit) = withContext(Dispatchers.IO) {
        combine(
            recentActivityRepository.getDbRecentActivityItems(),
            preferencesRepository.getPreviewResolution()
        ) { recentActivities, previewResolution ->
            recentActivities.map {
                it.toDomainRecentActivityItem(previewResolution)
            }
        }.resolveResult()
    }
}