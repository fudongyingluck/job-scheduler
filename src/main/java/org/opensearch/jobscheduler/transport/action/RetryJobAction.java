/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.jobscheduler.transport.action;

import org.opensearch.action.ActionType;
import org.opensearch.jobscheduler.transport.response.RetryJobResponse;

public class RetryJobAction extends ActionType<RetryJobResponse> {
    public static final RetryJobAction INSTANCE = new RetryJobAction();
    public static final String NAME = "indices:admin/_plugins/_job_scheduler/retry_job";

    private RetryJobAction() {
        super(NAME, RetryJobResponse::new);
    }
}
