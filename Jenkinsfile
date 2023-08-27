pipeline {
    agent any
    environment {
        JAVA_HOME = "/path/to/java17"
        PATH = "/path/to/java17/bin:$PATH"
    }
    stages {
        stage('Checkout SCM') {
            steps {
                sh 'export JAVA_HOME=/path/to/java17'
                sh 'export PATH=$JAVA_HOME/bin:$PATH'
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
