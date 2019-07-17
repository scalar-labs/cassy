// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassandra-backup.proto

package com.scalar.backup.cassandra.rpc;

public interface ClusterRegistrationResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rpc.ClusterRegistrationResponse)
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
   * <code>repeated string target_ips = 2;</code>
   */
  java.util.List<java.lang.String>
      getTargetIpsList();
  /**
   * <code>repeated string target_ips = 2;</code>
   */
  int getTargetIpsCount();
  /**
   * <code>repeated string target_ips = 2;</code>
   */
  java.lang.String getTargetIps(int index);
  /**
   * <code>repeated string target_ips = 2;</code>
   */
  com.google.protobuf.ByteString
      getTargetIpsBytes(int index);

  /**
   * <code>repeated string keyspaces = 3;</code>
   */
  java.util.List<java.lang.String>
      getKeyspacesList();
  /**
   * <code>repeated string keyspaces = 3;</code>
   */
  int getKeyspacesCount();
  /**
   * <code>repeated string keyspaces = 3;</code>
   */
  java.lang.String getKeyspaces(int index);
  /**
   * <code>repeated string keyspaces = 3;</code>
   */
  com.google.protobuf.ByteString
      getKeyspacesBytes(int index);

  /**
   * <code>string data_dir = 4;</code>
   */
  java.lang.String getDataDir();
  /**
   * <code>string data_dir = 4;</code>
   */
  com.google.protobuf.ByteString
      getDataDirBytes();
}
