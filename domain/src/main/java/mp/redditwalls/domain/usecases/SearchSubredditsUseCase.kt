package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
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
    override suspend operator fun invoke(params: String) = withContext(Dispatchers.IO) {
        combine(
            localSubredditsRepository.getDbSubreddits(),
            preferencesRepository.getAllowNsfw()
        ) { dbSubreddits, allowNsfw ->
            val nameToDbIdMap = dbSubreddits.associate { it.name to it.id }
            networkSubredditsRepository.searchSubreddits(
                params,
                allowNsfw
            ).subreddits.map {
                it.toDomainSubreddit(
                    isSaved = nameToDbIdMap.containsKey(it.name),
                    dbId = nameToDbIdMap[it.name] ?: -1
                )
            }
        }.resolveResult()
    }
}