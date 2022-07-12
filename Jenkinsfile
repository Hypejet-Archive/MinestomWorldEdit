pipeline {
  options { disableConcurrentBuilds() }
  environment {
    DOCKER_URL = 'docker.byteandbit.cloud'
  }

    agent {
    kubernetes {
      label 'podman'
      defaultContainer 'jenkins-slave'
      yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  component: ci
spec:
  securityContext:
    runAsUser: 0
    runAsGroup: 0
  serviceAccountName: jenkins
  volumes:
    - name: shared-data
      emptyDir: {}
  containers:
    - name: openjdk
      image: openjdk:17-jdk
      args:
        - sleep
        - "1000000"
      securityContext:
        privileged: true
      volumeMounts:
        - name: shared-data
          mountPath: /shared
    - name: homebrew
      image: homebrew/brew:3.4.1
      args:
        - sleep
        - "1000000"
      securityContext:
        privileged: true
      volumeMounts:
        - name: shared-data
          mountPath: /shared
"""
}
   }
    stages {
        stage('Build WorldEdit') {
          when {
            branch 'master'
          }
          steps {
            container('openjdk') {
              withCredentials([string(credentialsId: 'maven-username', variable: 'MAVEN_USERNAME')]) {
                withCredentials([string(credentialsId: 'maven-password', variable: 'MAVEN_PASSWORD')]) {
                    sh 'chmod +x gradlew'
                    sh './gradlew -PbyteandbitUsername=$MAVEN_USERNAME -PbyteandbitPassword=$MAVEN_PASSWORD clean build'
                    sh 'cp build/libs/MinestomWorldEdit-all.jar /shared/WorldEdit-b${BUILD_NUMBER}.jar'
                }
              }
            }

          }
        }
        stage('Release WorldEdit') {
            when {
              branch 'master'
            }
            steps {
              container('homebrew') {
                  sh 'brew install gh'
                  withCredentials([string(credentialsId: 'git-token', variable: 'GH_TOKEN')]) {
                      sh 'gh release create b$BUILD_NUMBER --title \'Build #' + env.BUILD_NUMBER + '\' /shared/WorldEdit-b${BUILD_NUMBER}.jar'
                  }
              }
            }
         }
    }
}