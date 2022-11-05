package com.ud.marvel2022.network

import com.ud.marvel2022.model.character.CharacterData
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigInteger
import java.security.MessageDigest

const val PRIVATE_KEY = "00a456fa3c27eeb9ffa7ad32986130b9d845eb4a"
const val PUBLIC_KEY = "1116eefee0856af6da81cf3138899bd9"

interface MarvelAPI {

    @GET("characters")
    suspend fun getCharacters(
        @Query("limit") limit: Int,
        @Query("offset") offset: String? = null,
        @Query("apikey") apikey: String = PUBLIC_KEY,
        @Query("ts") ts: String,
        @Query("hash") hash: String = md5(ts + PRIVATE_KEY + PUBLIC_KEY)
    ): CharacterData

    @GET("characters")
    suspend fun getCharactersByName(
        @Query("nameStartsWith") nameStartsWith: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: String? = null,
        @Query("apikey") apikey: String = PUBLIC_KEY,
        @Query("ts") ts: String,
        @Query("hash") hash: String = md5(ts + PRIVATE_KEY + PUBLIC_KEY)
    ): CharacterData

    companion object{
        private const val BASE_URL = "https://gateway.marvel.com:443/v1/public/"

        private var marvelAPI : MarvelAPI? = null
        fun getInstance(): MarvelAPI {
                marvelAPI = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MarvelAPI::class.java)

            return marvelAPI!!
        }
    }
}

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}