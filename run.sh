#!/bin/bash

LOG_DIR="./logs"
rm -rf $LOG_DIR
mkdir -p $LOG_DIR

show_menu() {
	echo "==========================================="
    echo "   Welcome to myApp Console"
    echo "==========================================="
    echo "1. Run on local environment"
    echo "2. Run on docker environment"
    echo "3. View logs"
    echo "4. Stop docker environment"
    echo "5. Exit"
    echo "==========================================="

    read -p "Choose an option: " choice
}

kill_processes() {
	echo ">>> Killing existing processes..."
	# shellcheck disable=SC2046
	kill -9 $(lsof -t -i:8761)
	# shellcheck disable=SC2046
	kill -9 $(lsof -t -i:8080)
	# shellcheck disable=SC2046
	kill -9 $(lsof -t -i:8081)
	# shellcheck disable=SC2046
	kill -9 $(lsof -t -i:8082)
	# shellcheck disable=SC2046
	kill -9 $(lsof -t -i:8083)
	pkill -f "ng serve"
	sleep 10
}

run_local() {
	echo ">>> Starting LOCAL environment..."
	echo "(Logs will be saved in: $LOG_DIR/)"

	echo ">>> Backend (Spring Boot services)"
	kill_processes
	stop_docker

	mvn clean package -DskipTests -pl eureka-server -am
	java -jar eureka-server/target/*.jar> $LOG_DIR/eureka-server.log 2>&1 &
	mvn clean package -DskipTests -pl api-gateway -am
	java -jar api-gateway/target/*.jar> $LOG_DIR/api-gateway.log 2>&1 &
	mvn clean package -DskipTests -pl user-service -am
	java -jar user-service/target/*.jar> $LOG_DIR/user-service.log 2>&1 &
	mvn clean package -DskipTests -pl product-service -am
	java -jar product-service/target/*.jar> $LOG_DIR/product-service.log 2>&1 &
	mvn clean package -DskipTests -pl media-service -am
	java -jar media-service/target/*.jar> $LOG_DIR/media-service.log 2>&1 &

	echo ">>> Frontend (Angular)"
	# shellcheck disable=SC2164
	cd frontend
	ng serve > ../$LOG_DIR/frontend.log 2>&1 &
	# shellcheck disable=SC2103
	cd ..

	echo ">>> Services are running in background."
    echo ">>> Use option 3 to view logs."
}

run_docker() {
	echo ">>> Starting DOCKER environment..."
	kill_processes
	stop_docker
	docker compose build
	docker compose up -d
}

view_logs() {
	echo "==================================="
	echo "Available logs:"
	echo "1. Eureka Server"
	echo "2. API Gateway"
	echo "3. User Service"
	echo "4. Product Service"
	echo "5. Media Service"
	echo "6. Frontend"
	echo "7. All logs (multitail if installed)"
	echo "8. Back to main menu"
	echo "==================================="
	read -p "Choose a log to view: " log_choice

	case $log_choice in
	1) tail -f $LOG_DIR/eureka-server.log ;;
	2) tail -f $LOG_DIR/api-gateway.log ;;
	3) tail -f $LOG_DIR/user-service.log ;;
	4) tail -f $LOG_DIR/product-service.log ;;
	5) tail -f $LOG_DIR/media-service.log ;;
	6) tail -f $LOG_DIR/frontend.log ;;
	7) multitail $LOG_DIR/*.log || less +F $LOG_DIR/*.log ;;
	8) return ;;
	*) echo "Invalid choice!" ;;
	esac
}

stop_docker() {
	echo ">>> Stopping DOCKER environment..."
	docker compose down
}

while true; do
	show_menu
	case $choice in
		1) run_local ;;
		2) run_docker ;;
		3) view_logs ;;
		4) stop_docker ;;
		5) echo "Bye 👋"; exit 0 ;;
		*) echo "Invalid choice!"; sleep 1 ;;
	esac
done