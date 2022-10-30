package mp.redditwalls.domain.usecases

import javax.inject.Inject
import mp.redditwalls.local.repositories.LocalSubredditsRepository

class DeleteSubredditUseCase @Inject constructor(
    private val localSubredditsRepository: LocalSubredditsRepository
) : UseCase<Int, Unit>() {
    override suspend fun execute(params: Int) {
        localSubredditsRepository.deleteDbSubreddit(params)
    }
}