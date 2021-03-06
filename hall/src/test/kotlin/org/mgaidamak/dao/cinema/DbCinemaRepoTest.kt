package org.mgaidamak.dao.cinema

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbCinemaRepoTest: ICinemaRepoTest(DbCinemaRepo(url)) {
    companion object {
        // TC_DAEMON - start container and keep it running till you stop it explicitly or JVM is shutdown
        const val url = "jdbc:tc:postgresql:12://127.0.0.1:5432/hall?TC_INITSCRIPT=schema.sql?TC_DAEMON=true"
    }
}