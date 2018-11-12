package main

import (
	"errors"
	"fmt"
	"net/http"
	"strconv"
)

type Marker struct {
	Id int `json"id"`
	Lat float64  `json:"lat"`
	Lng  float64 `json:"lng"`
}

func GetMarkers() ([]Marker, error) {
	rows, err := DB.Query("SELECT * FROM marker")
	if err != nil {
		fmt.Println(err)
		return nil, err
	}

	markers := make([]Marker, 0)
	for rows.Next() {
		marker := Marker{}

		err := rows.Scan(&marker.Id, &marker.Lat, &marker.Lng)
		if err != nil {
			fmt.Println(err)
			return nil, err
		}

		markers = append(markers, marker)
	}

	err = rows.Err()
	if err != nil {
		fmt.Println(err)
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
		fmt.Println(err)
		return marker, err
	}

	lng, err := strconv.ParseFloat(lngString, 64)
	if err != nil {
		fmt.Println(err)
		return marker, err
	}

	// Assign valid lat and lng
	marker.Lat = float64(lat)
	marker.Lng = float64(lng)

	// Insert into the database
	_, err = DB.Exec("INSERT INTO marker (lat, lng) VALUES ($1, $2)", marker.Lat, marker.Lng)
	if err != nil {
		fmt.Println(err)
		return marker, err
	}

	return marker, nil
}

func DeleteOneMarker(req * http.Request) error {
	lat := req.FormValue("lat")
	lng := req.FormValue("lng")

	if lat == "" || lng == "" {
		return errors.New("400")
	}

	_, err := DB.Exec("DELETE FROM marker WHERE lat = $1 AND lng = $2;", lat, lng)
	if err != nil {
		return errors.New("500. Internal Server Error")
	}

	return nil
}
