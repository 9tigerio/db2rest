#!/bin/bash

# Start services
docker-compose up -d

# Run API interaction script
chmod +x api-interaction.sh
./api-interaction.sh

# Optional: Keep services running
docker-compose logs -f
