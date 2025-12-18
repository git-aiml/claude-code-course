#!/bin/bash

# Database Configuration
DB_NAME="radioawa"
DB_USER="radioawa"
DB_PASSWORD="radioawa_dev_password"
DB_HOST="localhost"
DB_PORT="5432"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to run psql command
run_psql() {
    PGPASSWORD=$DB_PASSWORD /opt/homebrew/opt/postgresql@16/bin/psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME "$@"
}

# Function to display menu
show_menu() {
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║     Radioawa Database CLI Tool        ║${NC}"
    echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
    echo ""
    echo -e "${GREEN}1.${NC}  List all tables"
    echo -e "${GREEN}2.${NC}  Show songs table"
    echo -e "${GREEN}3.${NC}  Show ratings table"
    echo -e "${GREEN}4.${NC}  Count all songs"
    echo -e "${GREEN}5.${NC}  Count all ratings"
    echo -e "${GREEN}6.${NC}  Show top rated songs"
    echo -e "${GREEN}7.${NC}  Show all ratings with song details"
    echo -e "${GREEN}8.${NC}  Interactive psql session"
    echo -e "${GREEN}9.${NC}  Run custom query"
    echo -e "${GREEN}10.${NC} Clear all ratings (⚠️  Caution)"
    echo -e "${GREEN}11.${NC} Clear all data (⚠️  Caution)"
    echo -e "${RED}0.${NC}  Exit"
    echo ""
    echo -n "Enter your choice: "
}

# Function to pause
pause() {
    echo ""
    read -p "Press Enter to continue..."
}

# Main loop
while true; do
    clear
    show_menu
    read choice

    case $choice in
        1)
            echo -e "\n${YELLOW}Listing all tables...${NC}\n"
            run_psql -c "\dt"
            pause
            ;;
        2)
            echo -e "\n${YELLOW}Songs table:${NC}\n"
            run_psql -c "SELECT id, artist, title, thumbs_up_count, thumbs_down_count, created_at FROM songs ORDER BY created_at DESC LIMIT 20;"
            pause
            ;;
        3)
            echo -e "\n${YELLOW}Ratings table:${NC}\n"
            run_psql -c "SELECT id, song_id, user_id, rating_type, created_at FROM ratings ORDER BY created_at DESC LIMIT 20;"
            pause
            ;;
        4)
            echo -e "\n${YELLOW}Total songs count:${NC}\n"
            run_psql -c "SELECT COUNT(*) as total_songs FROM songs;"
            pause
            ;;
        5)
            echo -e "\n${YELLOW}Total ratings count:${NC}\n"
            run_psql -c "SELECT COUNT(*) as total_ratings FROM ratings;"
            echo ""
            run_psql -c "SELECT rating_type, COUNT(*) as count FROM ratings GROUP BY rating_type;"
            pause
            ;;
        6)
            echo -e "\n${YELLOW}Top rated songs (by total ratings):${NC}\n"
            run_psql -c "SELECT artist, title, thumbs_up_count, thumbs_down_count, (thumbs_up_count + thumbs_down_count) as total_ratings FROM songs ORDER BY total_ratings DESC LIMIT 10;"
            pause
            ;;
        7)
            echo -e "\n${YELLOW}All ratings with song details:${NC}\n"
            run_psql -c "SELECT r.id, s.artist, s.title, r.rating_type, r.user_id, r.created_at FROM ratings r JOIN songs s ON r.song_id = s.id ORDER BY r.created_at DESC LIMIT 20;"
            pause
            ;;
        8)
            echo -e "\n${YELLOW}Starting interactive psql session...${NC}"
            echo -e "${YELLOW}Type 'exit' or '\q' to return to menu${NC}\n"
            sleep 2
            run_psql
            ;;
        9)
            echo -e "\n${YELLOW}Enter your SQL query (end with semicolon):${NC}"
            read -p "> " query
            if [ ! -z "$query" ]; then
                echo ""
                run_psql -c "$query"
            else
                echo -e "${RED}No query entered${NC}"
            fi
            pause
            ;;
        10)
            echo -e "\n${RED}⚠️  WARNING: This will delete ALL ratings!${NC}"
            read -p "Type 'YES' to confirm: " confirm
            if [ "$confirm" == "YES" ]; then
                run_psql -c "DELETE FROM ratings;"
                echo -e "${GREEN}All ratings cleared${NC}"
            else
                echo -e "${YELLOW}Cancelled${NC}"
            fi
            pause
            ;;
        11)
            echo -e "\n${RED}⚠️  WARNING: This will delete ALL songs AND ratings!${NC}"
            read -p "Type 'DELETE ALL DATA' to confirm: " confirm
            if [ "$confirm" == "DELETE ALL DATA" ]; then
                run_psql -c "DELETE FROM ratings; DELETE FROM songs;"
                echo -e "${GREEN}All data cleared${NC}"
            else
                echo -e "${YELLOW}Cancelled${NC}"
            fi
            pause
            ;;
        0)
            echo -e "\n${GREEN}Goodbye!${NC}\n"
            exit 0
            ;;
        *)
            echo -e "\n${RED}Invalid choice. Please try again.${NC}"
            sleep 2
            ;;
    esac
done
