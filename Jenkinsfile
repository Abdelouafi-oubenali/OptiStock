pipeline {
    agent any

    tools {
        maven 'Maven-3.9.5'
        jdk 'JDK-17'
    }

    stages {
        stage('get images') {
            steps {
                git branch: 'main', url: 'https://github.com/ton-repo/gestion-stock.git'
            }
        }

        stage('build') {
            steps {
                sh 'mvn clean package -Dmaven.test.failure.ignore=true'
            }
        }

        stage('test') {
            steps {
                sh 'mvn test jacoco:report'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec'
                }
            }
        }

        stage(' SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube-Server') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=gestion-stock"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage(' create Docker') {
            steps {
                sh 'docker build -t gestion-stock:latest .'
            }
        }

        stage(' run continers') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }

    post {
        success {
            echo 'le code run avec sucise'
        }
        failure {
            echo 'errore dons exiction '
        }
    }
}
