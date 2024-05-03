docker run --rm --name openresty \
  -p 80:80 \
  -v /tmp/openresty/images:/fsimages \
  -v `pwd`:/etc/nginx/conf.d \
  restyluahttp:0.1

