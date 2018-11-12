package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

func ReadAllMarkers(res http.ResponseWriter, req *http.Request) {
	markers, err := GetMarkers()

	if err != nil {
		fmt.Println(err)
		http.Error(res, http.StatusText(400), http.StatusBadRequest)
		return
	}

	json.NewEncoder(res).Encode(markers)
}

func CreateMarker(res http.ResponseWriter, req *http.Request) {
	marker, err := PutMarker(req)

	if err != nil {
		fmt.Println(err)
		http.Error(res, http.StatusText(400), http.StatusBadRequest)
		return
	}

	json.NewEncoder(res).Encode(marker)
}

func DeleteMarker(res http.ResponseWriter, req *http.Request) {
	err := DeleteOneMarker(req)

	if err != nil {
		fmt.Println(err)
		http.Error(res, http.StatusText(400), http.StatusBadRequest)
	}
}
