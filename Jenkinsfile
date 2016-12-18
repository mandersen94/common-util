node {
    stage('Prepare') {
      'git clone https://github.com/mandersen94/common-util.git'
    }

    stage('Build') {
        'mvn clean compile'
    }

    stage('Unit Test') {
        sh 'mvn test'
        junit 'reports/**/*.xml' // <4>
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
