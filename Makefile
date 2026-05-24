# ====================
# CONFIG
# ====================

APP_COMPOSE = docker-compose.yaml
ELK_COMPOSE = elk-cluster/docker-compose.yaml

COMPOSE = docker compose


# ====================
# DEFAULT
# ====================
.PHONY: help
help:
	@echo "  ====================================="
	@echo "            PROJECT COMMANDS           "
	@echo "  ====================================="
	@echo "  make up          - start all services"
	@echo "  make ps          - lists the running containers"
	@echo "  make down        - stop all services"
	@echo "  make rebuild     - rebuild all services"
	@echo "  make logs        - show logs (app)"
	@echo "  make logs-elk    - show logs (ELK)"
	@echo "  make clean       - remove containers + volumes"
	@echo "  make run         - run frontend(for testing)"


# ====================
# START
# ====================
.PHONY: up
up:
	$(COMPOSE) -f $(APP_COMPOSE) up -d

	$(COMPOSE) -f $(ELK_COMPOSE) up -d


# ====================
# WATCH
# ====================
.PHONY: ps
ps:
	$(COMPOSE) -f $(APP_COMPOSE) ps

	$(COMPOSE) -f $(ELK_COMPOSE) ps

# ====================
# STOP
# ====================
.PHONY: down
down:
	$(COMPOSE) -f $(APP_COMPOSE) down

	$(COMPOSE) -f $(ELK_COMPOSE) down

# ====================
# REBUILD
# ====================
.PHONY: rebuild
rebuild:
	$(COMPOSE) -f $(APP_COMPOSE) up -d --build

	$(COMPOSE) -f $(ELK_COMPOSE) up -d --build

# ====================
# LOGS
# ====================
.PHONY: logs
logs:
	$(COMPOSE) -f $(APP_COMPOSE) logs -f
logs-elk:
	$(COMPOSE) -f $(ELK_COMPOSE) logs -f

# ====================
# CLEAN
# ====================
.PHONY: clean
clean:
	$(COMPOSE) -f $(APP_COMPOSE) down -v

	$(COMPOSE) -f $(ELK_COMPOSE) down -v

# ====================
# FRONTEND
# ====================
.PHONY: run
run:
	npm --prefix ./frontend/votcha-client run dev
