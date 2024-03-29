@file:Suppress("BlockingMethodInNonBlockingContext")

package mp.redditwalls.datasources

import android.content.res.Resources
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import mp.redditwalls.R
import mp.redditwalls.utils.Utils
import mp.redditwalls.utils.forEach
import mp.redditwalls.utils.fromId
import mp.redditwalls.utils.removeSubPrefix
import mp.redditwalls.models.Image
import mp.redditwalls.models.PostInfo
import mp.redditwalls.models.Subreddit
import mp.redditwalls.repositories.SettingsItem
import mp.redditwalls.repositories.SettingsRepository
import org.json.JSONArray
import org.json.JSONObject

class RWApi @Inject constructor(private val settingsRepository: SettingsRepository) {

    companion object {
        const val PAGE_SIZE = 25
        const val BASE = "https://www.reddit.com"
        const val RAW_JSON_QUERY = "raw_json=1"

        private val hosts = setOf(
            "www.reddit.com",
            "amp.reddit.com",
            "old.reddit.com",
            "www.google.com"
        )

        // subreddit to id
        // To use with search
        fun extractPostLinkInfo(link: String): Pair<String, String> {
            val uri = Uri.parse(link)

            val host = uri.host
            val pathSegments = uri.pathSegments

            if (!hosts.contains(host)) {
                return "" to ""
            }


            return try {
                val subredditIndex = pathSegments.indexOfFirst { it == "r" }.takeIf {
                    it != -1
                }
                val idIndex = pathSegments.indexOfFirst { it == "comments" }.takeIf { it != -1 }

                if (subredditIndex == null || idIndex == null) {
                    "" to ""
                } else {

                    pathSegments[subredditIndex + 1] to pathSegments[idIndex + 1]
                }
            } catch (e: Exception) {
                "" to ""
            }
        }
    }

    enum class Sort(
        val trailing: String,
        val queryParam: String,
        override val displayText: String,
        override val id: Int
    ) :
        SettingsItem {
        HOT("/hot.json", "sort=hot", "hot", 0),
        NEW("/new.json", "sort=new", "new", 1),
        TOP_DAY("/top.json", "sort=top&t=day", "top day", 2),
        TOP_WEEK("/top.json", "sort=top&t=week", "top week", 3),
        TOP_MONTH("/top.json", "sort=top&t=month", "top month", 4),
        TOP_YEAR("/top.json", "sort=top&t=year", "top year", 5),
        TOP_ALL("/top.json", "sort=top&t=all", "top all", 6);

        companion object {
            fun fromMenuId(id: Int) = when (id) {
                R.id.sort_hot -> HOT
                R.id.sort_new -> NEW
                R.id.sort_top_all -> TOP_ALL
                R.id.sort_top_year -> TOP_YEAR
                R.id.sort_top_month -> TOP_MONTH
                R.id.sort_top_week -> TOP_WEEK
                R.id.sort_top_day -> TOP_DAY
                else -> HOT
            }

            fun fromId(id: Int) = values().fromId(id, HOT)
        }
    }

    // http://reddit.com/r/anime/search/.json?q=kanojo&restrict_sr=true&sort=top&t=year&limit=25
    private fun buildImageListEndpoint(
        subreddit: String,
        query: String = "",
        sort: Sort,
        after: String
    ): String {
        val trailing = if (query.isNotBlank()) {
            "/search/.json?q=$query&restrict_sr=true&${sort.queryParam}"
        } else {
            "${sort.trailing}?${sort.queryParam}"
        }

        val suffix = if (subreddit.startsWith("r/")) {
            subreddit
        } else {
            "r/$subreddit"
        }

        val base = "$BASE/$suffix"
        return StringBuilder(base).apply {
            append(trailing)
            append("&limit=25")
            append("&after=$after")
            append("&include_over_18=${settingsRepository.nsfwAllowed()}")
            append("&$RAW_JSON_QUERY")
        }.toString()
    }

    suspend fun getImages(
        subreddit: String,
        query: String = "",
        sort: Sort,
        after: String = ""
    ): Pair<List<Image>, String> = withContext(Dispatchers.Default) {
        val endpoint = buildImageListEndpoint(subreddit, query, sort, after)

        val json = JSONObject(fetch(endpoint)).getJSONObject("data")
        val nextAfter = json.getString("after")
        val childrenArr = json.getJSONArray("children")

        val images = mutableListOf<Image>()
        val nsfwAllowed = settingsRepository.nsfwAllowed()
        childrenArr.forEach {
            val data = it.getJSONObject("data")
            val sub = data.getString("subreddit")

            val over18 = data.getBoolean("over_18")

            if (over18 && !nsfwAllowed) {
                return@forEach
            }

            val postLink = data.getString("permalink")
            val (previewLink, imageLink) = try {
                getImageInfoFromData(data)
            } catch (e: Exception) {
                null to null
            }

            if (previewLink.isNullOrEmpty() || imageLink.isNullOrEmpty()) {
                return@forEach
            }

            val image = Image(
                imageLink = imageLink,
                postLink = "https://m.reddit.com$postLink",
                subreddit = sub,
                previewLink = previewLink
            )
            images.add(image)
        }

        val res = if (after.isBlank() && images.size > 25) {
            images.slice(0 until PAGE_SIZE)
        } else {
            images
        }

        res to if (nextAfter == "null" || res.isEmpty()) "" else nextAfter
    }


