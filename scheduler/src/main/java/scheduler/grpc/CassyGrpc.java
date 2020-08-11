package scheduler.grpc;

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
    comments = "Source: cassy.proto")
public final class CassyGrpc {

  private CassyGrpc() {}

  public static final String SERVICE_NAME = "rpc.Cassy";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<scheduler.grpc.ClusterRegistrationRequest,
      scheduler.grpc.ClusterRegistrationResponse> getRegisterClusterMethod;

  public static io.grpc.MethodDescriptor<scheduler.grpc.ClusterRegistrationRequest,
      scheduler.grpc.ClusterRegistrationResponse> getRegisterClusterMethod() {
    io.grpc.MethodDescriptor<scheduler.grpc.ClusterRegistrationRequest, scheduler.grpc.ClusterRegistrationResponse> getRegisterClusterMethod;
    if ((getRegisterClusterMethod = CassyGrpc.getRegisterClusterMethod) == null) {
      synchronized (CassyGrpc.class) {
        if ((getRegisterClusterMethod = CassyGrpc.getRegisterClusterMethod) == null) {
          CassyGrpc.getRegisterClusterMethod = getRegisterClusterMethod =
              io.grpc.MethodDescriptor.<scheduler.grpc.ClusterRegistrationRequest, scheduler.grpc.ClusterRegistrationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Cassy", "RegisterCluster"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.ClusterRegistrationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.ClusterRegistrationResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassyMethodDescriptorSupplier("RegisterCluster"))
                  .build();
          }
        }
     }
     return getRegisterClusterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<scheduler.grpc.ClusterListingRequest,
      scheduler.grpc.ClusterListingResponse> getListClustersMethod;

  public static io.grpc.MethodDescriptor<scheduler.grpc.ClusterListingRequest,
      scheduler.grpc.ClusterListingResponse> getListClustersMethod() {
    io.grpc.MethodDescriptor<scheduler.grpc.ClusterListingRequest, scheduler.grpc.ClusterListingResponse> getListClustersMethod;
    if ((getListClustersMethod = CassyGrpc.getListClustersMethod) == null) {
      synchronized (CassyGrpc.class) {
        if ((getListClustersMethod = CassyGrpc.getListClustersMethod) == null) {
          CassyGrpc.getListClustersMethod = getListClustersMethod =
              io.grpc.MethodDescriptor.<scheduler.grpc.ClusterListingRequest, scheduler.grpc.ClusterListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Cassy", "ListClusters"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.ClusterListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.ClusterListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassyMethodDescriptorSupplier("ListClusters"))
                  .build();
          }
        }
     }
     return getListClustersMethod;
  }

  private static volatile io.grpc.MethodDescriptor<scheduler.grpc.BackupListingRequest,
      scheduler.grpc.BackupListingResponse> getListBackupsMethod;

  public static io.grpc.MethodDescriptor<scheduler.grpc.BackupListingRequest,
      scheduler.grpc.BackupListingResponse> getListBackupsMethod() {
    io.grpc.MethodDescriptor<scheduler.grpc.BackupListingRequest, scheduler.grpc.BackupListingResponse> getListBackupsMethod;
    if ((getListBackupsMethod = CassyGrpc.getListBackupsMethod) == null) {
      synchronized (CassyGrpc.class) {
        if ((getListBackupsMethod = CassyGrpc.getListBackupsMethod) == null) {
          CassyGrpc.getListBackupsMethod = getListBackupsMethod =
              io.grpc.MethodDescriptor.<scheduler.grpc.BackupListingRequest, scheduler.grpc.BackupListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Cassy", "ListBackups"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.BackupListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.BackupListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassyMethodDescriptorSupplier("ListBackups"))
                  .build();
          }
        }
     }
     return getListBackupsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<scheduler.grpc.BackupRequest,
      scheduler.grpc.BackupResponse> getTakeBackupMethod;

  public static io.grpc.MethodDescriptor<scheduler.grpc.BackupRequest,
      scheduler.grpc.BackupResponse> getTakeBackupMethod() {
    io.grpc.MethodDescriptor<scheduler.grpc.BackupRequest, scheduler.grpc.BackupResponse> getTakeBackupMethod;
    if ((getTakeBackupMethod = CassyGrpc.getTakeBackupMethod) == null) {
      synchronized (CassyGrpc.class) {
        if ((getTakeBackupMethod = CassyGrpc.getTakeBackupMethod) == null) {
          CassyGrpc.getTakeBackupMethod = getTakeBackupMethod =
              io.grpc.MethodDescriptor.<scheduler.grpc.BackupRequest, scheduler.grpc.BackupResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Cassy", "TakeBackup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.BackupRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.BackupResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassyMethodDescriptorSupplier("TakeBackup"))
                  .build();
          }
        }
     }
     return getTakeBackupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<scheduler.grpc.RestoreRequest,
      scheduler.grpc.RestoreResponse> getRestoreBackupMethod;

  public static io.grpc.MethodDescriptor<scheduler.grpc.RestoreRequest,
      scheduler.grpc.RestoreResponse> getRestoreBackupMethod() {
    io.grpc.MethodDescriptor<scheduler.grpc.RestoreRequest, scheduler.grpc.RestoreResponse> getRestoreBackupMethod;
    if ((getRestoreBackupMethod = CassyGrpc.getRestoreBackupMethod) == null) {
      synchronized (CassyGrpc.class) {
        if ((getRestoreBackupMethod = CassyGrpc.getRestoreBackupMethod) == null) {
          CassyGrpc.getRestoreBackupMethod = getRestoreBackupMethod =
              io.grpc.MethodDescriptor.<scheduler.grpc.RestoreRequest, scheduler.grpc.RestoreResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Cassy", "RestoreBackup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.RestoreRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.RestoreResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassyMethodDescriptorSupplier("RestoreBackup"))
                  .build();
          }
        }
     }
     return getRestoreBackupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<scheduler.grpc.RestoreStatusListingRequest,
      scheduler.grpc.RestoreStatusListingResponse> getListRestoreStatusesMethod;

  public static io.grpc.MethodDescriptor<scheduler.grpc.RestoreStatusListingRequest,
      scheduler.grpc.RestoreStatusListingResponse> getListRestoreStatusesMethod() {
    io.grpc.MethodDescriptor<scheduler.grpc.RestoreStatusListingRequest, scheduler.grpc.RestoreStatusListingResponse> getListRestoreStatusesMethod;
    if ((getListRestoreStatusesMethod = CassyGrpc.getListRestoreStatusesMethod) == null) {
      synchronized (CassyGrpc.class) {
        if ((getListRestoreStatusesMethod = CassyGrpc.getListRestoreStatusesMethod) == null) {
          CassyGrpc.getListRestoreStatusesMethod = getListRestoreStatusesMethod =
              io.grpc.MethodDescriptor.<scheduler.grpc.RestoreStatusListingRequest, scheduler.grpc.RestoreStatusListingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Cassy", "ListRestoreStatuses"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.RestoreStatusListingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  scheduler.grpc.RestoreStatusListingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CassyMethodDescriptorSupplier("ListRestoreStatuses"))
                  .build();
          }
        }
     }
     return getListRestoreStatusesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CassyStub newStub(io.grpc.Channel channel) {
    return new CassyStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CassyBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CassyBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CassyFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CassyFutureStub(channel);
  }

  /**
   */
  public static abstract class CassyImplBase implements io.grpc.BindableService {

    /**
     */
    public void registerCluster(scheduler.grpc.ClusterRegistrationRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.ClusterRegistrationResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterClusterMethod(), responseObserver);
    }

    /**
     */
    public void listClusters(scheduler.grpc.ClusterListingRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.ClusterListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListClustersMethod(), responseObserver);
    }

    /**
     */
    public void listBackups(scheduler.grpc.BackupListingRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.BackupListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListBackupsMethod(), responseObserver);
    }

    /**
     */
    public void takeBackup(scheduler.grpc.BackupRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.BackupResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTakeBackupMethod(), responseObserver);
    }

