package com.multica.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.multica.app.data.local.dao.IssueDao
import com.multica.app.data.local.dao.UserDao
import com.multica.app.data.local.entity.IssueEntity
import com.multica.app.data.local.entity.UserEntity

@Database(
    entities = [IssueEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MulticaDatabase : RoomDatabase() {
    abstract fun issueDao(): IssueDao
    abstract fun userDao(): UserDao
}
