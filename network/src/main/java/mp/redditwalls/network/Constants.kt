package mp.redditwalls.network

import okhttp3.Credentials

object Constants {
    const val BASE_REDDIT_URL = "https://www.reddit.com"
    const val BASE_REDDIT_MOBILE_URL = "https://m.reddit.com"
    const val BASE_REDDIT_OAUTH_URL = "https://oauth.reddit.com"
    const val BASE_IMGUR_URL = "https://api.imgur.com"
    const val GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
    const val DEVICE_ID = "DO_NOT_TRACK_THIS_DEVICE"
    const val IMGUR_CLIENT_ID = "9e53f7e44f27bdb"
    val CREDENTIALS = Credentials.basic("HsLRDKT4ayRwkyXZSp1Xvg", "")
    val AMP = "amp;".toRegex()
}