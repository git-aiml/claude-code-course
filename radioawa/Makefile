# RadioAWA Makefile
# Quick commands for development, production, and testing workflows

.PHONY: help dev prod test clean logs status rebuild

# Default target - show help
help:
	@echo "RadioAWA - Available Make Targets"
	@echo ""
	@echo "Development Commands:"
	@echo "  make dev              - Start development environment (Vite + Maven)"
	@echo "  make dev-build        - Rebuild and start development environment"
	@echo "  make dev-stop         - Stop development environment"
	@echo "  make dev-logs         - Show development logs (follow mode)"
	@echo "  make dev-logs-backend - Show backend logs only"
	@echo "  make dev-logs-frontend- Show frontend logs only"
	@echo "  make dev-restart      - Restart development environment"
	@echo ""
	@echo "Production Commands:"
	@echo "  make prod             - Start production environment (Nginx + JAR)"
	@echo "  make prod-build       - Rebuild and start production environment"
	@echo "  make prod-stop        - Stop production environment"
	@echo "  make prod-logs        - Show production logs (follow mode)"
	@echo "  make prod-restart     - Restart production environment"
	@echo ""
	@echo "Testing Commands:"
	@echo "  make test             - Run all tests (backend + frontend)"
	@echo "  make test-backend     - Run backend tests only (JUnit + Maven)"
	@echo "  make test-frontend    - Run frontend tests only (Vitest)"
	@echo "  make test-api         - Run API integration tests (requires running dev)"
	@echo ""
	@echo "Database Commands:"
	@echo "  make db-shell         - Connect to PostgreSQL shell"
	@echo "  make db-logs          - Show database logs"
	@echo "  make db-reset         - Reset database (WARNING: deletes all data)"
	@echo ""
	@echo "Utility Commands:"
	@echo "  make status           - Show status of all containers"
	@echo "  make clean            - Stop all containers and remove volumes"
	@echo "  make clean-all        - Clean + remove all Docker images"
	@echo "  make rebuild          - Full rebuild (clean + dev-build)"
	@echo "  make health           - Check health of all services"
	@echo ""

# ============================================================================
# DEVELOPMENT COMMANDS
# ============================================================================

dev:
	@echo "Starting development environment..."
	@docker compose up -d
	@echo "✓ Development environment started"
	@echo "  Frontend: http://localhost:5171 (Vite dev server)"
	@echo "  Backend:  http://localhost:8081 (Spring Boot dev mode)"
	@echo "  Database: localhost:5432"
	@echo ""
	@echo "Run 'make dev-logs' to view logs"

dev-build:
	@echo "Rebuilding development environment..."
	@docker compose down
	@echo "Removing cached images to prevent dev/prod conflicts..."
	@docker rmi -f radioawa-frontend radioawa-backend 2>/dev/null || true
	@docker compose build --no-cache
	@docker compose up -d
	@echo "✓ Development environment rebuilt and started"
	@echo "  Frontend: http://localhost:5171"
	@echo "  Backend:  http://localhost:8081"

dev-stop:
	@echo "Stopping development environment..."
	@docker compose down
	@echo "✓ Development environment stopped"

dev-logs:
	@echo "Showing development logs (Ctrl+C to exit)..."
	@docker compose logs -f

dev-logs-backend:
	@echo "Showing backend logs (Ctrl+C to exit)..."
	@docker compose logs -f backend

dev-logs-frontend:
	@echo "Showing frontend logs (Ctrl+C to exit)..."
	@docker compose logs -f frontend

dev-restart:
	@echo "Restarting development environment..."
	@docker compose restart
	@echo "✓ Development environment restarted"

# ============================================================================
# PRODUCTION COMMANDS
# ============================================================================

prod:
	@echo "Starting production environment..."
	@docker compose -f docker-compose.prod.yml up -d
	@echo "✓ Production environment started"
	@echo "  Frontend: http://localhost:80 (Nginx)"
	@echo "  Backend:  http://localhost:8081 (Spring Boot production)"
	@echo "  Database: localhost:5432"
	@echo ""
	@echo "Run 'make prod-logs' to view logs"

prod-build:
	@echo "Rebuilding production environment..."
	@docker compose -f docker-compose.prod.yml down
	@echo "Removing cached images to prevent dev/prod conflicts..."
	@docker rmi -f radioawa-frontend radioawa-backend 2>/dev/null || true
	@docker compose -f docker-compose.prod.yml build --no-cache
	@docker compose -f docker-compose.prod.yml up -d
	@echo "✓ Production environment rebuilt and started"
	@echo "  Frontend: http://localhost:80"
	@echo "  Backend:  http://localhost:8081"

