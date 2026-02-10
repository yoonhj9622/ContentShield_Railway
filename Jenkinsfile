pipeline {
    agent any
    
    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins Pipeline handles checkout automatically when configured with Git
                checkout scm
            }
        }
        
        stage('Docker Setup') {
            steps {
                echo 'Cleaning up existing containers...'
                sh 'docker compose down --volumes --remove-orphans || true'
            }
        }
        
        stage('Build & Deploy') {
            steps {
                echo 'Building and starting containers...'
                sh 'docker compose up -d --build'
            }
        }

        stage('Verify') {
            steps {
                sh 'docker ps'
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed. Please check logs.'
        }
    }
}
