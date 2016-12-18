node {

     def mvnHome
     def gradleHome

    stage('Prepare') {

      git 'https://github.com/mandersen94/common-util.git'
      mvnHome = tool 'M3'
      gradleHome = tool 'GRADLE'
    }

    stage('Build') {

        if (isUnix()) {
            sh "'${gradleHome}/bin/gradle' clean compileJava"
        } else {
//            bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
        }
    }

    stage('Unit Test') {

        sh "'${gradleHome}/bin/gradle' test"

        junit 'build/reports/**/*.xml'
    }

    stage ('Open Source Scan') {
      //look for bad open source libraries
      input message: "Does http://localhost:8888/staging/ look good?"
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
