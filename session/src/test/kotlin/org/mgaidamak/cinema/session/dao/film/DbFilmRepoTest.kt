package org.mgaidamak.cinema.session.dao.film

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbFilmRepoTest: IFilmRepoTest(DbFilmRepo(url)) {
    companion object {
        // TC_DAEMON - start container and keep it running till you stop it explicitly or JVM is shutdown
        const val url = "jdbc:tc:postgresql:12://127.0.0.1:5432/session?TC_INITSCRIPT=schema.sql?TC_DAEMON=true"
    }
}