pipeline {
    agent{
        label 'jenkins-workers'
    }

    stages {
        stage('Run SonarQube analysis') {
            steps {
                dir('.') {
                    runSonarQubeAnalysis()
                }
            }
        }
    }
}

void runSonarQubeAnalysis() {
    sh label: 'Running SonarQube analysis', script: "sonar-scanner -Dsonar.host.url=${SONAR_HOST} -Dsonar.login=${SONAR_TOKEN}"
}