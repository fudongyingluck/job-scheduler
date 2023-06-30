/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.jobscheduler.transport.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.support.DefaultShardOperationFailedException;
import org.opensearch.action.support.broadcast.BroadcastResponse;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.ConstructingObjectParser;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RetryJobResponse extends BroadcastResponse {
    private final boolean retryStatus; // if actually retry job schedule
    private final String message; // some info about this retry

    private static final Logger logger = LogManager.getLogger(RetryJobResponse.class);

    private static final ConstructingObjectParser<RetryJobResponse, Void> PARSER = new ConstructingObjectParser<>(
        "retry_job",
        true,
        arg -> {
            RetryJobResponse response = (RetryJobResponse) arg[0];
            logger.info("parser response: {}", response);
            return new RetryJobResponse(
                response.getTotalShards(),
                response.getSuccessfulShards(),
                response.getFailedShards(),
                Arrays.asList(response.getShardFailures()),
                response.retryStatus,
                response.message
            );
        }
    );

    static {
        declareBroadcastFields(PARSER);
    }

    public RetryJobResponse(boolean retryStatus, String message) {
        this.retryStatus = retryStatus;
        this.message = message;
    }

    public RetryJobResponse(StreamInput in) throws IOException {
        super(in);
        retryStatus = in.readBoolean();
        message = in.readString();
    }

    public RetryJobResponse(
        int totalShards,
        int successfulShards,
        int failedShards,
        List<DefaultShardOperationFailedException> shardFailures,
        boolean retryStatus,
        String message
    ) {
        super(totalShards, successfulShards, failedShards, shardFailures);
        this.retryStatus = retryStatus;
        this.message = message;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeBoolean(this.retryStatus);
        out.writeString(message);
    }

    public static RetryJobResponse fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null);
    }
}
