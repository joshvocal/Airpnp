package main

import (
	"net/http"
	"strconv"
)

type Marker struct {
	Lat float64  `json:"author"`
	Lng  float64 `json:"price"`
}

func GetMarkers() ([]Marker, error) {
	rows, err := DB.Query("SELECT * FROM marker")
	if err != nil {
		return nil, err
	}

	markers := make([]Marker, 0)
	for rows.Next() {
		marker := Marker{}

		err := rows.Scan(&marker.Lat, &marker.Lng)
		if err != nil {
			return nil, err
		}

		markers = append(markers, marker)
	}

	err = rows.Err()
	if err != nil {
		return nil, err
	}

	return markers, nil
}

func PutMarker(req *http.Request) (Marker,  error) {

	// Get form values
	marker := Marker{}
	latString := req.FormValue("lat")
	lngString := req.FormValue("lng")

	// Check that lat and lng are able to parse into floats
	var err error
	lat, err := strconv.ParseFloat(latString, 64)
	if err != nil {
		return marker, err
	}

	lng, err := strconv.ParseFloat(lngString, 64)
	if err != nil {
		return marker, err
	}

	// Assign valid lat and lng
	marker.Lat = float64(lat)
	marker.Lng = float64(lng)

	// Insert into the database
	_, err = DB.Exec("INSERT INTO marker (lat, lng) VALUES ($1, $2)", marker.Lat, marker.Lng)
	if err != nil {
		return marker, err
	}

	return marker, nil
}
