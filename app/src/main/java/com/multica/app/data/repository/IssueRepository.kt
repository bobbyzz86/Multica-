package com.multica.app.data.repository

import com.multica.app.data.api.MulticaApi
import com.multica.app.data.local.dao.IssueDao
import com.multica.app.data.local.entity.IssueEntity
import com.multica.app.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepository @Inject constructor(
    private val api: MulticaApi,
    private val issueDao: IssueDao
) {
    fun getCachedIssues(status: String? = null): Flow<List<IssueEntity>> {
        return if (status != null) {
            issueDao.getIssuesByStatus(status)
        } else {
            issueDao.getAllIssues()
        }
    }

    suspend fun refreshIssues(status: String? = null): Result<List<Issue>> {
        return try {
            val response = api.getIssues(status = status)
            if (response.isSuccessful) {
                val issues = response.body()?.data ?: emptyList()
                issueDao.insertAll(issues.map { IssueEntity.fromIssue(it) })
                Result.success(issues)
            } else {
                Result.failure(Exception("Failed to fetch issues: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIssue(id: String): Result<Issue> {
        return try {
            val cached = issueDao.getIssueById(id)
            val response = api.getIssue(id)
            if (response.isSuccessful) {
                val issue = response.body()?.data
                if (issue != null) {
                    issueDao.insert(IssueEntity.fromIssue(issue))
                }
                Result.success(issue!!)
            } else {
                cached?.let { Result.success(it.toIssue()) }
                    ?: Result.failure(Exception("Failed to fetch issue: ${response.code()}"))
            }
        } catch (e: Exception) {
            val cached = issueDao.getIssueById(id)
            cached?.let { Result.success(it.toIssue()) }
                ?: Result.failure(e)
        }
    }

    suspend fun createIssue(request: CreateIssueRequest): Result<Issue> {
        return try {
            val response = api.createIssue(request)
            if (response.isSuccessful) {
                val issue = response.body()?.data!!
                issueDao.insert(IssueEntity.fromIssue(issue))
                Result.success(issue)
            } else {
                Result.failure(Exception("Failed to create issue: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIssue(id: String, request: UpdateIssueRequest): Result<Issue> {
        return try {
            val response = api.updateIssue(id, request)
            if (response.isSuccessful) {
                val issue = response.body()?.data!!
                issueDao.insert(IssueEntity.fromIssue(issue))
                Result.success(issue)
            } else {
                Result.failure(Exception("Failed to update issue: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComments(issueId: String): Result<List<Comment>> {
        return try {
            val response = api.getComments(issueId)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch comments: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createComment(issueId: String, content: String, parentId: String? = null): Result<Comment> {
        return try {
            val response = api.createComment(issueId, CreateCommentRequest(content, parentId))
            if (response.isSuccessful) {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception("Failed to create comment: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
