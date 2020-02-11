#!/bin/bash

docker run --name cs223_postgres --rm --volume=$(pwd)/postgres_data:/var/lib/postgresql/data -p 10020:5432 -e POSTGRES_PASSWORD=password -d postgres:12.1

