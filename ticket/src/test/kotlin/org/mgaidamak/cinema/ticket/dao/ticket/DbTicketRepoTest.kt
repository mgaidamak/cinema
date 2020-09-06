package org.mgaidamak.cinema.ticket.dao.ticket

import org.mgaidamak.cinema.ticket.dao.bill.DbBillRepo
import org.mgaidamak.cinema.ticket.dao.bill.DbBillRepoTest

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbTicketRepoTest: ITicketRepoTest(DbTicketRepo(DbBillRepoTest.url),
                                    DbBillRepo(DbBillRepoTest.url))