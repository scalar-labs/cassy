// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassy.proto

package com.scalar.cassy.rpc;

public interface BackupListingRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rpc.BackupListingRequest)
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
   * <code>string target_ip = 2;</code>
   */
  java.lang.String getTargetIp();
  /**
   * <code>string target_ip = 2;</code>
   */
  com.google.protobuf.ByteString
      getTargetIpBytes();

  /**
   * <code>int32 limit = 3;</code>
   */
  int getLimit();

  /**
   * <code>string snapshot_id = 4;</code>
   */
  java.lang.String getSnapshotId();
  /**
   * <code>string snapshot_id = 4;</code>
   */
  com.google.protobuf.ByteString
      getSnapshotIdBytes();
}
