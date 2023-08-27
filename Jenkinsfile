pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'chmod +x gradlew ./gradlew clean build'
            }
        }
        stage("Complete"){
            steps{
                echo "Job complete!"
            }
        }
    }
}
