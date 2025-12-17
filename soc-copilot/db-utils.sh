#!/bin/bash
# Database utility commands

PSQL_PATH="/opt/homebrew/opt/postgresql@16/bin/psql"
DB_NAME="aisoc_copilot"
DB_USER="sujit"

case "$1" in
    connect)
        echo "Connecting to $DB_NAME database..."
        echo "Type \q to exit, \? for help"
        echo ""
        $PSQL_PATH -U $DB_USER -d $DB_NAME
        ;;
    tables)
        echo "Tables in $DB_NAME:"
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "\dt"
        ;;
    indexes)
        echo "Indexes in $DB_NAME:"
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "\di"
        ;;
    describe)
        if [ -z "$2" ]; then
            echo "Usage: ./db-utils.sh describe <table_name>"
            echo "Example: ./db-utils.sh describe alerts"
            exit 1
        fi
        echo "Description of table '$2':"
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "\d $2"
        ;;
    schema)
        echo "Database schema (all tables with details):"
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "\dt+"
        ;;
    query)
        if [ -z "$2" ]; then
            echo "Usage: ./db-utils.sh query \"SELECT * FROM table_name;\""
            exit 1
        fi
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "$2"
        ;;
    count)
        if [ -z "$2" ]; then
            echo "Usage: ./db-utils.sh count <table_name>"
            echo "Example: ./db-utils.sh count alerts"
            exit 1
        fi
        echo "Counting rows in table '$2':"
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "SELECT COUNT(*) as row_count FROM $2;"
        ;;
    info)
        echo "Database Information:"
        echo "===================="
        echo ""
        echo "Database: $DB_NAME"
        echo "User: $DB_USER"
        echo ""
        echo "Tables:"
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "\dt"
        echo ""
        echo "Indexes:"
        $PSQL_PATH -U $DB_USER -d $DB_NAME -c "\di"
        ;;
    status)
        echo "PostgreSQL service status:"
        brew services list | grep postgres
        ;;
    restart)
        echo "Restarting PostgreSQL service..."
        brew services restart postgresql@16
        ;;
    backup)
        BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
        echo "Creating backup: $BACKUP_FILE"
        /opt/homebrew/opt/postgresql@16/bin/pg_dump -U $DB_USER $DB_NAME > "$BACKUP_FILE"
        echo "Backup created: $BACKUP_FILE"
        ;;
    *)
        echo "Database Utility Commands"
        echo "========================="
        echo ""
        echo "Usage: ./db-utils.sh [command] [args]"
        echo ""
        echo "Connection Commands:"
        echo "  connect              - Connect to the database interactively"
        echo ""
        echo "Inspection Commands:"
        echo "  tables               - List all tables"
        echo "  indexes              - List all indexes"
        echo "  schema               - Show detailed schema information"
        echo "  describe <table>     - Describe table structure"
        echo "  count <table>        - Count rows in a table"
        echo "  info                 - Show comprehensive database info"
        echo ""
        echo "Query Commands:"
        echo "  query \"<SQL>\"        - Execute a custom SQL query"
        echo ""
        echo "Service Commands:"
        echo "  status               - Check PostgreSQL service status"
        echo "  restart              - Restart PostgreSQL service"
        echo ""
        echo "Backup Commands:"
        echo "  backup               - Create a database backup"
        echo ""
        echo "Examples:"
        echo "  ./db-utils.sh connect"
        echo "  ./db-utils.sh describe alerts"
        echo "  ./db-utils.sh query \"SELECT * FROM alerts LIMIT 5;\""
        echo "  ./db-utils.sh count alerts"
        echo ""
        ;;
esac
