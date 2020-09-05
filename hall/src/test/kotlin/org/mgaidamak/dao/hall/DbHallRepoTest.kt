package org.mgaidamak.dao.hall

import org.mgaidamak.dao.cinema.DbCinemaRepo
import org.mgaidamak.dao.cinema.DbCinemaRepoTest

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbHallRepoTest: IHallRepoTest(DbHallRepo(DbCinemaRepoTest.url),
    DbCinemaRepo(DbCinemaRepoTest.url))