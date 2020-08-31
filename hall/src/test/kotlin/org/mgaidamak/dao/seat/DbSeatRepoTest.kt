package org.mgaidamak.dao.seat

import org.mgaidamak.dao.cinema.DbCinemaRepo
import org.mgaidamak.dao.hall.DbHallRepo

// TC_DAEMON - start container and keep it running till you stop it explicitly or JVM is shutdown
const val url = "jdbc:tc:postgresql:12://127.0.0.1:5432/hall?TC_INITSCRIPT=schema.sql?TC_DAEMON=true"

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbHallRepoTest: ISeatRepoTest(DbSeatRepo(url),
    DbHallRepo(url), DbCinemaRepo(url))