    suspend fun searchSubs(query: String): List<Subreddit> =
        withContext(Dispatchers.Default) {

            val endpoint = "$BASE/api/search_reddit_names/.json?query=$query"
            val json = JSONObject(fetch(endpoint))
            val names = json.getJSONArray("names")

            val subJobs = mutableListOf<Deferred<Subreddit>>()
            for (i in 0 until names.length()) {
                val subOperation = async {
                    val name = names.getString(i)
                    val subInfo = getSubInfo(name)
                    val data = subInfo.getJSONObject("data")
                    val iconUrl = data.getString("icon_img")
                    val title = data.getString("display_name_prefixed")
                    val desc = data.getString("public_description")
                    val subs = data.getInt("subscribers")

                    Subreddit(
                        name = title,
                        description = desc,
                        numSubscribers = Utils.formatNumber(subs + 0.0, false),
                        icon = iconUrl,
                        isSaved = false
                    )
                }
                subJobs.add(subOperation)
            }


            subJobs.map { it.await() }
        }

    suspend fun getPostInfo(postLink: String, imageSize: Double) =
        withContext(Dispatchers.Default) {
            val endpoint = "$postLink.json?$RAW_JSON_QUERY"
            val jsonArr = JSONArray(fetch(endpoint))

            val json = jsonArr.getJSONObject(0)
                .getJSONObject("data")
                .getJSONArray("children")
                .getJSONObject(0)
                .getJSONObject("data")
            val sub = json.getString("subreddit")
            val title = json.getString("title").trim()
            val ups = json.getInt("ups")
            val utcTime = json.getLong("created_utc")
            val author = json.getString("author")
            val numComments = json.getString("num_comments")
            val uploadDate = Utils.convertUTC(utcTime * 1000)

            PostInfo(
                imageSize = "${Utils.formatNumber(imageSize, true)} MB",
                subreddit = "r/$sub",
                postTitle = title,
                upvotes = Utils.formatNumber(ups.toDouble()),
                uploadDate = uploadDate,
                numComments = Utils.formatNumber(numComments.toDouble()),
                author = "u/$author"
            )
        }

    private suspend fun getSubInfo(subreddit: String = ""): JSONObject {
        val endpoint = "$BASE/r/$subreddit/about/.json?$RAW_JSON_QUERY"
        return JSONObject(fetch(endpoint))
    }

    // preview to image link
    private fun getImageInfoFromData(data: JSONObject): Pair<String, String> {
        val preview = data.getJSONObject("preview")
        val imageJSON = preview.getJSONArray("images").getJSONObject(0)
        val source = imageJSON.getJSONObject("source")
        val url = source.getString("url").replace("amp;".toRegex(), "")
        val resolutions = imageJSON.getJSONArray("resolutions")
        val previewUrl =
            resolutions.getJSONObject(0)
                .getString("url")
                .replace("amp;".toRegex(), "")
        return previewUrl to url
    }

    // To use with deeplink query
    private fun buildPostLink(subreddit: String, id: String): String {
        return "$BASE/r/${subreddit.removeSubPrefix()}/comments/$id/.json?$RAW_JSON_QUERY"
    }

    suspend fun getImageFromPost(postLink: String = "", subreddit: String = "", id: String = "") =
        withContext(Dispatchers.Default) {

            val endpoint = if (postLink.isEmpty()) {
                buildPostLink(subreddit, id)
            } else {
                postLink
            }

            val json = JSONArray(fetch(endpoint)).getJSONObject(0)
            val data = json.getJSONObject("data")
            val postInfo = data.getJSONArray("children").getJSONObject(0).getJSONObject("data")

            if (!postInfo.has("preview")) {
                throw Resources.NotFoundException("No image found in this post")
            }

            val (preview, imageLink) = getImageInfoFromData(postInfo)

            Image(
                id = -1,
                imageLink = imageLink,
                // work around to stop infinite loop with intent filters
                postLink = "https://m.reddit.com" + postInfo.getString("permalink"),
                subreddit = postInfo.getString("subreddit"),
                previewLink = preview
            )
        }


    private suspend fun fetch(endpoint: String) = withContext(Dispatchers.IO) {
        val url = URL(endpoint)
        with(url.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "GET"

            if (responseCode == 404) {
                throw Resources.NotFoundException("404 resource not found")
            }

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuilder()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                response.toString()
            }
        }
    }
}