# OpenShift Notes

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
