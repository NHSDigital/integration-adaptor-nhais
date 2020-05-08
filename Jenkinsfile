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
//                             sh label: 'testing _clean_tag_element', script: 'echo -n ${BUILD_TAG}'
//                             sh label: 'testing _clean_tag_element', script: 'echo -n ${BUILD_TAG_LOWER}'
                                                           //Does outbound need to be change to nhais?
//                             sh label: 'Building outbound image', script: "docker build -t local/nhais:${BUILD_TAG} ."
//                             sh label: 'Building dyanamodb image', script: "docker build -t local/dynamodb-nhais -f Dockerfile.dynamodb ."
                            sh label: 'Stopping running containers (preventative maintenance)', script: 'docker-compose down -v'
                            sh label: 'Running docker-compose build', script: 'docker-compose build --build-arg BUILD_TAG=${BUILD_TAG}'

                            sh label: 'Starting containers', script: 'docker-compose up -d rabbitmq dynamodb nhais'
                            sh label: 'Show all running containers', script: 'docker ps'
                            sh label: 'testing _clean_tag_element', script: 'echo -n ${BUILD_TAG}'
                            sh label: 'testing _clean_tag_element', script: 'echo -n ${BUILD_TAG_LOWER}'
                            echo "Waiting 10 seconds for containers to start"
                            sleep 10

                            sh label: 'Running tests', script: 'docker-compose run nhais-tests'
                            sh label: 'Copy test reports to folder', script: 'docker cp "$(docker ps -lq)":/usr/src/app/nhais/test-reports .'
                            sh label: 'Copy test coverage to folder', script: 'docker cp "$(docker ps -lq)":/usr/src/app/nhais/coverage.xml ./coverage.xml'

                        }
                    }
                }
                stage('Deploy Locally') {
                    steps {
                        echo 'skip'
                    }
                }
                stage('Run tests') {
                    steps {
                        echo 'skip'
//                         script {
//
//
//                             //executeUnitTestsWithCoverage()
//                         }
                    }
                    post {
                        always {
                            sh label: 'Show all running containers', script: 'docker ps'
                            // Need to get the docker image name
                            sh label: 'Create logs directory', script: 'mkdir logs'
                            sh label: 'Copy nhais container logs', script: 'docker-compose logs nhais > logs/nhais.log'
                            sh label: 'Copy dynamo container logs', script: 'docker-compose logs dynamodb > logs/outbound.log'
                            sh label: 'Copy rabbitmq logs', script: 'docker-compose logs rabbitmq > logs/inbound.log'
                            sh label: 'Copy nhais-tests logs', script: 'docker-compose logs nhais-tests > logs/nhais-tests.log'
//                             sh label: 'Dump container logs to files', script: '''
//                                 mkdir logs
//                                 docker logs ${BUILD_TAG}_nhais_1 > logs/nhais.log
//                                 docker logs ${BUILD_TAG}_dynamodb_1 > logs/outbound.log
//                                 docker logs ${BUILD_TAG}_rabbitmq_1 > logs/inbound.log
//                                 docker logs ${BUILD_TAG}_nhais-tests > logs/nhais-tests.log
//                             '''
                            archiveArtifacts artifacts: 'logs/*.log', fingerprint: true
//                             sh label: 'Docker compose logs', script: 'docker-compose -f docker-compose.yml -p ${BUILD_TAG} logs'
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
    post {
        always {
            cobertura coberturaReportFile: '**/coverage.xml'
            junit '**/test-reports/*.xml'
            sh label: 'Stopping containers', script: 'docker-compose down -v'
            //sh label: 'Attempt to delete child images from image', script:'docker rmi $(docker images -q) -f'
            // Note that the * in the glob patterns doesn't match /
            sh label: 'Remove all unused images not just dangling ones', script:'docker system prune'
            sh 'docker image rm -f $(docker images "*/*:*${BUILD_TAG}" -q) $(docker images "*/*/*:*${BUILD_TAG}" -q) || true'
        }
    }
}

// void teardownLocally() {
//     sh label: 'Stopping containers', script: 'docker-compose down'
//     // TODO: cleanup images after publishing
//     // Note that the * in the glob patterns doesn't match /
//     sh 'docker image rm -f $(docker images "*/*:*${BUILD_TAG}" -q) $(docker images "*/*/*:*${BUILD_TAG}" -q) || true'
// }

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

void buildModules(String action) {
    sh label: action, script: 'pipenv install --dev --deploy --ignore-pipfile'
}
