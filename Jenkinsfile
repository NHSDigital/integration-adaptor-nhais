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
        stage('Build & test NHAIS Common') {
            when {
              expression { runBuild }
            }
            steps {
                dir('common') {
                    buildModules('Installing nhais common dependencies')
                    executeUnitTestsWithCoverage()
                }
            }
        }
        stage('Deploy NHAIS') {
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