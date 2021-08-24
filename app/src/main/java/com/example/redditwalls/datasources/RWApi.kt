package com.example.redditwalls.datasources

import com.example.redditwalls.models.Image
import com.example.redditwalls.models.PostInfo
import com.example.redditwalls.models.Subreddit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object RWApi {

    const val PAGE_SIZE = 25

    enum class Sort(val queryParam: String) {
        HOT("/hot.json?sort=hot"),
        NEW("/new.json?sort=new"),
        TOP_WEEK("/top.json?sort=top&t=week"),
        TOP_MONTH("/top.json?sort=top&t=month"),
        TOP_YEAR("/top.json?sort=top&t=year"),
        TOP_ALL("/top.json?sort=top&t=all")
    }

    suspend fun getImages(
        subreddit: String,
        sort: Sort,
        after: String = ""
    ): Pair<List<Image>, String> = withContext(Dispatchers.Default) {
        val endpoint = StringBuilder("https://www.reddit.com/r/").apply {
            append(subreddit)
            append(sort.queryParam)
            if (after.isNotEmpty()) {
                append("&after=$after")
            }
        }.toString()

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
            val preview = data.getJSONObject("preview")
            val imageJSON = preview.getJSONArray("images").getJSONObject(0)
            val source = imageJSON.getJSONObject("source")
            val url = source.getString("url").replace("amp;".toRegex(), "")
            val resolutions = imageJSON.getJSONArray("resolutions")
            val previewUrl =
                resolutions.getJSONObject(0).getString("url").replace("amp;".toRegex(), "")

            val image = Image(
                imageLink = url,
                postLink = "https://www.reddit.com$postLink",
                subreddit = subreddit,
                previewLink = previewUrl
            )
            images.add(image)
        }

        if (after.isBlank()) {
            images.slice(0 until PAGE_SIZE)
        } else {
            images
        } to nextAfter
    }

    suspend fun searchSubs(query: String) = withContext(Dispatchers.Default) {

        val endpoint = "https://www.reddit.com/api/search_reddit_names/.json?query=$query"
        val json = JSONObject(fetch(endpoint))
        val names = json.getJSONArray("names")

        val results: MutableList<Subreddit> = mutableListOf()
        for (i in 0..names.length()) {
            val name = names.getString(i)
            val subInfo = getSubInfo(name)
            val data = subInfo.getJSONObject("data")
            val iconUrl = data.getString("icon_img")
            val title = data.getString("display_name_prefixed")
            val desc = data.getString("public_description")
            val subs = data.getInt("subscribers")

            val sub = Subreddit(
                name = title,
                description = desc,
                numSubscribers = subs,
                icon = iconUrl
            )
            results.add(sub)
        }

        results
    }

    suspend fun getPostInfo(postLink: String, imageSize: Int) =
        withContext(Dispatchers.Default) {
            val endpoint = "$postLink.json"
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
            val uploadDate = " "//AppUtils.convertUTC(utcTime * 1000)

            PostInfo(
                imageSize = imageSize,
                subreddit = sub,
                postTitle = title,
                upvotes = ups,
                uploadDate = uploadDate,
                numComments = numComments,
                author = author
            )
        }

    private suspend fun getSubInfo(subreddit: String = ""): JSONObject {
        val endpoint = "https://www.reddit.com/r/$subreddit/about/.json"
        return JSONObject(fetch(endpoint))
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

inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    val length = length()
    for (i in 0 until length) {
        action(getJSONObject(i))
    }
}