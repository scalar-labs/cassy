// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassandra-backup.proto

package com.scalar.backup.cassandra.rpc;

public final class CassandraBackupProto {
  private CassandraBackupProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_ClusterRegistrationRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_ClusterRegistrationRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_ClusterRegistrationResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_ClusterRegistrationResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_ClusterListingResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_ClusterListingResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_NodeListingRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_NodeListingRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_NodeListingResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_NodeListingResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_NodeListingResponse_Entry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_NodeListingResponse_Entry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_BackupListingRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_BackupListingRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_BackupListingResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_BackupListingResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_BackupListingResponse_Entry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_BackupListingResponse_Entry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_BackupRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_BackupRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_BackupResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_BackupResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_RestoreRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_RestoreRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_RestoreResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_RestoreResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_RestoreStatusListingRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_RestoreStatusListingRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_RestoreStatusListingResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_RestoreStatusListingResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_rpc_RestoreStatusListingResponse_Entry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_RestoreStatusListingResponse_Entry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026cassandra-backup.proto\022\003rpc\032\034google/ap" +
      "i/annotations.proto\032\033google/protobuf/emp" +
      "ty.proto\"F\n\032ClusterRegistrationRequest\022\026" +
      "\n\016cassandra_host\030\001 \001(\t\022\020\n\010jmx_port\030\002 \001(\r" +
      "\"j\n\033ClusterRegistrationResponse\022\022\n\nclust" +
      "er_id\030\001 \001(\t\022\022\n\ntarget_ips\030\002 \003(\t\022\021\n\tkeysp" +
      "aces\030\003 \003(\t\022\020\n\010data_dir\030\004 \001(\t\",\n\026ClusterL" +
      "istingResponse\022\022\n\ncluster_id\030\001 \003(\t\"(\n\022No" +
      "deListingRequest\022\022\n\ncluster_id\030\001 \001(\t\"\363\001\n" +
      "\023NodeListingResponse\022/\n\007entries\030\001 \003(\0132\036." +
      "rpc.NodeListingResponse.Entry\032\252\001\n\005Entry\022" +
      "\n\n\002ip\030\001 \001(\t\0229\n\006status\030\002 \001(\0162).rpc.NodeLi" +
      "stingResponse.Entry.NodeStatus\"Z\n\nNodeSt" +
      "atus\022\013\n\007UNKNOWN\020\000\022\010\n\004LIVE\020\001\022\013\n\007LEAVING\020\002" +
      "\022\n\n\006MOVING\020\003\022\013\n\007JOINING\020\004\022\017\n\013UNREACHABLE" +
      "\020\005\"]\n\024BackupListingRequest\022\022\n\ncluster_id" +
      "\030\001 \001(\t\022\021\n\ttarget_ip\030\002 \001(\t\022\t\n\001n\030\003 \001(\005\022\023\n\013" +
      "snapshot_id\030\004 \001(\t\"\363\001\n\025BackupListingRespo" +
      "nse\0221\n\007entries\030\001 \003(\0132 .rpc.BackupListing" +
      "Response.Entry\032\246\001\n\005Entry\022\022\n\ncluster_id\030\001" +
      " \001(\t\022\021\n\ttarget_ip\030\002 \001(\t\022\023\n\013snapshot_id\030\003" +
      " \001(\t\022\022\n\ncreated_at\030\004 \001(\004\022\022\n\nupdated_at\030\005" +
      " \001(\004\022\023\n\013backup_type\030\006 \001(\r\022$\n\006status\030\007 \001(" +
      "\0162\024.rpc.OperationStatus\"a\n\rBackupRequest" +
      "\022\022\n\ncluster_id\030\001 \001(\t\022\022\n\ntarget_ips\030\002 \003(\t" +
      "\022\023\n\013snapshot_id\030\003 \001(\t\022\023\n\013backup_type\030\004 \001" +
      "(\r\"\234\001\n\016BackupResponse\022$\n\006status\030\001 \001(\0162\024." +
      "rpc.OperationStatus\022\022\n\ncluster_id\030\002 \001(\t\022" +
      "\022\n\ntarget_ips\030\003 \003(\t\022\023\n\013snapshot_id\030\004 \001(\t" +
      "\022\022\n\ncreated_at\030\005 \001(\004\022\023\n\013backup_type\030\006 \001(" +
      "\r\"z\n\016RestoreRequest\022\022\n\ncluster_id\030\001 \001(\t\022" +
      "\022\n\ntarget_ips\030\002 \003(\t\022\023\n\013snapshot_id\030\003 \001(\t" +
      "\022\024\n\014restore_type\030\004 \001(\r\022\025\n\rsnapshot_only\030" +
      "\005 \001(\010\"\241\001\n\017RestoreResponse\022$\n\006status\030\001 \001(" +
      "\0162\024.rpc.OperationStatus\022\022\n\ncluster_id\030\002 " +
      "\001(\t\022\022\n\ntarget_ips\030\003 \003(\t\022\023\n\013snapshot_id\030\004" +
      " \001(\t\022\024\n\014restore_type\030\005 \001(\r\022\025\n\rsnapshot_o" +
      "nly\030\006 \001(\010\"d\n\033RestoreStatusListingRequest" +
      "\022\022\n\ncluster_id\030\001 \001(\t\022\021\n\ttarget_ip\030\002 \001(\t\022" +
      "\023\n\013snapshot_id\030\003 \001(\t\022\t\n\001n\030\004 \001(\005\"\202\002\n\034Rest" +
      "oreStatusListingResponse\022\022\n\ncluster_id\030\001" +
      " \001(\t\0228\n\007entries\030\003 \003(\0132\'.rpc.RestoreStatu" +
      "sListingResponse.Entry\032\223\001\n\005Entry\022\021\n\ttarg" +
      "et_ip\030\001 \001(\t\022\023\n\013snapshot_id\030\002 \001(\t\022\022\n\ncrea" +
      "ted_at\030\003 \001(\004\022\022\n\nupdated_at\030\004 \001(\004\022\024\n\014rest" +
      "ore_type\030\005 \001(\r\022$\n\006status\030\006 \001(\0162\024.rpc.Ope" +
      "rationStatus*W\n\017OperationStatus\022\013\n\007UNKNO" +
      "WN\020\000\022\017\n\013INITIALIZED\020\001\022\013\n\007STARTED\020\002\022\r\n\tCO" +
      "MPLETED\020\003\022\n\n\006FAILED\020\0042\234\006\n\017CassandraBacku" +
      "p\022m\n\017registerCluster\022\037.rpc.ClusterRegist" +
      "rationRequest\032 .rpc.ClusterRegistrationR" +
      "esponse\"\027\202\323\344\223\002\021\"\014/v1/clusters:\001*\022Y\n\014Show" +
      "Clusters\022\026.google.protobuf.Empty\032\033.rpc.C" +
      "lusterListingResponse\"\024\202\323\344\223\002\016\022\014/v1/clust" +
      "ers\022a\n\tListNodes\022\027.rpc.NodeListingReques" +
      "t\032\030.rpc.NodeListingResponse\"!\202\323\344\223\002\033\022\031/v1" +
      "/clusters/{cluster_id}\022o\n\013ListBackups\022\031." +
      "rpc.BackupListingRequest\032\032.rpc.BackupLis" +
      "tingResponse\")\202\323\344\223\002#\022!/v1/clusters/{clus" +
      "ter_id}/backups\022c\n\nTakeBackup\022\022.rpc.Back" +
      "upRequest\032\023.rpc.BackupResponse\",\202\323\344\223\002&\"!" +
      "/v1/clusters/{cluster_id}/backups:\001*\022s\n\r" +
      "RestoreBackup\022\023.rpc.RestoreRequest\032\024.rpc" +
      ".RestoreResponse\"7\202\323\344\223\0021\032,/v1/clusters/{" +
      "cluster_id}/data/{snapshot_id}:\001*\022\220\001\n\023Li" +
      "stRestoreStatuses\022 .rpc.RestoreStatusLis" +
      "tingRequest\032!.rpc.RestoreStatusListingRe" +
      "sponse\"4\202\323\344\223\002.\022,/v1/clusters/{cluster_id" +
      "}/data/{snapshot_id}B9\n\037com.scalar.backu" +
      "p.cassandra.rpcB\024CassandraBackupProtoP\001b" +
      "\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.api.AnnotationsProto.getDescriptor(),
          com.google.protobuf.EmptyProto.getDescriptor(),
        }, assigner);
    internal_static_rpc_ClusterRegistrationRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_rpc_ClusterRegistrationRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_ClusterRegistrationRequest_descriptor,
        new java.lang.String[] { "CassandraHost", "JmxPort", });
    internal_static_rpc_ClusterRegistrationResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_rpc_ClusterRegistrationResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_ClusterRegistrationResponse_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIps", "Keyspaces", "DataDir", });
    internal_static_rpc_ClusterListingResponse_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_rpc_ClusterListingResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_ClusterListingResponse_descriptor,
        new java.lang.String[] { "ClusterId", });
    internal_static_rpc_NodeListingRequest_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_rpc_NodeListingRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_NodeListingRequest_descriptor,
        new java.lang.String[] { "ClusterId", });
    internal_static_rpc_NodeListingResponse_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_rpc_NodeListingResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_NodeListingResponse_descriptor,
        new java.lang.String[] { "Entries", });
    internal_static_rpc_NodeListingResponse_Entry_descriptor =
      internal_static_rpc_NodeListingResponse_descriptor.getNestedTypes().get(0);
    internal_static_rpc_NodeListingResponse_Entry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_NodeListingResponse_Entry_descriptor,
        new java.lang.String[] { "Ip", "Status", });
    internal_static_rpc_BackupListingRequest_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_rpc_BackupListingRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupListingRequest_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIp", "N", "SnapshotId", });
    internal_static_rpc_BackupListingResponse_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_rpc_BackupListingResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupListingResponse_descriptor,
        new java.lang.String[] { "Entries", });
    internal_static_rpc_BackupListingResponse_Entry_descriptor =
      internal_static_rpc_BackupListingResponse_descriptor.getNestedTypes().get(0);
    internal_static_rpc_BackupListingResponse_Entry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupListingResponse_Entry_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIp", "SnapshotId", "CreatedAt", "UpdatedAt", "BackupType", "Status", });
    internal_static_rpc_BackupRequest_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_rpc_BackupRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupRequest_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIps", "SnapshotId", "BackupType", });
    internal_static_rpc_BackupResponse_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_rpc_BackupResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupResponse_descriptor,
        new java.lang.String[] { "Status", "ClusterId", "TargetIps", "SnapshotId", "CreatedAt", "BackupType", });
    internal_static_rpc_RestoreRequest_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_rpc_RestoreRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_RestoreRequest_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIps", "SnapshotId", "RestoreType", "SnapshotOnly", });
    internal_static_rpc_RestoreResponse_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_rpc_RestoreResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_RestoreResponse_descriptor,
        new java.lang.String[] { "Status", "ClusterId", "TargetIps", "SnapshotId", "RestoreType", "SnapshotOnly", });
    internal_static_rpc_RestoreStatusListingRequest_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_rpc_RestoreStatusListingRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_RestoreStatusListingRequest_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIp", "SnapshotId", "N", });
    internal_static_rpc_RestoreStatusListingResponse_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_rpc_RestoreStatusListingResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_RestoreStatusListingResponse_descriptor,
        new java.lang.String[] { "ClusterId", "Entries", });
    internal_static_rpc_RestoreStatusListingResponse_Entry_descriptor =
      internal_static_rpc_RestoreStatusListingResponse_descriptor.getNestedTypes().get(0);
    internal_static_rpc_RestoreStatusListingResponse_Entry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_RestoreStatusListingResponse_Entry_descriptor,
        new java.lang.String[] { "TargetIp", "SnapshotId", "CreatedAt", "UpdatedAt", "RestoreType", "Status", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.protobuf.EmptyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
