package org.mgaidamak.cinema.ticket.dao.seat

import org.mgaidamak.cinema.ticket.dao.bill.DbBillRepo
import org.mgaidamak.cinema.ticket.dao.bill.DbBillRepoTest

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbSeatRepoTest: ISeatRepoTest(DbSeatRepo(DbBillRepoTest.url),
                                    DbBillRepo(DbBillRepoTest.url))