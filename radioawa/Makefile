# RadioAWA Makefile
# Quick commands for development, production, and testing workflows

.PHONY: help dev prod test clean logs status rebuild security-scan security-backend security-frontend security-docker security-secrets security-install check-trivy

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
	@echo "Security Commands:"
	@echo "  make security-scan    - Run all security scans (dependencies + docker + secrets)"
	@echo "  make security-backend - Scan backend dependencies for vulnerabilities"
	@echo "  make security-frontend- Scan frontend dependencies for vulnerabilities"
	@echo "  make security-docker  - Scan Docker images for vulnerabilities"
	@echo "  make security-secrets - Scan for exposed secrets in code"
	@echo "  make security-install - Install Trivy security scanner"
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
	@docker compose build --no-cache
	@docker compose up -d
	@echo "✓ Development environment rebuilt and started"
	@echo "  Frontend: http://localhost:5171 (Vite dev server)"
	@echo "  Backend:  http://localhost:8081 (Spring Boot dev mode)"

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
	@docker compose -f docker-compose.prod.yml build --no-cache
	@docker compose -f docker-compose.prod.yml up -d
	@echo "✓ Production environment rebuilt and started"
	@echo "  Frontend: http://localhost:80 (Nginx)"
	@echo "  Backend:  http://localhost:8081 (Spring Boot production)"

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
# SECURITY COMMANDS
# ============================================================================

# Check if Trivy is installed
check-trivy:
	@command -v trivy >/dev/null 2>&1 || { \
		echo "❌ Trivy is not installed"; \
		echo ""; \
		echo "Install Trivy:"; \
		echo "  macOS:   brew install aquasecurity/trivy/trivy"; \
		echo "  Linux:   wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add - && \\"; \
		echo "           echo 'deb https://aquasecurity.github.io/trivy-repo/deb $$(lsb_release -sc) main' | sudo tee -a /etc/apt/sources.list.d/trivy.list && \\"; \
		echo "           sudo apt-get update && sudo apt-get install trivy"; \
		echo "  Or run:  make security-install"; \
		echo ""; \
		exit 1; \
	}

security-install:
	@echo "Installing Trivy security scanner..."
	@if [[ "$$(uname)" == "Darwin" ]]; then \
		if command -v brew >/dev/null 2>&1; then \
			brew install aquasecurity/trivy/trivy; \
		else \
			echo "❌ Homebrew not found. Please install from https://brew.sh"; \
			exit 1; \
		fi \
	elif [[ "$$(uname)" == "Linux" ]]; then \
		wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add - && \
		echo "deb https://aquasecurity.github.io/trivy-repo/deb $$(lsb_release -sc) main" | sudo tee -a /etc/apt/sources.list.d/trivy.list && \
		sudo apt-get update && \
		sudo apt-get install -y trivy; \
	else \
		echo "❌ Unsupported OS. Please install Trivy manually from https://github.com/aquasecurity/trivy"; \
		exit 1; \
	fi
	@echo "✓ Trivy installed successfully"

security-scan: check-trivy
	@echo "Running comprehensive security scans..."
	@echo ""
	@$(MAKE) security-backend
	@echo ""
	@$(MAKE) security-frontend
	@echo ""
	@$(MAKE) security-docker
	@echo ""
	@$(MAKE) security-secrets
	@echo ""
	@echo "✓ All security scans completed"

security-backend: check-trivy
	@echo "Scanning backend dependencies for vulnerabilities..."
	@trivy fs --scanners vuln --severity HIGH,CRITICAL ./backend
	@echo "✓ Backend dependency scan completed"

security-frontend: check-trivy
	@echo "Scanning frontend dependencies for vulnerabilities..."
	@echo ""
	@echo "Running npm audit..."
	@cd frontend && npm audit --audit-level=moderate || true
	@echo ""
	@echo "Running Trivy scan..."
	@trivy fs --scanners vuln --severity HIGH,CRITICAL ./frontend
	@echo "✓ Frontend dependency scan completed"

security-docker: check-trivy
	@echo "Scanning Docker images for vulnerabilities..."
	@echo ""
	@echo "Building Docker images for scanning..."
	@docker compose build --quiet
	@echo ""
	@echo "Scanning backend Docker image..."
	@trivy image --severity HIGH,CRITICAL radioawa-backend:dev || true
	@echo ""
	@echo "Scanning frontend Docker image..."
	@trivy image --severity HIGH,CRITICAL radioawa-frontend:dev || true
	@echo "✓ Docker image scan completed"

security-secrets: check-trivy
	@echo "Scanning for exposed secrets..."
	@trivy fs --scanners secret --severity HIGH,CRITICAL .
	@echo "✓ Secret scan completed"

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
	@docker compose build
	@docker compose up -d
	@echo "✓ Switched to development mode"
	@echo "  Frontend: http://localhost:5171 (Vite dev server)"

# Switch from development to production
switch-to-prod:
	@echo "Switching from development to production..."
	@docker compose down
	@docker compose -f docker-compose.prod.yml build
	@docker compose -f docker-compose.prod.yml up -d
	@echo "✓ Switched to production mode"
	@echo "  Frontend: http://localhost:80 (Nginx)"

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
