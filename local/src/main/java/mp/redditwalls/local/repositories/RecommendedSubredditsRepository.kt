package mp.redditwalls.local.repositories

import javax.inject.Inject

class RecommendedSubredditsRepository @Inject constructor() {
    fun getRecommendedSubreddits() = listOf(
        "mobilewallpaper",
        "animewallpaper",
        "earthporn"
    )
}