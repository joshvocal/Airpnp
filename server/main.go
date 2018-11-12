package main

import (
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
)

var port = ":3000"

func main() {
	router := mux.NewRouter()

	router.Path("/markers").Methods(http.MethodGet).HandlerFunc(ReadAllMarkers)
	router.Path("/marker").Methods(http.MethodPut).HandlerFunc(CreateMarker)

	fmt.Printf("Server listening on http://localhost%s\n", port)
	http.ListenAndServe(port, router)
}
