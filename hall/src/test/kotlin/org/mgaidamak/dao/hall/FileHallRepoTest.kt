package org.mgaidamak.dao.hall

import org.mgaidamak.dao.cinema.FileCinemaRepo

/**
 * Test in-memory implementation of repo
 */
class FileHallRepoTest: IHallRepoTest(FileHallRepo(), FileCinemaRepo())