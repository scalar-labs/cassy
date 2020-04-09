package main

import (
	gw "./src/main/proto"
	"flag"
	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"golang.org/x/net/context"
	"google.golang.org/grpc"
	"net/http"
)

var (
	endpoint = flag.String("cassy server endpoint", "localhost:20051", "endpoint of Cassy server")
	mode     = flag.String("mode", "prod", "set to development or production mode")
)

func run() error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}
	err := gw.RegisterCassyHandlerFromEndpoint(ctx, mux, *endpoint, opts)
	if err != nil {
		return err
	}

	if *mode == "dev" {
		return http.ListenAndServe(":8090", allowCORS(mux))
	} else if *mode == "prod" {
		return http.ListenAndServe(":8080", mux)
	} else {
		glog.Fatal("Please select a valid mode")
	}
	return nil
}

func allowCORS(h http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		h.ServeHTTP(w, r)
	})
}

func main() {
	flag.Parse()
	defer glog.Flush()

	if err := run(); err != nil {
		glog.Fatal(err)
	}
}
