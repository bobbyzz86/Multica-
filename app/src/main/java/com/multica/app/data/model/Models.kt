package com.multica.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val email: String?,
    val name: String?,
    val avatarUrl: String?
)

data class Workspace(
    val id: String,
    val name: String,
    val slug: String?,
    val description: String?
)

data class Issue(
    val id: String,
    val number: Int,
    val identifier: String,
    val title: String,
    val description: String?,
    val status: String,
    val priority: String?,
    val assigneeId: String?,
    val assigneeType: String?,
    val assignee: User?,
    val creatorId: String?,
    val creatorType: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val dueDate: String?,
    val parentIssueId: String?,
    val labels: List<String>?,
    val stage: Int?
)

data class Comment(
    val id: String,
    val content: String?,
    val authorId: String?,
    val authorType: String?,
    val author: User?,
    val createdAt: String?,
    val updatedAt: String?,
    val parentId: String?,
    val resolvedAt: String?,
    val attachments: List<Attachment>?,
    val reactions: List<Reaction>?
)

data class Attachment(
    val id: String,
    val filename: String?,
    val contentType: String?,
    val sizeBytes: Long?,
    val url: String?,
    val downloadUrl: String?
)

data class Reaction(
    val emoji: String?,
    val count: Int?
)

data class Notification(
    val id: String,
    val title: String?,
    val body: String?,
    val issueId: String?,
    val read: Boolean?,
    val createdAt: String?
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User?,
    val workspace: Workspace?
)

data class TokenRefreshRequest(
    val refreshToken: String
)

data class TokenRefreshResponse(
    val token: String,
    val refreshToken: String?
)

data class CreateIssueRequest(
    val title: String,
    val description: String? = null,
    val priority: String? = null,
    val assigneeId: String? = null,
    val status: String? = null
)

data class UpdateIssueRequest(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val priority: String? = null,
    val assigneeId: String? = null
)

data class CreateCommentRequest(
    val content: String,
    val parentId: String? = null
)

data class ApiListResponse<T>(
    val data: List<T>?,
    val total: Int?,
    val limit: Int?,
    val offset: Int?
)

data class ApiSingleResponse<T>(
    val data: T?
)
