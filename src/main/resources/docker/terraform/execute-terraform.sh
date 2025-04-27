#!/bin/sh

export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin
export KC_HTTP_RELATIVE_PATH=auth
export KC_DB=dev-file

# Start kc.sh in the background
/opt/keycloak/bin/kc.sh start-dev &
KC_PID=$!

# Sleeping here since waiting for Keycloak is not easily possible
# Quarkus Augmentation: 12s
# Keycloak Startup: 12s
# The remaining time is buffer
sleep 40

export KEYCLOAK_URL=http://localhost:8180/auth
export KEYCLOAK_USER=admin
export KEYCLOAK_PASSWORD=admin

# Make sure the terraform state is clean
rm -rf terraform.tfstate*

echo "TFX: init"
terraform init || true

sleep 1

echo "TFX: First apply"
terraform apply -parallelism=1 -auto-approve || true
sleep 1

echo "TFX: Finished"
exit 0
