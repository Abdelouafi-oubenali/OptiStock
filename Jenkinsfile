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

        stage('Création du package') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts 'target/*.jar'
            }
        }

        /*
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
        */

        /*
        stage('Création de l’image Docker') {
            steps {
                script {
                    docker.build("gestion-stock:${env.BUILD_ID}")
                }
            }
        }

        stage('Exécution des conteneurs Docker') {
            steps {
                sh 'docker-compose up -d'
                script {
                    sleep(30)
                    sh 'curl -f http://localhost:8080/actuator/health || exit 1'
                }
            }
        }
        */
    }

    post {
        always {
            echo 'Nettoyage de l’environnement'
            // sh 'docker-compose down || true'  // À décommenter lors de l’intégration de Docker
        }
        success {
            echo ' Construction et tests effectués avec succès'
            /*
            emailext (
                subject: "SUCCÈS : Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "L’application a été construite et testée avec succès.\n\nLien Jenkins : ${env.BUILD_URL}",
                to: "abdelouafi@admin.com"
            )
            */
        }
        failure {
            echo ' Une erreur s’est produite lors de l’exécution'
            /*
            emailext (
                subject: "ÉCHEC : Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: "Échec de la construction ou des tests de l’application.\n\nConsultez les journaux : ${env.BUILD_URL}",
                to: "abdelouafi@admin.com"
            )
            */
        }
    }
}
