package mp.redditwalls.domain.usecases

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mp.redditwalls.domain.models.DomainResult
import mp.redditwalls.domain.models.DomainSubreddit
import mp.redditwalls.domain.models.toDomainSubreddit
import mp.redditwalls.local.repositories.LocalSubredditsRepository
import mp.redditwalls.network.repositories.NetworkSubredditsRepository

class GetSavedSubredditsUseCase @Inject constructor(
    private val localSubredditsRepository: LocalSubredditsRepository,
    private val networkSubredditsRepository: NetworkSubredditsRepository
): FlowUseCase<List<DomainSubreddit>, Unit>(emptyList()) {
    override suspend operator fun invoke(params: Unit) {
        withContext(Dispatchers.IO) {
            localSubredditsRepository.getDbSubreddits().map { dbSubreddits ->
                val subreddits = dbSubreddits.map { dbSubreddit -> dbSubreddit.name }
                val nameToDbIds = dbSubreddits.associate { dbSubreddit ->
                    dbSubreddit.name to dbSubreddit.id
                }
                networkSubredditsRepository.getSubredditsInfo(subreddits).subreddits.map {
                    it.toDomainSubreddit(
                        isSaved = true,
                        dbId = nameToDbIds[it.name] ?: -1
                    )
                }
            }.catch { e ->
                updateData(DomainResult.Error(e.localizedMessage.orEmpty()))
            }.flowOn(Dispatchers.IO).collect {
                updateData(DomainResult.Success(it))
            }
        }
    }
}