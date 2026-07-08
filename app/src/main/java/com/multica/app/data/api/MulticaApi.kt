package com.multica.app.data.api

import com.multica.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface MulticaApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<LoginResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: TokenRefreshRequest): Response<TokenRefreshResponse>

    @GET("api/issues")
    suspend fun getIssues(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ApiListResponse<Issue>>

    @GET("api/issues/{id}")
    suspend fun getIssue(@Path("id") id: String): Response<ApiSingleResponse<Issue>>

    @POST("api/issues")
    suspend fun createIssue(@Body request: CreateIssueRequest): Response<ApiSingleResponse<Issue>>

    @PATCH("api/issues/{id}")
    suspend fun updateIssue(
        @Path("id") id: String,
        @Body request: UpdateIssueRequest
    ): Response<ApiSingleResponse<Issue>>

    @GET("api/issues/{id}/comments")
    suspend fun getComments(@Path("id") issueId: String): Response<ApiListResponse<Comment>>

    @POST("api/issues/{id}/comments")
    suspend fun createComment(
        @Path("id") issueId: String,
        @Body request: CreateCommentRequest
    ): Response<ApiSingleResponse<Comment>>

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiListResponse<Notification>>

    @GET("api/workspaces")
    suspend fun getWorkspaces(): Response<ApiListResponse<Workspace>>

    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<ApiSingleResponse<User>>

    @GET("api/users")
    suspend fun getUsers(): Response<ApiListResponse<User>>
}
