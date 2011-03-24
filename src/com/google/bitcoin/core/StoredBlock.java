/**
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.bitcoin.core;

import java.math.BigInteger;

/**
 * Wraps a {@link Block} object with extra data that can be derived from the block chain but is slow or inconvenient to
 * calculate. By storing it alongside the block header we reduce the amount of work required significantly.
 * Recalculation is slow because the fields are cumulative - to find the chainWork you have to iterate over every
 * block in the chain back to the genesis block, which involves lots of seeking/loading etc. So we just keep a
 * running total: it's a disk space vs cpu/io tradeoff.<p>
 *
 * StoredBlocks are put inside a {@link BlockStore} which saves them to memory or disk.
 */
class StoredBlock {
    /**
     * The block header this object wraps. The referenced block object must not have any transactions in it.
     */
    Block header;

    /**
     * The total sum of work done in this block, and all the blocks below it in the chain. Work is a measure of how
     * many tries are needed to solve a block. If the target is set to cover 10% of the total hash value space,
     * then the work represented by a block is 10.
     */
    BigInteger chainWork;

    /**
     * Position in the chain for this block. The genesis block has a height of zero.
     */
    int height;

    StoredBlock(Block header, BigInteger chainWork, int height) {
        assert header.transactions == null : "Should not have transactions in a block header object";
        this.header = header;
        this.chainWork = chainWork;
        this.height = height;
    }

    /** Returns true if this objects chainWork is higher than the others. */
    boolean moreWorkThan(StoredBlock other) {
        return chainWork.compareTo(other.chainWork) > 0;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof StoredBlock)) return false;
        StoredBlock o = (StoredBlock) other;
        return o.header.equals(header) && o.chainWork.equals(chainWork) && o.height == height;
    }
}