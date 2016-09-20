#!/usr/bin/env bash

get-secret() {
  local prompt="${1}: "
  local rval=''
  while IFS= read -p "$prompt" -r -s -n 1 char; do
    if [[ $char == $'\0' ]]; then
      break
    fi
    prompt='*'
    rval+="$char"
  done
  echo "$rval"
}

if [ "$MAVEN_CENTRAL_DEPLOY_PASSWORD" = "" ]; then
  export MAVEN_CENTRAL_DEPLOY_PASSWORD=$(get-secret 'Maven Central Password')
  echo
else
  echo "Using MAVEN_CENTRAL_DEPLOY_PASSWORD from environment"
fi

if [ "$GPG_PASSPHRASE" = "" ]; then
  export GPG_PASSPHRASE=$(get-secret 'GPG Passphrase')
  echo
else
  echo "Using GPG_PASSPHRASE from environment"
fi


mvn -P release-central clean deploy


