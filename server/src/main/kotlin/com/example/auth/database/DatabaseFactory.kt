package com.example.auth.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object DatabaseFactory {
    
    private lateinit var dataSource: HikariDataSource
    
    fun init() {
        dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/student_organizer"
            username = "postgres"
            password = "postgres"
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 30000
            connectionTimeout = 10000
        })
        
        createTablesIfNotExist()
    }
    
    private fun createTablesIfNotExist() {
        val createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                full_name VARCHAR(255) NOT NULL,
                course VARCHAR(50),
                institute VARCHAR(255),
                avatar_filename VARCHAR(500),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        
        getConnection().use { conn ->
            conn.createStatement().executeUpdate(createTableSQL)
        }
    }
    
    fun getConnection(): Connection {
        return dataSource.connection
    }
    
    fun close() {
        if (::dataSource.isInitialized) {
            dataSource.close()
        }
    }
}
