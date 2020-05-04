// Options to switch off certain steps if needed
 Boolean runBuildCommon     = false
 Boolean runBuild           = false
 Boolean runUnitTest        = false
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
        stage('Build & test common') {
            when {
              expression { runBuildCommon }
            }
            steps {
                dir('common') {
                    buildModules('Installing common dependencies')
                    executeUnitTestsWithCoverage()
                }
            }
        }
        stage('Unit test') {
            when {
              expression { runUnitTest }
            }
            steps {
                dir('******') {
                executeUnitTestsWithCoverage()
               }
            }
        }
        stage('Integration Tests') {
            when {
              expression { runIntegrationTest }
            }
            steps {
                dir('integration-tests/integration_tests') {
                sh label: 'Installing integration test dependencies', script: 'pipenv install --dev --deploy --ignore-pipfile'

                                    // Wait for MHS load balancers to have healthy targets
                                    dir('../../pipeline/scripts/check-target-group-health') {
                                        sh script: 'pipenv install'

                                        timeout(13) {
                                            waitUntil {
                                                script {
                                                    def r = sh script: 'sleep 10; AWS_DEFAULT_REGION=eu-west-2 pipenv run main ${MHS_OUTBOUND_TARGET_GROUP} ${MHS_INBOUND_TARGET_GROUP}  ${MHS_ROUTE_TARGET_GROUP}', returnStatus: true
                                                    return (r == 0);
                                                }
                                            }
                                        }
                                    }
                sh label: 'Running integration tests', script: 'pipenv run inttests'
                                }
                            }
                        }
                    }
                }
            }
        }
        stage('Deploy NHAIS terraform') {
            when {
              expression { runTerraform }
            }
            steps {
                dir('pipeline/terraform/nhais') {
                }
            }
        }
        stage('Run SonarQube analysis') {
            when {
              expression { runSonarQube }
            } 
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