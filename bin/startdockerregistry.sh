mkdir -p /var/lib/libvirt/images/registry

podman run -d \
  -p 5000:5000 \
  --restart=always \
  --name registry \
  -v /var/lib/libvirt/images/registry:/var/lib/registry:Z \
  registry:2

podman run -d --restart always --name registry-ui \
  -p 80:80 \
  -e SINGLE_REGISTRY=true \
  -e REGISTRY_TITLE="Docker Registry UI" \
  -e DELETE_IMAGES=true \
  -e SHOW_CONTENT_DIGEST=true \
  -e NGINX_PROXY_PASS_URL="http://127.0.0.1:5000" \
  -e SHOW_CATALOG_NB_TAGS=true \
  -e CATALOG_MIN_BRANCHES=1 \
  -e CATALOG_MAX_BRANCHES=1 \
  -e TAGLIST_PAGE_SIZE=100 \
  -e REGISTRY_SECURED=false \
  -e CATALOG_ELEMENTS_LIMIT=1000 \
  joxit/docker-registry-ui:main