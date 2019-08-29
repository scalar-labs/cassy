// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: cassy.proto

package com.scalar.cassy.rpc;

/**
 * Protobuf type {@code rpc.ClusterRegistrationResponse}
 */
public  final class ClusterRegistrationResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:rpc.ClusterRegistrationResponse)
    ClusterRegistrationResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ClusterRegistrationResponse.newBuilder() to construct.
  private ClusterRegistrationResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ClusterRegistrationResponse() {
    clusterId_ = "";
    clusterName_ = "";
    targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    keyspaces_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    dataDir_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ClusterRegistrationResponse(
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

            clusterName_ = s;
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();
            if (!((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
              targetIps_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000004;
            }
            targetIps_.add(s);
            break;
          }
          case 34: {
            java.lang.String s = input.readStringRequireUtf8();
            if (!((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
              keyspaces_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000008;
            }
            keyspaces_.add(s);
            break;
          }
          case 42: {
            java.lang.String s = input.readStringRequireUtf8();

            dataDir_ = s;
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
      if (((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
        targetIps_ = targetIps_.getUnmodifiableView();
      }
      if (((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
        keyspaces_ = keyspaces_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.scalar.cassy.rpc.CassyProto.internal_static_rpc_ClusterRegistrationResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.scalar.cassy.rpc.CassyProto.internal_static_rpc_ClusterRegistrationResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.scalar.cassy.rpc.ClusterRegistrationResponse.class, com.scalar.cassy.rpc.ClusterRegistrationResponse.Builder.class);
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

  public static final int CLUSTER_NAME_FIELD_NUMBER = 2;
  private volatile java.lang.Object clusterName_;
  /**
   * <code>string cluster_name = 2;</code>
   */
  public java.lang.String getClusterName() {
    java.lang.Object ref = clusterName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      clusterName_ = s;
      return s;
    }
  }
  /**
   * <code>string cluster_name = 2;</code>
   */
  public com.google.protobuf.ByteString
      getClusterNameBytes() {
    java.lang.Object ref = clusterName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      clusterName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TARGET_IPS_FIELD_NUMBER = 3;
  private com.google.protobuf.LazyStringList targetIps_;
  /**
   * <code>repeated string target_ips = 3;</code>
   */
  public com.google.protobuf.ProtocolStringList
      getTargetIpsList() {
    return targetIps_;
  }
  /**
   * <code>repeated string target_ips = 3;</code>
   */
  public int getTargetIpsCount() {
    return targetIps_.size();
  }
  /**
   * <code>repeated string target_ips = 3;</code>
   */
  public java.lang.String getTargetIps(int index) {
    return targetIps_.get(index);
  }
  /**
   * <code>repeated string target_ips = 3;</code>
   */
  public com.google.protobuf.ByteString
      getTargetIpsBytes(int index) {
    return targetIps_.getByteString(index);
  }

  public static final int KEYSPACES_FIELD_NUMBER = 4;
  private com.google.protobuf.LazyStringList keyspaces_;
  /**
   * <code>repeated string keyspaces = 4;</code>
   */
  public com.google.protobuf.ProtocolStringList
      getKeyspacesList() {
    return keyspaces_;
  }
  /**
   * <code>repeated string keyspaces = 4;</code>
   */
  public int getKeyspacesCount() {
    return keyspaces_.size();
  }
  /**
   * <code>repeated string keyspaces = 4;</code>
   */
  public java.lang.String getKeyspaces(int index) {
    return keyspaces_.get(index);
  }
  /**
   * <code>repeated string keyspaces = 4;</code>
   */
  public com.google.protobuf.ByteString
      getKeyspacesBytes(int index) {
    return keyspaces_.getByteString(index);
  }

  public static final int DATA_DIR_FIELD_NUMBER = 5;
  private volatile java.lang.Object dataDir_;
  /**
   * <code>string data_dir = 5;</code>
   */
  public java.lang.String getDataDir() {
    java.lang.Object ref = dataDir_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      dataDir_ = s;
      return s;
    }
  }
  /**
   * <code>string data_dir = 5;</code>
   */
  public com.google.protobuf.ByteString
      getDataDirBytes() {
    java.lang.Object ref = dataDir_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      dataDir_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    if (!getClusterNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, clusterName_);
    }
    for (int i = 0; i < targetIps_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, targetIps_.getRaw(i));
    }
    for (int i = 0; i < keyspaces_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, keyspaces_.getRaw(i));
    }
    if (!getDataDirBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 5, dataDir_);
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
    if (!getClusterNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, clusterName_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < targetIps_.size(); i++) {
        dataSize += computeStringSizeNoTag(targetIps_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getTargetIpsList().size();
    }
    {
      int dataSize = 0;
      for (int i = 0; i < keyspaces_.size(); i++) {
        dataSize += computeStringSizeNoTag(keyspaces_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getKeyspacesList().size();
    }
    if (!getDataDirBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, dataDir_);
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
    if (!(obj instanceof com.scalar.cassy.rpc.ClusterRegistrationResponse)) {
      return super.equals(obj);
    }
    com.scalar.cassy.rpc.ClusterRegistrationResponse other = (com.scalar.cassy.rpc.ClusterRegistrationResponse) obj;

    boolean result = true;
    result = result && getClusterId()
        .equals(other.getClusterId());
    result = result && getClusterName()
        .equals(other.getClusterName());
    result = result && getTargetIpsList()
        .equals(other.getTargetIpsList());
    result = result && getKeyspacesList()
        .equals(other.getKeyspacesList());
    result = result && getDataDir()
        .equals(other.getDataDir());
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
    hash = (37 * hash) + CLUSTER_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getClusterName().hashCode();
    if (getTargetIpsCount() > 0) {
      hash = (37 * hash) + TARGET_IPS_FIELD_NUMBER;
      hash = (53 * hash) + getTargetIpsList().hashCode();
    }
    if (getKeyspacesCount() > 0) {
      hash = (37 * hash) + KEYSPACES_FIELD_NUMBER;
      hash = (53 * hash) + getKeyspacesList().hashCode();
    }
    hash = (37 * hash) + DATA_DIR_FIELD_NUMBER;
    hash = (53 * hash) + getDataDir().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.scalar.cassy.rpc.ClusterRegistrationResponse parseFrom(
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
  public static Builder newBuilder(com.scalar.cassy.rpc.ClusterRegistrationResponse prototype) {
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
   * Protobuf type {@code rpc.ClusterRegistrationResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:rpc.ClusterRegistrationResponse)
      com.scalar.cassy.rpc.ClusterRegistrationResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.scalar.cassy.rpc.CassyProto.internal_static_rpc_ClusterRegistrationResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.scalar.cassy.rpc.CassyProto.internal_static_rpc_ClusterRegistrationResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.scalar.cassy.rpc.ClusterRegistrationResponse.class, com.scalar.cassy.rpc.ClusterRegistrationResponse.Builder.class);
    }

    // Construct using com.scalar.cassy.rpc.ClusterRegistrationResponse.newBuilder()
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

      clusterName_ = "";

      targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000004);
      keyspaces_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000008);
      dataDir_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.scalar.cassy.rpc.CassyProto.internal_static_rpc_ClusterRegistrationResponse_descriptor;
    }

    @java.lang.Override
    public com.scalar.cassy.rpc.ClusterRegistrationResponse getDefaultInstanceForType() {
      return com.scalar.cassy.rpc.ClusterRegistrationResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.scalar.cassy.rpc.ClusterRegistrationResponse build() {
      com.scalar.cassy.rpc.ClusterRegistrationResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.scalar.cassy.rpc.ClusterRegistrationResponse buildPartial() {
      com.scalar.cassy.rpc.ClusterRegistrationResponse result = new com.scalar.cassy.rpc.ClusterRegistrationResponse(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.clusterId_ = clusterId_;
      result.clusterName_ = clusterName_;
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        targetIps_ = targetIps_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000004);
      }
      result.targetIps_ = targetIps_;
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        keyspaces_ = keyspaces_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000008);
      }
      result.keyspaces_ = keyspaces_;
      result.dataDir_ = dataDir_;
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
      if (other instanceof com.scalar.cassy.rpc.ClusterRegistrationResponse) {
        return mergeFrom((com.scalar.cassy.rpc.ClusterRegistrationResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.scalar.cassy.rpc.ClusterRegistrationResponse other) {
      if (other == com.scalar.cassy.rpc.ClusterRegistrationResponse.getDefaultInstance()) return this;
      if (!other.getClusterId().isEmpty()) {
        clusterId_ = other.clusterId_;
        onChanged();
      }
      if (!other.getClusterName().isEmpty()) {
        clusterName_ = other.clusterName_;
        onChanged();
      }
      if (!other.targetIps_.isEmpty()) {
        if (targetIps_.isEmpty()) {
          targetIps_ = other.targetIps_;
          bitField0_ = (bitField0_ & ~0x00000004);
        } else {
          ensureTargetIpsIsMutable();
          targetIps_.addAll(other.targetIps_);
        }
        onChanged();
      }
      if (!other.keyspaces_.isEmpty()) {
        if (keyspaces_.isEmpty()) {
          keyspaces_ = other.keyspaces_;
          bitField0_ = (bitField0_ & ~0x00000008);
        } else {
          ensureKeyspacesIsMutable();
          keyspaces_.addAll(other.keyspaces_);
        }
        onChanged();
      }
      if (!other.getDataDir().isEmpty()) {
        dataDir_ = other.dataDir_;
        onChanged();
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
      com.scalar.cassy.rpc.ClusterRegistrationResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.scalar.cassy.rpc.ClusterRegistrationResponse) e.getUnfinishedMessage();
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

    private java.lang.Object clusterName_ = "";
    /**
     * <code>string cluster_name = 2;</code>
     */
    public java.lang.String getClusterName() {
      java.lang.Object ref = clusterName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        clusterName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string cluster_name = 2;</code>
     */
    public com.google.protobuf.ByteString
        getClusterNameBytes() {
      java.lang.Object ref = clusterName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        clusterName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string cluster_name = 2;</code>
     */
    public Builder setClusterName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      clusterName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string cluster_name = 2;</code>
     */
    public Builder clearClusterName() {
      
      clusterName_ = getDefaultInstance().getClusterName();
      onChanged();
      return this;
    }
    /**
     * <code>string cluster_name = 2;</code>
     */
    public Builder setClusterNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      clusterName_ = value;
      onChanged();
      return this;
    }

    private com.google.protobuf.LazyStringList targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureTargetIpsIsMutable() {
      if (!((bitField0_ & 0x00000004) == 0x00000004)) {
        targetIps_ = new com.google.protobuf.LazyStringArrayList(targetIps_);
        bitField0_ |= 0x00000004;
       }
    }
    /**
     * <code>repeated string target_ips = 3;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getTargetIpsList() {
      return targetIps_.getUnmodifiableView();
    }
    /**
     * <code>repeated string target_ips = 3;</code>
     */
    public int getTargetIpsCount() {
      return targetIps_.size();
    }
    /**
     * <code>repeated string target_ips = 3;</code>
     */
    public java.lang.String getTargetIps(int index) {
      return targetIps_.get(index);
    }
    /**
     * <code>repeated string target_ips = 3;</code>
     */
    public com.google.protobuf.ByteString
        getTargetIpsBytes(int index) {
      return targetIps_.getByteString(index);
    }
    /**
     * <code>repeated string target_ips = 3;</code>
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
     * <code>repeated string target_ips = 3;</code>
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
     * <code>repeated string target_ips = 3;</code>
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
     * <code>repeated string target_ips = 3;</code>
     */
    public Builder clearTargetIps() {
      targetIps_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000004);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string target_ips = 3;</code>
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

    private com.google.protobuf.LazyStringList keyspaces_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureKeyspacesIsMutable() {
      if (!((bitField0_ & 0x00000008) == 0x00000008)) {
        keyspaces_ = new com.google.protobuf.LazyStringArrayList(keyspaces_);
        bitField0_ |= 0x00000008;
       }
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public com.google.protobuf.ProtocolStringList
        getKeyspacesList() {
      return keyspaces_.getUnmodifiableView();
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public int getKeyspacesCount() {
      return keyspaces_.size();
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public java.lang.String getKeyspaces(int index) {
      return keyspaces_.get(index);
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public com.google.protobuf.ByteString
        getKeyspacesBytes(int index) {
      return keyspaces_.getByteString(index);
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public Builder setKeyspaces(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureKeyspacesIsMutable();
      keyspaces_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public Builder addKeyspaces(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureKeyspacesIsMutable();
      keyspaces_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public Builder addAllKeyspaces(
        java.lang.Iterable<java.lang.String> values) {
      ensureKeyspacesIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, keyspaces_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public Builder clearKeyspaces() {
      keyspaces_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000008);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string keyspaces = 4;</code>
     */
    public Builder addKeyspacesBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      ensureKeyspacesIsMutable();
      keyspaces_.add(value);
      onChanged();
      return this;
    }

    private java.lang.Object dataDir_ = "";
    /**
     * <code>string data_dir = 5;</code>
     */
    public java.lang.String getDataDir() {
      java.lang.Object ref = dataDir_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        dataDir_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string data_dir = 5;</code>
     */
    public com.google.protobuf.ByteString
        getDataDirBytes() {
      java.lang.Object ref = dataDir_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        dataDir_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string data_dir = 5;</code>
     */
    public Builder setDataDir(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      dataDir_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string data_dir = 5;</code>
     */
    public Builder clearDataDir() {
      
      dataDir_ = getDefaultInstance().getDataDir();
      onChanged();
      return this;
    }
    /**
     * <code>string data_dir = 5;</code>
     */
    public Builder setDataDirBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      dataDir_ = value;
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


    // @@protoc_insertion_point(builder_scope:rpc.ClusterRegistrationResponse)
  }

  // @@protoc_insertion_point(class_scope:rpc.ClusterRegistrationResponse)
  private static final com.scalar.cassy.rpc.ClusterRegistrationResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.scalar.cassy.rpc.ClusterRegistrationResponse();
  }

  public static com.scalar.cassy.rpc.ClusterRegistrationResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ClusterRegistrationResponse>
      PARSER = new com.google.protobuf.AbstractParser<ClusterRegistrationResponse>() {
    @java.lang.Override
    public ClusterRegistrationResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ClusterRegistrationResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ClusterRegistrationResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ClusterRegistrationResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.scalar.cassy.rpc.ClusterRegistrationResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

