package main

import (
	"database/sql"
	"encoding/json"
	"net/http"
)

func ReadAll(res http.ResponseWriter, req *http.Request) {
	markers, err := AllMarkers()

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
