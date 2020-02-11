#!/bin/bash

echo "MySQL high concurrency: dropping old db"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -e "drop database cs223w2020_high_concurrency"

echo "MySQL high concurrency: creating new db"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -e "create database cs223w2020_high_concurrency"

echo "MySQL high concurrency: creating new table"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -D cs223w2020_high_concurrency  < ./inputs/schema/create.sql

echo "MySQL high concurrency: populating metadata"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -D cs223w2020_high_concurrency  < ./inputs/data/high_concurrency/metadata_mysql.sql