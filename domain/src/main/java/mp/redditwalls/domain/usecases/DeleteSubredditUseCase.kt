package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.LocalSubredditsRepository

class DeleteSubredditUseCase @Inject constructor(
    private val localSubredditsRepository: LocalSubredditsRepository
) : UseCase<String, Unit>() {
    override suspend fun execute(params: String) {
        localSubredditsRepository.deleteDbSubreddit(params)
    }
}