package main

import (
	"database/sql"
	"encoding/json"
	"net/http"
)

func ReadAllMarkers(res http.ResponseWriter, req *http.Request) {
	markers, err := GetMarkers()

	switch {
	case err == sql.ErrNoRows:
		http.NotFound(res, req)
		return
	case err != nil:
		http.Error(res, http.StatusText(500), http.StatusInternalServerError)
		return
	}

	json.NewEncoder(res).Encode(markers)
}

func CreateMarker(res http.ResponseWriter, req *http.Request) {
	marker, err := PutMarker(req)

	if err != nil {
		http.Error(res, http.StatusText(400), http.StatusBadRequest)
		return
	}

	json.NewEncoder(res).Encode(marker)
}
