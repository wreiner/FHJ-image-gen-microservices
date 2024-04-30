# Postgres notes

## Access the database from inside the pod

To get an interactive shell in the postgres pod, run:

```bash

```

To access the database from inside the pod, run:

```bash
psql -U app -h 127.0.0.1 -W
```

The password can be found in the secret image-gen/postgresql01-app.

To get the generation requests, run:

```bash
select * from generation_request;
```
