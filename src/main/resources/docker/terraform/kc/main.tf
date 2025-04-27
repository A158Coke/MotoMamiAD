terraform {
  required_providers {
    keycloak = {
      source  = "mrparkers/keycloak"
      version = ">= 3.0.0"
    }
  }
}

resource "keycloak_realm" "motomami_realm" {
  realm   = "motomami"
  enabled = true
}