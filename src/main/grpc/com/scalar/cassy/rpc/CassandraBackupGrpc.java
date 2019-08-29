package com.scalar.cassy.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
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
  private static volatile io.grpc.MethodDescriptor<ClusterRegistrationRequest,
      ClusterRegistrationResponse> getRegisterClusterMethod;

  public static io.grpc.MethodDescriptor<ClusterRegistrationRequest,
      ClusterRegistrationResponse> getRegisterClusterMethod() {
    io.grpc.MethodDescriptor<ClusterRegistrationRequest, ClusterRegistrationResponse> getRegisterClusterMethod;
    if ((getRegisterClusterMethod = CassandraBackupGrpc.getRegisterClusterMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getRegisterClusterMethod = CassandraBackupGrpc.getRegisterClusterMethod) == null) {
          CassandraBackupGrpc.getRegisterClusterMethod = getRegisterClusterMethod = 
              io.grpc.MethodDescriptor.<ClusterRegistrationRequest, ClusterRegistrationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "RegisterCluster"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClusterRegistrationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClusterRegistrationResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("RegisterCluster"))
                  .build();
          }
        }
     }
     return getRegisterClusterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ClusterListingRequest,
      ClusterListingResponse> getListClustersMethod;

  public static io.grpc.MethodDescriptor<ClusterListingRequest,
      ClusterListingResponse> getListClustersMethod() {
    io.grpc.MethodDescriptor<ClusterListingRequest, ClusterListingResponse> getListClustersMethod;
    if ((getListClustersMethod = CassandraBackupGrpc.getListClustersMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getListClustersMethod = CassandraBackupGrpc.getListClustersMethod) == null) {
          CassandraBackupGrpc.getListClustersMethod = getListClustersMethod = 
              io.grpc.MethodDescriptor.<ClusterListingRequest, ClusterListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "ListClusters"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClusterListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ClusterListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("ListClusters"))
                  .build();
          }
        }
     }
     return getListClustersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<BackupListingRequest,
      BackupListingResponse> getListBackupsMethod;

  public static io.grpc.MethodDescriptor<BackupListingRequest,
      BackupListingResponse> getListBackupsMethod() {
    io.grpc.MethodDescriptor<BackupListingRequest, BackupListingResponse> getListBackupsMethod;
    if ((getListBackupsMethod = CassandraBackupGrpc.getListBackupsMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getListBackupsMethod = CassandraBackupGrpc.getListBackupsMethod) == null) {
          CassandraBackupGrpc.getListBackupsMethod = getListBackupsMethod = 
              io.grpc.MethodDescriptor.<BackupListingRequest, BackupListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "ListBackups"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  BackupListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  BackupListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("ListBackups"))
                  .build();
          }
        }
     }
     return getListBackupsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<BackupRequest,
      BackupResponse> getTakeBackupMethod;

  public static io.grpc.MethodDescriptor<BackupRequest,
      BackupResponse> getTakeBackupMethod() {
    io.grpc.MethodDescriptor<BackupRequest, BackupResponse> getTakeBackupMethod;
    if ((getTakeBackupMethod = CassandraBackupGrpc.getTakeBackupMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getTakeBackupMethod = CassandraBackupGrpc.getTakeBackupMethod) == null) {
          CassandraBackupGrpc.getTakeBackupMethod = getTakeBackupMethod = 
              io.grpc.MethodDescriptor.<BackupRequest, BackupResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "TakeBackup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  BackupRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  BackupResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("TakeBackup"))
                  .build();
          }
        }
     }
     return getTakeBackupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<RestoreRequest,
      RestoreResponse> getRestoreBackupMethod;

  public static io.grpc.MethodDescriptor<RestoreRequest,
      RestoreResponse> getRestoreBackupMethod() {
    io.grpc.MethodDescriptor<RestoreRequest, RestoreResponse> getRestoreBackupMethod;
    if ((getRestoreBackupMethod = CassandraBackupGrpc.getRestoreBackupMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getRestoreBackupMethod = CassandraBackupGrpc.getRestoreBackupMethod) == null) {
          CassandraBackupGrpc.getRestoreBackupMethod = getRestoreBackupMethod = 
              io.grpc.MethodDescriptor.<RestoreRequest, RestoreResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "RestoreBackup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RestoreRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RestoreResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassandraBackupMethodDescriptorSupplier("RestoreBackup"))
                  .build();
          }
        }
     }
     return getRestoreBackupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<RestoreStatusListingRequest,
      RestoreStatusListingResponse> getListRestoreStatusesMethod;

  public static io.grpc.MethodDescriptor<RestoreStatusListingRequest,
      RestoreStatusListingResponse> getListRestoreStatusesMethod() {
    io.grpc.MethodDescriptor<RestoreStatusListingRequest, RestoreStatusListingResponse> getListRestoreStatusesMethod;
    if ((getListRestoreStatusesMethod = CassandraBackupGrpc.getListRestoreStatusesMethod) == null) {
      synchronized (CassandraBackupGrpc.class) {
        if ((getListRestoreStatusesMethod = CassandraBackupGrpc.getListRestoreStatusesMethod) == null) {
          CassandraBackupGrpc.getListRestoreStatusesMethod = getListRestoreStatusesMethod = 
              io.grpc.MethodDescriptor.<RestoreStatusListingRequest, RestoreStatusListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.CassandraBackup", "ListRestoreStatuses"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RestoreStatusListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RestoreStatusListingResponse.getDefaultInstance()))
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
    public void registerCluster(ClusterRegistrationRequest request,
        io.grpc.stub.StreamObserver<ClusterRegistrationResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterClusterMethod(), responseObserver);
    }

    /**
     */
    public void listClusters(ClusterListingRequest request,
        io.grpc.stub.StreamObserver<ClusterListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListClustersMethod(), responseObserver);
    }

    /**
     */
    public void listBackups(BackupListingRequest request,
        io.grpc.stub.StreamObserver<BackupListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListBackupsMethod(), responseObserver);
    }

    /**
     */
    public void takeBackup(BackupRequest request,
        io.grpc.stub.StreamObserver<BackupResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTakeBackupMethod(), responseObserver);
    }

    /**
     */
    public void restoreBackup(RestoreRequest request,
        io.grpc.stub.StreamObserver<RestoreResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRestoreBackupMethod(), responseObserver);
    }

    /**
     */
    public void listRestoreStatuses(RestoreStatusListingRequest request,
        io.grpc.stub.StreamObserver<RestoreStatusListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListRestoreStatusesMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterClusterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                  ClusterRegistrationRequest,
                  ClusterRegistrationResponse>(
                  this, METHODID_REGISTER_CLUSTER)))
          .addMethod(
            getListClustersMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                  ClusterListingRequest,
                  ClusterListingResponse>(
                  this, METHODID_LIST_CLUSTERS)))
          .addMethod(
            getListBackupsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                  BackupListingRequest,
                  BackupListingResponse>(
                  this, METHODID_LIST_BACKUPS)))
          .addMethod(
            getTakeBackupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                  BackupRequest,
                  BackupResponse>(
                  this, METHODID_TAKE_BACKUP)))
          .addMethod(
            getRestoreBackupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                  RestoreRequest,
                  RestoreResponse>(
                  this, METHODID_RESTORE_BACKUP)))
          .addMethod(
            getListRestoreStatusesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                  RestoreStatusListingRequest,
                  RestoreStatusListingResponse>(
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
    public void registerCluster(ClusterRegistrationRequest request,
        io.grpc.stub.StreamObserver<ClusterRegistrationResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRegisterClusterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listClusters(ClusterListingRequest request,
        io.grpc.stub.StreamObserver<ClusterListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListClustersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listBackups(BackupListingRequest request,
        io.grpc.stub.StreamObserver<BackupListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListBackupsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void takeBackup(BackupRequest request,
        io.grpc.stub.StreamObserver<BackupResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTakeBackupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void restoreBackup(RestoreRequest request,
        io.grpc.stub.StreamObserver<RestoreResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRestoreBackupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listRestoreStatuses(RestoreStatusListingRequest request,
        io.grpc.stub.StreamObserver<RestoreStatusListingResponse> responseObserver) {
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
    public ClusterRegistrationResponse registerCluster(ClusterRegistrationRequest request) {
      return blockingUnaryCall(
          getChannel(), getRegisterClusterMethod(), getCallOptions(), request);
    }

    /**
     */
    public ClusterListingResponse listClusters(ClusterListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListClustersMethod(), getCallOptions(), request);
    }

    /**
     */
    public BackupListingResponse listBackups(BackupListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListBackupsMethod(), getCallOptions(), request);
    }

    /**
     */
    public BackupResponse takeBackup(BackupRequest request) {
      return blockingUnaryCall(
          getChannel(), getTakeBackupMethod(), getCallOptions(), request);
    }

    /**
     */
    public RestoreResponse restoreBackup(RestoreRequest request) {
      return blockingUnaryCall(
          getChannel(), getRestoreBackupMethod(), getCallOptions(), request);
    }

    /**
     */
    public RestoreStatusListingResponse listRestoreStatuses(RestoreStatusListingRequest request) {
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
    public com.google.common.util.concurrent.ListenableFuture<ClusterRegistrationResponse> registerCluster(
        ClusterRegistrationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRegisterClusterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ClusterListingResponse> listClusters(
        ClusterListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListClustersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<BackupListingResponse> listBackups(
        BackupListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListBackupsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<BackupResponse> takeBackup(
        BackupRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTakeBackupMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<RestoreResponse> restoreBackup(
        RestoreRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRestoreBackupMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<RestoreStatusListingResponse> listRestoreStatuses(
        RestoreStatusListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListRestoreStatusesMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_CLUSTER = 0;
  private static final int METHODID_LIST_CLUSTERS = 1;
  private static final int METHODID_LIST_BACKUPS = 2;
  private static final int METHODID_TAKE_BACKUP = 3;
  private static final int METHODID_RESTORE_BACKUP = 4;
  private static final int METHODID_LIST_RESTORE_STATUSES = 5;

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
          serviceImpl.registerCluster((ClusterRegistrationRequest) request,
              (io.grpc.stub.StreamObserver<ClusterRegistrationResponse>) responseObserver);
          break;
        case METHODID_LIST_CLUSTERS:
          serviceImpl.listClusters((ClusterListingRequest) request,
              (io.grpc.stub.StreamObserver<ClusterListingResponse>) responseObserver);
          break;
        case METHODID_LIST_BACKUPS:
          serviceImpl.listBackups((BackupListingRequest) request,
              (io.grpc.stub.StreamObserver<BackupListingResponse>) responseObserver);
          break;
        case METHODID_TAKE_BACKUP:
          serviceImpl.takeBackup((BackupRequest) request,
              (io.grpc.stub.StreamObserver<BackupResponse>) responseObserver);
          break;
        case METHODID_RESTORE_BACKUP:
          serviceImpl.restoreBackup((RestoreRequest) request,
              (io.grpc.stub.StreamObserver<RestoreResponse>) responseObserver);
          break;
        case METHODID_LIST_RESTORE_STATUSES:
          serviceImpl.listRestoreStatuses((RestoreStatusListingRequest) request,
              (io.grpc.stub.StreamObserver<RestoreStatusListingResponse>) responseObserver);
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
      return CassandraBackupProto.getDescriptor();
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
              .addMethod(getListClustersMethod())
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
