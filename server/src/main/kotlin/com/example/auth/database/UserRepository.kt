package com.example.auth.database

import com.example.auth.model.UserResponse
import org.mindrot.jbcrypt.BCrypt
import java.sql.PreparedStatement
import java.sql.ResultSet

object UserRepository {
    
    fun createUser(email: String, password: String, fullName: String, course: String?, institute: String?): UserResponse {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        
        val sql = """
            INSERT INTO users (email, password_hash, full_name, course, institute)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, email, full_name, course, institute
        """.trimIndent()
        
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email.lowercase())
                stmt.setString(2, passwordHash)
                stmt.setString(3, fullName.trim())
                stmt.setObject(4, course?.trim())
                stmt.setObject(5, institute?.trim())
                
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
        val sql = "SELECT id, email, password_hash, full_name, course, institute FROM users WHERE email = ?"
        
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
    
    private fun mapToUserResponse(rs: ResultSet): UserResponse {
        return UserResponse(
            id = rs.getInt("id"),
            email = rs.getString("email"),
            fullName = rs.getString("full_name"),
            course = rs.getString("course"),
            institute = rs.getString("institute")
        )
    }
}
