pipeline {
    agent any
    environment {
        JAVA_HOME="/usr/lib/jvm/openjdk-17"
    }
    stages {
        stage('Checkout SCM') {
            steps {
                sh 'export JAVA_HOME=/usr/lib/jvm/openjdk-17'
                sh 'java -version'
                sh 'echo "Checking out SCM.."'
                sh 'echo $GRADLE_HOME'
                sh 'echo $PATH'
                checkout scm
            }
        }
        stage('Set Permissions') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('Clean') {
            steps {
                sh './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
}
