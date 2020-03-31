package main

import (
	"flag"
	"net/http"

	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"golang.org/x/net/context"
	"google.golang.org/grpc"

	gw "./src/main/proto"
)

var (
	endpoint = flag.String("cassy server endpoint", "localhost:20051", "endpoint of Cassy server")
	devMode     = flag.Bool("dev", false, "enable this option to run as dev server")
)

func run() error {
	flag.Parse()
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}
	err := gw.RegisterCassyHandlerFromEndpoint(ctx, mux, *endpoint, opts)
	if err != nil {
		return err
	}

	if *devMode {
		return http.ListenAndServe(":8090", allowCORS(mux))
	} else {
		return http.ListenAndServe(":8080", mux)
	}
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
