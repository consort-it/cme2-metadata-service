#!/bin/bash
if [ "${1}" == "-h" ]; then
  usage
  exit
fi

if [[ -z "${1// }" ]]; then
  echo "ERROR: No secret set."
  usage
  exit 1
fi
SECRET="$1"

if [[ -z "${2// }" ]]; then
  NAMESPACE=$(kubectl config get-contexts --no-headers | tr -s ' ' | cut -d ' ' -f5)
  echo "No namespace set, using default: ${NAMESPACE}"
else
  NAMESPACE="$2"
fi

for row in $(kubectl get secret "${SECRET}" -o json -n "${NAMESPACE}" | jq -c '.data | to_entries[]'); do
  KEY=$(echo "${row}" | jq -r '.key')
  DECODED=$(echo "${row}" | jq -r '.value' | base64 --decode)
  echo "${KEY}=${DECODED}"
done