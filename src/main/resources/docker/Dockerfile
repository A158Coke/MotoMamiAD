FROM quay.io/keycloak/keycloak:26.1.4

# Switch to root user for installation and modification of system files
USER root

# Set the working directory for Keycloak
WORKDIR /opt/keycloak

# Copy terraform folder and scripts to the container
COPY ./terraform /opt/keycloak/terraform
COPY ./terraform/download-terraform.sh /opt/keycloak/download-terraform.sh
COPY ./terraform/execute-terraform.sh /opt/keycloak/execute-terraform.sh
# Install curl
RUN apk update && apk add --no-cache curl unzip

# Make the Terraform scripts executable
RUN chmod +x /opt/keycloak/terraform/*.sh
RUN chmod +x /opt/keycloak/download-terraform.sh
RUN chmod +x /opt/keycloak/execute-terraform.sh



# Set environment variables for Keycloak
ENV KC_DB=postgres
ENV KC_DB_URL=jdbc:postgresql://motomami-db:5432/keycloak
ENV KC_DB_USERNAME=keycloak
ENV KC_DB_PASSWORD=keycloak
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin

# Execute Terraform script and then start Keycloak
ENTRYPOINT ["/bin/sh", "-c", "/opt/keycloak/download-terraform.sh && /opt/keycloak/execute-terraform.sh && /opt/keycloak/bin/kc.sh start-dev"]
