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
    internal_static_rpc_StatusUpdateRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_rpc_StatusUpdateRequest_fieldAccessorTable;

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
      "ty.proto\032\037google/protobuf/timestamp.prot" +
      "o\",\n\026ClusterListingResponse\022\022\n\ncluster_i" +
      "d\030\001 \003(\t\"(\n\022NodeListingRequest\022\022\n\ncluster" +
      "_id\030\001 \001(\t\"\363\001\n\023NodeListingResponse\022/\n\007ent" +
      "ries\030\001 \003(\0132\036.rpc.NodeListingResponse.Ent" +
      "ry\032\252\001\n\005Entry\022\n\n\002ip\030\001 \001(\t\0229\n\006status\030\002 \001(\016" +
      "2).rpc.NodeListingResponse.Entry.NodeSta" +
      "tus\"Z\n\nNodeStatus\022\013\n\007UNKNOWN\020\000\022\010\n\004LIVE\020\001" +
      "\022\013\n\007LEAVING\020\002\022\n\n\006MOVING\020\003\022\013\n\007JOINING\020\004\022\017" +
      "\n\013UNREACHABLE\020\005\"H\n\024BackupListingRequest\022" +
      "\022\n\ncluster_id\030\001 \001(\t\022\021\n\ttarget_ip\030\002 \001(\t\022\t" +
      "\n\001n\030\003 \001(\r\"\370\001\n\025BackupListingResponse\0221\n\007e" +
      "ntries\030\001 \003(\0132 .rpc.BackupListingResponse" +
      ".Entry\032\253\001\n\005Entry\022\022\n\ncluster_id\030\001 \001(\t\022\021\n\t" +
      "target_ip\030\002 \001(\t\022\023\n\013backup_type\030\003 \001(\r\022\021\n\t" +
      "backup_id\030\004 \001(\t\022$\n\006status\030\005 \001(\0162\024.rpc.Op" +
      "erationStatus\022-\n\ttimestamp\030\006 \001(\0132\032.googl" +
      "e.protobuf.Timestamp\"^\n\rBackupRequest\022\022\n" +
      "\ncluster_id\030\001 \001(\t\022\023\n\013backup_type\030\002 \001(\r\022\021" +
      "\n\ttarget_ip\030\003 \001(\t\022\021\n\tkeyspaces\030\004 \003(\t\"Z\n\016" +
      "BackupResponse\022$\n\006status\030\001 \001(\0162\024.rpc.Ope" +
      "rationStatus\022\017\n\007message\030\002 \001(\t\022\021\n\tbackup_" +
      "id\030\003 \001(\t\"a\n\016RestoreRequest\022\022\n\ncluster_id" +
      "\030\001 \001(\t\022\021\n\tbackup_id\030\002 \001(\t\022\024\n\014restore_typ" +
      "e\030\003 \001(\r\022\022\n\ntarget_ips\030\004 \003(\t\"H\n\017RestoreRe" +
      "sponse\022$\n\006status\030\001 \001(\0162\024.rpc.OperationSt" +
      "atus\022\017\n\007message\030\002 \001(\t\"u\n\023StatusUpdateReq" +
      "uest\022\022\n\ncluster_id\030\001 \001(\t\022\021\n\ttarget_ip\030\002 " +
      "\001(\t\022\021\n\tbackup_id\030\003 \001(\t\022$\n\006status\030\004 \001(\0162\024" +
      ".rpc.OperationStatus*F\n\017OperationStatus\022" +
      "\013\n\007UNKNOWN\020\000\022\013\n\007STARTED\020\001\022\r\n\tSUCCEEDED\020\002" +
      "\022\n\n\006FAILED\020\0032\234\005\n\017CassandraBackup\022Y\n\014Show" +
      "Clusters\022\026.google.protobuf.Empty\032\033.rpc.C" +
      "lusterListingResponse\"\024\202\323\344\223\002\016\022\014/v1/clust" +
      "ers\022a\n\tListNodes\022\027.rpc.NodeListingReques" +
      "t\032\030.rpc.NodeListingResponse\"!\202\323\344\223\002\033\022\031/v1" +
      "/clusters/{cluster_id}\022o\n\013ListBackups\022\031." +
      "rpc.BackupListingRequest\032\032.rpc.BackupLis" +
      "tingResponse\")\202\323\344\223\002#\022!/v1/clusters/{clus" +
      "ter_id}/backups\022c\n\nTakeBackup\022\022.rpc.Back" +
      "upRequest\032\023.rpc.BackupResponse\",\202\323\344\223\002&\"!" +
      "/v1/clusters/{cluster_id}/backups:\001*\022q\n\r" +
      "RestoreBackup\022\023.rpc.RestoreRequest\032\024.rpc" +
      ".RestoreResponse\"5\202\323\344\223\002/\032*/v1/clusters/{" +
      "cluster_id}/data/{backup_id}:\001*\022\201\001\n\014Upda" +
      "teStatus\022\030.rpc.StatusUpdateRequest\032\026.goo" +
      "gle.protobuf.Empty\"?\202\323\344\223\0029\0324/v1/clusters" +
      "/{cluster_id}/backups/{backup_id}/status" +
      ":\001*B9\n\037com.scalar.backup.cassandra.rpcB\024" +
      "CassandraBackupProtoP\001b\006proto3"
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
          com.google.protobuf.TimestampProto.getDescriptor(),
        }, assigner);
    internal_static_rpc_ClusterListingResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_rpc_ClusterListingResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_ClusterListingResponse_descriptor,
        new java.lang.String[] { "ClusterId", });
    internal_static_rpc_NodeListingRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_rpc_NodeListingRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_NodeListingRequest_descriptor,
        new java.lang.String[] { "ClusterId", });
    internal_static_rpc_NodeListingResponse_descriptor =
      getDescriptor().getMessageTypes().get(2);
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
      getDescriptor().getMessageTypes().get(3);
    internal_static_rpc_BackupListingRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupListingRequest_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIp", "N", });
    internal_static_rpc_BackupListingResponse_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_rpc_BackupListingResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupListingResponse_descriptor,
        new java.lang.String[] { "Entries", });
    internal_static_rpc_BackupListingResponse_Entry_descriptor =
      internal_static_rpc_BackupListingResponse_descriptor.getNestedTypes().get(0);
    internal_static_rpc_BackupListingResponse_Entry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupListingResponse_Entry_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIp", "BackupType", "BackupId", "Status", "Timestamp", });
    internal_static_rpc_BackupRequest_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_rpc_BackupRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupRequest_descriptor,
        new java.lang.String[] { "ClusterId", "BackupType", "TargetIp", "Keyspaces", });
    internal_static_rpc_BackupResponse_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_rpc_BackupResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_BackupResponse_descriptor,
        new java.lang.String[] { "Status", "Message", "BackupId", });
    internal_static_rpc_RestoreRequest_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_rpc_RestoreRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_RestoreRequest_descriptor,
        new java.lang.String[] { "ClusterId", "BackupId", "RestoreType", "TargetIps", });
    internal_static_rpc_RestoreResponse_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_rpc_RestoreResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_RestoreResponse_descriptor,
        new java.lang.String[] { "Status", "Message", });
    internal_static_rpc_StatusUpdateRequest_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_rpc_StatusUpdateRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_rpc_StatusUpdateRequest_descriptor,
        new java.lang.String[] { "ClusterId", "TargetIp", "BackupId", "Status", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.AnnotationsProto.http);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.protobuf.EmptyProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