prod-stop:
	@echo "Stopping production environment..."
	@docker compose -f docker-compose.prod.yml down
	@echo "✓ Production environment stopped"

prod-logs:
	@echo "Showing production logs (Ctrl+C to exit)..."
	@docker compose -f docker-compose.prod.yml logs -f

prod-restart:
	@echo "Restarting production environment..."
	@docker compose -f docker-compose.prod.yml restart
	@echo "✓ Production environment restarted"

# ============================================================================
# TESTING COMMANDS
# ============================================================================

test:
	@echo "Running all tests..."
	@$(MAKE) test-backend
	@$(MAKE) test-frontend
	@echo "✓ All tests completed"

test-backend:
	@echo "Running backend tests (JUnit + Maven)..."
	@docker compose exec backend mvn clean test
	@echo "✓ Backend tests completed"

test-frontend:
	@echo "Running frontend tests (Vitest)..."
	@docker compose exec frontend npm test
	@echo "✓ Frontend tests completed"

test-api:
	@echo "Running API integration tests..."
	@echo ""
	@echo "Testing Health Endpoint..."
	@curl -s http://localhost:8081/api/health | jq .
	@echo ""
	@echo "Testing Stations Endpoint..."
	@curl -s http://localhost:8081/api/stations | jq .
	@echo ""
	@echo "Testing Rating Submission..."
	@curl -s -X POST http://localhost:8081/api/ratings \
		-H "Content-Type: application/json" \
		-H "Origin: http://localhost:5171" \
		-d '{"artist":"Test Artist","title":"Test Song","userId":"test-'$$(date +%s)'","ratingType":"THUMBS_UP","stationCode":"ENGLISH"}' | jq .
	@echo ""
	@echo "✓ API integration tests completed"

# ============================================================================
# DATABASE COMMANDS
# ============================================================================

db-shell:
	@echo "Connecting to PostgreSQL shell..."
	@docker compose exec postgres psql -U radioawa -d radioawa

db-logs:
	@echo "Showing database logs (Ctrl+C to exit)..."
	@docker compose logs -f postgres

db-reset:
	@echo "WARNING: This will delete all data in the database!"
	@read -p "Are you sure? [y/N] " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		echo "Resetting database..."; \
		docker compose down -v; \
		docker compose up -d; \
		echo "✓ Database reset complete"; \
	else \
		echo "Cancelled"; \
	fi

# ============================================================================
# UTILITY COMMANDS
# ============================================================================

status:
	@echo "Container Status:"
	@docker compose ps
	@echo ""
	@echo "Docker Compose Services:"
	@docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"

clean:
	@echo "Cleaning up containers and volumes..."
	@docker compose down -v
	@docker compose -f docker-compose.prod.yml down -v
	@echo "✓ Cleanup complete"

clean-all: clean
	@echo "Removing all RadioAWA Docker images..."
	@docker images | grep radioawa | awk '{print $$3}' | xargs -r docker rmi -f
	@echo "✓ All images removed"

rebuild: clean dev-build
	@echo "✓ Full rebuild complete"

health:
	@echo "Checking service health..."
	@echo ""
	@echo "Backend Health:"
	@curl -s http://localhost:8081/api/health || echo "❌ Backend not responding"
	@echo ""
	@echo ""
	@echo "Frontend Health:"
	@curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:5171 || echo "❌ Frontend not responding"
	@echo ""
	@echo "Database Health:"
	@docker compose exec -T postgres pg_isready -U radioawa || echo "❌ Database not responding"
	@echo ""

# ============================================================================
# ADVANCED COMMANDS
# ============================================================================

# Switch from production to development
switch-to-dev:
	@echo "Switching from production to development..."
	@docker compose -f docker-compose.prod.yml down
	@echo "Removing cached images to prevent dev/prod conflicts..."
	@docker rmi -f radioawa-frontend radioawa-backend 2>/dev/null || true
	@docker compose build --no-cache
	@docker compose up -d
	@echo "✓ Switched to development mode"

# Switch from development to production
switch-to-prod:
	@echo "Switching from development to production..."
	@docker compose down
	@echo "Removing cached images to prevent dev/prod conflicts..."
	@docker rmi -f radioawa-frontend radioawa-backend 2>/dev/null || true
	@docker compose -f docker-compose.prod.yml build --no-cache
	@docker compose -f docker-compose.prod.yml up -d
	@echo "✓ Switched to production mode"

# Shell access to containers
shell-backend:
	@docker compose exec backend /bin/bash

shell-frontend:
	@docker compose exec frontend /bin/sh

shell-db:
	@docker compose exec postgres /bin/bash

# Monitor resource usage
monitor:
	@docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
