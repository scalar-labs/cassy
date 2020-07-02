package main

import (
	"flag"
	"net/http"
	"strings"

	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"golang.org/x/net/context"
	"google.golang.org/grpc"

	gw "./src/main/proto"
)

var (
	endpoint = flag.String("cassy server endpoint", "localhost:20051", "endpoint of Cassy server")
	mode     = flag.String("mode", "prod",
		"prod: runs the HTTP server on port 8080. Recommended for most users.\n " +
		"dev: runs the HTTP server on port 8090 to avoid conflict with dev servers.\n")
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
		if r.Method == "OPTIONS" && r.Header.Get("Access-Control-Request-Method") != "" {
			preflightHandler(w, r)
			return
		}
		h.ServeHTTP(w, r)
	})
}

// We need to implement a handler for OPTIONS requests that are done in preflight,
// because grpc-gateway cannot handle complex requests from cross origin
func preflightHandler(w http.ResponseWriter, r *http.Request) {
	headers := []string{"Content-Type", "Accept", "Authorization"}
	w.Header().Set("Access-Control-Allow-Headers", strings.Join(headers, ","))
	methods := []string{"GET", "HEAD", "POST", "PUT", "DELETE"}
	w.Header().Set("Access-Control-Allow-Methods", strings.Join(methods, ","))
	glog.Infof("preflight request for %s", r.URL.Path)
}

func main() {
	flag.Parse()
	defer glog.Flush()

	if err := run(); err != nil {
		glog.Fatal(err)
	}
}
