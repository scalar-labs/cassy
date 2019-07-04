// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassandra-backup.proto

package com.scalar.backup.cassandra.rpc;

/**
 * Protobuf type {@code rpc.BackupRequest}
 */
public  final class BackupRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:rpc.BackupRequest)
    BackupRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use BackupRequest.newBuilder() to construct.
  private BackupRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private BackupRequest() {
    clusterId_ = "";
    targetIp_ = "";
    snapshotId_ = "";
    backupType_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private BackupRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            clusterId_ = s;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            targetIp_ = s;
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            snapshotId_ = s;
            break;
          }
          case 32: {

            backupType_ = input.readUInt32();
            break;
          }
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.scalar.backup.cassandra.rpc.CassandraBackupProto.internal_static_rpc_BackupRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.scalar.backup.cassandra.rpc.CassandraBackupProto.internal_static_rpc_BackupRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.scalar.backup.cassandra.rpc.BackupRequest.class, com.scalar.backup.cassandra.rpc.BackupRequest.Builder.class);
  }

  public static final int CLUSTER_ID_FIELD_NUMBER = 1;
  private volatile java.lang.Object clusterId_;
  /**
   * <code>string cluster_id = 1;</code>
   */
  public java.lang.String getClusterId() {
    java.lang.Object ref = clusterId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      clusterId_ = s;
      return s;
    }
  }
  /**
   * <code>string cluster_id = 1;</code>
   */
  public com.google.protobuf.ByteString
      getClusterIdBytes() {
    java.lang.Object ref = clusterId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      clusterId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TARGET_IP_FIELD_NUMBER = 2;
  private volatile java.lang.Object targetIp_;
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>string target_ip = 2;</code>
   */
  public java.lang.String getTargetIp() {
    java.lang.Object ref = targetIp_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      targetIp_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>string target_ip = 2;</code>
   */
  public com.google.protobuf.ByteString
      getTargetIpBytes() {
    java.lang.Object ref = targetIp_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      targetIp_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int SNAPSHOT_ID_FIELD_NUMBER = 3;
  private volatile java.lang.Object snapshotId_;
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>string snapshot_id = 3;</code>
   */
  public java.lang.String getSnapshotId() {
    java.lang.Object ref = snapshotId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      snapshotId_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>string snapshot_id = 3;</code>
   */
  public com.google.protobuf.ByteString
      getSnapshotIdBytes() {
    java.lang.Object ref = snapshotId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      snapshotId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int BACKUP_TYPE_FIELD_NUMBER = 4;
  private int backupType_;
  /**
   * <code>uint32 backup_type = 4;</code>
   */
  public int getBackupType() {
    return backupType_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getClusterIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, clusterId_);
    }
    if (!getTargetIpBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, targetIp_);
    }
    if (!getSnapshotIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, snapshotId_);
    }
    if (backupType_ != 0) {
      output.writeUInt32(4, backupType_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getClusterIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, clusterId_);
    }
    if (!getTargetIpBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, targetIp_);
    }
    if (!getSnapshotIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, snapshotId_);
    }
    if (backupType_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(4, backupType_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.scalar.backup.cassandra.rpc.BackupRequest)) {
      return super.equals(obj);
    }
    com.scalar.backup.cassandra.rpc.BackupRequest other = (com.scalar.backup.cassandra.rpc.BackupRequest) obj;

    boolean result = true;
    result = result && getClusterId()
        .equals(other.getClusterId());
    result = result && getTargetIp()
        .equals(other.getTargetIp());
    result = result && getSnapshotId()
        .equals(other.getSnapshotId());
    result = result && (getBackupType()
        == other.getBackupType());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + CLUSTER_ID_FIELD_NUMBER;
    hash = (53 * hash) + getClusterId().hashCode();
    hash = (37 * hash) + TARGET_IP_FIELD_NUMBER;
    hash = (53 * hash) + getTargetIp().hashCode();
    hash = (37 * hash) + SNAPSHOT_ID_FIELD_NUMBER;
    hash = (53 * hash) + getSnapshotId().hashCode();
    hash = (37 * hash) + BACKUP_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getBackupType();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.scalar.backup.cassandra.rpc.BackupRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.scalar.backup.cassandra.rpc.BackupRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code rpc.BackupRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:rpc.BackupRequest)
      com.scalar.backup.cassandra.rpc.BackupRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.scalar.backup.cassandra.rpc.CassandraBackupProto.internal_static_rpc_BackupRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.scalar.backup.cassandra.rpc.CassandraBackupProto.internal_static_rpc_BackupRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.scalar.backup.cassandra.rpc.BackupRequest.class, com.scalar.backup.cassandra.rpc.BackupRequest.Builder.class);
    }

    // Construct using com.scalar.backup.cassandra.rpc.BackupRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      clusterId_ = "";

      targetIp_ = "";

      snapshotId_ = "";

      backupType_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.scalar.backup.cassandra.rpc.CassandraBackupProto.internal_static_rpc_BackupRequest_descriptor;
    }

    @java.lang.Override
    public com.scalar.backup.cassandra.rpc.BackupRequest getDefaultInstanceForType() {
      return com.scalar.backup.cassandra.rpc.BackupRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.scalar.backup.cassandra.rpc.BackupRequest build() {
      com.scalar.backup.cassandra.rpc.BackupRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.scalar.backup.cassandra.rpc.BackupRequest buildPartial() {
      com.scalar.backup.cassandra.rpc.BackupRequest result = new com.scalar.backup.cassandra.rpc.BackupRequest(this);
      result.clusterId_ = clusterId_;
      result.targetIp_ = targetIp_;
      result.snapshotId_ = snapshotId_;
      result.backupType_ = backupType_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return (Builder) super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.scalar.backup.cassandra.rpc.BackupRequest) {
        return mergeFrom((com.scalar.backup.cassandra.rpc.BackupRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.scalar.backup.cassandra.rpc.BackupRequest other) {
      if (other == com.scalar.backup.cassandra.rpc.BackupRequest.getDefaultInstance()) return this;
      if (!other.getClusterId().isEmpty()) {
        clusterId_ = other.clusterId_;
        onChanged();
      }
      if (!other.getTargetIp().isEmpty()) {
        targetIp_ = other.targetIp_;
        onChanged();
      }
      if (!other.getSnapshotId().isEmpty()) {
        snapshotId_ = other.snapshotId_;
        onChanged();
      }
      if (other.getBackupType() != 0) {
        setBackupType(other.getBackupType());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.scalar.backup.cassandra.rpc.BackupRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.scalar.backup.cassandra.rpc.BackupRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object clusterId_ = "";
    /**
     * <code>string cluster_id = 1;</code>
     */
    public java.lang.String getClusterId() {
      java.lang.Object ref = clusterId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        clusterId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string cluster_id = 1;</code>
     */
    public com.google.protobuf.ByteString
        getClusterIdBytes() {
      java.lang.Object ref = clusterId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        clusterId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string cluster_id = 1;</code>
     */
    public Builder setClusterId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      clusterId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string cluster_id = 1;</code>
     */
    public Builder clearClusterId() {
      
      clusterId_ = getDefaultInstance().getClusterId();
      onChanged();
      return this;
    }
    /**
     * <code>string cluster_id = 1;</code>
     */
    public Builder setClusterIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      clusterId_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object targetIp_ = "";
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string target_ip = 2;</code>
     */
    public java.lang.String getTargetIp() {
      java.lang.Object ref = targetIp_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        targetIp_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string target_ip = 2;</code>
     */
    public com.google.protobuf.ByteString
        getTargetIpBytes() {
      java.lang.Object ref = targetIp_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        targetIp_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string target_ip = 2;</code>
     */
    public Builder setTargetIp(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      targetIp_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string target_ip = 2;</code>
     */
    public Builder clearTargetIp() {
      
      targetIp_ = getDefaultInstance().getTargetIp();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string target_ip = 2;</code>
     */
    public Builder setTargetIpBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      targetIp_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object snapshotId_ = "";
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string snapshot_id = 3;</code>
     */
    public java.lang.String getSnapshotId() {
      java.lang.Object ref = snapshotId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        snapshotId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string snapshot_id = 3;</code>
     */
    public com.google.protobuf.ByteString
        getSnapshotIdBytes() {
      java.lang.Object ref = snapshotId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        snapshotId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string snapshot_id = 3;</code>
     */
    public Builder setSnapshotId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      snapshotId_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string snapshot_id = 3;</code>
     */
    public Builder clearSnapshotId() {
      
      snapshotId_ = getDefaultInstance().getSnapshotId();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>string snapshot_id = 3;</code>
     */
    public Builder setSnapshotIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      snapshotId_ = value;
      onChanged();
      return this;
    }

    private int backupType_ ;
    /**
     * <code>uint32 backup_type = 4;</code>
     */
    public int getBackupType() {
      return backupType_;
    }
    /**
     * <code>uint32 backup_type = 4;</code>
     */
    public Builder setBackupType(int value) {
      
      backupType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint32 backup_type = 4;</code>
     */
    public Builder clearBackupType() {
      
      backupType_ = 0;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:rpc.BackupRequest)
  }

  // @@protoc_insertion_point(class_scope:rpc.BackupRequest)
  private static final com.scalar.backup.cassandra.rpc.BackupRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.scalar.backup.cassandra.rpc.BackupRequest();
  }

  public static com.scalar.backup.cassandra.rpc.BackupRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<BackupRequest>
      PARSER = new com.google.protobuf.AbstractParser<BackupRequest>() {
    @java.lang.Override
    public BackupRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new BackupRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<BackupRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<BackupRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.scalar.backup.cassandra.rpc.BackupRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

