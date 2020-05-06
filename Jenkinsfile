// Options to switch off certain steps if needed
 Boolean runBuild           = true
 Boolean runTests        = false
 Boolean runIntegrationTest = false
 Boolean runComponentTest   = false
 Boolean runTerraform       = false
 Boolean runSonarQube       = true    


pipeline {
    agent{
        label 'jenkins-workers'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: "10")) // keep only last 10 builds
    }
    
    environment {
        BUILD_TAG = sh label: 'Generating build tag', returnStdout: true, script: 'python3 pipeline/scripts/tag.py ${GIT_BRANCH} ${BUILD_NUMBER} ${GIT_COMMIT}'
        BUILD_TAG_LOWER = sh label: 'Lowercase build tag', returnStdout: true, script: "echo -n ${BUILD_TAG} | tr '[:upper:]' '[:lower:]'"
        ENVIRONMENT_ID = "nhais-build"
    }    

    stages {
        stage('Build NHAIS') {
            stages {
                stage('Build') {
                    steps {
                        buildModules('Installing outbound dependencies')
                    }
                }
                // stage('Unit test') {
                //     steps {
                //         executeUnitTestsWithCoverage()
                //     }
                // }
                stage('Build image') {
                    steps {
                        script {
                            sh label: 'Building outbound image', script: "docker build -t local/nhais:${BUILD_TAG} ."
                            sh label: 'Building dyanamodb image', script: "docker build -t local/dynamodb-nhais -f Dockerfile.tests ."
                        }
                    }
                }
                stage('Push image') {
                    steps {
                        script {
                            sh label: 'Pushing outbound image', script: "packer build -color=false pipeline/packer/nhais.json"
                        }
                    }
                }
                stage('Run tests') {
                    steps {
                        script {
                            sh label: 'Running docker-compose build', script: 'docker-compose build --build-arg BUILD_TAG=${BUILD_TAG}'
                            sh label: 'Pushing tests', script: 'docker-compose run nhais-tests'
                        }
                    }
                }
            }
        }
        // TODO: ensure deploy and test steps have a dedicated worker
//         stage('Deploy Locally') {
//             when {
//                 expression { runTests }
//             }
//             steps {
//                 deployLocally()
//                 echo "Waiting 10 seconds for containers to start"
//                 sleep 10
//
//             }
//         }
//         stage('Test') {
//             when {
//               expression { runTests }
//             }
//             steps {
//                 executeTestsWithCoverage()
//             }
//         }
//         stage('Deploy NHAIS terraform') {
//             when {
//               expression { runTerraform }
//             }
//             steps {
//                 dir('pipeline/terraform/nhais') {
//                 }
//             }
//         }
        // TODO run integration tests against deployed service
        stage('Run SonarQube analysis') {
            steps {
                dir('.') {
                    runSonarQubeAnalysis()
                }
            }
        }
    }
}

void deployLocally() {
    sh label: 'Starting containers', script: 'docker-compose up rabbitmq dynamodb nhais'
}

void teardownLocally() {
    sh label: 'Stopping containers', script: 'docker-compose down'
    // TODO: cleanup images after publishing
}

void executeTestsWithCoverage() {
    sh label: 'Running all tests', script: 'docker-compose run nhais-tests'
    // TODO: copy build result xml out of container
    // TODO: archive container logs
    // TODO: publish test results
    // TODO: update Pipfile to run tests with coverage
}

void executeBuild() {
    sh label: 'Running docker-compose build', script: 'docker-compose build --build-arg BUILD_TAG=${BUILD_TAG_LOWER}'
}

void runSonarQubeAnalysis() {
    sh label: 'Running SonarQube analysis', script: "sonar-scanner -Dsonar.host.url=${SONAR_HOST} -Dsonar.login=${SONAR_TOKEN}"
}

void executeUnitTestsWithCoverage() {
    sh label: 'Running unit tests', script: 'pipenv run unittests'
//     sh label: 'Displaying code coverage report', script: 'pipenv run coverage-report'
//     sh label: 'Exporting code coverage report', script: 'pipenv run coverage-report-xml'
}

void buildModules(String action) {
    sh label: action, script: 'pipenv install --dev --deploy --ignore-pipfile'
}