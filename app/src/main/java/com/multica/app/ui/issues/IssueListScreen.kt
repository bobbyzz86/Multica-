package com.multica.app.ui.issues

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multica.app.data.model.Issue
import com.multica.app.ui.components.EmptyView
import com.multica.app.ui.components.ErrorView
import com.multica.app.ui.components.LoadingIndicator
import com.multica.app.ui.components.PriorityChip
import com.multica.app.ui.components.StatusChip
import com.multica.app.util.formatIssueDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueListScreen(
    onIssueClick: (String) -> Unit,
    onCreateIssue: () -> Unit,
    viewModel: IssueViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Issues") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateIssue) {
                Icon(Icons.Default.Add, contentDescription = "Create Issue")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { filter ->
                    selectedFilter = filter
                    viewModel.loadIssues(filter)
                }
            )

            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorView(
                    message = state.error!!,
                    onRetry = { viewModel.loadIssues(selectedFilter) }
                )
                state.issues.isEmpty() -> EmptyView("No issues found")
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.issues, key = { it.id }) { issue ->
                        IssueCard(issue = issue, onClick = { onIssueClick(issue.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit
) {
    val filters = listOf(null to "All", "todo" to "Todo", "in_progress" to "In Progress", "done" to "Done")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (value, label) ->
            FilterChip(
                selected = selectedFilter == value,
                onClick = { onFilterSelected(value) },
                label = { Text(label) }
            )
        }
    }
    HorizontalDivider()
}

@Composable
private fun IssueCard(issue: Issue, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = issue.identifier,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusChip(issue.status)
                issue.priority?.let { PriorityChip(it) }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = issue.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (issue.assignee != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Assignee: ${issue.assignee.name ?: issue.assignee.email ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (issue.updatedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = issue.updatedAt.formatIssueDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
