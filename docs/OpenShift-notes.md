# OpenShift Notes

## Use with OpenShift Data Foundation and Noobaa

To get connection details use:

```bash
oc describe noobaa -n openshift-storage
```

Source: [ODF Documentation](https://access.redhat.com/documentation/de-de/red_hat_openshift_container_storage/4.8/html/managing_hybrid_and_multicloud_resources/accessing-the-multicloud-object-gateway-with-your-applications_rhocs#accessing-the-Multicloud-object-gateway-from-the-terminal_rhocs)

## Trust an unsecure registry in OpenShift

For development/testing purposes, you can trust an unsecure registry in OpenShift by adding this registry to the clusters image config.

```bash 
oc edit image.config cluster
```

Add the registry in the following way:

```bash
apiVersion: config.openshift.io/v1
kind: Image
metadata:
  name: cluster
  ...
spec:
  registrySources:
    insecureRegistries:
    - <your-regsitry>
```

You have to reboot the whole cluster to apply the changes.
