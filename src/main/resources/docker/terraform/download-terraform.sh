#!/bin/sh

TERRAFORM_VERSION=1.8.5

(
 rm -rf download &&
 mkdir download &&
 cd download &&
 wget https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip &&
 unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip &&
 rm terraform_${TERRAFORM_VERSION}_linux_amd64.zip LICENSE.txt &&
 cd ..
 )
