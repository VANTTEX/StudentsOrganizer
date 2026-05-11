package com.example.auth.database

import com.example.auth.model.*
import org.mindrot.jbcrypt.BCrypt
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID

object UserRepository {

    fun createUser(email: String, password: String, fullName: String, course: String?, institute: String?): UserResponse {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        val friendId = UUID.randomUUID().toString()

        val sql = """
            INSERT INTO users (email, password_hash, full_name, course, institute, friend_id)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id, friend_id, email, full_name, course, institute, avatar_filename
        """.trimIndent()

        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email.lowercase())
                stmt.setString(2, passwordHash)
                stmt.setString(3, fullName.trim())
                stmt.setObject(4, course?.trim())
                stmt.setObject(5, institute?.trim())
                stmt.setObject(6, java.util.UUID.fromString(friendId))

                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return mapToUserResponse(rs)
                    }
                    throw RuntimeException("Не удалось создать пользователя")
                }
            }
        }
    }

    fun findByEmail(email: String): UserResponse? {
        val sql = "SELECT id, friend_id, email, password_hash, full_name, course, institute, avatar_filename FROM users WHERE email = ?"

        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email.lowercase())
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return mapToUserResponse(rs)
                    }
                }
            }
        }
        return null
    }

    fun findById(id: Int): UserResponse? {
        val sql = "SELECT id, friend_id, email, full_name, course, institute, avatar_filename FROM users WHERE id = ?"

        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return mapToUserResponse(rs)
                    }
                }
            }
        }
        return null
    }

    fun findByFriendId(friendId: String): UserResponse? {
        val sql = "SELECT id, friend_id, email, full_name, course, institute, avatar_filename FROM users WHERE friend_id = ?"

        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, java.util.UUID.fromString(friendId))
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return mapToUserResponse(rs)
                    }
                }
            }
        }
        return null
    }

    fun verifyPassword(email: String, password: String): UserResponse? {
        val user = findByEmail(email) ?: return null

        val sql = "SELECT password_hash FROM users WHERE id = ?"
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, user.id)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        val hash = rs.getString("password_hash")
                        if (BCrypt.checkpw(password, hash)) {
                            return user
                        }
                    }
                }
            }
        }
        return null
    }

    fun emailExists(email: String): Boolean {
        val sql = "SELECT 1 FROM users WHERE email = ?"
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email.lowercase())
                stmt.executeQuery().use { rs ->
                    return rs.next()
                }
            }
        }
    }

    fun updateAvatar(userId: Int, filename: String): Boolean {
        val sql = "UPDATE users SET avatar_filename = ? WHERE id = ?"
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, filename)
                stmt.setInt(2, userId)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun addFriendship(userId: Int, friendUserId: Int, status: String = "pending"): Boolean {
        val sql = """
            INSERT INTO friendships (user_id, friend_id, status)
            VALUES (?, ?, ?)
            ON CONFLICT (user_id, friend_id) DO NOTHING
        """.trimIndent()
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, userId)
                stmt.setInt(2, friendUserId)
                stmt.setString(3, status)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun getFriendships(userId: Int): List<FriendResponse> {
        val sql = """
            SELECT u.id as user_id, u.friend_id, u.full_name, u.email, u.course, u.institute, u.avatar_filename, f.status
            FROM friendships f
            JOIN users u ON f.friend_id = u.id
            WHERE f.user_id = ?
        """.trimIndent()

        val friends = mutableListOf<FriendResponse>()
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, userId)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        friends.add(FriendResponse(
                            userId = rs.getInt("user_id"),
                            friendId = rs.getString("friend_id"),
                            fullName = rs.getString("full_name"),
                            email = rs.getString("email"),
                            course = rs.getString("course"),
                            institute = rs.getString("institute"),
                            avatarUrl = rs.getString("avatar_filename")?.let { "/api/avatars/$it" },
                            status = rs.getString("status")
                        ))
                    }
                }
            }
        }
        return friends
    }

    fun getAvatarFilename(userId: Int): String? {
        val sql = "SELECT avatar_filename FROM users WHERE id = ?"
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, userId)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return rs.getString("avatar_filename")
                    }
                }
            }
        }
        return null
    }

    private fun mapToUserResponse(rs: ResultSet): UserResponse {
        val avatarFilename = rs.getString("avatar_filename")
        return UserResponse(
            id = rs.getInt("id"),
            friendId = rs.getString("friend_id"),
            email = rs.getString("email"),
            fullName = rs.getString("full_name"),
            course = rs.getString("course"),
            institute = rs.getString("institute"),
            avatarUrl = avatarFilename // Возвращаем только имя файла, клиент построит URL
        )
    }
}
