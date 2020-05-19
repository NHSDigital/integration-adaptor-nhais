String region = "eu-west-2"

pipeline {
  agent{
    label 'jenkins-workers'
  } //agent

  parameters {
    choice (name: "Project",     choices: ['NIA'],                           description: "Choose a project")
    choice (name: "Environment", choices: ['build', 'vp', 'ptl', 'account'], description: "Choose environment")
    choice (name: "Component",   choices: ['base', 'nhais', 'account'],      description: "Choose component")
    choice (name: "Action",      choices: ['plan', 'apply'],                 description: "Choose Terraform action")
    string (name: "Variables",   defaultValue: "",                           description: "Terrafrom variables, format: variable1=value,variable2=value")
    //string (name: "Git_Branch",  defaultValue: "develop",                    description: "Git branch")
    //string (name: "Git_Repo",    defaultValue: "https://github.com/nhsconnect/integration-adaptor-nhais.git", description: "Git Repo to clone")
  }

  stages {
    stage("Clone the repository") {
      steps {
        //git (branch: Git_Branch, url: Git_Repo)
        script {
          //println(sh(label: "Check the directory contents", script: "ls -laR", returnStdout: true))
          println("TODO Clone the branch from Git_Branch")
        } // script
      }  // steps
    } // stage Clone

    stage("Terraform Plan") {
      steps {
        dir("terraform/aws") {
          script {
            terraformInit()
            if (terraform('plan', Project, Environment, Component, region, [], [:])) { error("Terraform Plan failed")}
          } // script
        } //dir terraform/aws
      } // steps
    } // stage Terraform Plan
 
    stage("Terraform Apply") {
      when {
        expression {
          Action == "apply"
        }
      }
      steps {
        dir("terraform/aws") {
          script {
            if (terraform('apply', Project, Environment, Component, region, [], [:])) { error("Terraform Plan failed")}
          } // script
        } //dir terraform/aws
      } // steps
    } // stage Terraform Apply
  } // stages
} // pipeline


// int terraformScaffold(String action, String project, String environment, String component, Map<String, String> tfVariables, List<String> tfParams) {

// }

void terraformInit() {
  println("Terraform Init will go here")
}

int terraform(String action, String project, String, environment, String component, String region, List<String> parameters, Map<String, String> variables, Map<String, String> backendConfig=[:]) {
    println("Running Terraform ${action} in region ${region} with: \n Project: ${project} \n Environment: ${environment} \n Component: ${component}")
    List<String> variablesList=variables.collect { key, value -> "-var ${key}=${value}" }
    String command = "terraform ${action} ${parameters.join(" ")} ${variablesList.join(" ")} -var-file=../../etc/global.tfvars -var-file=../../etc/${region}_${environment}.tfvars"
    dir("components/${component}") {
      return sh(label:"Terraform: "+action, script: command, returnStatus=true)
    }
}