package org.example.kmpapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ApiService(
    private val client: HttpClient
) {
    private val BASE_URL = "Http://"
    suspend fun getAllPosts(): List<String> {
        return client.get("$BASE_URL/posts").body()
    }
    suspend fun getPostById(id: Int): String {
        return client.get("$BASE_URL/$id").body()
    }
}