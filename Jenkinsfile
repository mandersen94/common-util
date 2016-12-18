node {

     def mvnHome

    stage('Prepare') {
      //'git clone https://github.com/mandersen94/common-util.git'
      git 'https://github.com/mandersen94/common-util.git'
      mvnHome = tool 'M3'
    }

    stage('Build') {
        //'mvn clean compile'
        // Run the maven build
        if (isUnix()) {
            sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
        } else {
            bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
        }
    }

    stage('Unit Test') {
        'mvn test'
        //junit 'reports/**/*.xml'
    }

    stage ('Open Source Scan') {
      //look for bad open source libraries
    }

    stage ('Static Code Scan') {
      //find bugs, etc.
    }

    stage ('Static Security Scan') {
      //fortify
    }

    stage ('Integration Test with Mocks') {

    }

    stage('Deploy to Dev as Green') {

    }

    stage('Smoke Test on Dev Green') {

    }

    stage ('Security Tests in Dev Env') {

    }

    stage('Move Traffic to Dev Green') {

    }

}
