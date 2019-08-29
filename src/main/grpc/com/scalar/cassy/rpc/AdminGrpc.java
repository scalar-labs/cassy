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
public final class AdminGrpc {

  private AdminGrpc() {}

  public static final String SERVICE_NAME = "rpc.Admin";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<PauseRequest,
      com.google.protobuf.Empty> getPauseMethod;

  public static io.grpc.MethodDescriptor<PauseRequest,
      com.google.protobuf.Empty> getPauseMethod() {
    io.grpc.MethodDescriptor<PauseRequest, com.google.protobuf.Empty> getPauseMethod;
    if ((getPauseMethod = AdminGrpc.getPauseMethod) == null) {
      synchronized (AdminGrpc.class) {
        if ((getPauseMethod = AdminGrpc.getPauseMethod) == null) {
          AdminGrpc.getPauseMethod = getPauseMethod = 
              io.grpc.MethodDescriptor.<PauseRequest, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Admin", "Pause"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  PauseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new AdminMethodDescriptorSupplier("Pause"))
                  .build();
          }
        }
     }
     return getPauseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getUnpauseMethod;

  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getUnpauseMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getUnpauseMethod;
    if ((getUnpauseMethod = AdminGrpc.getUnpauseMethod) == null) {
      synchronized (AdminGrpc.class) {
        if ((getUnpauseMethod = AdminGrpc.getUnpauseMethod) == null) {
          AdminGrpc.getUnpauseMethod = getUnpauseMethod = 
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Admin", "Unpause"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new AdminMethodDescriptorSupplier("Unpause"))
                  .build();
          }
        }
     }
     return getUnpauseMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      StatsResponse> getStatsMethod;

  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      StatsResponse> getStatsMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, StatsResponse> getStatsMethod;
    if ((getStatsMethod = AdminGrpc.getStatsMethod) == null) {
      synchronized (AdminGrpc.class) {
        if ((getStatsMethod = AdminGrpc.getStatsMethod) == null) {
          AdminGrpc.getStatsMethod = getStatsMethod = 
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, StatsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "rpc.Admin", "Stats"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  StatsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AdminMethodDescriptorSupplier("Stats"))
                  .build();
          }
        }
     }
     return getStatsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AdminStub newStub(io.grpc.Channel channel) {
    return new AdminStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AdminBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new AdminBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AdminFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new AdminFutureStub(channel);
  }

  /**
   */
  public static abstract class AdminImplBase implements io.grpc.BindableService {

    /**
     */
    public void pause(PauseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getPauseMethod(), responseObserver);
    }

    /**
     */
    public void unpause(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getUnpauseMethod(), responseObserver);
    }

    /**
     */
    public void stats(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<StatsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getStatsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPauseMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                  PauseRequest,
                com.google.protobuf.Empty>(
                  this, METHODID_PAUSE)))
          .addMethod(
            getUnpauseMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.google.protobuf.Empty>(
                  this, METHODID_UNPAUSE)))
          .addMethod(
            getStatsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                  StatsResponse>(
                  this, METHODID_STATS)))
          .build();
    }
  }

  /**
   */
  public static final class AdminStub extends io.grpc.stub.AbstractStub<AdminStub> {
    private AdminStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AdminStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AdminStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AdminStub(channel, callOptions);
    }

    /**
     */
    public void pause(PauseRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPauseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void unpause(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUnpauseMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stats(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<StatsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStatsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AdminBlockingStub extends io.grpc.stub.AbstractStub<AdminBlockingStub> {
    private AdminBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AdminBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AdminBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AdminBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty pause(PauseRequest request) {
      return blockingUnaryCall(
          getChannel(), getPauseMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty unpause(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getUnpauseMethod(), getCallOptions(), request);
    }

    /**
     */
    public StatsResponse stats(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getStatsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AdminFutureStub extends io.grpc.stub.AbstractStub<AdminFutureStub> {
    private AdminFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AdminFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AdminFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AdminFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> pause(
        PauseRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPauseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> unpause(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getUnpauseMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<StatsResponse> stats(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getStatsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PAUSE = 0;
  private static final int METHODID_UNPAUSE = 1;
  private static final int METHODID_STATS = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AdminImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AdminImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PAUSE:
          serviceImpl.pause((PauseRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_UNPAUSE:
          serviceImpl.unpause((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_STATS:
          serviceImpl.stats((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<StatsResponse>) responseObserver);
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

  private static abstract class AdminBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AdminBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return CassandraBackupProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Admin");
    }
  }

  private static final class AdminFileDescriptorSupplier
      extends AdminBaseDescriptorSupplier {
    AdminFileDescriptorSupplier() {}
  }

  private static final class AdminMethodDescriptorSupplier
      extends AdminBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AdminMethodDescriptorSupplier(String methodName) {
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
      synchronized (AdminGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AdminFileDescriptorSupplier())
              .addMethod(getPauseMethod())
              .addMethod(getUnpauseMethod())
              .addMethod(getStatsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
