#!/bin/bash 

LIGHT_GREEN='\033[1;32m' 
NC='\033[0m' 
echo -e "${LIGHT_GREEN}Exporting environment variables${NC}" 

source ../export-env-vars.sh

echo -e "${LIGHT_GREEN}Stopping running containers${NC}" 
docker-compose -f docker-compose.yml stop; 

echo -e "${LIGHT_GREEN}Build and starting containers${NC}" 
docker-compose -f docker-compose.yml up -d --no-build
