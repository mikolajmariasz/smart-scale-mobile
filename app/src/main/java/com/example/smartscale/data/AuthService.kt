
package com.example.smartscale.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val username: String, val email: String, val password: String)
data class LoginRequest(val username: String, val password: String)
data class BasicResponse(val message: String)

interface AuthService {
    @POST("register/")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<BasicResponse>

    @POST("login/")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<BasicResponse>
}
