pipeline {
    agent{
        label 'jenkins-workers'
    }
    environment {
        BUILD_TAG = sh label: 'Generating build tag', returnStdout: true, script: 'python3 nhais/pipeline/scripts/tag.py ${GIT_BRANCH} ${BUILD_NUMBER} ${GIT_COMMIT}'
        BUILD_TAG_LOWER = sh label: 'Lowercase build tag', returnStdout: true, script: "echo -n ${BUILD_TAG} | tr '[:upper:]' '[:lower:]'"
        ENVIRONMENT_ID = "build"
    }   

    stages {
        stage('Run SonarQube analysis') {
            steps {
                dir('nhais') {
                    runSonarQubeAnalysis()
                }
            }
        }
    }
}

void runSonarQubeAnalysis() {
    sh label: 'Running SonarQube analysis', script: "sonar-scanner -Dsonar.host.url=${SONAR_HOST} -Dsonar.login=${SONAR_TOKEN}"
}