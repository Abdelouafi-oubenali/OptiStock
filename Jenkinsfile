pipeline {
    agent any

    environment {
        SPRING_PROFILES_ACTIVE = 'test'
        SONAR_TOKEN = credentials('sonar-token')
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
                sh 'mvn clean compile'
            }
        }

        stage('Exécution des tests unitaires') {
            steps {
                sh 'mvn test jacoco:report'
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
                withSonarQubeEnv('SonarQube-Server') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=gestion-stock \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.login=${SONAR_TOKEN} \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
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
                sh 'mvn package -DskipTests'
                archiveArtifacts 'target/*.jar'
            }
        }
    }

    post {
        always {
            echo 'Nettoyage de l’environnement'
        }
        success {
            echo 'Construction et tests effectués avec succès'
        }
        failure {
            echo 'Une erreur s’est produite lors de l’exécution'
        }
    }
}
