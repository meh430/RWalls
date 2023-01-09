package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import mp.redditwalls.domain.models.DomainSubreddit
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository
import mp.redditwalls.preferences.PreferencesRepository

class SearchSubredditsUseCase @Inject constructor(
    private val networkSubredditsRepository: NetworkSubredditsRepository,
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val preferencesRepository: PreferencesRepository
) : FlowUseCase<List<DomainSubreddit>, String>(emptyList()) {
    override fun execute() = combine(
        paramsFlow,
        preferencesRepository.getAllowNsfw()
    ) { params, allowNsfw ->
        val dbSubredditNames =
            localSubredditsRepository.getDbSubredditsList().map { it.name }.toSet()
        networkSubredditsRepository.searchSubreddits(
            params,
            allowNsfw
        ).subreddits.map {
            it.toDomainSubreddit(
                isSaved = dbSubredditNames.contains(it.name),
            )
        }
    }
}