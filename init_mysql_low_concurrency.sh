#!/bin/bash

echo "MySQL low concurrency: dropping old db"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -e "drop database cs223w2020_low_concurrency"

echo "MySQL low concurrency: creating new db"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -e "create database cs223w2020_low_concurrency"

echo "MySQL low concurrency: creating new table"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -D cs223w2020_low_concurrency  < ./inputs/schema/create.sql

echo "MySQL low concurrency: populating metadata"
mysql --host=127.0.0.1 --port=10030 --user=root --password=password -D cs223w2020_low_concurrency  < ./inputs/data/low_concurrency/metadata_mysql.sql