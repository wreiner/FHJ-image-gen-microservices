#!/bin/sh
docker run -d --name image_gen_postgres \
 -p 5432:5432 \
 -v /var/lib/docker/volumes/image_gen/pgdata:/var/lib/postgresql/data \
 -e POSTGRES_USER=imagegen \
 -e POSTGRES_DB=imagegen \
 -e POSTGRES_PASSWORD=imagegen \
 postgres:15.4
