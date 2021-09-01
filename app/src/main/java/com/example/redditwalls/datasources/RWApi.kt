@file:Suppress("BlockingMethodInNonBlockingContext")

package com.example.redditwalls.datasources

import android.net.Uri
import com.example.redditwalls.R
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.misc.forEach
import com.example.redditwalls.models.Image
import com.example.redditwalls.models.PostInfo
import com.example.redditwalls.models.Subreddit
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class RWApi @Inject constructor() {

    companion object {
        const val PAGE_SIZE = 25
        const val BASE = "https://www.reddit.com"
        const val RAW_JSON_QUERY = "raw_json=true"
    }

    enum class Sort(val trailing: String, val queryParam: String) {
        HOT("/hot.json", "sort=hot"),
        NEW("/new.json", "sort=new"),
        TOP_WEEK("/top.json", "sort=top&t=week"),
        TOP_MONTH("/top.json", "sort=top&t=month"),
        TOP_YEAR("/top.json", "sort=top&t=year"),
        TOP_ALL("/top.json", "sort=top&t=all");

        companion object {
            fun fromId(id: Int) = when (id) {
                R.id.sort_hot -> HOT
                R.id.sort_new -> NEW
                R.id.sort_top_all -> TOP_ALL
                R.id.sort_top_year -> TOP_YEAR
                R.id.sort_top_month -> TOP_MONTH
                R.id.sort_top_week -> TOP_WEEK
                else -> HOT
            }
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
            append("&include_over_18=true")
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

        childrenArr.forEach {
            val data = it.getJSONObject("data")
            if (!data.has("preview")) {
                return@forEach
            }

            val postLink = data.getString("permalink")
            val (previewLink, imageLink) = getImageInfoFromData(data)
            val image = Image(
                imageLink = imageLink,
                postLink = "$BASE$postLink",
                subreddit = subreddit,
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

    // subreddit to id
    // To use with search
    fun extractPostLinkInfo(link: String): Pair<String, String> {
        val uri = Uri.parse(link)

        val scheme = uri.scheme
        val host = uri.host
        val pathSegments = uri.pathSegments

        if (scheme != "https" || host != "www.reddit.com") {
            return "" to ""
        }

        return try {
            pathSegments[1] to pathSegments[3]
        } catch (e: IndexOutOfBoundsException) {
            "" to ""
        }
    }

    // To use with deeplink query
    fun buildPostLink(subreddit: String, id: String): String {
        var sub = subreddit
        if (subreddit.startsWith("r/")) {
            sub = subreddit.substring(2)
        }

        return "$BASE/r/$sub/comments/$id/.json?$RAW_JSON_QUERY"
    }

    suspend fun getImageFromPost(postLink: String): Image {
        val data = JSONObject(fetch(postLink)).getJSONObject("data")
        val postInfo = data.getJSONArray("children").getJSONObject(0).getJSONObject("data")
        val (preview, imageLink) = getImageInfoFromData(postInfo)

        return Image(
            id = -1,
            imageLink = imageLink,
            postLink = BASE + postInfo.getString("permalink"),
            subreddit = postInfo.getString("subreddit"),
            previewLink = preview
        )
    }


    private suspend fun fetch(endpoint: String) = withContext(Dispatchers.IO) {
        val url = URL(endpoint)
        with(url.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "GET"

            println("URL : $url")
            println("Response Code : $responseCode")

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