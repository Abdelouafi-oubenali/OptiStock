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


        stage('Verify JaCoCo Report') {
                    steps {
                        script {
                            sh '''
                                echo "=== Vérification du rapport JaCoCo ==="
                                ls -la target/site/jacoco/ || echo "Dossier jacoco n'existe pas!"
                                if [ -f target/site/jacoco/jacoco.xml ]; then
                                    echo "✅ jacoco.xml trouvé"
                                    echo "Taille du fichier:"
                                    du -h target/site/jacoco/jacoco.xml
                                    echo "Premières lignes:"
                                    head -30 target/site/jacoco/jacoco.xml
                                else
                                    echo "❌ jacoco.xml NOT FOUND!"
                                    exit 1
                                fi
                            '''
                        }
                    }
                }

        stage('Analyse SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('SonarQube-Server') {
                      sh '''#!/bin/bash
                          mvn sonar:sonar \
                            -Dsonar.projectKey=gestion-stock \
                            -Dsonar.login=$SONAR_TOKEN \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.junit.reportPaths=target/surefire-reports \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.java.coveragePlugin=jacoco \
                            -Dsonar.java.test.binaries=target/test-classes \
                            -Dsonar.host.url=http://sonarqube:9000/ \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.tests=src/test/java
                      '''

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
