package com.scalar.backup.cassandra.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.13.2)",
    comments = "Source: cassandra-backup.proto")
public final class CassandraBackupGrpc {

  private CassandraBackupGrpc() {}

  public static final String SERVICE_NAME = "rpc.CassandraBackup";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest,
      com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse> getRegisterClusterMethod;

  public static io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest,
      com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse> getRegisterClusterMethod() {
    io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest, com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse> getRegisterClusterMethod;
    if ((getRegisterClusterMethod = CassandraBackupGrpc.getRegisterClusterMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getRegisterClusterMethod = CassandraBackupGrpc.getRegisterClusterMethod) == null) {
          CassandraBackupGrpc.getRegisterClusterMethod = getRegisterClusterMethod = 
              io.grpc.MethodDescriptor.<com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest, com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "registerCluster"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("registerCluster"))
                  .build();
          }
        }
     }
     return getRegisterClusterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.scalar.backup.cassandra.rpc.ClusterListingResponse> getShowClustersMethod;

  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.scalar.backup.cassandra.rpc.ClusterListingResponse> getShowClustersMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.scalar.backup.cassandra.rpc.ClusterListingResponse> getShowClustersMethod;
    if ((getShowClustersMethod = CassandraBackupGrpc.getShowClustersMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getShowClustersMethod = CassandraBackupGrpc.getShowClustersMethod) == null) {
          CassandraBackupGrpc.getShowClustersMethod = getShowClustersMethod = 
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.scalar.backup.cassandra.rpc.ClusterListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "ShowClusters"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.ClusterListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("ShowClusters"))
                  .build();
          }
        }
     }
     return getShowClustersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.NodeListingRequest,
      com.scalar.backup.cassandra.rpc.NodeListingResponse> getListNodesMethod;

  public static io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.NodeListingRequest,
      com.scalar.backup.cassandra.rpc.NodeListingResponse> getListNodesMethod() {
    io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.NodeListingRequest, com.scalar.backup.cassandra.rpc.NodeListingResponse> getListNodesMethod;
    if ((getListNodesMethod = CassandraBackupGrpc.getListNodesMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getListNodesMethod = CassandraBackupGrpc.getListNodesMethod) == null) {
          CassandraBackupGrpc.getListNodesMethod = getListNodesMethod = 
              io.grpc.MethodDescriptor.<com.scalar.backup.cassandra.rpc.NodeListingRequest, com.scalar.backup.cassandra.rpc.NodeListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "ListNodes"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.NodeListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.NodeListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("ListNodes"))
                  .build();
          }
        }
     }
     return getListNodesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.BackupListingRequest,
      com.scalar.backup.cassandra.rpc.BackupListingResponse> getListBackupsMethod;

  public static io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.BackupListingRequest,
      com.scalar.backup.cassandra.rpc.BackupListingResponse> getListBackupsMethod() {
    io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.BackupListingRequest, com.scalar.backup.cassandra.rpc.BackupListingResponse> getListBackupsMethod;
    if ((getListBackupsMethod = CassandraBackupGrpc.getListBackupsMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getListBackupsMethod = CassandraBackupGrpc.getListBackupsMethod) == null) {
          CassandraBackupGrpc.getListBackupsMethod = getListBackupsMethod = 
              io.grpc.MethodDescriptor.<com.scalar.backup.cassandra.rpc.BackupListingRequest, com.scalar.backup.cassandra.rpc.BackupListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "ListBackups"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.BackupListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.BackupListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("ListBackups"))
                  .build();
          }
        }
     }
     return getListBackupsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.BackupRequest,
      com.scalar.backup.cassandra.rpc.BackupResponse> getTakeBackupMethod;

  public static io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.BackupRequest,
      com.scalar.backup.cassandra.rpc.BackupResponse> getTakeBackupMethod() {
    io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.BackupRequest, com.scalar.backup.cassandra.rpc.BackupResponse> getTakeBackupMethod;
    if ((getTakeBackupMethod = CassandraBackupGrpc.getTakeBackupMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getTakeBackupMethod = CassandraBackupGrpc.getTakeBackupMethod) == null) {
          CassandraBackupGrpc.getTakeBackupMethod = getTakeBackupMethod = 
              io.grpc.MethodDescriptor.<com.scalar.backup.cassandra.rpc.BackupRequest, com.scalar.backup.cassandra.rpc.BackupResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "TakeBackup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.BackupRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.BackupResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("TakeBackup"))
                  .build();
          }
        }
     }
     return getTakeBackupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.RestoreRequest,
      com.scalar.backup.cassandra.rpc.RestoreResponse> getRestoreBackupMethod;

  public static io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.RestoreRequest,
      com.scalar.backup.cassandra.rpc.RestoreResponse> getRestoreBackupMethod() {
    io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.RestoreRequest, com.scalar.backup.cassandra.rpc.RestoreResponse> getRestoreBackupMethod;
    if ((getRestoreBackupMethod = CassandraBackupGrpc.getRestoreBackupMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getRestoreBackupMethod = CassandraBackupGrpc.getRestoreBackupMethod) == null) {
          CassandraBackupGrpc.getRestoreBackupMethod = getRestoreBackupMethod = 
              io.grpc.MethodDescriptor.<com.scalar.backup.cassandra.rpc.RestoreRequest, com.scalar.backup.cassandra.rpc.RestoreResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "RestoreBackup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.RestoreRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.RestoreResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("RestoreBackup"))
                  .build();
          }
        }
     }
     return getRestoreBackupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest,
      com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse> getListRestoreStatusesMethod;

  public static io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest,
      com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse> getListRestoreStatusesMethod() {
    io.grpc.MethodDescriptor<com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest, com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse> getListRestoreStatusesMethod;
    if ((getListRestoreStatusesMethod = CassandraBackupGrpc.getListRestoreStatusesMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getListRestoreStatusesMethod = CassandraBackupGrpc.getListRestoreStatusesMethod) == null) {
          CassandraBackupGrpc.getListRestoreStatusesMethod = getListRestoreStatusesMethod = 
              io.grpc.MethodDescriptor.<com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest, com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "ListRestoreStatuses"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("ListRestoreStatuses"))
                  .build();
          }
        }
     }
     return getListRestoreStatusesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CassandraBackupStub newStub(io.grpc.Channel channel) {
    return new CassandraBackupStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CassandraBackupBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CassandraBackupBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CassandraBackupFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CassandraBackupFutureStub(channel);
  }

  /**
   */
  public static abstract class CassandraBackupImplBase implements io.grpc.BindableService {

    /**
     */
    public void registerCluster(com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterClusterMethod(), responseObserver);
    }

    /**
     */
    public void showClusters(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.ClusterListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getShowClustersMethod(), responseObserver);
    }

    /**
     */
    public void listNodes(com.scalar.backup.cassandra.rpc.NodeListingRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.NodeListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListNodesMethod(), responseObserver);
    }

    /**
     */
    public void listBackups(com.scalar.backup.cassandra.rpc.BackupListingRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.BackupListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListBackupsMethod(), responseObserver);
    }

    /**
     */
    public void takeBackup(com.scalar.backup.cassandra.rpc.BackupRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.BackupResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTakeBackupMethod(), responseObserver);
    }

    /**
     */
    public void restoreBackup(com.scalar.backup.cassandra.rpc.RestoreRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.RestoreResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRestoreBackupMethod(), responseObserver);
    }

    /**
     */
    public void listRestoreStatuses(com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListRestoreStatusesMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterClusterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest,
                com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse>(
                  this, METHODID_REGISTER_CLUSTER)))
          .addMethod(
            getShowClustersMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.scalar.backup.cassandra.rpc.ClusterListingResponse>(
                  this, METHODID_SHOW_CLUSTERS)))
          .addMethod(
            getListNodesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.scalar.backup.cassandra.rpc.NodeListingRequest,
                com.scalar.backup.cassandra.rpc.NodeListingResponse>(
                  this, METHODID_LIST_NODES)))
          .addMethod(
            getListBackupsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.scalar.backup.cassandra.rpc.BackupListingRequest,
                com.scalar.backup.cassandra.rpc.BackupListingResponse>(
                  this, METHODID_LIST_BACKUPS)))
          .addMethod(
            getTakeBackupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.scalar.backup.cassandra.rpc.BackupRequest,
                com.scalar.backup.cassandra.rpc.BackupResponse>(
                  this, METHODID_TAKE_BACKUP)))
          .addMethod(
            getRestoreBackupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.scalar.backup.cassandra.rpc.RestoreRequest,
                com.scalar.backup.cassandra.rpc.RestoreResponse>(
                  this, METHODID_RESTORE_BACKUP)))
          .addMethod(
            getListRestoreStatusesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest,
                com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse>(
                  this, METHODID_LIST_RESTORE_STATUSES)))
          .build();
    }
  }

  /**
   */
  public static final class CassandraBackupStub extends io.grpc.stub.AbstractStub<CassandraBackupStub> {
    private CassandraBackupStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CassandraBackupStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CassandraBackupStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CassandraBackupStub(channel, callOptions);
    }

    /**
     */
    public void registerCluster(com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRegisterClusterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void showClusters(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.ClusterListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getShowClustersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listNodes(com.scalar.backup.cassandra.rpc.NodeListingRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.NodeListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListNodesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listBackups(com.scalar.backup.cassandra.rpc.BackupListingRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.BackupListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListBackupsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void takeBackup(com.scalar.backup.cassandra.rpc.BackupRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.BackupResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTakeBackupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void restoreBackup(com.scalar.backup.cassandra.rpc.RestoreRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.RestoreResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRestoreBackupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listRestoreStatuses(com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest request,
        io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListRestoreStatusesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CassandraBackupBlockingStub extends io.grpc.stub.AbstractStub<CassandraBackupBlockingStub> {
    private CassandraBackupBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CassandraBackupBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CassandraBackupBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CassandraBackupBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse registerCluster(com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest request) {
      return blockingUnaryCall(
          getChannel(), getRegisterClusterMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.scalar.backup.cassandra.rpc.ClusterListingResponse showClusters(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getShowClustersMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.scalar.backup.cassandra.rpc.NodeListingResponse listNodes(com.scalar.backup.cassandra.rpc.NodeListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListNodesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.scalar.backup.cassandra.rpc.BackupListingResponse listBackups(com.scalar.backup.cassandra.rpc.BackupListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListBackupsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.scalar.backup.cassandra.rpc.BackupResponse takeBackup(com.scalar.backup.cassandra.rpc.BackupRequest request) {
      return blockingUnaryCall(
          getChannel(), getTakeBackupMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.scalar.backup.cassandra.rpc.RestoreResponse restoreBackup(com.scalar.backup.cassandra.rpc.RestoreRequest request) {
      return blockingUnaryCall(
          getChannel(), getRestoreBackupMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse listRestoreStatuses(com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListRestoreStatusesMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CassandraBackupFutureStub extends io.grpc.stub.AbstractStub<CassandraBackupFutureStub> {
    private CassandraBackupFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CassandraBackupFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CassandraBackupFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CassandraBackupFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse> registerCluster(
        com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRegisterClusterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.scalar.backup.cassandra.rpc.ClusterListingResponse> showClusters(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getShowClustersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.scalar.backup.cassandra.rpc.NodeListingResponse> listNodes(
        com.scalar.backup.cassandra.rpc.NodeListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListNodesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.scalar.backup.cassandra.rpc.BackupListingResponse> listBackups(
        com.scalar.backup.cassandra.rpc.BackupListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListBackupsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.scalar.backup.cassandra.rpc.BackupResponse> takeBackup(
        com.scalar.backup.cassandra.rpc.BackupRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTakeBackupMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.scalar.backup.cassandra.rpc.RestoreResponse> restoreBackup(
        com.scalar.backup.cassandra.rpc.RestoreRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRestoreBackupMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse> listRestoreStatuses(
        com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListRestoreStatusesMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_CLUSTER = 0;
  private static final int METHODID_SHOW_CLUSTERS = 1;
  private static final int METHODID_LIST_NODES = 2;
  private static final int METHODID_LIST_BACKUPS = 3;
  private static final int METHODID_TAKE_BACKUP = 4;
  private static final int METHODID_RESTORE_BACKUP = 5;
  private static final int METHODID_LIST_RESTORE_STATUSES = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CassandraBackupImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CassandraBackupImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER_CLUSTER:
          serviceImpl.registerCluster((com.scalar.backup.cassandra.rpc.ClusterRegistrationRequest) request,
              (io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.ClusterRegistrationResponse>) responseObserver);
          break;
        case METHODID_SHOW_CLUSTERS:
          serviceImpl.showClusters((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.ClusterListingResponse>) responseObserver);
          break;
        case METHODID_LIST_NODES:
          serviceImpl.listNodes((com.scalar.backup.cassandra.rpc.NodeListingRequest) request,
              (io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.NodeListingResponse>) responseObserver);
          break;
        case METHODID_LIST_BACKUPS:
          serviceImpl.listBackups((com.scalar.backup.cassandra.rpc.BackupListingRequest) request,
              (io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.BackupListingResponse>) responseObserver);
          break;
        case METHODID_TAKE_BACKUP:
          serviceImpl.takeBackup((com.scalar.backup.cassandra.rpc.BackupRequest) request,
              (io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.BackupResponse>) responseObserver);
          break;
        case METHODID_RESTORE_BACKUP:
          serviceImpl.restoreBackup((com.scalar.backup.cassandra.rpc.RestoreRequest) request,
              (io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.RestoreResponse>) responseObserver);
          break;
        case METHODID_LIST_RESTORE_STATUSES:
          serviceImpl.listRestoreStatuses((com.scalar.backup.cassandra.rpc.RestoreStatusListingRequest) request,
              (io.grpc.stub.StreamObserver<com.scalar.backup.cassandra.rpc.RestoreStatusListingResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CassandraBackupBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CassandraBackupBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.scalar.backup.cassandra.rpc.CassandraBackupProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CassandraBackup");
    }
  }

  private static final class CassandraBackupFileDescriptorSupplier
      extends CassandraBackupBaseDescriptorSupplier {
    CassandraBackupFileDescriptorSupplier() {}
  }

  private static final class CassandraBackupMethodDescriptorSupplier
      extends CassandraBackupBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CassandraBackupMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CassandraBackupGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CassandraBackupFileDescriptorSupplier())
              .addMethod(getRegisterClusterMethod())
              .addMethod(getShowClustersMethod())
              .addMethod(getListNodesMethod())
              .addMethod(getListBackupsMethod())
              .addMethod(getTakeBackupMethod())
              .addMethod(getRestoreBackupMethod())
              .addMethod(getListRestoreStatusesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
