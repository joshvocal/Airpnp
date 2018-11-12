# :hankey: Airpnp

Practice writing backend

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Linux

```
sudo -u postgres -i
systemctl start postgresql.service
```

### Installing

Start server

```
cd/server
go run *.go
```

Start client

```
cd/client
./gradlew installDebug
```

## Built With

* [PostgreSQL](https://www.postgresql.org/) - The database used for the server  
* [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java
* [mux](https://github.com/gorilla/mux) - A powerful URL router and dispatcher for golang.
