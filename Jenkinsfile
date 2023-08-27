pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'gradlew ./gradlew clean build'
            }
        }
        stage("Complete"){
            steps{
                echo "Job complete!"
            }
        }
    }
}