package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.models.DbSubreddit
import mp.redditwalls.local.repositories.LocalSubredditsRepository

class SaveSubredditUseCase @Inject constructor(
    private val localSubredditsRepository: LocalSubredditsRepository
) : UseCase<String, Unit>() {
    override suspend fun execute(params: String) {
        localSubredditsRepository.insertDbSubreddit(
            dbSubreddit = DbSubreddit(
                name = params
            )
        )
    }
}