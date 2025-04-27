module "postgres" {
  source = "./pg"

  providers = {
    postgresql = postgresql
  }
}

module "keycloak" {
  source = "./kc"

  providers = {
    keycloak = keycloak
  }
}
