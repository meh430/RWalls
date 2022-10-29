package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.domain.models.FeedResult
import mp.redditwalls.local.repositories.LocalImagesRepository
import mp.redditwalls.preferences.PreferencesRepository

class GetSavedSubredditsUseCase @Inject constructor(
    private val localImagesRepository: LocalImagesRepository,
    private val preferencesRepository: PreferencesRepository
): FlowUseCase<FeedResult, Unit>(FeedResult()) {
    override suspend operator fun invoke(params: Unit) {

    }
}