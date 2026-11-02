package com.example

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AuthResult(val accessToken:String,val refreshToken:String,val userId:String,val email:String)
data class RemoteMovie(
    val id:String,
    val title:String,
    val year:Int,
    val genre:String,
    val rating:String,
    val summary:String,
    val videoUrl:String,
    val isPremium:Boolean
)

class SupabaseApi(private val projectUrl:String, private val anonKey:String) {
    val isConfigured:Boolean get() = projectUrl.startsWith("https://") && anonKey.isNotBlank()

    fun signUp(email:String,password:String,name:String):AuthResult {
        val body=JSONObject().put("email",email).put("password",password)
            .put("data",JSONObject().put("name",name))
        return parseAuth(request("/auth/v1/signup","POST",body,null))
    }

    fun signIn(email:String,password:String):AuthResult {
        val body=JSONObject().put("email",email).put("password",password)
        return parseAuth(request("/auth/v1/token?grant_type=password","POST",body,null))
    }

    fun refresh(refreshToken:String):AuthResult {
        val body=JSONObject().put("refresh_token",refreshToken)
        return parseAuth(request("/auth/v1/token?grant_type=refresh_token","POST",body,null))
    }

    fun getMovies(accessToken:String?):List<RemoteMovie> {
        val json=request("/rest/v1/movies?select=id,title,description,video_url,year,genres,rating,is_premium&is_published=eq.true&order=created_at.desc","GET",null,accessToken) as JSONArray
        return (0 until json.length()).map { i ->
            val item=json.getJSONObject(i)
            val genres=item.optJSONArray("genres")
            val genre=if(genres==null || genres.length()==0) "Kino" else
                (0 until genres.length()).joinToString(" • ") { genres.optString(it) }
            RemoteMovie(
                id=item.getString("id"),
                title=item.optString("title","Nomsiz kino"),
                year=item.optInt("year",0),
                genre=genre,
                rating=if(item.has("rating")) item.optString("rating","0") else "0",
                summary=item.optString("description",""),
                videoUrl=item.optString("video_url",""),
                isPremium=item.optBoolean("is_premium",false)
            )
        }
    }

    fun getProfileRole(accessToken:String,userId:String):String {\n        val json=request("/rest/v1/profiles?select=role,is_banned&id=eq.$userId","GET",null,accessToken) as JSONArray\n        if(json.length()==0) return "user"\n        val profile=json.getJSONObject(0)\n        if(profile.optBoolean("is_banned",false)) throw IllegalStateException("Hisobingiz bloklangan.")\n        return profile.optString("role","user")\n    }\n\n    fun createMovie(accessToken:String,title:String,description:String,year:Int,videoUrl:String) {\n        val body=JSONArray().put(JSONObject().put("title",title).put("description",description).put("year",year)\n            .put("video_url",videoUrl).put("is_published",true))\n        request("/rest/v1/movies","POST",body,accessToken,mapOf("Prefer" to "return=minimal"))\n    }\n\n    fun deleteMovie(accessToken:String,movieId:String) {\n        request("/rest/v1/movies?id=eq.$movieId","DELETE",null,accessToken)\n    }\n\n    fun getFavoriteIds(accessToken:String,userId:String):Set<String> {
        val json=request("/rest/v1/favorites?select=movie_id&user_id=eq.$userId","GET",null,accessToken) as JSONArray
        return (0 until json.length()).map { json.getJSONObject(it).getString("movie_id") }.toSet()
    }

    fun addFavorite(accessToken:String,userId:String,movieId:String) {
        val body=JSONArray().put(JSONObject().put("user_id",userId).put("movie_id",movieId))
        request("/rest/v1/favorites","POST",body,accessToken, mapOf("Prefer" to "resolution=ignore-duplicates,return=minimal"))
    }

    fun removeFavorite(accessToken:String,userId:String,movieId:String) {
        request("/rest/v1/favorites?user_id=eq.$userId&movie_id=eq.$movieId","DELETE",null,accessToken)
    }

    private fun parseAuth(json:JSONObject):AuthResult {
        val user=json.optJSONObject("user") ?: throw IllegalStateException("Hisob ma’lumoti qaytmadi.")
        return AuthResult(
            json.optString("access_token"),
            json.optString("refresh_token"),
            user.optString("id"),
            user.optString("email")
        )
    }

    private fun request(path:String,method:String,body:Any?,accessToken:String?,extra:Map<String,String> = emptyMap()):Any {
        val base=projectUrl.trimEnd('/')
        val connection=(URL(base+path).openConnection() as HttpURLConnection)
        try {
            connection.requestMethod=method
            connection.connectTimeout=15000
            connection.readTimeout=20000
            connection.setRequestProperty("apikey",anonKey)
            connection.setRequestProperty("Authorization","Bearer ${accessToken ?: anonKey}")
            connection.setRequestProperty("Content-Type","application/json")
            extra.forEach { (key,value)->connection.setRequestProperty(key,value) }
            if(body!=null) {
                connection.doOutput=true
                val payload=body.toString().toByteArray(Charsets.UTF_8)
                connection.outputStream.use { it.write(payload) }
            }
            val code=connection.responseCode
            val stream=if(code in 200..299) connection.inputStream else connection.errorStream
            val response=stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if(code !in 200..299) {
                val message=try { JSONObject(response).optString("message",JSONObject(response).optString("msg",response)) } catch(_:Exception) { response }
                throw IllegalStateException(if(message.isBlank()) "Server xatosi: HTTP $code" else message)
            }
            if(response.isBlank()) return JSONObject()
            return if(response.trimStart().startsWith("[")) JSONArray(response) else JSONObject(response)
        } finally {
            connection.disconnect()
        }
    }
}
