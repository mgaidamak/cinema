package org.mgaidamak.cinema.ticket.dao.seat

import org.mgaidamak.cinema.ticket.dao.order.DbOrderRepo
import org.mgaidamak.cinema.ticket.dao.order.DbOrderRepoTest

/**
 * Test PostgreSQL implementation of repo
 * It uses url-based testcontainer management
 */
class DbSeatRepoTest: ISeatRepoTest(DbSeatRepo(DbOrderRepoTest.url),
                                    DbOrderRepo(DbOrderRepoTest.url))