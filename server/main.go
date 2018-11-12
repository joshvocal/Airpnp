package main

import (
	"fmt"
	"net/http"
)

var port = ":3000"

func main() {
	http.HandleFunc("/markers", ReadAll)

	fmt.Printf("Server listening on http://localhost%s\n", port)
	http.ListenAndServe(port, nil)
}
