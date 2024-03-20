# Dev Containers

## RabbitMQ

```bash
docker run --rm -d --name image_gen_rabbitmq \
 -p 5672:5672 \
 -p 15672:15672 \
 rabbitmq:3-management
```

## PostgreSQL

```bash
docker run --rm -d --name image_gen_postgres \
 -p 5432:5432 \
 -v `pwd`/pgdata:/var/lib/postgresql/data \
 -e POSTGRES_USER=imagegen \
 -e POSTGRES_DB=imagegen \
 -e POSTGRES_PASSWORD=imagegen \
 postgres:15.4
```