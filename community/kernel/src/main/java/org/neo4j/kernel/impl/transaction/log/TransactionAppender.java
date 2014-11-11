/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.transaction.log;

import java.io.IOException;

import org.neo4j.kernel.impl.transaction.TransactionRepresentation;

/**
 * Writing groups of commands, in a way that is guaranteed to be recoverable, i.e. consistently readable,
 * in the event of failure.
 */
public interface TransactionAppender
{
    /**
     * Appends a transaction to a log, effectively committing it. After this method have returned the
     * returned transaction id should be visible in {@link TransactionIdStore#getLastCommittedTransactionId()}.
     *
     * @param transaction transaction representation to append.
     * @return transaction id the appended transaction got.
     * @throws IOException if there was a problem appending the transaction.
     */
    long append( TransactionRepresentation transaction ) throws IOException;

    /**
     * Appends a transaction to a log with an expected transaction id. The written data is not forced as
     * part of this method call, instead that is controlled manually by {@link #force()}. It's assumed
     * that only a single thread calls this method.
     *
     * @param transaction transaction representation to append.
     * @param transaction id the appended transaction is expected to have.
     * @throws IOException if there was a problem appending the transaction, or if the transaction id
     * generated by this appender doesn't match.
     */
    void append( TransactionRepresentation transaction, long transactionId ) throws IOException;

    /**
     * Forces all changes down to disk. Normally this should not be needing manual calls,
     * {@link #append(TransactionRepresentation)} should do the right thing. But some implementations
     * might choose to force at other points in time, for example for batching.
     *
     * @throws IOException if there was any problem forcing.
     */
    void force() throws IOException;

    /**
     * Closes resources held by this appender.
     */
    void close();
}