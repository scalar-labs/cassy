// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassandra-backup.proto

package com.scalar.backup.cassandra.rpc;

public interface ClusterListingResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rpc.ClusterListingResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .rpc.ClusterListingResponse.Entry entries = 1;</code>
   */
  java.util.List<com.scalar.backup.cassandra.rpc.ClusterListingResponse.Entry> 
      getEntriesList();
  /**
   * <code>repeated .rpc.ClusterListingResponse.Entry entries = 1;</code>
   */
  com.scalar.backup.cassandra.rpc.ClusterListingResponse.Entry getEntries(int index);
  /**
   * <code>repeated .rpc.ClusterListingResponse.Entry entries = 1;</code>
   */
  int getEntriesCount();
  /**
   * <code>repeated .rpc.ClusterListingResponse.Entry entries = 1;</code>
   */
  java.util.List<? extends com.scalar.backup.cassandra.rpc.ClusterListingResponse.EntryOrBuilder> 
      getEntriesOrBuilderList();
  /**
   * <code>repeated .rpc.ClusterListingResponse.Entry entries = 1;</code>
   */
  com.scalar.backup.cassandra.rpc.ClusterListingResponse.EntryOrBuilder getEntriesOrBuilder(
      int index);
}
