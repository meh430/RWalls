package mp.redditwalls.network

import okhttp3.Credentials

internal object Constants {
    const val BASE_URL = "https://www.reddit.com"
    const val BASE_MOBILE_URL = "https://m.reddit.com"
    const val BASE_OAUTH_URL = "https://oauth.reddit.com"
    const val GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client"
    const val DEVICE_ID = "DO_NOT_TRACK_THIS_DEVICE"
    val CREDENTIALS = Credentials.basic("HsLRDKT4ayRwkyXZSp1Xvg", "")
    val AMP = "amp;".toRegex()
}