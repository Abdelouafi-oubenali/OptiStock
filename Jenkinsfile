pipeline {
    agent any

    tools {
        maven 'Maven-3.9.5'
        jdk 'JDK-17'
    }

    stages {
        stage('get images') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/Abdelouafi-oubenali/OptiStock',
                        credentialsId: 'github-token'
                    ]]
                ])
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

        stage('SonarQube') {
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

        stage('create Docker') {
            steps {
                sh 'docker build -t gestion-stock:latest .'
            }
        }

        stage('run containers') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }

    post {
        success {
            echo '✅ Le code a été exécuté avec succès !'
        }
        failure {
            echo '❌ Erreur pendant l’exécution du pipeline.'
        }
    }
}
