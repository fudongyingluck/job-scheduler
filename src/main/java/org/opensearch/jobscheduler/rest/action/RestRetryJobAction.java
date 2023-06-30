/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.jobscheduler.rest.action;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.node.NodeClient;
import org.opensearch.jobscheduler.JobSchedulerPlugin;
import org.opensearch.jobscheduler.transport.action.RetryJobAction;
import org.opensearch.jobscheduler.transport.request.RetryJobRequest;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.RestToXContentListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.opensearch.rest.RestRequest.Method.POST;

public class RestRetryJobAction extends BaseRestHandler {
    public static final String RETRY_JOB_ACTION = "retry_job_action";
    private static final Logger logger = LogManager.getLogger(RestRetryJobAction.class);

    @Override
    public String getName() {
        return RETRY_JOB_ACTION;
    }

    @Override
    public List<Route> routes() {
        String url = String.format(Locale.ROOT, "%s/%s/{index}", JobSchedulerPlugin.JS_BASE_URI, "_retry_job");
        return ImmutableList.of(new Route(POST, url));
    }

    @VisibleForTesting
    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient client) throws IOException {
        // XContentParser parser = restRequest.contentParser();
        // ensureExpectedToken(XContentParser.Token.START_OBJECT, parser.nextToken(), parser);

        RetryJobRequest retryJobRequest = fromRequest(restRequest);
        logger.info("get retry job reuqest: {}", retryJobRequest);

        return channel -> client.admin().indices().execute(RetryJobAction.INSTANCE, retryJobRequest, new RestToXContentListener<>(channel));
    }

    public static RetryJobRequest fromRequest(final RestRequest request) {
        final String index = request.param("index");
        final String documentId = request.param("document_id");
        final boolean force = request.paramAsBoolean("force", false);
        return new RetryJobRequest(index, documentId, force);
    }
}
