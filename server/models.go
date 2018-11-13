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

var RenderDistanceMeters = 100.0

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

func GetAllMarkersWithinDistance(req *http.Request) ([]Marker, error) {
	// Get form values
	markers := make([]Marker, 0)
	latOrig := req.FormValue("lat")
	lngOrig := req.FormValue("lng")

	// Check that lat and lng are able to parse into floats
	var err error
	lat1, err := strconv.ParseFloat(latOrig, 64)
	if err != nil {
		fmt.Println(err)
		return markers, err
	}

	lng1, err := strconv.ParseFloat(lngOrig, 64)
	if err != nil {
		fmt.Println(err)
		return markers, err
	}
	
	allMarkers, err := GetMarkers()
	if err != nil {
		fmt.Println(err)
		return nil, err
	}
	
	for _, marker := range allMarkers {
		if Distance(lat1, lng1, marker.Lat, marker.Lng) < RenderDistanceMeters {
			markers = append(markers, marker)
		} 
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
