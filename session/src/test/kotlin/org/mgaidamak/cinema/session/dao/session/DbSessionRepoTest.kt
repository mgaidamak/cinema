package org.mgaidamak.cinema.session.dao.session

import org.mgaidamak.cinema.session.dao.film.DbFilmRepo
import org.mgaidamak.cinema.session.dao.film.DbFilmRepoTest

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbHallRepoTest: ISessionRepoTest(DbSessionRepo(DbFilmRepoTest.url),
    DbFilmRepo(DbFilmRepoTest.url))