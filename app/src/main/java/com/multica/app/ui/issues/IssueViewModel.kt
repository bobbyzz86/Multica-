package com.multica.app.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multica.app.data.model.*
import com.multica.app.data.repository.IssueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssueListUiState(
    val isLoading: Boolean = false,
    val issues: List<Issue> = emptyList(),
    val error: String? = null,
    val selectedStatus: String? = null
)

data class IssueDetailUiState(
    val isLoading: Boolean = false,
    val issue: Issue? = null,
    val comments: List<Comment> = emptyList(),
    val error: String? = null
)

data class CreateIssueUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val priority: String = "medium",
    val error: String? = null,
    val created: Boolean = false
)

@HiltViewModel
class IssueViewModel @Inject constructor(
    private val issueRepository: IssueRepository
) : ViewModel() {
    private val _listState = MutableStateFlow(IssueListUiState())
    val listState: StateFlow<IssueListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(IssueDetailUiState())
    val detailState: StateFlow<IssueDetailUiState> = _detailState.asStateFlow()

    private val _createState = MutableStateFlow(CreateIssueUiState())
    val createState: StateFlow<CreateIssueUiState> = _createState.asStateFlow()

    init {
        loadIssues()
    }

    fun loadIssues(status: String? = null) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null, selectedStatus = status)
            val result = issueRepository.refreshIssues(status = status)
            result.fold(
                onSuccess = { issues ->
                    _listState.value = _listState.value.copy(isLoading = false, issues = issues)
                },
                onFailure = { e ->
                    _listState.value = _listState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun loadIssue(issueId: String) {
        viewModelScope.launch {
            _detailState.value = IssueDetailUiState(isLoading = true)
            val result = issueRepository.getIssue(issueId)
            result.fold(
                onSuccess = { issue ->
                    _detailState.value = _detailState.value.copy(isLoading = false, issue = issue)
                    loadComments(issueId)
                },
                onFailure = { e ->
                    _detailState.value = _detailState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    private suspend fun loadComments(issueId: String) {
        val result = issueRepository.getComments(issueId)
        result.fold(
            onSuccess = { comments ->
                _detailState.value = _detailState.value.copy(comments = comments)
            },
            onFailure = { }
        )
    }

    fun createIssue() {
        val state = _createState.value
        if (state.title.isBlank()) {
            _createState.value = state.copy(error = "Title is required")
            return
        }
        viewModelScope.launch {
            _createState.value = state.copy(isLoading = true, error = null)
            val result = issueRepository.createIssue(
                CreateIssueRequest(
                    title = state.title,
                    description = state.description.ifBlank { null },
                    priority = state.priority
                )
            )
            result.fold(
                onSuccess = {
                    _createState.value = _createState.value.copy(isLoading = false, created = true)
                    loadIssues()
                },
                onFailure = { e ->
                    _createState.value = _createState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun updateCreateTitle(title: String) {
        _createState.value = _createState.value.copy(title = title)
    }

    fun updateCreateDescription(desc: String) {
        _createState.value = _createState.value.copy(description = desc)
    }

    fun updateCreatePriority(priority: String) {
        _createState.value = _createState.value.copy(priority = priority)
    }

    fun resetCreateState() {
        _createState.value = CreateIssueUiState()
    }

    fun updateIssueStatus(issueId: String, status: String) {
        viewModelScope.launch {
            issueRepository.updateIssue(issueId, UpdateIssueRequest(status = status))
            loadIssue(issueId)
        }
    }

    fun addComment(issueId: String, content: String, parentId: String? = null) {
        viewModelScope.launch {
            issueRepository.createComment(issueId, content, parentId)
            loadComments(issueId)
        }
    }
}
