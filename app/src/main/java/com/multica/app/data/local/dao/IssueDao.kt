package com.multica.app.data.local.dao

import androidx.room.*
import com.multica.app.data.local.entity.IssueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {
    @Query("SELECT * FROM issues ORDER BY updatedAt DESC")
    fun getAllIssues(): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE status = :status ORDER BY updatedAt DESC")
    fun getIssuesByStatus(status: String): Flow<List<IssueEntity>>

    @Query("SELECT * FROM issues WHERE id = :id")
    suspend fun getIssueById(id: String): IssueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(issues: List<IssueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(issue: IssueEntity)

    @Query("DELETE FROM issues")
    suspend fun deleteAll()
}
