package org.mgaidamak.dao.seat

import org.mgaidamak.dao.cinema.DbCinemaRepo
import org.mgaidamak.dao.cinema.DbCinemaRepoTest
import org.mgaidamak.dao.hall.DbHallRepo

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbHallRepoTest: ISeatRepoTest(DbSeatRepo(DbCinemaRepoTest.url),
    DbHallRepo(DbCinemaRepoTest.url), DbCinemaRepo(DbCinemaRepoTest.url))