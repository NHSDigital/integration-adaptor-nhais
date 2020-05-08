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
        stage('Build and Test Locally') {
            stages {
                stage('Build Docker Images') {
                    steps {
                        script {
                            sh label: 'Stopping running containers (preventative maintenance)', script: 'docker-compose down -v'
                            sh label: 'Running docker-compose build', script: 'docker-compose build --build-arg BUILD_TAG=${BUILD_TAG}'
                        }
                    }
                }
                stage('Deploy Locally') {
                    steps {
                        sh label: 'Starting containers', script: 'docker-compose up -d rabbitmq dynamodb nhais'
                        echo "Waiting 10 seconds for containers to start"
                        sleep 10
                        sh label: 'Show all running containers', script: 'docker ps'
                    }
                }
                stage('Run tests') {
                    steps {
                        sh label: 'Running tests', script: 'docker-compose run nhais-tests'
                    }
                    post {
                        always {
                            sh label: 'Copy test reports to folder', script: 'docker cp "$(docker ps -lq)":/usr/src/app/nhais/test-reports .'
                            junit '**/test-reports/*.xml'
                            sh label: 'Copy test coverage to folder', script: 'docker cp "$(docker ps -lq)":/usr/src/app/nhais/coverage.xml ./coverage.xml'
                            cobertura coberturaReportFile: '**/coverage.xml'
                        }
                    }
                }
                stage('Push image') {
                    steps {
                        script {
                            sh label: 'Pushing nhais image', script: "packer build -color=false pipeline/packer/nhais.json"
                        }
                    }
                }
            }
            post {
                always {
                    sh label: 'Create logs directory', script: 'mkdir logs'
                    sh label: 'Copy nhais container logs', script: 'docker-compose logs nhais > logs/nhais.log'
                    sh label: 'Copy dynamo container logs', script: 'docker-compose logs dynamodb > logs/outbound.log'
                    sh label: 'Copy rabbitmq logs', script: 'docker-compose logs rabbitmq > logs/inbound.log'
                    sh label: 'Copy nhais-tests logs', script: 'docker-compose logs nhais-tests > logs/nhais-tests.log'
                    archiveArtifacts artifacts: 'logs/*.log', fingerprint: true
                    sh label: 'Stopping containers', script: 'docker-compose down -v'
                }
            }
        }
        stage('Deploy and Integration Test') {
            stages {
                stage('Deploy using Terraform') {
                    steps {
                        echo 'TODO deploy NHAIS using terraform'
                    }
                }
                stage('Run integration tests') {
                    steps {
                        echo 'TODO run integration tests'
                        echo 'TODO archive test results'
                    }
                 }
            }

        }
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

            sh label: 'Stopping containers', script: 'docker-compose down -v'
            sh label: 'Remove all unused images not just dangling ones', script:'docker system prune --force'
            sh 'docker image rm -f $(docker images "*/*:*${BUILD_TAG}" -q) $(docker images "*/*/*:*${BUILD_TAG}" -q) || true'
        }
    }
}

void runSonarQubeAnalysis() {
    sh label: 'Running SonarQube analysis', script: "sonar-scanner -Dsonar.host.url=${SONAR_HOST} -Dsonar.login=${SONAR_TOKEN}"
}


