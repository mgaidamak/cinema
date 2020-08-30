package org.mgaidamak.repo

import kotlin.test.BeforeTest

/**
 * Test in-memory implementation of repo
 */
class FileHallRepoTest: IHallRepoTest(FileHallRepo()) {
    @BeforeTest
    fun `clean up`() {
        repo.clear()
    }
}