FROM openresty/openresty:1.25.3.1-3-bullseye-fat

RUN opm get ledgetech/lua-resty-http
RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d
