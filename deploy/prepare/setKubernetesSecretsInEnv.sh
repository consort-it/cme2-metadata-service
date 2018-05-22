#!/bin/bash
# $1 ENV_PATH               .env relative path for docker
# $2 DEPLOYMENT_TARGET      like 'dev' or 'live' | can be used to identify scopes or namespaces
# $3 MICROSERVICE_NAME      name of microservice

ENV="${1?Need to set ENV_PATH}"
TARGET="${2?Need to set DEPLOYMENT_TARGET}"
NAME="${3?Need to set MICROSERVICE_NAME}"

NAMESPACE="default"
[ "$TARGET" == "dev" ] && NAMESPACE="${CON_IT_KUBERNETES_DEV_NAMESPACE?Need to set env CON_IT_KUBERNETES_DEV_NAMESPACE}"
[ "$TARGET" == "live" ] && NAMESPACE="${CON_IT_KUBERNETES_LIVE_NAMESPACE?Need to set env CON_IT_KUBERNETES_LIVE_NAMESPACE}"

: "${NAMESPACE?Need to set CON_IT_KUBERNETES_XXX_NAMESPACE according DEPLOYMENT_TARGET (2nd Argument)}"
SECRET="${NAME}-secret"

for row in $(kubectl get secret "${SECRET}-test" -o json -n "${NAMESPACE}" | jq -c '.data | to_entries[]'); do
  KEY=$(echo "${row}" | jq -r '.key')
  DECODED=$(echo "${row}" | jq -r '.value' | base64 --decode 2>/dev/null)
  echo "${KEY}=${DECODED}">>${ENV}
done
