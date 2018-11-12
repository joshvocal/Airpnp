package main

type Marker struct {
	Lat float32  `json:"author"`
	Lng  float32 `json:"price"`
}

func AllMarkers() ([]Marker, error) {
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
