// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassandra-backup.proto

package com.scalar.backup.cassandra.rpc;

public interface ClusterListingRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rpc.ClusterListingRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string cluster_id = 1;</code>
   */
  java.lang.String getClusterId();
  /**
   * <code>string cluster_id = 1;</code>
   */
  com.google.protobuf.ByteString
      getClusterIdBytes();

  /**
   * <code>int32 limit = 2;</code>
   */
  int getLimit();
}