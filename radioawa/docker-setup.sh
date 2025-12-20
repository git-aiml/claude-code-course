#!/bin/bash
# RadioAwa Docker Setup Script
# This script helps you quickly set up the RadioAwa application with Docker

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "================================================"
echo "   RadioAwa Docker Setup"
echo "================================================"
echo ""

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

print_success "Docker and Docker Compose are installed"

# Ask user which environment to set up
echo ""
echo "Which environment do you want to set up?"
echo "1) Development (with hot reload)"
echo "2) Production (optimized builds)"
read -p "Enter your choice (1 or 2): " choice

case $choice in
    1)
        ENV="dev"
        COMPOSE_FILE="docker compose.yml"
        ENV_FILE=".env.docker.dev"
        print_info "Setting up Development environment..."
        ;;
    2)
        ENV="prod"
        COMPOSE_FILE="docker compose.prod.yml"
        ENV_FILE=".env.docker.prod"
        print_info "Setting up Production environment..."
        ;;
    *)
        print_error "Invalid choice. Exiting."
        exit 1
        ;;
esac

echo ""

# Copy environment file if not exists
if [ ! -f ".env" ]; then
    cp "$ENV_FILE" .env
    print_success "Created .env file from $ENV_FILE"

    if [ "$ENV" == "prod" ]; then
        print_info "IMPORTANT: Edit .env file and change default passwords before starting!"
        read -p "Press Enter after you've updated .env file..."
    fi
else
    print_info ".env file already exists, skipping copy"
fi

# Stop any running containers
print_info "Stopping any existing containers..."
docker compose -f "$COMPOSE_FILE" down 2>/dev/null || true
print_success "Stopped existing containers"

echo ""

# Build images
print_info "Building Docker images (this may take a few minutes)..."
if docker compose -f "$COMPOSE_FILE" build; then
    print_success "Images built successfully"
else
    print_error "Failed to build images"
    exit 1
fi

echo ""

# Start services
print_info "Starting services..."
if [ "$ENV" == "prod" ]; then
    docker compose -f "$COMPOSE_FILE" --env-file .env up -d
else
    docker compose -f "$COMPOSE_FILE" up -d
fi

print_success "Services started"

echo ""
echo "================================================"
echo "   Waiting for services to be healthy..."
echo "================================================"
echo ""

# Wait for services to be healthy
sleep 10

# Check service status
docker compose -f "$COMPOSE_FILE" ps

echo ""
echo "================================================"
echo "   Setup Complete!"
echo "================================================"
echo ""

if [ "$ENV" == "dev" ]; then
    print_success "Development environment is ready!"
    echo ""
    echo "Access your application:"
    echo "  Frontend: http://localhost:5171"
    echo "  Backend:  http://localhost:8081"
    echo "  Database: localhost:5432"
    echo ""
    echo "Useful commands:"
    echo "  View logs:        docker compose logs -f"
    echo "  Stop services:    docker compose down"
    echo "  Restart service:  docker compose restart backend"
else
    print_success "Production environment is ready!"
    echo ""
    echo "Access your application:"
    echo "  Application: http://localhost"
    echo ""
    echo "Useful commands:"
    echo "  View logs:        docker compose -f docker compose.prod.yml logs -f"
    echo "  Stop services:    docker compose -f docker compose.prod.yml down"
    echo "  Backup database:  docker compose -f docker compose.prod.yml exec postgres pg_dump -U radioawa radioawa > backup.sql"
fi

echo ""
echo "For more information, see DOCKER-DEPLOYMENT.md"
