package org.mgaidamak.dao.seat

import org.mgaidamak.dao.cinema.FileCinemaRepo
import org.mgaidamak.dao.hall.FileHallRepo

/**
 * Test in-memory implementation of repo
 */
class FileSeatRepoTest: ISeatRepoTest(FileSeatRepo(), FileHallRepo(), FileCinemaRepo())