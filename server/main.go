package main

import (
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
)

var port = ":3000"

func main() {
	router := mux.NewRouter()

	router.HandleFunc("/markers", ReadAllMarkers).Methods(http.MethodGet)
	router.HandleFunc("/marker", CreateMarker).Methods(http.MethodPut)

	fmt.Printf("Server listening on http://localhost%s\n", port)
	http.ListenAndServe(port, router)
}
