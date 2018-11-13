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
	router.Path("/markers/distance").Methods(http.MethodGet).HandlerFunc(ReadAllMarkersWithinDistance)
	router.Path("/marker").Methods(http.MethodPut).HandlerFunc(CreateMarker)
	router.Path("/marker").Methods(http.MethodDelete).HandlerFunc(DeleteMarker)

	fmt.Printf("Server listening on http://localhost%s\n", port)
	http.ListenAndServe(port, router)
}
