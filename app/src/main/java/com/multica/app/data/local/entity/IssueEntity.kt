package com.multica.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.multica.app.data.model.Issue

@Entity(tableName = "issues")
data class IssueEntity(
    @PrimaryKey val id: String,
    val number: Int,
    val identifier: String,
    val title: String,
    val description: String?,
    val status: String,
    val priority: String?,
    val assigneeId: String?,
    val assigneeName: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val dueDate: String?,
    val labels: String?
) {
    fun toIssue() = Issue(
        id = id,
        number = number,
        identifier = identifier,
        title = title,
        description = description,
        status = status,
        priority = priority,
        assigneeId = assigneeId,
        assigneeType = null,
        assignee = null,
        creatorId = null,
        creatorType = null,
        createdAt = createdAt,
        updatedAt = updatedAt,
        dueDate = dueDate,
        parentIssueId = null,
        labels = labels?.split(",")?.filter { it.isNotBlank() },
        stage = null
    )

    companion object {
        fun fromIssue(issue: Issue) = IssueEntity(
            id = issue.id,
            number = issue.number,
            identifier = issue.identifier,
            title = issue.title,
            description = issue.description,
            status = issue.status,
            priority = issue.priority,
            assigneeId = issue.assigneeId,
            assigneeName = issue.assignee?.name,
            createdAt = issue.createdAt,
            updatedAt = issue.updatedAt,
            dueDate = issue.dueDate,
            labels = issue.labels?.joinToString(",")
        )
    }
}
