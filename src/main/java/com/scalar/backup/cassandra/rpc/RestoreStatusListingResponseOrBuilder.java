// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassy.proto

package com.scalar.backup.cassandra.rpc;

public interface RestoreStatusListingResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rpc.RestoreStatusListingResponse)
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
   * <code>repeated .rpc.RestoreStatusListingResponse.Entry entries = 3;</code>
   */
  java.util.List<com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse.Entry> 
      getEntriesList();
  /**
   * <code>repeated .rpc.RestoreStatusListingResponse.Entry entries = 3;</code>
   */
  com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse.Entry getEntries(int index);
  /**
   * <code>repeated .rpc.RestoreStatusListingResponse.Entry entries = 3;</code>
   */
  int getEntriesCount();
  /**
   * <code>repeated .rpc.RestoreStatusListingResponse.Entry entries = 3;</code>
   */
  java.util.List<? extends com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse.EntryOrBuilder> 
      getEntriesOrBuilderList();
  /**
   * <code>repeated .rpc.RestoreStatusListingResponse.Entry entries = 3;</code>
   */
  com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse.EntryOrBuilder getEntriesOrBuilder(
      int index);
}
