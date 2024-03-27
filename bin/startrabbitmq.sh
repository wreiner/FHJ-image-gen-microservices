#!/bin/sh
docker run -d --name image_gen_rabbitmq \
 -p 5672:5672 \
 -p 15672:15672 \
 -v /var/lib/docker/volumes/image_gen/rabbitmq/data:/var/lib/rabbitmq \
 --hostname image_gen_rabbitmq \
 rabbitmq:3-management
