pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = 'test'
    }

    tools {
        jdk 'jdk-21'
        maven 'maven-3.8.5'
    }

    stages {
        stage('Récupération du code source') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Abdelouafi-oubenali/OptiStock',
                    credentialsId: 'github-ssh'
            }
        }

        stage('Compilation du projet') {
            steps {
                sh 'mvn clean compile -B'
            }
        }

        stage('Exécution des tests unitaires') {
            steps {
                sh 'mvn test jacoco:report -B'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('SonarQube-Server') {
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=gestion-stock \
                            -Dsonar.host.url=http://sonarqube:9000 \
                            -Dsonar.login=${SONAR_TOKEN} \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.tests=src/test/java
                        """
                    }
                }
            }
        }

        stage('Vérification de la qualité du code') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Création du package') {
            steps {
                sh 'mvn package -DskipTests -B'
                archiveArtifacts 'target/*.jar'
            }
        }
    }

    post {
        always {
            echo "Nettoyage de l'environnement"
        }
        success {
            echo "Construction et tests effectués avec succès"
        }
        failure {
            echo "Une erreur s'est produite lors de l'exécution"
        }
    }
}
