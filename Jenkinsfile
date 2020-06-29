String tfProject     = "nia"
String tfEnvironment = "build1" // change for ptl, vp goes here
String tfComponent   = "nhais"  // this defines the application - nhais, mhs, 111 etc

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
        ECR_REPO_DIR = "nhais"
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/${ECR_REPO_DIR}:${BUILD_TAG}"
    }    

    stages {
        stage('Build and Test Locally') {
            stages {
                stage('Run Tests') {
                    steps {
                        script {
                            sh label: 'Create logs directory', script: 'mkdir -p logs build'
                            sh label: 'Build tests', script: 'docker build -t local/nhais-tests:${BUILD_TAG} -f Dockerfile.tests .'
                            sh label: 'Running tests', script: 'docker run -v /var/run/docker.sock:/var/run/docker.sock local/nhais-tests:${BUILD_TAG} gradle check -i'
                        }
                    }
                }
                stage('Build Docker Images') {
                    steps {
                        script {
                            sh label: 'Running docker build', script: 'docker build -t ${DOCKER_IMAGE} .'
                        }
                    }
                }
                stage('Push image') {
                    when {
                        expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
                    }
                    steps {
                        script {
                            if (ecrLogin(TF_STATE_BUCKET_REGION) != 0 )  { error("Docker login to ECR failed") }
                            String dockerPushCommand = "docker push ${DOCKER_IMAGE}"
                            if (sh (label: "Pushing image", script: dockerPushCommand, returnStatus: true) !=0) { error("Docker push image failed") }
                        }
                    }
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                    sh label: 'Copy nhais container logs', script: 'docker-compose logs nhais > logs/nhais.log'
//                     sh label: 'Create logs directory', script: 'mkdir logs'
//                     sh label: 'Copy nhais container logs', script: 'docker-compose logs nhais > logs/nhais.log'
                    // sh label: 'Copy dynamo container logs', script: 'docker-compose logs dynamodb-local > logs/outbound.log'
//                     sh label: 'Copy activemq logs', script: 'docker-compose logs activemq > logs/inbound.log'
                    // sh label: 'Copy nhais-tests logs', script: 'docker-compose logs nhais-tests > logs/nhais-tests.log'
//                     archiveArtifacts artifacts: 'logs/*.log', fingerprint: true
                    sh label: 'Stopping containers', script: 'docker-compose down -v'
                }
            }
        }
        stage('Deploy and Integration Test') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            options {
              lock("${tfProject}-${tfEnvironment}-${tfComponent}")
            }
            stages {
                stage('Deploy using Terraform') {
                    steps {
                        script {
                            String tfCodeBranch  = "develop"
                            String tfCodeRepo    = "https://github.com/nhsconnect/integration-adaptors"
                            String tfRegion      = "${TF_STATE_BUCKET_REGION}"
                            List<String> tfParams = []
                            Map<String,String> tfVariables = ["${tfComponent}_build_id": BUILD_TAG]
                            dir ("integration-adaptors") {
                              // Clone repository with terraform
                              git (branch: tfCodeBranch, url: tfCodeRepo)
                              dir ("terraform/aws") {
                                // Run TF Init
                                if (terraformInit(TF_STATE_BUCKET, tfProject, tfEnvironment, tfComponent, tfRegion) !=0) { error("Terraform init failed")}

                                // Run TF Plan
                                if (terraform('plan', TF_STATE_BUCKET, tfProject, tfEnvironment, tfComponent, tfRegion, tfVariables) !=0 ) { error("Terraform Plan failed")}

                                //Run TF Apply
                                if (terraform('apply', TF_STATE_BUCKET, tfProject, tfEnvironment, tfComponent, tfRegion, tfVariables) !=0 ) { error("Terraform Apply failed")}
                              } // dir terraform/aws
                            } // dir integration-adaptors
                        } //script
                    } //steps
                } //stage
                stage('Run integration tests') {
                    steps {
                        echo 'TODO run integration tests'
                        echo 'TODO archive test results'
                    }
                 } //stage
            } //stages
        } //stage Deploy and Test
        // stage('Run SonarQube analysis') {
        //     steps {
        //         runSonarQubeAnalysis()
        //     }
        // }
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

int terraformInit(String tfStateBucket, String project, String environment, String component, String region) {
  println("Terraform Init for Environment: ${environment} Component: ${component} in region: ${region} using bucket: ${tfStateBucket}")
  String command = "terraform init -backend-config='bucket=${tfStateBucket}' -backend-config='region=${region}' -backend-config='key=${project}-${environment}-${component}.tfstate' -input=false -no-color"
  dir("components/${component}") {
    return( sh( label: "Terraform Init", script: command, returnStatus: true))
  } // dir
} // int TerraformInit

int terraform(String action, String tfStateBucket, String project, String environment, String component, String region, Map<String, String> variables=[:], List<String> parameters=[]) {
    println("Running Terraform ${action} in region ${region} with: \n Project: ${project} \n Environment: ${environment} \n Component: ${component}")
    variablesMap = variables
    variablesMap.put('region',region)
    variablesMap.put('project', project)
    variablesMap.put('environment', environment)
    variablesMap.put('tf_state_bucket',tfStateBucket)
    parametersList = parameters
    parametersList.add("-no-color")
    //parametersList.add("-compact-warnings")  /TODO update terraform to have this working
    List<String> variableFilesList = [
      "-var-file=../../etc/global.tfvars",
      "-var-file=../../etc/${region}_${environment}.tfvars"
    ]
    if (action == "apply"|| action == "destroy") {parametersList.add("-auto-approve")}
    List<String> variablesList=variablesMap.collect { key, value -> "-var ${key}=${value}" }
    String command = "terraform ${action} ${variableFilesList.join(" ")} ${parametersList.join(" ")} ${variablesList.join(" ")} "
    dir("components/${component}") {
      return sh(label:"Terraform: "+action, script: command, returnStatus: true)
    } // dir
} // int Terraform

int ecrLogin(String aws_region) {
    String ecrCommand = "aws ecr get-login --region ${aws_region}"
    String dockerLogin = sh (label: "Getting Docker login from ECR", script: ecrCommand, returnStdout: true).replace("-e none","") // some parameters that AWS provides and docker does not recognize
    return sh(label: "Logging in with Docker", script: dockerLogin, returnStatus: true)
}
