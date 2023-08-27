pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                sh './gradlew clean'
            }
        }

        stage('Build') {
            steps {
                // Run the Gradle build using the wrapper
                sh './gradlew build'
            }
        }

        stage('Test') {
            steps {
                // Run tests (if any)
                sh './gradlew test'
            }
        }
    }
}
