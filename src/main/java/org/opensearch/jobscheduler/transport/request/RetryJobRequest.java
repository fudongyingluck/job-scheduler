/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.jobscheduler.transport.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.ActionRequestValidationException;
import org.opensearch.action.support.broadcast.BroadcastRequest;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;

import java.io.IOException;

public class RetryJobRequest extends BroadcastRequest<RetryJobRequest> {
    private static String documentId;
    private static boolean force = false;
    private static final Logger logger = LogManager.getLogger(RetryJobRequest.class);

    public RetryJobRequest(StreamInput in) throws IOException {
        super(in);
        logger.info("after super read");
        documentId = in.readString();
        logger.info("after documentId read");
        force = in.readBoolean();
    }

    @Override
    public String toString() {
        return "jobIndex: " + this.indices.toString() + " documentId: " + this.documentId + " force: " + this.force;
    }

    public RetryJobRequest(String jobIndex, String documentId, boolean force) {
        super(jobIndex);
        this.documentId = documentId;
        this.force = force;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(documentId);
        out.writeBoolean(force);
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean getForce() {
        return this.force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}
