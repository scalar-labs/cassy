#!/bin/sh

protoc -I/usr/local/include -I. \
    -I$GOPATH/src \
    -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
    --plugin=protoc-gen-go=$GOPATH/bin/protoc-gen-go \
    --go_out=plugins=grpc:. \
    ./src/main/proto/cassy.proto

protoc -I/usr/local/include -I. \
    -I$GOPATH/src \
    -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
    --plugin=protoc-gen-grpc-gateway=$GOPATH/bin/protoc-gen-grpc-gateway \
    --grpc-gateway_out=logtostderr=true:. \
    src/main/proto/cassy.proto
