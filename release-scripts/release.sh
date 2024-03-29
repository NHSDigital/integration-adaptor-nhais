#!/bin/bash 

set -e

export BUILD_TAG=latest
export RELEASE_VERSION=1.4.2
cd ..

docker-compose build nhais

docker tag local/nhais:latest nhsdev/nia-nhais-adaptor:${RELEASE_VERSION}

if [ "$1" == "-y" ];
then
  echo "Tagging and pushing Docker image and git tag"
  docker push nhsdev/nia-nhais-adaptor:${RELEASE_VERSION}
  git tag -a ${RELEASE_VERSION} -m "Release ${RELEASE_VERSION}"
  git push origin ${RELEASE_VERSION}
fi

