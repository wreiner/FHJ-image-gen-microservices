docker run --rm --name openresty \
  -p 80:80 \
  -v /tmp/openresty/images:/fsimages \
  -v `pwd`:/etc/nginx/conf.d \
  192.168.2.102:5000/image-gen-cdn-openresty:0.1


