/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.jobscheduler.transport.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.support.ActionFilters;
import org.opensearch.action.support.DefaultShardOperationFailedException;
import org.opensearch.action.support.broadcast.node.TransportBroadcastByNodeAction;
import org.opensearch.client.node.NodeClient;
import org.opensearch.cluster.ClusterState;
import org.opensearch.cluster.block.ClusterBlockException;
import org.opensearch.cluster.block.ClusterBlockLevel;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.routing.ShardRouting;
import org.opensearch.cluster.routing.ShardsIterator;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.inject.Inject;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.jobscheduler.transport.request.RetryJobRequest;
import org.opensearch.jobscheduler.transport.response.RetryJobResponse;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.TransportService;

import java.io.IOException;
import java.util.List;

public class TransportRetryJobAction extends TransportBroadcastByNodeAction<RetryJobRequest, RetryJobResponse, RetryJobResponse> {
    public final NodeClient client;
    private static final Logger logger = LogManager.getLogger(TransportRetryJobAction.class);

    @Inject
    public TransportRetryJobAction(
        ClusterService clusterService,
        TransportService transportService,
        NodeClient client,
        ActionFilters actionFilters,
        IndexNameExpressionResolver indexNameExpressionResolver
    ) {
        super(
            RetryJobAction.NAME,
            clusterService,
            transportService,
            actionFilters,
            indexNameExpressionResolver,
            RetryJobRequest::new,
            ThreadPool.Names.MANAGEMENT,
            false
        );
        this.client = client;
    }

    @Override
    protected RetryJobResponse newResponse(
        RetryJobRequest request,
        int totalShards,
        int successfulShards,
        int failedShards,
        List<RetryJobResponse> responses,
        List<DefaultShardOperationFailedException> shardFailures,
        ClusterState clusterState
    ) {
        logger.info("here in newResponse");
        return new RetryJobResponse(totalShards, successfulShards, failedShards, shardFailures, false, "missing message");
    }

    @Override
    protected RetryJobRequest readRequestFrom(StreamInput in) throws IOException {
        logger.info("here in readRequestFrom");
        return new RetryJobRequest(in);
    }

    @Override
    protected ShardsIterator shards(ClusterState clusterState, RetryJobRequest request, String[] concreteIndices) {
        logger.info("here in shards");
        return clusterState.routingTable().allShards(concreteIndices);
    }

    @Override
    protected RetryJobResponse shardOperation(RetryJobRequest request, ShardRouting shardRouting) {
        logger.info("here in shardOperation");
        return new RetryJobResponse(1, 1, 0, null, false, "missing message");
    }

    @Override
    protected RetryJobResponse readShardResult(StreamInput in) throws IOException {
        logger.info("here in readShardResult");
        return new RetryJobResponse(1, 1, 0, null, false, "missing message");
    }

    @Override
    protected ClusterBlockException checkGlobalBlock(ClusterState state, RetryJobRequest request) {
        logger.info("here in checkGlobalBlock");
        return state.blocks().globalBlockedException(ClusterBlockLevel.METADATA_WRITE);
    }

    @Override
    protected ClusterBlockException checkRequestBlock(ClusterState state, RetryJobRequest request, String[] concreteIndices) {
        logger.info("here in checkRequestBlock");
        return state.blocks().indicesBlockedException(ClusterBlockLevel.METADATA_WRITE, concreteIndices);
    }

    // @Override
    // public void doExecute(Task task, RetryJobRequest request, ActionListener<RetryJobResponse> listener) {
    // logger.info("do execute with request 1 : {}", request);
    // // 1. search local to find job
    // // 2. if owning job, try to retry it
    // RetryJobResponse retryJobResponse = new RetryJobResponse(1, 1, 0, null, false, "job already running");
    // listener.onResponse(retryJobResponse);
    // }
}
