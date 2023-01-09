package mp.redditwalls.domain.models

data class RecentActivityResult(
    val recentActivity: List<DomainRecentActivityItem> = emptyList(),
    val recentActivityGroupedByDay: Map<String, List<DomainRecentActivityItem>> = emptyMap()
)