    /**
     */
    public void restoreBackup(scheduler.grpc.RestoreRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.RestoreResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRestoreBackupMethod(), responseObserver);
    }

    /**
     */
    public void listRestoreStatuses(scheduler.grpc.RestoreStatusListingRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.RestoreStatusListingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListRestoreStatusesMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterClusterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                scheduler.grpc.ClusterRegistrationRequest,
                scheduler.grpc.ClusterRegistrationResponse>(
                  this, METHODID_REGISTER_CLUSTER)))
          .addMethod(
            getListClustersMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                scheduler.grpc.ClusterListingRequest,
                scheduler.grpc.ClusterListingResponse>(
                  this, METHODID_LIST_CLUSTERS)))
          .addMethod(
            getListBackupsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                scheduler.grpc.BackupListingRequest,
                scheduler.grpc.BackupListingResponse>(
                  this, METHODID_LIST_BACKUPS)))
          .addMethod(
            getTakeBackupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                scheduler.grpc.BackupRequest,
                scheduler.grpc.BackupResponse>(
                  this, METHODID_TAKE_BACKUP)))
          .addMethod(
            getRestoreBackupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                scheduler.grpc.RestoreRequest,
                scheduler.grpc.RestoreResponse>(
                  this, METHODID_RESTORE_BACKUP)))
          .addMethod(
            getListRestoreStatusesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                scheduler.grpc.RestoreStatusListingRequest,
                scheduler.grpc.RestoreStatusListingResponse>(
                  this, METHODID_LIST_RESTORE_STATUSES)))
          .build();
    }
  }

  /**
   */
  public static final class CassyStub extends io.grpc.stub.AbstractStub<CassyStub> {
    private CassyStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CassyStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CassyStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CassyStub(channel, callOptions);
    }

    /**
     */
    public void registerCluster(scheduler.grpc.ClusterRegistrationRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.ClusterRegistrationResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRegisterClusterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listClusters(scheduler.grpc.ClusterListingRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.ClusterListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListClustersMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listBackups(scheduler.grpc.BackupListingRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.BackupListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListBackupsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void takeBackup(scheduler.grpc.BackupRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.BackupResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTakeBackupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void restoreBackup(scheduler.grpc.RestoreRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.RestoreResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRestoreBackupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listRestoreStatuses(scheduler.grpc.RestoreStatusListingRequest request,
        io.grpc.stub.StreamObserver<scheduler.grpc.RestoreStatusListingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListRestoreStatusesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CassyBlockingStub extends io.grpc.stub.AbstractStub<CassyBlockingStub> {
    private CassyBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CassyBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CassyBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CassyBlockingStub(channel, callOptions);
    }

    /**
     */
    public scheduler.grpc.ClusterRegistrationResponse registerCluster(scheduler.grpc.ClusterRegistrationRequest request) {
      return blockingUnaryCall(
          getChannel(), getRegisterClusterMethod(), getCallOptions(), request);
    }

    /**
     */
    public scheduler.grpc.ClusterListingResponse listClusters(scheduler.grpc.ClusterListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListClustersMethod(), getCallOptions(), request);
    }

    /**
     */
    public scheduler.grpc.BackupListingResponse listBackups(scheduler.grpc.BackupListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListBackupsMethod(), getCallOptions(), request);
    }

    /**
     */
    public scheduler.grpc.BackupResponse takeBackup(scheduler.grpc.BackupRequest request) {
      return blockingUnaryCall(
          getChannel(), getTakeBackupMethod(), getCallOptions(), request);
    }

    /**
     */
    public scheduler.grpc.RestoreResponse restoreBackup(scheduler.grpc.RestoreRequest request) {
      return blockingUnaryCall(
          getChannel(), getRestoreBackupMethod(), getCallOptions(), request);
    }

    /**
     */
    public scheduler.grpc.RestoreStatusListingResponse listRestoreStatuses(scheduler.grpc.RestoreStatusListingRequest request) {
      return blockingUnaryCall(
          getChannel(), getListRestoreStatusesMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CassyFutureStub extends io.grpc.stub.AbstractStub<CassyFutureStub> {
    private CassyFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CassyFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CassyFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CassyFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<scheduler.grpc.ClusterRegistrationResponse> registerCluster(
        scheduler.grpc.ClusterRegistrationRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRegisterClusterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<scheduler.grpc.ClusterListingResponse> listClusters(
        scheduler.grpc.ClusterListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListClustersMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<scheduler.grpc.BackupListingResponse> listBackups(
        scheduler.grpc.BackupListingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListBackupsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<scheduler.grpc.BackupResponse> takeBackup(
        scheduler.grpc.BackupRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTakeBackupMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<scheduler.grpc.RestoreResponse> restoreBackup(
        scheduler.grpc.RestoreRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRestoreBackupMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<scheduler.grpc.RestoreStatusListingResponse> listRestoreStatuses(
        scheduler.grpc.RestoreStatusListingRequest request) {
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
    private final CassyImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CassyImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER_CLUSTER:
          serviceImpl.registerCluster((scheduler.grpc.ClusterRegistrationRequest) request,
              (io.grpc.stub.StreamObserver<scheduler.grpc.ClusterRegistrationResponse>) responseObserver);
          break;
        case METHODID_LIST_CLUSTERS:
          serviceImpl.listClusters((scheduler.grpc.ClusterListingRequest) request,
              (io.grpc.stub.StreamObserver<scheduler.grpc.ClusterListingResponse>) responseObserver);
          break;
        case METHODID_LIST_BACKUPS:
          serviceImpl.listBackups((scheduler.grpc.BackupListingRequest) request,
              (io.grpc.stub.StreamObserver<scheduler.grpc.BackupListingResponse>) responseObserver);
          break;
        case METHODID_TAKE_BACKUP:
          serviceImpl.takeBackup((scheduler.grpc.BackupRequest) request,
              (io.grpc.stub.StreamObserver<scheduler.grpc.BackupResponse>) responseObserver);
          break;
        case METHODID_RESTORE_BACKUP:
          serviceImpl.restoreBackup((scheduler.grpc.RestoreRequest) request,
              (io.grpc.stub.StreamObserver<scheduler.grpc.RestoreResponse>) responseObserver);
          break;
        case METHODID_LIST_RESTORE_STATUSES:
          serviceImpl.listRestoreStatuses((scheduler.grpc.RestoreStatusListingRequest) request,
              (io.grpc.stub.StreamObserver<scheduler.grpc.RestoreStatusListingResponse>) responseObserver);
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

  private static abstract class CassyBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CassyBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return scheduler.grpc.CassyProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Cassy");
    }
  }

  private static final class CassyFileDescriptorSupplier
      extends CassyBaseDescriptorSupplier {
    CassyFileDescriptorSupplier() {}
  }

  private static final class CassyMethodDescriptorSupplier
      extends CassyBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CassyMethodDescriptorSupplier(String methodName) {
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
      synchronized (CassyGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CassyFileDescriptorSupplier())
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
