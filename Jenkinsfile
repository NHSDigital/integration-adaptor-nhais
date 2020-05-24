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
                stage('Run Tests') {
                    steps {
                        script {
                            sh label: 'Build tests', script: 'docker build -t local/nhais-tests:${BUILD_TAG} -f Dockerfile.tests .'
                            sh label: 'Running tests', script: 'docker run -v /var/run/docker.sock:/var/run/docker.sock local.nhais-tests:${BUILD_TAG} gradle check'
                        }
                    }
                }
                stage('Build Docker Images') {
                    steps {
                        script {
                            sh label: 'Running docker build', script: 'docker build -t local/nhais:${BUILD_TAG} .'
                        }
                    }
                }
                stage('Push image') {
                    when {
                        expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
                    }
                    steps {
                        script {
                            sh label: 'Pushing nhais image', script: "packer build -color=false pipeline/packer/nhais-push.json"
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
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
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
                runSonarQubeAnalysis()
            }
        }
    }
    post {
        always {

            // sh label: 'Stopping containers', script: 'docker-compose down -v'
            sh label: 'Remove all unused images not just dangling ones', script:'docker system prune --force'
            sh 'docker image rm -f $(docker images "*/*:*${BUILD_TAG}" -q) $(docker images "*/*/*:*${BUILD_TAG}" -q) || true'
        }
    }
}

void runSonarQubeAnalysis() {
    sh label: 'Running SonarQube analysis', script: "sonar-scanner -Dsonar.host.url=${SONAR_HOST} -Dsonar.login=${SONAR_TOKEN}"
}


