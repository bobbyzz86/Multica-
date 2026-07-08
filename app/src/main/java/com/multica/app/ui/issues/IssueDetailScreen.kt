package com.multica.app.ui.issues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multica.app.data.model.Comment
import com.multica.app.ui.components.*
import com.multica.app.util.formatIssueDate
import com.multica.app.util.orDash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueDetailScreen(
    issueId: String,
    onBack: () -> Unit,
    viewModel: IssueViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(issueId) {
        viewModel.loadIssue(issueId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Issue Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingIndicator(Modifier.padding(padding))
            state.error != null -> ErrorView(
                message = state.error!!,
                onRetry = { viewModel.loadIssue(issueId) },
                modifier = Modifier.padding(padding)
            )
            state.issue != null -> {
                val issue = state.issue!!
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = issue.identifier,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = issue.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusChip(issue.status)
                            issue.priority?.let { PriorityChip(it) }
                        }
                    }

                    if (issue.assignee != null) {
                        item {
                            Text(
                                text = "Assignee: ${issue.assignee.name ?: issue.assignee.email.orDash()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (issue.description != null) {
                        item {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = issue.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Created: ${issue.createdAt?.formatIssueDate().orDash()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Updated: ${issue.updatedAt?.formatIssueDate().orDash()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        HorizontalDivider()
                        Text(
                            text = "Comments (${state.comments.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (state.comments.isEmpty()) {
                        item {
                            Text(
                                text = "No comments yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(state.comments, key = { it.id }) { comment ->
                            CommentItem(comment = comment)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        var commentText by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            label = { Text("Add a comment") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            trailingIcon = {
                                if (commentText.isNotBlank()) {
                                    TextButton(onClick = {
                                        viewModel.addComment(issueId, commentText)
                                        commentText = ""
                                    }) {
                                        Text("Send")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.author?.name ?: comment.authorId.orDash(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = comment.createdAt?.formatIssueDate().orDash(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!comment.content.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
