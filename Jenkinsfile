pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    tools {
        jdk 'jdk-17'
        maven 'maven-3.8.5'
    }

    stages {
        stage('Récupération du code') {
            steps {
                git branch: 'main',
                url: 'https://github.com/Abdelouafi-oubenali/OptiStock',
                credentialsId: 'github-ssh'
            }
        }

        stage('Compilation') {
            steps {
                sh 'mvn clean compile -Dspring.profiles.active=test'
            }
        }

        stage('Tests unitaires') {
            steps {
                sh 'mvn test jacoco:report -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec'
                }
            }
        }

        stage('Création du package') {
            steps {
                sh 'mvn package -DskipTests -Dspring.profiles.active=test'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube-Server') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=gestion-stock -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
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

        stage('Création de l’image Docker') {
            steps {
                sh 'docker build -t gestion-stock:latest .'
            }
        }

        stage('Exécution des conteneurs') {
            steps {
                sh 'docker-compose up -d'
                script {
                    sleep(30)
                    sh 'curl -f http://localhost:8080/actuator/health || exit 1'
                }
            }
        }
    }

    post {
        always {
            sh 'docker-compose down || true'
        }
        success {
            echo 'Construction et tests effectués avec succès'
            emailext (
                subject: "SUCCÈS : Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "L’application a été construite et déployée avec succès.\n\nLien Jenkins : ${env.BUILD_URL}",
                to: "abdelouafi@admin.com"
            )
        }
        failure {
            echo ' Une erreur s’est produite lors de l’exécution'
            emailext (
                subject: "ÉCHEC : Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "Échec de la construction ou du déploiement de l’application.\n\nConsultez les journaux : ${env.BUILD_URL}",
                to: "abdelouafi@admin.com"
            )
        }
    }
}
