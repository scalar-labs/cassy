// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassandra-backup.proto

package com.scalar.cassy.rpc;

/**
 * Protobuf type {@code rpc.RestoreRequest}
 */
public  final class RestoreRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:rpc.RestoreRequest)
    RestoreRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use RestoreRequest.newBuilder() to construct.
  private RestoreRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private RestoreRequest() {
    clusterId_ = "";
    targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    snapshotId_ = "";
    restoreType_ = 0;
    snapshotOnly_ = false;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private RestoreRequest(
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
            if (!((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
              targetIps_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000002;
            }
            targetIps_.add(s);
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            snapshotId_ = s;
            break;
          }
          case 32: {

            restoreType_ = input.readUInt32();
            break;
          }
          case 40: {

            snapshotOnly_ = input.readBool();
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
      if (((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
        targetIps_ = targetIps_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return CassandraBackupProto.internal_static_rpc_RestoreRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return CassandraBackupProto.internal_static_rpc_RestoreRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            RestoreRequest.class, RestoreRequest.Builder.class);
  }

  private int bitField0_;
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

  public static final int TARGET_IPS_FIELD_NUMBER = 2;
  private com.google.protobuf.LazyStringList targetIps_;
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>repeated string target_ips = 2;</code>
   */
  public com.google.protobuf.ProtocolStringList
      getTargetIpsList() {
    return targetIps_;
  }
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>repeated string target_ips = 2;</code>
   */
  public int getTargetIpsCount() {
    return targetIps_.size();
  }
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>repeated string target_ips = 2;</code>
   */
  public java.lang.String getTargetIps(int index) {
    return targetIps_.get(index);
  }
  /**
   * <pre>
   * optional
   * </pre>
   *
   * <code>repeated string target_ips = 2;</code>
   */
  public com.google.protobuf.ByteString
      getTargetIpsBytes(int index) {
    return targetIps_.getByteString(index);
  }

  public static final int SNAPSHOT_ID_FIELD_NUMBER = 3;
  private volatile java.lang.Object snapshotId_;
  /**
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

  public static final int RESTORE_TYPE_FIELD_NUMBER = 4;
  private int restoreType_;
  /**
   * <code>uint32 restore_type = 4;</code>
   */
  public int getRestoreType() {
    return restoreType_;
  }

  public static final int SNAPSHOT_ONLY_FIELD_NUMBER = 5;
  private boolean snapshotOnly_;
  /**
   * <pre>
   * optional (default: false)
   * </pre>
   *
   * <code>bool snapshot_only = 5;</code>
   */
  public boolean getSnapshotOnly() {
    return snapshotOnly_;
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
    for (int i = 0; i < targetIps_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, targetIps_.getRaw(i));
    }
    if (!getSnapshotIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, snapshotId_);
    }
    if (restoreType_ != 0) {
      output.writeUInt32(4, restoreType_);
    }
    if (snapshotOnly_ != false) {
      output.writeBool(5, snapshotOnly_);
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
    {
      int dataSize = 0;
      for (int i = 0; i < targetIps_.size(); i++) {
        dataSize += computeStringSizeNoTag(targetIps_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getTargetIpsList().size();
    }
    if (!getSnapshotIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, snapshotId_);
    }
    if (restoreType_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(4, restoreType_);
    }
    if (snapshotOnly_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(5, snapshotOnly_);
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
    if (!(obj instanceof RestoreRequest)) {
      return super.equals(obj);
    }
    RestoreRequest other = (RestoreRequest) obj;

    boolean result = true;
    result = result && getClusterId()
        .equals(other.getClusterId());
    result = result && getTargetIpsList()
        .equals(other.getTargetIpsList());
    result = result && getSnapshotId()
        .equals(other.getSnapshotId());
    result = result && (getRestoreType()
        == other.getRestoreType());
    result = result && (getSnapshotOnly()
        == other.getSnapshotOnly());
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
    if (getTargetIpsCount() > 0) {
      hash = (37 * hash) + TARGET_IPS_FIELD_NUMBER;
      hash = (53 * hash) + getTargetIpsList().hashCode();
    }
    hash = (37 * hash) + SNAPSHOT_ID_FIELD_NUMBER;
    hash = (53 * hash) + getSnapshotId().hashCode();
    hash = (37 * hash) + RESTORE_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getRestoreType();
    hash = (37 * hash) + SNAPSHOT_ONLY_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getSnapshotOnly());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static RestoreRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static RestoreRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static RestoreRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static RestoreRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static RestoreRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static RestoreRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static RestoreRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static RestoreRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static RestoreRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static RestoreRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static RestoreRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static RestoreRequest parseFrom(
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
  public static Builder newBuilder(RestoreRequest prototype) {
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
   * Protobuf type {@code rpc.RestoreRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:rpc.RestoreRequest)
      RestoreRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CassandraBackupProto.internal_static_rpc_RestoreRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CassandraBackupProto.internal_static_rpc_RestoreRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              RestoreRequest.class, RestoreRequest.Builder.class);
    }

    // Construct using RestoreRequest.newBuilder()
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

      targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000002);
      snapshotId_ = "";

      restoreType_ = 0;

      snapshotOnly_ = false;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return CassandraBackupProto.internal_static_rpc_RestoreRequest_descriptor;
    }

    @java.lang.Override
    public RestoreRequest getDefaultInstanceForType() {
      return RestoreRequest.getDefaultInstance();
    }

    @java.lang.Override
    public RestoreRequest build() {
      RestoreRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public RestoreRequest buildPartial() {
      RestoreRequest result = new RestoreRequest(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.clusterId_ = clusterId_;
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        targetIps_ = targetIps_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000002);
      }
      result.targetIps_ = targetIps_;
      result.snapshotId_ = snapshotId_;
      result.restoreType_ = restoreType_;
      result.snapshotOnly_ = snapshotOnly_;
      result.bitField0_ = to_bitField0_;
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
      if (other instanceof RestoreRequest) {
        return mergeFrom((RestoreRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(RestoreRequest other) {
      if (other == RestoreRequest.getDefaultInstance()) return this;
      if (!other.getClusterId().isEmpty()) {
        clusterId_ = other.clusterId_;
        onChanged();
      }
      if (!other.targetIps_.isEmpty()) {
        if (targetIps_.isEmpty()) {
          targetIps_ = other.targetIps_;
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          ensureTargetIpsIsMutable();
          targetIps_.addAll(other.targetIps_);
        }
        onChanged();
      }
      if (!other.getSnapshotId().isEmpty()) {
        snapshotId_ = other.snapshotId_;
        onChanged();
      }
      if (other.getRestoreType() != 0) {
        setRestoreType(other.getRestoreType());
      }
      if (other.getSnapshotOnly() != false) {
        setSnapshotOnly(other.getSnapshotOnly());
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
      RestoreRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (RestoreRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

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

    private com.google.protobuf.LazyStringList targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureTargetIpsIsMutable() {
      if (!((bitField0_ & 0x00000002) == 0x00000002)) {
        targetIps_ = new com.google.protobuf.LazyStringArrayList(targetIps_);
        bitField0_ |= 0x00000002;
       }
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getTargetIpsList() {
      return targetIps_.getUnmodifiableView();
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public int getTargetIpsCount() {
      return targetIps_.size();
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public java.lang.String getTargetIps(int index) {
      return targetIps_.get(index);
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public com.google.protobuf.ByteString
        getTargetIpsBytes(int index) {
      return targetIps_.getByteString(index);
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public Builder setTargetIps(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureTargetIpsIsMutable();
      targetIps_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public Builder addTargetIps(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureTargetIpsIsMutable();
      targetIps_.add(value);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public Builder addAllTargetIps(
        java.lang.Iterable<java.lang.String> values) {
      ensureTargetIpsIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, targetIps_);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public Builder clearTargetIps() {
      targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional
     * </pre>
     *
     * <code>repeated string target_ips = 2;</code>
     */
    public Builder addTargetIpsBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      ensureTargetIpsIsMutable();
      targetIps_.add(value);
      onChanged();
      return this;
    }

    private java.lang.Object snapshotId_ = "";
    /**
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
     * <code>string snapshot_id = 3;</code>
     */
    public Builder clearSnapshotId() {
      
      snapshotId_ = getDefaultInstance().getSnapshotId();
      onChanged();
      return this;
    }
    /**
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

    private int restoreType_ ;
    /**
     * <code>uint32 restore_type = 4;</code>
     */
    public int getRestoreType() {
      return restoreType_;
    }
    /**
     * <code>uint32 restore_type = 4;</code>
     */
    public Builder setRestoreType(int value) {
      
      restoreType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint32 restore_type = 4;</code>
     */
    public Builder clearRestoreType() {
      
      restoreType_ = 0;
      onChanged();
      return this;
    }

    private boolean snapshotOnly_ ;
    /**
     * <pre>
     * optional (default: false)
     * </pre>
     *
     * <code>bool snapshot_only = 5;</code>
     */
    public boolean getSnapshotOnly() {
      return snapshotOnly_;
    }
    /**
     * <pre>
     * optional (default: false)
     * </pre>
     *
     * <code>bool snapshot_only = 5;</code>
     */
    public Builder setSnapshotOnly(boolean value) {
      
      snapshotOnly_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * optional (default: false)
     * </pre>
     *
     * <code>bool snapshot_only = 5;</code>
     */
    public Builder clearSnapshotOnly() {
      
      snapshotOnly_ = false;
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


    // @@protoc_insertion_point(builder_scope:rpc.RestoreRequest)
  }

  // @@protoc_insertion_point(class_scope:rpc.RestoreRequest)
  private static final RestoreRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new RestoreRequest();
  }

  public static RestoreRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<RestoreRequest>
      PARSER = new com.google.protobuf.AbstractParser<RestoreRequest>() {
    @java.lang.Override
    public RestoreRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new RestoreRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<RestoreRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<RestoreRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public RestoreRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

