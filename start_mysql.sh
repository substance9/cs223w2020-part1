#!/bin/bash

docker container stop cs223_mysql
docker run --name cs223_mysql --rm -v $(pwd)/mysql_data:/var/lib/mysql -v $(pwd)/mysql_config:/etc/mysql/conf.d -p 10030:3306 -e MYSQL_ROOT_PASSWORD=password -d mysql:8.0.19 
