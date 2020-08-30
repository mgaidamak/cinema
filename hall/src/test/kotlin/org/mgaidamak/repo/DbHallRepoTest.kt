package org.mgaidamak.repo

import org.junit.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.util.Properties

/**
 * Test PostgreSQL implementation of repo
 */
class DbHallRepoTest: IHallRepoTest(DbHallRepo(url, props)) {
    companion object {
        val image = DockerImageName.parse("postgres:12")
        val url = "jdbc:tc:postgresql:12://127.0.0.1:5432/hall"
        val props = Properties().apply {
            put("user", "cinema")
            put("password", "cinemapass")
        }
    }

    @Test
    override fun `create cinema`() {
        PostgreSQLContainer<Nothing>(image).apply {
            withInitScript("schema.sql")
            withUsername("cinema")
            withPassword("cinemapass")
        }.use { postgres ->
            postgres.start()
            super.`create cinema`()
        }
    }
}