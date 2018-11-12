package main

import (
	"database/sql"
	"fmt"
	_ "github.com/lib/pq"
)

var (
	DB *sql.DB
	err error
)

func init() {
	DB, err = sql.Open("postgres",
		"postgres://postgres:password@localhost/marker?sslmode=disable")
	if err != nil {
		panic(err)
	}

	if err = DB.Ping(); err != nil {
		panic(err)
	}

	fmt.Println("Postgres database successfully connected.")
}