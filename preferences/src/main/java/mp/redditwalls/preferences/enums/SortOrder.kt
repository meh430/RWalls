package mp.redditwalls.preferences.enums

import mp.redditwalls.preferences.R

enum class SortOrder(
    override val stringId: Int
) : PreferenceEnum {
    HOT(stringId = R.string.sort_hot),
    NEW(stringId = R.string.sort_new),
    TOP_HOUR(stringId = R.string.sort_top_hour),
    TOP_DAY(stringId = R.string.sort_top_day),
    TOP_WEEK(stringId = R.string.sort_top_week),
    TOP_MONTH(stringId = R.string.sort_top_month),
    TOP_YEAR(stringId = R.string.sort_top_year),
    TOP_ALL(stringId = R.string.sort_top_all);
}