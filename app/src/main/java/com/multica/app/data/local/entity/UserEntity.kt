package com.multica.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.multica.app.data.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String?,
    val name: String?,
    val avatarUrl: String?
) {
    fun toUser() = User(id, email, name, avatarUrl)

    companion object {
        fun fromUser(user: User) = UserEntity(
            id = user.id,
            email = user.email,
            name = user.name,
            avatarUrl = user.avatarUrl
        )
    }
}
